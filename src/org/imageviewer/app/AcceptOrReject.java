package org.imageviewer.app;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.imageio.ImageIO;

import org.imageviewer.api.IAppServices;

public class AcceptOrReject implements IAppServices {

	private static AppUI ui;
	private List<File> Lfiles;
	private List<File> Duplicatefiles;
	List<File> ImageSets[];
	int nextsetofimage = -1;
	private int imagesetsize = 0;
	private int imageLeft = 0;
	private AcceptOrReject aor = null;
	private final Object lock = new Object();

	public AcceptOrReject(AppUI ui) {
		AcceptOrReject.ui = ui;
		// aor=new AcceptOrReject();
	}

	public void acceptInCurrentImageSet(int index) {

		for (int i = 0; i < aor.ImageSets[nextsetofimage].size(); i++) {
			String filepath = aor.Lfiles.get((nextsetofimage * 4) + i).getPath();
			Path s_path = Paths.get(filepath);
			Path d_path;
			if (i == index) {
				Iterator<File> itr = Duplicatefiles.iterator();

				while (itr.hasNext()) {
					File df = itr.next();
					if (filepath.substring(0, filepath.lastIndexOf('.'))
							.equals(df.getPath().substring(0, df.getPath().lastIndexOf('.')))) {
						Path d_s_path = Paths.get(df.getPath());
						String d_dPathString = new StringBuilder(df.getPath())
								.insert(filepath.lastIndexOf('\\') + 1, "accept\\").toString();
						Path d_d_path = Paths.get(d_dPathString);

						try {

							Files.move(d_s_path, d_d_path, StandardCopyOption.REPLACE_EXISTING);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}

				String dPathString = new StringBuilder(filepath).insert(filepath.lastIndexOf('\\') + 1, "accept\\")
						.toString();
				d_path = Paths.get(dPathString);
				try {

					Files.move(s_path, d_path, StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e) {
					// TODO Auto-generated catch block
				}

			} else {

				Iterator<File> itr = Duplicatefiles.iterator();

				while (itr.hasNext()) {
					File df = itr.next();
					if (filepath.substring(0, filepath.lastIndexOf('.'))
							.equals(df.getPath().substring(0, df.getPath().lastIndexOf('.')))) {
						Path d_s_path = Paths.get(df.getPath());
						String d_dPathString = new StringBuilder(df.getPath())
								.insert(filepath.lastIndexOf('\\') + 1, "reject\\").toString();
						Path d_d_path = Paths.get(d_dPathString);

						try {

							Files.move(d_s_path, d_d_path, StandardCopyOption.REPLACE_EXISTING);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}

				String dPathString = new StringBuilder(filepath).insert(filepath.lastIndexOf('\\') + 1, "reject\\")
						.toString();
				d_path = Paths.get(dPathString);
				try {
					Files.move(s_path, d_path, StandardCopyOption.REPLACE_EXISTING);
				} catch (NoSuchFileException e) {
					// JPEG file already moved
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		imageLeft = imageLeft - ImageSets[nextsetofimage].size();

		ui.setStatus(String.format(">> %d Images Left.", imageLeft));
	}

	public boolean isSetAvailable() {
		if (aor.nextsetofimage < aor.imagesetsize)
			return true;

		return false;
	}

	public void onOpen(File dir) {
		// String[] names;
		aor = this;

		nextsetofimage = -1;

		File files[] = dir.listFiles(file -> file.getName()
				.matches("(?i).*\\.(arw|bmp|gif|j2c|j2k|jff|jp2|jpe|jpeg|jpg|jpx|png|pnm|raw|tif|tiff|wbmp)$"));

		File accept = new File(dir.getPath() + "\\accept");
		File reject = new File(dir.getPath() + "\\reject");
		if (!accept.exists())
			accept.mkdir();
		if (!reject.exists())
			reject.mkdir();

		Map<String, File> hm = new TreeMap<String, File>();
		Lfiles = new ArrayList<File>();
		Duplicatefiles = new ArrayList<File>();
		int flag = 0;

		String UniqueString;
		for (int i = 0; i < files.length; i++) {
			List<String> inputFormats = Arrays.asList(ImageIO.getReaderFormatNames());
			long waiting = 0;
			synchronized (lock) {
				UniqueString = files[i].getName().substring(0, files[i].getName().lastIndexOf('.'));
				String ext = files[i].getName().substring(files[i].getName().lastIndexOf('.') + 1);

				if (!inputFormats.contains(ext)) {
					StringBuilder path = new StringBuilder(files[i].getPath());
					String[] cmd = { "cmd.exe", "/c", "z:\\usr\\exiftool " + "\"" + files[i].getPath() + "\""
							+ " -previewimage -b > " + "\"" + files[i].getPath().replace("." + ext, ".jpeg") + "\"" };
					waiting += files[i].getTotalSpace();

					synchronized (lock) {

						try {
							Process p = Runtime.getRuntime().exec(cmd);
							p.waitFor();
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

						File f = new File(files[i].getPath());
						Duplicatefiles.add(f);

						files[i] = new File(files[i].getPath().replace("." + ext, ".jpeg"));

					}

				}

				if (hm.get(UniqueString) == null) {
					hm.put(UniqueString, files[i]);
					Lfiles.add(files[i]);
				} else {
					if (UniqueString.substring(UniqueString.lastIndexOf('.') + 1).equalsIgnoreCase("jpeg")) {
						Duplicatefiles.add(hm.get(UniqueString));
						hm.put(UniqueString, files[i]);
					} else {
						Duplicatefiles.add(files[i]);
					}
				}
			}
		}

		imageLeft = Lfiles.size();

		if (Lfiles.size() % 4 == 0) {
			imagesetsize = Lfiles.size() / 4;
		} else {
			imagesetsize = Lfiles.size() / 4 + 1;
		}

		ImageSets = new ArrayList[imagesetsize];

		int indexOfImage = 0;
		for (int i = 0; i < imagesetsize; i++) {
			ImageSets[i] = new ArrayList<File>();

			for (int j = 0; j < 4 && indexOfImage < Lfiles.size(); indexOfImage++, j++) {
				ImageSets[i].add(Lfiles.get(indexOfImage));
			}
		}

		workingImages();

	}

	public void workingImages() {
		aor.nextsetofimage++;

		if (nextsetofimage <= ImageSets.length - 1) {
			ui.setStatus(String.format(">> %d Images Left.", imageLeft));

			try {
				ui.setWorkingImages(ImageSets[nextsetofimage], aor);
			} catch (IOException e) {
				ui.setStatus("ERROR: " + e.toString());
			}

		}
	}

	public void addDuplicate(File f) {
		Duplicatefiles.add(f);
	}

}
