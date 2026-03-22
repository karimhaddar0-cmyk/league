package process.manager;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.TreeMap;

import config.CalendarConfiguration;
import data.calendar.GameDay;
import data.league.League;
import data.sport.setup.Game;
import data.team.Team;
import data.team.finance.financialpolicy.AmbitiousPolicy;
import data.team.finance.financialpolicy.BalancedPolicy;
import data.team.finance.financialpolicy.ThriftyPolicy;
import data.team.finance.marketsize.LargeSize;
import data.team.finance.marketsize.MediumSize;
import data.team.finance.marketsize.SmallSize;
import process.utilitary.CalendarUtilitary;

//cerveau de la simulation 
public class SimulationManager {
    private LeagueManager leagueManager = new LeagueManager();
    private int month = 1;
    private Month debutMonthDate = CalendarConfiguration.REGULAR_SEASON_DEBUT_DATE.getMonth();
    private Month currentMonthDate = CalendarConfiguration.REGULAR_SEASON_DEBUT_DATE.getMonth();
    private int week = 1;
    private LocalDate debutWeekDate = CalendarConfiguration.REGULAR_SEASON_DEBUT_DATE;
    private LocalDate date = CalendarConfiguration.REGULAR_SEASON_DEBUT_DATE;

    public SimulationManager() {

    }

    // methddes pour la presaison
    // pour page de garde
    public void randomFinance() {
        leagueManager.randomFinancialPolicy();
        leagueManager.randomMarketSize();
    }

    public void chooseAmbitiousPolicy(Team team) {
        leagueManager.chooseFinancialPolicy(team, new AmbitiousPolicy());
    }

    public void chooseBalancedPolicy(Team team) {
        leagueManager.chooseFinancialPolicy(team, new BalancedPolicy());
    }

    public void chooseThriftyPolicy(Team team) {
        leagueManager.chooseFinancialPolicy(team, new ThriftyPolicy());
    }

    public void chooseLargeMarketSize(Team team) {
        leagueManager.chooseMarketSize(team, new LargeSize());
    }

    public void chooseMediumMarketSize(Team team) {
        leagueManager.chooseMarketSize(team, new MediumSize());
    }

    public void chooseSmallMarketSize(Team team) {
        leagueManager.chooseMarketSize(team, new SmallSize());
    }

    // méthode à utiliser pour lancer la saison
    public void startSeason() {
        leagueManager.startSeason();
        resetCalendarCursor();
    }

    // passe le prochain jour, méthode à utiliser pour la simulation et tout se fais
    // tous seul
    public void nextDay() {
        date = date.plusDays(1);
        currentMonthDate = date.getMonth();
        verifyMonth();
        verifyWeek();
    }

    // si nouveau mois les évènements des nouveaux mois sont appliqués comme le
    // partage des revenus etc ...
    private void verifyMonth() {
        int monthsBetween = currentMonthDate.getValue() - debutMonthDate.getValue();
        if (monthsBetween < 0) {
            monthsBetween += 12;
        }
        int newMonth = monthsBetween + 1;
        if (newMonth != month) {
            month = newMonth;
            leagueManager.newMonth(month);
        }
    }

    private void verifyWeek() {
        int daysBetween = (int) ChronoUnit.DAYS.between(debutWeekDate, date);
        int weeksBetween = daysBetween / 7;
        int newWeek = weeksBetween + 1;
        if (newWeek != week) {
            week = newWeek;
            leagueManager.newWeek(date, week);
        }
    }

    // simuler jour par jour
    private void simulateDay() {
        if (CalendarUtilitary.checkDate(date, CalendarConfiguration.REGULAR_SEASON_DEBUT_DATE,
                CalendarConfiguration.REGULAR_SEASON_END_DATE)) {
            leagueManager.simulateRegularSeasonDay(date, month);
        }
        if (CalendarUtilitary.checkDate(date, CalendarConfiguration.PLAYOFF_DEBUT_DATE,
                CalendarConfiguration.PLAYOFF_END_DATE)) {

        }

    }

    private void simulateUpTo(LocalDate targetDate) {
        if (targetDate == null || targetDate.isBefore(date)) {
            return;
        }

        while (!date.isAfter(targetDate)) {
            verifyMonth();
            verifyWeek();
            simulateDay();
            if (date.equals(targetDate)) {
                break;
            }
            nextDay();
        }
    }

    // simuler la fin de saison régulière ou fin playoff
    public void simulateCurrentSeason() {
        if (CalendarUtilitary.checkDate(date, CalendarConfiguration.REGULAR_SEASON_DEBUT_DATE,
                CalendarConfiguration.REGULAR_SEASON_END_DATE)) {
            while (!date.equals(CalendarConfiguration.REGULAR_SEASON_END_DATE)) {
                simulateDay();
                nextDay();
            }
        } else if (CalendarUtilitary.checkDate(date, CalendarConfiguration.PLAYOFF_DEBUT_DATE,
                CalendarConfiguration.PLAYOFF_END_DATE)) {
            while (!date.equals(CalendarConfiguration.PLAYOFF_END_DATE)) {
                simulateDay();
                nextDay();
            }
        }
    }

    private void resetCalendarCursor() {
        date = CalendarConfiguration.REGULAR_SEASON_DEBUT_DATE;
        debutMonthDate = CalendarConfiguration.REGULAR_SEASON_DEBUT_DATE.getMonth();
        currentMonthDate = debutMonthDate;
        month = 1;
    }

    public void displayGameDay(LocalDate date) {
        if (date == null) {
            return;
        }
        simulateUpTo(date);
        GameDay gameDay = leagueManager.getLeague().getReagularSeason().getCalendar().getCalendar().get(date);
        if (gameDay != null) {
            gameDay.setDisplayed(true);
            for (data.sport.setup.Game game : gameDay.getGames()) {
                game.setDisplayed(true);
            }
        }
    }

    public void displayWeek(LocalDate startDate) {
        if (startDate == null) {
            return;
        }
        for (int offset = 0; offset < 7; offset++) {
            displayGameDay(startDate.plusDays(offset));
        }
    }

    public void displayCurrentSeason() {
        TreeMap<LocalDate, GameDay> calendar = leagueManager.getLeague().getReagularSeason().getCalendar()
                .getCalendar();
        for (LocalDate gameDate : calendar.keySet()) {
            displayGameDay(gameDate);
        }
    }

    public League getLeague() {
        return leagueManager.getLeague();
    }

    public LeagueManager getLeagueManager() {
        return leagueManager;
    }

    public LocalDate getCurrentDate() {
        return date;
    }
}
