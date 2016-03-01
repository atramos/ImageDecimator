package org.imageviewer.app;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class AppMain {

	public static void main(String[] args) {
		AppUI ui = new AppUI();

		AcceptOrReject aor = new AcceptOrReject(ui);

		ui.imagePanel.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				if (aor.isSetAvailable() && (e.getKeyCode() - 49) < aor.ImageSets[aor.nextsetofimage].size()
						&& (e.getKeyCode() - 49) >= 0) {
					aor.acceptInCurrentImageSet(e.getKeyCode() - 49);
					ui.imagePanel.removeTopBottom();
					ui.imagePanel.removeAll();
					ui.imagePanel.repaint();
					aor.workingImages();
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub

			}
		});

		ui.start(aor);
	}

}