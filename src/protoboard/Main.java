package protoboard;

import java.awt.AWTException;
import java.awt.EventQueue;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.UIManager;

import protoboard.blackboard.Blackboard;
import protoboard.input.Input;
import protoboard.leapmotion.LeapMotionListener;
import protoboard.swing.MainIface;

import com.leapmotion.leap.Controller;

/**
 * Singleton starting point of protoboard.
 * 
 */
public final class Main {
	private static Controller lm_controller = new Controller();
	public static final LeapMotionListener lm_listener = new LeapMotionListener();
	
	private static MainIface main_iface;
	
	private static AtomicBoolean running_input_mode = new AtomicBoolean(false);
	private static Input input_mode;
	
	private static Blackboard blackboard_mode = null;
	private static File[] load_files = null;
	private static AtomicBoolean running_blackboard_mode = new AtomicBoolean(false);
	
	public static synchronized File[] getAndNullLoadFiles_blckbrdMode() {
		File[] ret = load_files;
		load_files = null;
		return ret;
	}
	
	public static void main(String[] args) {
		lm_controller.addListener(lm_listener);
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) { }
		
		main_iface = new MainIface();
		
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				main_iface.setVisible(true);
			}
		});
	}
	
	public static void quitBlackboardMode() {
		if (running_blackboard_mode.compareAndSet(true, false)) {
			stopBlackboardMode();
			blackboard_mode = null;
		}
		
		main_iface.deselectBlackboardButton();
	}
	
	public static void runBlackboardMode() {
		stopInputMode();
		
		Blackboard blck = blackboard_mode;
	
		if (running_blackboard_mode.compareAndSet(false, true))
			blackboard_mode = new Blackboard();
		else if (runningBlackboardMode() && (blck != null)) {
			blck.toFrontAndLoad(getAndNullLoadFiles_blckbrdMode());
			main_iface.clean_LoadSelectLabel();
		}
	}
	
	public static void runInputMode() {
		if (running_input_mode.compareAndSet(false, true)) {
			stopBlackboardMode();
			
			try {
				Input inp = new Input(lm_listener);
				input_mode = inp;
				inp.run();
			} catch (AWTException e) {
				running_input_mode.compareAndSet(true, false);
				Constants.printExceptionToErr("", e);
			}
		}
	}
	
	public static boolean runningBlackboardMode() {
		return running_blackboard_mode.get();
	}
	
	public static boolean runningInputMode() {
		return running_input_mode.get();
	}
	
	public static void saveImagesBlackboard(File path) {
		Blackboard blck = blackboard_mode;
		
		if (runningBlackboardMode() && (blck != null))
			blck.saveAllScreens(path);
	}
	
	public static synchronized void setLoadFolder_blckbrdMode(File[] folder) {
		load_files = folder;
	}
	
	public static void stopBlackboardMode() {
		Blackboard blck = blackboard_mode;
		
		if (runningBlackboardMode() && (blck != null))
			blck.minimize();
		
		main_iface.deselectBlackboardButton();
	}
	
	public static void stopInputMode() {
		if (running_input_mode.compareAndSet(true, false)) {
			Input inp = input_mode;
			
			if (inp != null)
				inp.stop();
			
			input_mode = null;
		}
		
		main_iface.deselectInputButton();
	}
}