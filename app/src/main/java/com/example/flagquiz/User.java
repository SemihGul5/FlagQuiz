package com.example.flagquiz;

public class User {
    private String name_family;
    private String userName;
    private String eMail;
    private String score;
    private String kalanHak;
    private String docId;

    public User(String name_family, String userName, String eMail, String score, String kalanHak,String docId) {
        this.name_family = name_family;
        this.userName = userName;
        this.eMail = eMail;
        this.score = score;
        this.kalanHak = kalanHak;
        this.docId=docId;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getName_family() {
        return name_family;
    }

    public void setName_family(String name_family) {
        this.name_family = name_family;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getKalanHak() {
        return kalanHak;
    }

    public void setKalanHak(String kalanHak) {
        this.kalanHak = kalanHak;
    }
}
