package process.factory;

import data.finance.budget.Budget;
import data.team.Stadium;
import data.team.Team;
import data.team.finance.TeamFinance;
import data.team.finance.financialpolicy.FinancialPolicy;
import data.team.finance.marketsize.MarketSize;
import data.team.finance.transfer.TeamTransferStrategy;
import process.utilitary.TeamUtilitary;
import process.visitor.financialprofil.ChooseTransferStrategyVisitor;

public class TeamFactory {
    private static String checkRivalTeam(String rivalTeam) {
        if (rivalTeam.equals("")) {
            return "none";
        }
        return rivalTeam;
    }

    public static Team createTeam(String line) {
        String[] data = line.split(",", -1);
        String teamName = data[2];
        String rivalTeamName = TeamFactory.checkRivalTeam(data[11]);
        double teamPopularity = Float.valueOf(data[12]).floatValue();
        FinancialPolicy financialProfil = TeamUtilitary.randomFinancialProfil();
        MarketSize marketSize = TeamUtilitary.randomMarketSize();
        Budget budget = new Budget(0.0);
        TeamTransferStrategy teamTransferStrategy = financialProfil
                .accept(new ChooseTransferStrategyVisitor(rivalTeamName));
        TeamFinance teamFinance = new TeamFinance(financialProfil, budget, marketSize, teamTransferStrategy);
        String stadiumName = data[33];
        Stadium stadium = new Stadium(stadiumName, 0.0, 0);
        Team team = new Team(teamName, rivalTeamName, teamPopularity, teamFinance, stadium);
        team.setAbbreviation(data[3]);
        team.setConference(data[4]);
        team.setDivision(data[5]);
        team.setCity(data[34]);
        team.setShortName(data[35]);
        return team;
    }
}
