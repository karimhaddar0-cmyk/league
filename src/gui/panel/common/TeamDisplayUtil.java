package gui.panel.common;

import data.team.Team;

public class TeamDisplayUtil {

	public static String getAbbreviation(Team team) {
		if (team == null || team.getAbbreviation() == null || team.getAbbreviation().equals("")) {
			return "---";
		}
		return team.getAbbreviation();
	}

	public static String getShortName(Team team) {
		if (team == null || team.getShortName() == null || team.getShortName().equals("")) {
			return "-";
		}
		return team.getShortName();
	}

	public static String getShortName(String teamName) {
		if (teamName == null || teamName.equals("")) {
			return "-";
		}
		String[] words = teamName.split(" ");
		if (words.length == 0) {
			return teamName;
		}
		return words[words.length - 1];
	}

	public static String getCityName(Team team) {
		if (team == null || team.getCity() == null || team.getCity().equals("")) {
			return "-";
		}
		return team.getCity();
	}

	public static String getConferenceLabel(String conferenceName) {
		if (conferenceName == null || conferenceName.equals("")) {
			return "-";
		}
		return conferenceName;
	}
}
