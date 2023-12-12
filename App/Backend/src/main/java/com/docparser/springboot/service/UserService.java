package com.docparser.springboot.service;


import com.docparser.springboot.repository.DocumentRepository;
import com.docparser.springboot.repository.UserRepository;
import com.docparser.springboot.errorhandler.SessionNotFoundException;
import com.docparser.springboot.errorhandler.UserNotFoundException;
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
    private final DocumentRepository documentRepository;
    private final S3BucketStorage s3BucketStorage;
    private static final String USER_NOT_FOUND = "User not found";


    // Fetches a user by their ID.
    public Optional<UserAccount> fetchUserById(String id) {
        Optional<UserAccount> existingAccount = userRepository.getUserInfo(id);
        if (existingAccount.isPresent()) {
            List<UserDocument> userDocuments = existingAccount.map(UserAccount::getUserDocuments).orElseGet(ArrayList::new);
            userDocuments.removeIf(userDoc ->
                    userDoc.getExpirationTime() != null && userDoc.getExpirationTime().isBefore(Instant.now()));
            existingAccount.get().setUserDocuments(userDocuments);
            saveUser(existingAccount.get());
        }

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
        return fetchUserById(userId);
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
        String userId;
        if (userAccount.isEmpty()) {
            userId = UUID.randomUUID().toString();
            UserAccount userAccount1 = new UserAccount();
            userAccount1.setEmail(email);
            userAccount1.setUserId(userId);
            userAccount1.setProvider("magicLink");
            userAccount1.setUsername(SessionUtils.getUsernameFromEmail(email));
            userAccount1.setUserPresets(new DocumentConfig("arial", "12", "000000", "FFFFFF", "1.5", "2.5", "LEFT",
                    false, false, false, false, false, false, false));
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
            throw new UserNotFoundException(USER_NOT_FOUND);
        });
    }

    // Checks if the user is logged in based on the session information.
    public void checkUserLoggedIn(String userId) {
        Optional<SessionInfo> userSession = sessionService.getSessionInfo(userId);
        logger.info(" session info :{}", userSession);
        if (userSession.isEmpty()) {
            throw new SessionNotFoundException("User not logged in");
        }
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
            throw new UserNotFoundException(USER_NOT_FOUND);
        });
    }

    public void deleteUserDocuments(String token, Set<String> documentids) {
        String userId = SessionUtils.getSessionIdFromToken(token);
        checkUserLoggedIn(userId);
        Set<String> documentKeys;
        if (documentids.size() == 1) {
            documentKeys = new HashSet<>();
            documentRepository.getDocumentInfo(documentids.iterator().next()).ifPresent(documentInfo ->
                    documentKeys.add(documentInfo.getDocumentKey()));
            documentRepository.deleteSingleDocument(documentids.iterator().next());
            s3BucketStorage.deleteBucketObjects(documentKeys);
        } else {
            documentRepository.deleteDocument(documentids);
            documentKeys = documentRepository.getDocumentKeys(documentids);
            s3BucketStorage.deleteBucketObjects(documentKeys);
        }

        Optional<UserAccount> userAccount = fetchUserById(userId);
        userAccount.ifPresentOrElse(user -> {
            List<UserDocument> userDocuments = user.getUserDocuments();
            documentids.stream().forEach(documentID -> userDocuments.removeIf(userDocument -> userDocument.getDocumentID().equals(documentID)));
            user.setUserDocuments(userDocuments);
            userRepository.saveUser(user);
        }, () -> {
            throw new UserNotFoundException(USER_NOT_FOUND);
        });
    }

    public String getDocumentVersions(DocumentInfo documentInfo) {
        Optional<VersionInfo> versionInfoLatest = documentInfo.getDocumentVersions().stream()
                .max(Comparator.comparing(VersionInfo::getCreatedDate));
        String latestUrl="";
        if(versionInfoLatest.isPresent()){
            latestUrl = s3BucketStorage.getUploadedObjectUrl(documentInfo.getDocumentKey(), versionInfoLatest.get().getVersionID());
        }
        return latestUrl;
    }

    public List<UserDocumentResponse> getUserDocuments(String token) {
        String userId = SessionUtils.getSessionIdFromToken(token);
        checkUserLoggedIn(userId);
        Optional<UserAccount> userAccount = fetchUserById(userId);
        List<UserDocumentResponse> documentResponseList = new ArrayList<>();
        if (userAccount.isPresent()) {
            List<UserDocument> userDocuments = userAccount.get().getUserDocuments();
            List<String> documentIds = new ArrayList<>();
            userDocuments.stream().forEach(userDocument -> documentIds.add(userDocument.getDocumentID()));
            List<DocumentInfo> documentInfos = documentIds.stream().map(documentId -> documentRepository.getDocumentInfo(documentId).get()).toList();
            documentResponseList = documentInfos.stream().map(documentInfo -> {
                UserDocumentResponse documentResponse = new UserDocumentResponse();
                documentResponse.setDocumentID(documentInfo.getDocumentID());
                documentResponse.setDocumentKey(documentInfo.getDocumentKey());
                documentResponse.setCreatedDate(documentInfo.getCreatedDate());
                documentResponse.setVersion(getDocumentVersions(documentInfo));
                return documentResponse;
            }).toList();

        }
        logger.info("documentResponseList:{}", documentResponseList);
        return documentResponseList;
    }
}
