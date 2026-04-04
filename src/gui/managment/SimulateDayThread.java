package gui.managment;

import java.time.LocalDate;

import gui.panel.calendarPanel.WeekViewPanel;
import process.orchestrator.GUIInterface;

public class SimulateDayThread implements Runnable {

   private final GUIInterface guiInterface;
   private final LocalDate day;
   private final WeekViewPanel panel;

   public SimulateDayThread(GUIInterface guiInterface, LocalDate day, WeekViewPanel panel) {
      this.guiInterface = guiInterface;
      this.day = day;
      this.panel = panel;
   }

   @Override
   public void run() {
      panel.setFinishedSimulation(false);
      guiInterface.simulateDay(day);
      guiInterface.displayGameDay(day);
      panel.setFinishedSimulation(true);
   }

}
