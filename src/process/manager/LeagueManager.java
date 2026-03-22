package process.manager;

import java.time.LocalDate;
import java.time.Month;

import config.CalendarConfiguration;
import data.league.League;
import data.league.finance.LeagueFinancialRules;
import data.sport.setup.Game;
import data.team.Team;
import data.team.finance.financialpolicy.FinancialPolicy;
import data.team.finance.marketsize.MarketSize;
import process.builder.CalendarBuilder;
import process.builder.LeagueBuilder;
import process.builder.SimulationBuilder;
import process.manager.leaguetools.TeamPopularityUpdater;
import process.manager.submanager.FinanceManager;
import process.manager.submanager.GameManager;
import process.manager.submanager.TradeManager;
import process.repositery.TeamRepositery;
import process.utilitary.FinanceUtilitary;
import process.utilitary.TeamUtilitary;
import process.visitor.financialprofil.ChooseTransferStrategyVisitor;

public class LeagueManager {
    private League league;
    private LeagueBuilder leagueBuilder = new LeagueBuilder();
    private CalendarBuilder calendarBuilder;
    private SimulationBuilder simulationBuilder = new SimulationBuilder();
    private GameManager gameManager = null;
    private TradeManager tradeManager;
    private FinanceManager financeManager;
    private TeamPopularityUpdater teamPopularityUpdater = new TeamPopularityUpdater();

    public LeagueManager() {
        league = leagueBuilder.build();
        FinanceUtilitary.updateLeaguePayroll();

        calendarBuilder = new CalendarBuilder(league);
        financeManager = new FinanceManager(league);
        gameManager = new GameManager(league, financeManager);
        LeagueFinancialRules leagueFinancialRules = league.getLeagueFinance().getLeagueFinancialRules();
        tradeManager = new TradeManager(leagueFinancialRules.getSalaryCap(), leagueFinancialRules.getLuxuryTaxLine());

    }

    public void startSeason() {
        simulationBuilder.build();
        simulatePreSeasonTrade();
        teamPopularityUpdater.updateBeforeSeason();
        buildRegularSeasonCalendar();
        league.getLeagueFinance().getBudget().getInitialAmount();
    }

    public void prepareOpeningData() {
        simulationBuilder.build();
        FinanceUtilitary.updateLeaguePayroll();
    }

    private void simulatePreSeasonTrade() {
        tradeManager.simulatePreSeasonTrade(0);
    }

    public void simulateTrade(LocalDate date, int month) {
        tradeManager.simulateSeasonTrade(date, month);
    }

    private void buildRegularSeasonCalendar() {
        calendarBuilder.buildRegulaSeasonCalendar();
    }

    public boolean simulateRegularSeasonDay(LocalDate date, int month) {
        return gameManager.simulateRegularSeasonDay(date, month);
    }

    public void newMonth(int month) {
        teamPopularityUpdater.updateMonthlyPopularity();
        financeManager.applyMonthlyFinance(month);
    }

    public void newWeek(LocalDate date, int month) {
        tradeManager.simulateSeasonTrade(date, month);
    }

    public void randomFinancialPolicy() {
        for (Team team : TeamRepositery.getInstance().getAllTeams()) {
            FinancialPolicy financialProfil = TeamUtilitary.randomFinancialProfil();
            chooseFinancialPolicy(team, financialProfil);
        }
    }

    public void chooseFinancialPolicy(Team team, FinancialPolicy financialProfil) {
        team.getTeamFinance().setFinancialProfil(financialProfil);
        team.getTeamFinance()
                .setTeamTransferStrategy(financialProfil.accept(new ChooseTransferStrategyVisitor(team.getRival())));
    }

    public void chooseMarketSize(Team team, MarketSize marketSize) {
        team.getTeamFinance().setMarketSize(marketSize);
    }

    public void randomMarketSize() {
        for (Team team : TeamRepositery.getInstance().getAllTeams()) {
            MarketSize marketSize = TeamUtilitary.randomMarketSize();
            chooseMarketSize(team, marketSize);
        }
    }

    public League getLeague() {
        return league;
    }

    public FinanceManager getFinanceManager() {
        return financeManager;
    }

    public boolean simulateGameDay(LocalDate date, int month) {
        return gameManager.simulateGameDay(date, month);
    }

    public boolean simulateGame(Game game, LocalDate date) {
        return gameManager.simulateGame(game, date, this.computeMonth(date));
    }

    private int computeMonth(LocalDate date) {
        Month debutMonth = CalendarConfiguration.REGULAR_SEASON_DEBUT_DATE.getMonth();
        Month currentMonth = date.getMonth();
        int monthsBetween = currentMonth.getValue() - debutMonth.getValue();
        if (monthsBetween < 0) {
            monthsBetween += 12;
        }
        return monthsBetween + 1;
    }
}
