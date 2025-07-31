package com.example.fm25;

public class User {
    private String username;
    private String password;
    private String league;
    private String team;

    public User(String username, String password, String league, String team){
        this.league=league;
        this.username=username;
        this.team=team;
        this.password=password;
    }

    public String getLeague() {
        return league;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public String getTeam() {
        return team;
    }

    @Override
    public String toString(){
        return username + "," + password + "," + league + "," + team;
    }
}
