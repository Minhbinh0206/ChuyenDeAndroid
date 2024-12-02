package com.example.socialmediatdcproject.model;

import java.util.List;

public class QuestionSurvey {
    private int questionId;
    private String questionContent;
    private List<AnswerSurvey> answerSurveys;

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getQuestionContent() {
        return questionContent;
    }

    public void setQuestionContent(String questionContent) {
        this.questionContent = questionContent;
    }

    public List<AnswerSurvey> getAnswerSurveys() {
        return answerSurveys;
    }

    public void setAnswerSurveys(List<AnswerSurvey> list) {
        this.answerSurveys = list;
    }

    public QuestionSurvey(int questionId, String questionContent, List<AnswerSurvey> list) {
        this.questionId = questionId;
        this.questionContent = questionContent;
        this.answerSurveys = list;
    }

    public QuestionSurvey() {
    }
}
