package com.example.socialmediatdcproject.model;

public class RollCall {
    private String studentNumber;
    private String codeRollCall;
    private int isVerify;

    public static final int JOIN = 0;
    public static final int SENT = 1;
    public static final int VERIFIED = 2;

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getCodeRollCall() {
        return codeRollCall;
    }

    public void setCodeRollCall(String codeRollCall) {
        this.codeRollCall = codeRollCall;
    }

    public int getIsVerify() {
        return isVerify;
    }

    public void setIsVerify(int isVerify) {
        this.isVerify = isVerify;
    }

    public RollCall(String studentNumber, String codeRollCall, int isVerify) {
        this.studentNumber = studentNumber;
        this.codeRollCall = codeRollCall;
        this.isVerify = isVerify;
    }

    public RollCall() {
    }
}
