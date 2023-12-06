package com.docparser.springboot.model;

/**
 * The FeedBackForm class represents a model for collecting user feedback.
 * It includes properties for user email, what they liked, disliked, and
 * suggestions for new features.
 */
public class FeedBackForm {

    // Email address of the user providing feedback
    private String email;

    // Details about what the user liked
    private String whatUserLiked;

    // Details about what the user disliked
    private String whatUserDisliked;

    // User suggestions for new features
    private String newFeatures;

    /**
     * Gets the email address of the user.
     * 
     * @return A string representing the user's email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email address.
     * 
     * @param email A string containing the user's email.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the user's feedback about what they liked.
     * 
     * @return A string representing what the user liked.
     */
    public String getWhatUserLiked() {
        return whatUserLiked;
    }

    /**
     * Sets the user's feedback about what they liked.
     * 
     * @param whatUserLiked A string containing the user's likes.
     */
    public void setWhatUserLiked(String whatUserLiked) {
        this.whatUserLiked = whatUserLiked;
    }

    /**
     * Gets the user's feedback about what they disliked.
     * 
     * @return A string representing what the user disliked.
     */
    public String getWhatUserDisliked() {
        return whatUserDisliked;
    }

    /**
     * Sets the user's feedback about what they disliked.
     * 
     * @param whatUserDisliked A string containing the user's dislikes.
     */
    public void setWhatUserDisliked(String whatUserDisliked) {
        this.whatUserDisliked = whatUserDisliked;
    }

    /**
     * Gets the user's suggestions for new features.
     * 
     * @return A string representing the user's suggestions for new features.
     */
    public String getNewFeatures() {
        return newFeatures;
    }

    /**
     * Sets the user's suggestions for new features.
     * 
     * @param newFeatures A string containing the user's new feature suggestions.
     */
    public void setNewFeatures(String newFeatures) {
        this.newFeatures = newFeatures;
    }
}
