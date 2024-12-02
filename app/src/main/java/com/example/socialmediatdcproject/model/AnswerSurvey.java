package com.example.socialmediatdcproject.model;

public class AnswerSurvey {
    private int answerId;
    private String answerContent;
    private int userId;
    private int questionId;

    public int getAnswerId() {
        return answerId;
    }

    public void setAnswerId(int answerId) {
        this.answerId = answerId;
    }

    public String getAnswerContent() {
        return answerContent;
    }

    public void setAnswerContent(String answerContent) {
        this.answerContent = answerContent;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public AnswerSurvey(int answerId, String answerContent, int userId, int questionId) {
        this.answerId = answerId;
        this.answerContent = answerContent;
        this.userId = userId;
        this.questionId = questionId;
    }

    public AnswerSurvey() {
    }
}
