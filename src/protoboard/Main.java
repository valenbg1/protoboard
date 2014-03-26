package protoboard;

import java.awt.AWTException;
import java.awt.EventQueue;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

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
	
	private static AtomicReference<Blackboard> blackboard_mode = new AtomicReference<Blackboard>();
	private static AtomicReference<File[]> load_files = new AtomicReference<File[]>();
	private static AtomicBoolean blck_created = new AtomicBoolean(false);
	
	public static File[] getAndNullLoadFiles_blckbrdMode() {
		File[] ret = load_files.get();
		load_files.set(null);
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
		else if (blackboard_mode.get() != null)
			blackboard_mode.get().maximizeAndLoad(getAndNullLoadFiles_blckbrdMode());
	}
	
	public static void runInputMode() {
		if (running_input_mode.compareAndSet(false, true)) {
			stopBlackboardMode();
			
			try {
				input_mode = new Input(lm_listener);
				
				new Thread(input_mode).start();
			} catch (AWTException e) {
				running_input_mode.compareAndSet(true, false);
				Constants.printExceptionToErr("", e);
			}
		}
	}
	
	public static boolean runningBlackboardMode() {
		return (blackboard_mode.get() != null) && blackboard_mode.get().isMaximized();
	}
	
	public static boolean runningInputMode() {
		return running_input_mode.get();
	}
	
	public static void setBlackBoardMode(Blackboard blck) {
		blackboard_mode.compareAndSet(null, blck);
	}
	
	public static void setLoadFolder_blckbrdMode(File[] folder) {
		load_files.set(folder);
	}
	
	public static void stopBlackboardMode() {
		if (blackboard_mode.get() != null)
			blackboard_mode.get().minimize();
		
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