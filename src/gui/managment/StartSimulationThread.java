package gui.managment;

import gui.frame.MainGui;
import process.orchestrator.GUIInterface;

public class StartSimulationThread implements Runnable {

	private GUIInterface guiInterface;
	private MainGui mainGui;

	public StartSimulationThread(GUIInterface guiInterface, MainGui mainGui) {
		this.guiInterface = guiInterface;
		this.mainGui = mainGui;
	}

	@Override
	public void run() {
		guiInterface.startSeason();
		mainGui.finishSimulationLoading();
	}
}
