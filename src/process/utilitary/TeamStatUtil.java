package process.utilitary;

import data.league.League;
import data.player.Player;
import data.team.Team;
import process.repositery.TeamRepositery;

public class TeamStatUtil {

	public static double getAverageNote(Team team) {
		double total = 0;
		int count = 0;

		for (Player player : team.getPlayers().values()) {
			total += PlayerStatUtil.getDisplayedNote(player);
			count++;
		}

		if (count == 0) {
			return 0;
		}
		return total / count;
	}

	public static double getAveragePoints(Team team, boolean currentSeasonSelected) {
		double total = 0;
		int count = 0;

		for (Player player : team.getPlayers().values()) {
			total += PlayerStatUtil.getDisplayedAssets(player, currentSeasonSelected).getPointPerMatch();
			count++;
		}

		if (count == 0) {
			return 0;
		}
		return total / count;
	}

	public static Team findTeamByName(String teamName) {
		return TeamRepositery.getInstance().getTeam(teamName);
	}

	public static String getConferenceName(Team team, League league) {
		if (team == null || team.getConference() == null || team.getConference().equals("")) {
			return "-";
		}
		if ("Ouest".equals(team.getConference())) {
			return "Ouest";
		}
		if ("Est".equals(team.getConference())) {
			return "Est";
		}
		return "-";
	}

	public static String getDivisionName(Team team, League league) {
		if (team == null || team.getDivision() == null || team.getDivision().equals("")) {
			return "-";
		}
		return team.getDivision();
	}
}
