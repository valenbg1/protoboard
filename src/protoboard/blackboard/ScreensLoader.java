package protoboard.blackboard;

import java.io.File;
import java.util.ArrayList;

import protoboard.Constants.BlackboardC;

/**
 * Loads the previous saved screens within a folder
 * 
 */
class ScreensLoader {
	private Blackboard context;
	private File[] files;

	public ScreensLoader(Blackboard context, File[] files) {
		this.context = context;
		this.files = files;
	}

	public void loadScreens() {
		ArrayList<File> screens = new ArrayList<File>();

		for (int i = 0; (i < files.length) && (i < BlackboardC.max_screens); ++i) {
			if (files[i].getName().matches("^.*\\" + BlackboardC.save_extension + "$"))
				screens.add(files[i]);
		}

		context.loadAndAddScreens(screens);
	}
}