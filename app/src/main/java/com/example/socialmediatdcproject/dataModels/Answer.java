package com.example.socialmediatdcproject.dataModels;

public class Answer {
    private int answerId;
    private int userId;
    private String answerContent;
    private int questionId;

    public int getAnswerId() {
        return answerId;
    }

    public void setAnswerId(int answerId) {
        this.answerId = answerId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAnswerContent() {
        return answerContent;
    }

    public void setAnswerContent(String answerContent) {
        this.answerContent = answerContent;
    }

    public Answer(int answerId, int userId, String content, int questionId) {
        this.answerId = answerId;
        this.userId = userId;
        this.answerContent = content;
        this.questionId = questionId;
    }

    public Answer() {
    }
}
