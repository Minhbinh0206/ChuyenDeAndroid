package com.example.socialmediatdcproject.model;

import java.util.HashMap;
import java.util.List;

public class Survey {
    private int eventId;
    private int adminId;
    private int surveyId;
    private List<QuestionSurvey> questionSurveys;

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(int surveyId) {
        this.surveyId = surveyId;
    }

    public List<QuestionSurvey> getQuestionSurveys() {
        return questionSurveys;
    }

    public void setQuestionSurveys(List<QuestionSurvey> list) {
        this.questionSurveys = list;
    }

    public Survey(int eventId, int adminId, int surveyId, List<QuestionSurvey> questionSurveys) {
        this.eventId = eventId;
        this.adminId = adminId;
        this.surveyId = surveyId;
        this.questionSurveys = questionSurveys;
    }

    public Survey() {
    }
}
