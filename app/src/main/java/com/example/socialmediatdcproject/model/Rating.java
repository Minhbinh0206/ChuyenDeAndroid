package com.example.socialmediatdcproject.model;

public class Rating {
    private int userId;
    private int ratingStar;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRatingStar() {
        return ratingStar;
    }

    public void setRatingStar(int ratingStar) {
        this.ratingStar = ratingStar;
    }

    public Rating(int userId, int surveyId, int ratingStar) {
        this.userId = userId;
        this.ratingStar = ratingStar;
    }

    public Rating() {
    }
}
