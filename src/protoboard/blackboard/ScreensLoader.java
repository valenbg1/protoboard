package protoboard.blackboard;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import protoboard.Constants.BlackboardC;

/**
 * Loads the previous saved screens within a folder
 * 
 */
class ScreensLoader {
	private Blackboard context;
	private File folder;
	private FilenameFilter filter;

	public ScreensLoader(Blackboard context, File folder) {
		this.context = context;
		this.folder = folder;

		this.filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.matches(BlackboardC.save_name + "\\[\\d+\\]_\\d+"
						+ BlackboardC.save_extension);
			}
		};
	}

	private int getScreenNumber(String name) {
		int ret;

		try {
			ret = Integer.valueOf(name.split("[\\[\\]]")[1]);
		} catch (NumberFormatException e) {
			ret = -1;
		}

		return ret;
	}

	public void loadScreens() {
		File[] screens_files = folder.listFiles(filter);
		ArrayList<File> screens = new ArrayList<File>();

		for (int i = 0; i < screens_files.length; ++i) {
			int number = getScreenNumber(screens_files[i].getName());

			if (number < BlackboardC.max_screens) {
				while (number >= screens.size())
					screens.add(null);

				screens.set(number, screens_files[i]);
			}
		}

		context.loadAndAddScreens(screens);
	}
}