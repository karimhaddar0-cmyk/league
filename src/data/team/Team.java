/*
 * Decompiled with CFR 0.152.
 */
package data.team;

import data.player.Player;
import data.sport.setup.Game;
import data.team.Stadium;
import data.team.TeamPerformance;
import data.team.calendar.Schedule;
import data.team.finance.TeamFinance;
import java.util.HashMap;

public class Team {
    private String name;
    private String city;
    private String shortName;
    private String abbreviation;
    private String conference;
    private String division;
    private String rival;
    private double popularity;
    private TeamPerformance teamPerformance = new TeamPerformance();
    private HashMap<String, Player> players = new HashMap();
    private Schedule schedule;
    private Player starPlayer = null;
    private TeamFinance teamFinance;
    private Stadium stadium;

    public Team(String name, String rival, double popularity, TeamFinance teamFinance, Stadium stadium) {
        this.name = name;
        this.city = "";
        this.shortName = "";
        this.abbreviation = "";
        this.conference = "";
        this.division = "";
        this.rival = rival;
        this.schedule = new Schedule();
        this.popularity = popularity;
        this.schedule = new Schedule();
        this.starPlayer = null;
        this.teamFinance = teamFinance;
        this.stadium = stadium;
    }

    public String getName() {
        return this.name;
    }

    public void setNom(String name) {
        this.name = name;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getShortName() {
        return this.shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getAbbreviation() {
        return this.abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getConference() {
        return this.conference;
    }

    public void setConference(String conference) {
        this.conference = conference;
    }

    public String getDivision() {
        return this.division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getRival() {
        return this.rival;
    }

    public void setRival(String rival) {
        this.rival = rival;
    }

    public double getPopularity() {
        return this.popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public HashMap<String, Player> getPlayers() {
        return this.players;
    }

    public void setPlayers(HashMap<String, Player> players) {
        this.players = players;
    }

    public void addPlayer(Player player) {
        this.players.put(player.getName(), player);
    }

    public Schedule getSchedule() {
        return this.schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public boolean hasStarPlayer() {
        return this.starPlayer == null;
    }

    public void addGame(Game game) {
        this.schedule.addGame(game);
    }

    public String toString() {
        String s = "Team [name=" + this.name + ", " + ", rival=" + this.rival + ", popularity=" + this.popularity;
        for (Player player : this.players.values()) {
            s = String.valueOf(s) + "\n" + player.toString();
        }
        s = String.valueOf(s) + "\n schedule=" + this.schedule;
        if (this.starPlayer != null) {
            s = String.valueOf(s) + "starPLayer =" + this.starPlayer.toString();
        }
        return s;
    }

    public Player getStarPlayer() {
        return this.starPlayer;
    }

    public void setStarPlayer(Player starPlayer) {
        this.starPlayer = starPlayer;
    }

    public TeamFinance getTeamFinance() {
        return this.teamFinance;
    }

    public Stadium getStadium() {
        return this.stadium;
    }

    public TeamPerformance getTeamPerformance() {
        return this.teamPerformance;
    }

    public void setTeamPerformance(TeamPerformance teamPerformance) {
        this.teamPerformance = teamPerformance;
    }
}
