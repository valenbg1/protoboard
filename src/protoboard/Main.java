package protoboard;

import java.awt.AWTException;
import java.awt.EventQueue;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.UIManager;

import processing.core.PApplet;
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
	public static final Controller lm_controller = new Controller();
	public static final LeapMotionListener lm_listener = new LeapMotionListener();
	
	private static MainIface main_iface;
	
	private static AtomicBoolean running_input_mode = new AtomicBoolean(false);
	private static Input input_mode;
	
	private static Blackboard blackboard_mode = null;
	private static File[] load_files = null;
	private static AtomicBoolean blck_created = new AtomicBoolean(false);
	
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
	
	public static void runBlackboardMode() {
		stopInputMode();
	
		if (blck_created.compareAndSet(false, true))
			PApplet.main(Blackboard.class.getName());
		else if (blackboard_mode != null) {
			blackboard_mode.maximizeAndLoad(getAndNullLoadFiles_blckbrdMode());
			main_iface.clean_LoadSelectLabel();
		}
	}
	
	public static void runInputMode() {
		if (running_input_mode.compareAndSet(false, true)) {
			stopBlackboardMode();
			
			try {
				input_mode = new Input(lm_listener);
				input_mode.run();
			} catch (AWTException e) {
				running_input_mode.compareAndSet(true, false);
				Constants.printExceptionToErr("", e);
			}
		}
	}
	
	public static boolean runningBlackboardMode() {
		return (blackboard_mode != null) && blackboard_mode.isMaximized();
	}
	
	public static boolean runningInputMode() {
		return running_input_mode.get();
	}
	
	public static synchronized void setBlackBoardMode(Blackboard blck) {
		if (blackboard_mode == null)
			blackboard_mode = blck;
	}
	
	public static synchronized void setLoadFolder_blckbrdMode(File[] folder) {
		load_files = folder;
	}
	
	public static void stopBlackboardMode() {
		if (blackboard_mode != null)
			blackboard_mode.minimize();
		
		main_iface.deselectBlackboardButton();
	}
	
	public static void stopInputMode() {
		if (running_input_mode.compareAndSet(true, false)) {
			if (input_mode != null)
				input_mode.stop();
		}
		
		main_iface.deselectInputButton();
	}
}