package process.visitor.actionresult;

import config.GameConfiguration;
import data.player.Player;
import data.sport.play.action.Block;
import data.sport.play.action.EndOfTime;
import data.sport.play.action.MissedShot;
import data.sport.play.action.PointScored;
import data.sport.play.action.Rebound;
import data.sport.play.action.Turnover;
import data.sport.setup.Game;
import java.util.HashMap;

import process.manager.LiveMatchStatistics;

public class StatsVisitor implements ActionResultVisitor<Void> {
    private LiveMatchStatistics liveMatchStatistics;

    public StatsVisitor(LiveMatchStatistics liveMatchStatistics) {
        this.liveMatchStatistics = liveMatchStatistics;
    }

    @Override
    public Void visit(PointScored pointScored) {
        Player scorer = pointScored.getScorerPlayer();
        boolean homeScorer = isHomePlayer(scorer, liveMatchStatistics.getGame());
        String shotType = pointScored.getOffensiveAction() == null ? "" : pointScored.getOffensiveAction().getName();

        int points = 1;
        if (GameConfiguration.THREEPOINT.equals(shotType)) {
            points = 3;
        } else if (GameConfiguration.TWOPOINT.equals(shotType)) {
            points = 2;
        }

        if (homeScorer) {
            liveMatchStatistics.setHomePoints(liveMatchStatistics.getHomePoints() + points);
            liveMatchStatistics.getHomePlayerPoints().put(scorer.getName(),
                    getPlayerPoints(liveMatchStatistics.getHomePlayerPoints(), scorer.getName()) + points);
            liveMatchStatistics.getHomePlayers().put(scorer.getName(), scorer);
        } else {
            liveMatchStatistics.setAwayPoints(liveMatchStatistics.getAwayPoints() + points);
            liveMatchStatistics.getAwayPlayerPoints().put(scorer.getName(),
                    getPlayerPoints(liveMatchStatistics.getAwayPlayerPoints(), scorer.getName()) + points);
            liveMatchStatistics.getAwayPlayers().put(scorer.getName(), scorer);
        }

        if (GameConfiguration.THREEPOINT.equals(shotType)) {
            if (homeScorer) {
                liveMatchStatistics.setHomeThreeMade(liveMatchStatistics.getHomeThreeMade() + 1);
                liveMatchStatistics.setHomeThreeAttempts(liveMatchStatistics.getHomeThreeAttempts() + 1);
                liveMatchStatistics.setHomeFgAttempts(liveMatchStatistics.getHomeFgAttempts() + 1);
            } else {
                liveMatchStatistics.setAwayThreeMade(liveMatchStatistics.getAwayThreeMade() + 1);
                liveMatchStatistics.setAwayThreeAttempts(liveMatchStatistics.getAwayThreeAttempts() + 1);
                liveMatchStatistics.setAwayFgAttempts(liveMatchStatistics.getAwayFgAttempts() + 1);
            }
        } else if (GameConfiguration.TWOPOINT.equals(shotType)) {
            if (homeScorer) {
                liveMatchStatistics.setHomeTwoMade(liveMatchStatistics.getHomeTwoMade() + 1);
                liveMatchStatistics.setHomeFgAttempts(liveMatchStatistics.getHomeFgAttempts() + 1);
            } else {
                liveMatchStatistics.setAwayTwoMade(liveMatchStatistics.getAwayTwoMade() + 1);
                liveMatchStatistics.setAwayFgAttempts(liveMatchStatistics.getAwayFgAttempts() + 1);
            }
        }

        Player assist = pointScored.getAssistPlayer();
        if (assist != null) {
            if (isHomePlayer(assist, liveMatchStatistics.getGame())) {
                liveMatchStatistics.setHomeAssists(liveMatchStatistics.getHomeAssists() + 1);
            } else {
                liveMatchStatistics.setAwayAssists(liveMatchStatistics.getAwayAssists() + 1);
            }
        }
        return null;
    }

    @Override
    public Void visit(MissedShot missedShot) {
        Player shooter = missedShot.getShooter();
        boolean homeShooter = isHomePlayer(shooter, liveMatchStatistics.getGame());
        String shotType = missedShot.getOffensiveAction() == null ? "" : missedShot.getOffensiveAction().getName();

        if (GameConfiguration.THREEPOINT.equals(shotType)) {
            if (homeShooter) {
                liveMatchStatistics.setHomeThreeAttempts(liveMatchStatistics.getHomeThreeAttempts() + 1);
                liveMatchStatistics.setHomeFgAttempts(liveMatchStatistics.getHomeFgAttempts() + 1);
            } else {
                liveMatchStatistics.setAwayThreeAttempts(liveMatchStatistics.getAwayThreeAttempts() + 1);
                liveMatchStatistics.setAwayFgAttempts(liveMatchStatistics.getAwayFgAttempts() + 1);
            }
        } else if (GameConfiguration.TWOPOINT.equals(shotType)) {
            if (homeShooter) {
                liveMatchStatistics.setHomeFgAttempts(liveMatchStatistics.getHomeFgAttempts() + 1);
            } else {
                liveMatchStatistics.setAwayFgAttempts(liveMatchStatistics.getAwayFgAttempts() + 1);
            }
        }
        return null;
    }

    @Override
    public Void visit(Turnover turnover) {
        if (isHomePlayer(turnover.getDefensePlayer(), liveMatchStatistics.getGame())) {
            liveMatchStatistics.setHomeTurnovers(liveMatchStatistics.getHomeTurnovers() + 1);
        } else {
            liveMatchStatistics.setAwayTurnovers(liveMatchStatistics.getAwayTurnovers() + 1);
        }
        return null;
    }

    @Override
    public Void visit(Block block) {
        return null;
    }

    @Override
    public Void visit(Rebound rebound) {
        if (isHomePlayer(rebound.getReboundPlayer(), liveMatchStatistics.getGame())) {
            liveMatchStatistics.setHomeRebounds(liveMatchStatistics.getHomeRebounds() + 1);
        } else {
            liveMatchStatistics.setAwayRebounds(liveMatchStatistics.getAwayRebounds() + 1);
        }
        return null;
    }

    @Override
    public Void visit(EndOfTime endOfTime) {
        return null;
    }

    private int getPlayerPoints(HashMap<String, Integer> map, String playerName) {
        Integer current = map.get(playerName);
        return current == null ? 0 : current.intValue();
    }

    private boolean isHomePlayer(Player player, Game game) {
        return game.getGameContext().getHomeTeam().getPlayers().containsKey(player.getName());
    }

}
