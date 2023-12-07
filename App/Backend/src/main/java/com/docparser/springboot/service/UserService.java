package com.docparser.springboot.service;

import com.docparser.springboot.Repository.SessionRepository;
import com.docparser.springboot.Repository.UserRepository;
import com.docparser.springboot.errorHandler.SessionNotFoundException;
import com.docparser.springboot.errorHandler.UserNotFoundException;
import com.docparser.springboot.model.*;
import com.docparser.springboot.utils.ParsingUtils;
import com.docparser.springboot.utils.SessionUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final SessionService sessionService;
    private final EmailService emailService;

    // Fetches a user by their email. Throws an exception if the user is not found.
    public Optional<UserAccount> fetchUserByEmail(String email) {
        Optional<UserAccount> existingAccount = userRepository.getUserInfobyEmail(email);
        existingAccount.orElseThrow(() -> {
            logger.error("User not found");
            return new UserNotFoundException("User not found");
        });
        return existingAccount;
    }

    // Fetches a user by their ID.
    public Optional<UserAccount> fetchUserById(String id) {
        Optional<UserAccount> existingAccount = userRepository.getUserInfo(id);
        return existingAccount;
    }

    // Saves a user account to the repository.
    public void saveUser(UserAccount user) {
        userRepository.saveUser(user);
    }

    /*
     * Retrieves the logged-in user based on the session token and updates their
     * documents.
     */
    public Optional<UserAccount> getLoggedInUser(String token) {
        String userId = SessionUtils.getSessionIdFromToken(token);
        checkUserLoggedIn(userId);
        Optional<UserAccount> existingAccount = userRepository.getUserInfo(userId);
        if (existingAccount.isPresent()) {
            List<UserDocument> userDocuments = existingAccount.map(UserAccount::getUserDocuments)
                    .orElseGet(ArrayList::new);
            userDocuments.removeIf(userDoc -> userDoc.getExpirationTime() != null
                    && userDoc.getExpirationTime().isBefore(Instant.now()));
            existingAccount.get().setUserDocuments(userDocuments);
            saveUser(existingAccount.get());
        }
        return existingAccount;
    }

    // Authenticates a user by generating a magic token and sending it via email.
    public void authenticateUserWithEmailLink(String email) {
        SessionInfo sessionInfo = new SessionInfo();
        String magicToken = UUID.randomUUID().toString();
        sessionInfo.setSessionID(magicToken);
        sessionInfo.setCreatedDate(Instant.now());
        sessionInfo.setExpirationTime(Instant.now().plusSeconds(15 * 60));
        sessionService.saveSessionInfo(sessionInfo);
        emailService.sendSimpleMessage(email, magicToken);
    }

    /*
     * Validates a magic token and returns a token response. Creates a new user if
     * not found.
     */
    public TokenResponse validateMagicToken(String token, String email) {
        Optional<SessionInfo> sessionInfo = sessionService.getSessionInfo(token);
        if (sessionInfo.isEmpty()) {
            throw new SessionNotFoundException("Code not Valid");
        }
        if (sessionInfo.get().getExpirationTime().isBefore(Instant.now())) {
            throw new SessionNotFoundException("Code Expired");
        }
        Optional<UserAccount> userAccount = userRepository.getUserInfobyEmail(email);
        String userId = null;
        if (userAccount.isEmpty()) {
            userId = UUID.randomUUID().toString();
            UserAccount userAccount1 = new UserAccount();
            userAccount1.setEmail(email);
            userAccount1.setUserId(userId);
            userAccount1.setProvider("magicLink");
            userAccount1.setUsername(email);
            userAccount1.setUserPresets(new DocumentConfig("arial", "12", "000000", "FFFFFF", "1.5", "2.5", "LEFT",
                    false, false, false, false, false, false));
            userRepository.saveUser(userAccount1);
        } else {
            userId = userAccount.get().getUserId();
        }
        String sessionID = SessionUtils.generateToken(userId, SessionUtils.getTime(), SessionUtils.getExpirationTime());
        sessionService.saveSessionInfo(new SessionInfo(userId, sessionID, SessionUtils.getTime().toInstant(),
                SessionUtils.getExpirationTime().toInstant()));
        return new TokenResponse(sessionID, SessionUtils.getExpirationTime().toInstant().toString());
    }

    // Updates user information based on the provided document configuration.
    public void updateUserInfo(String token, DocumentConfig documentConfig) {
        String userId = SessionUtils.getSessionIdFromToken(token);
        checkUserLoggedIn(userId);
        Optional<UserAccount> userAccount = fetchUserById(userId);
        userAccount.ifPresentOrElse(user -> {
            DocumentConfig targetConfig = user.getUserPresets();
            if (targetConfig == null) {
                targetConfig = new DocumentConfig();
            }
            ParsingUtils.copyDocumentConfig(documentConfig, targetConfig);
            user.setUserPresets(targetConfig);
            userRepository.saveUser(user);
        }, () -> {
            throw new UserNotFoundException("User not found");
        });
    }

    // Checks if the user is logged in based on the session information.
    public void checkUserLoggedIn(String userId) {
        Optional<SessionInfo> userSession = sessionService.getSessionInfo(userId);
        userSession.orElseThrow(() -> {
            logger.error("User not logged in");
            return new SessionNotFoundException("User not logged in");
        });
    }

    // Logs out a user by deleting their session.
    public void logoutUser(String token) {
        String userId = SessionUtils.getSessionIdFromToken(token);
        sessionService.deleteSession(userId);
    }

    // Saves user feedback information.
    public void saveFeedbackInfo(String token, FeedBackForm feedBackForm) {
        String userId = SessionUtils.getSessionIdFromToken(token);
        checkUserLoggedIn(userId);
        Optional<UserAccount> userAccount = fetchUserById(userId);
        userAccount.ifPresentOrElse(user -> {
            List<FeedBackForm> feedBackForms = user.getFeedBackForms();
            if (feedBackForms == null) {
                feedBackForms = new ArrayList<>();
            }
            feedBackForms.add(feedBackForm);
            user.setFeedBackForms(feedBackForms);
            userRepository.saveUser(user);
        }, () -> {
            throw new UserNotFoundException("User not found");
        });
    }
}
