package com.example.socialmediatdcproject.dataModels;

public class Question {
    private  int questionId;
    private int groupId;
    private String groupQuestion;

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getGroupQuestion() {
        return groupQuestion;
    }

    public void setGroupQuestion(String groupQuestion) {
        this.groupQuestion = groupQuestion;
    }

    public Question(int questionId, int groupId, String groupQuestion) {
        this.questionId = questionId;
        this.groupId = groupId;
        this.groupQuestion = groupQuestion;
    }

    public Question() {
    }
}
