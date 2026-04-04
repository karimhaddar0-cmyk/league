package gui.app;

import gui.frame.MainGui;
import process.orchestrator.GUIInterface;
import process.orchestrator.SimulationManager;

public class App {

	public static void main(String[] args) {
		GUIInterface guiInterface = new SimulationManager();
		MainGui gui = new MainGui(guiInterface);
	}
}
