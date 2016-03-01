package org.imageviewer.app;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.IIOException;
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
	int count = 0;
	JPanel top;
	JPanel bottom;
	AcceptOrReject aor;
	int old_height = 0;
	int old_width = 0;
	private final Object lock = new Object();

	public void populate(List<File> images, int sizeMod, AcceptOrReject aor) throws IOException {

		// int count=0;
		this.aor = aor;
		top = new JPanel();
		bottom = new JPanel();

		// sizeMod=(int)Math.round(sizeMod*0.5);

		images.stream().forEach(file -> {
			image = this;
			BufferedImage img;
			synchronized (lock) {
				img = readFile(file);
				old_height = height;

				height = super.getHeight() / 2 - 20;// image.getHeight() /
													// sizeMod;
				width = (height * img.getWidth()) / img.getHeight();// image.getWidth()
																	// /
																	// sizeMod;
				// width = (int) Math.min(width, height * (img.getWidth() * 1.0
				// / img.getHeight()));
			}
			ImageIcon icon = new ImageIcon(ImageResize.resizeImageWithHint(img, height, width));
			JLabel label = new JLabel(icon);

			if (count < 2) {
				if (count == 0)
					top.add(label, BorderLayout.WEST);
				else
					top.add(label, BorderLayout.EAST);
			} else {

				if (count == 2)
					bottom.add(label, BorderLayout.WEST);
				else
					bottom.add(label, BorderLayout.EAST);
			}
			components = this.getComponents();
			// img.flush();

			count++;
		});

		this.add(top, BorderLayout.NORTH);
		this.add(bottom, BorderLayout.SOUTH);

		this.setLayout(new WrapLayout());
		container = this;

	}

	private BufferedImage readFile(File file) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(file);

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return img;
	}

	public void removeTopBottom() {
		this.remove(top);
		this.remove(bottom);
		count = 0;
	}

}
