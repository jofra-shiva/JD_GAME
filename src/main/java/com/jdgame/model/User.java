package com.jdgame.model;

public class User {
    private int id;
    private String username;
    private String email;
    private int highLevelReached;
    private int score;
    private String lastSavedState;

    public User(int id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public int getHighLevelReached() { return highLevelReached; }
    public void setHighLevelReached(int lvl) { this.highLevelReached = lvl; }
    public int getScore() { return score; }
    public void setScore(int s) { this.score = s; }
    public String getLastSavedState() { return lastSavedState; }
    public void setLastSavedState(String st) { this.lastSavedState = st; }
}
