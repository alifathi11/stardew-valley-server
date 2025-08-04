package org.example.model.user;

public class SecurityQuestion {
    private String question;
    private String answer;

    public SecurityQuestion(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public static SecurityQuestion fromString(String securityQuestion) {
        String[] parts = securityQuestion.split(":");
        return new SecurityQuestion(parts[0], parts[1]);
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
