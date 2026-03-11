package com.jdgame.model;

public class Question {
    private int id;
    private int levelIndex;
    private String text;
    private String[] choices;
    private char correctChoice;

    public Question(int id, int levelIndex, String text, String a, String b, String c, String d, char correct) {
        this.id = id;
        this.levelIndex = levelIndex;
        this.text = text;
        this.choices = new String[]{a,b,c,d};
        this.correctChoice = correct;
    }

    public boolean isCorrect(int choiceIndex) {
        return ('A' + choiceIndex) == correctChoice;
    }
    public String getChoice(int i){ return choices[i]; }
    public String getText(){ return text; }
    public int getId(){ return id; }
}
