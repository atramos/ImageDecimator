package org.imageviewer.app;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.imageviewer.util.ImageResize;
import org.imageviewer.util.WrapLayout;

@SuppressWarnings("serial")
public class ImagePanel extends JPanel {

	int height;
	int width;
	boolean click = true;
	Component[] components;
	ImagePanel container;
	ImagePanel image;

	public void populate(List<File> images, int sizeMod) throws IOException {

		// sizeMod=(int)Math.round(sizeMod*0.5);
		images.stream().forEach(file -> {
			image = this;
			BufferedImage img = readFile(file);

			height = image.getHeight() / sizeMod;
			width = image.getWidth() / sizeMod;
			width = (int) Math.min(width, height * (img.getWidth() * 1.0 / img.getHeight()));

			ImageIcon icon = new ImageIcon(ImageResize.resizeImageWithHint(img, height, width));
			JLabel label = new JLabel(icon);

			label.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2 && !e.isConsumed() && click) {
						e.consume();
						label.setText(label.getName());
						height = container.getHeight();
						width = container.getWidth();
						width = (int) Math.min(width, height * (img.getWidth() * 1.0 / img.getHeight()));
						icon.setImage(ImageResize.resizeImageWithHint(img, height, width));
						click = !click;
						for (int i = 0; i < components.length; i++) {
							if (components[i] == label) {
								components[i].setVisible(true);
							} else {
								components[i].setVisible(false);
							}
						}

					}
					if (e.getClickCount() == 2 && !e.isConsumed() && !click) {
						e.consume();
						label.setText("");
						int size = 9;
						height = image.getHeight() / size;
						width = image.getWidth() / size;
						width = (int) Math.min(width, height * (img.getWidth() * 1.0 / img.getHeight()));
						icon.setImage(ImageResize.resizeImageWithHint(img, height, width));
						click = !click;
						for (int i = 0; i < components.length; i++) {
							components[i].setVisible(true);
						}
					}
				}
			});

			this.add(label);
			components = this.getComponents();
			img.flush();
		});

		this.setLayout(new WrapLayout());
		container = this;
	}

	private BufferedImage readFile(File file) {
		try {
			return ImageIO.read(file);

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
