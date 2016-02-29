package org.imageviewer.app;

public class AppMain {

	public static void main(String[] args) {
		AppUI ui = new AppUI();
		AcceptOrReject svc = new AcceptOrReject(ui);
		ui.start(svc);
	}
	
}