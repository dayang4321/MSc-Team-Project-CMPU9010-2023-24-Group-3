package com.docparser.springboot.model;

import lombok.*;

/**
 * The FeedBackForm class represents a model for collecting user feedback.
 * It includes properties for user email, what they liked, disliked, and
 * suggestions for new features.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FeedBackForm {

    // Email address of the user providing feedback
    private String email;

    // Details about what the user liked
    private String whatUserLiked;

    // Details about what the user disliked
    private String whatUserDisliked;

    // User suggestions for new features
    private String newFeatures;


}
