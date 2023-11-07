package com.docparser.springboot.model;


public class FeedBackForm {

   private  String email;
    private  String whatUserLiked;
    private  String whatUserDisliked;
    private  String newFeatures;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWhatUserLiked() {
        return whatUserLiked;
    }

    public void setWhatUserLiked(String whatUserLiked) {
        this.whatUserLiked = whatUserLiked;
    }

    public String getWhatUserDisliked() {
        return whatUserDisliked;
    }

    public void setWhatUserDisliked(String whatUserDisliked) {
        this.whatUserDisliked = whatUserDisliked;
    }

    public String getNewFeatures() {
        return newFeatures;
    }

    public void setNewFeatures(String newFeatures) {
        this.newFeatures = newFeatures;
    }
}
