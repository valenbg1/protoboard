package protoboard;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import processing.core.PApplet;
import processing.core.PGraphics;

import com.leapmotion.leap.Controller;

public class ProtoBoard extends PApplet {
	private static final long serialVersionUID = -2341687072941440919L;

	public static void main(String[] args) {
		PApplet.main(new String[] { "--present", ProtoBoard.class.getName() });
	}

	private Controller controller;
	private LeapMotionContrListener listener;

	private AtomicInteger[] drawColor = new AtomicInteger[3];

	private CopyOnWriteArrayList<PGraphics> screens = new CopyOnWriteArrayList<PGraphics>();
	private AtomicInteger screen_pos = new AtomicInteger(-1);
	private PGraphics curr_screen = null;
	private AtomicBoolean[] changeScreen = new AtomicBoolean[2];
	
	private AtomicBoolean saveText = new AtomicBoolean(false);
	private AtomicInteger saveText_time = new AtomicInteger(60);

	private synchronized void _changeScreenBack() {
		if ((screen_pos.get() - 1) > -1)
			curr_screen = screens.get(screen_pos.decrementAndGet());

		changeScreen[0].set(false);
	}

	private synchronized void _changeScreenForth() {
		if ((screen_pos.get() + 1) == screens.size())
			addAndSetNewScreen();
		else
			curr_screen = screens.get(screen_pos.incrementAndGet());

		changeScreen[1].set(false);
	}

	private synchronized void addAndSetNewScreen() {
		curr_screen = createGraphics(displayWidth, displayHeight);
		curr_screen.beginDraw();
		curr_screen.background(0);
		curr_screen.endDraw();
		screen_pos.incrementAndGet();
		screens.add(curr_screen);
	}

	public synchronized void changeDrawColorBack() {
		for (AtomicInteger ai : drawColor) {
			if (ai.addAndGet(-20) < 0)
				ai.set(256 + ai.get());
		}
	}

	public synchronized void changeDrawColorForth() {
		for (AtomicInteger ai : drawColor)
			ai.set((ai.get() + 20) % 256);
	}
	
	public synchronized void saveCurrentScreen() {
		curr_screen.save("board[" + screen_pos.get() + "]_"+ new Date().getTime() +".png");
		saveText.compareAndSet(false, true);
		saveText_time.set(100);
	}

	public void changeScreenBack() {
		changeScreen[0].compareAndSet(false, true);
	}

	public void changeScreenForth() {
		changeScreen[1].compareAndSet(false, true);
	}

	@Override
	public void draw() {
		// Change screen
		if (changeScreen[0].get())
			_changeScreenBack();
		else if (changeScreen[1].get())
			_changeScreenForth();

		// Current board
		if (mousePressed) {
			curr_screen.beginDraw();
			curr_screen.stroke(drawColor[0].get(), drawColor[1].get(),
					drawColor[2].get());
			curr_screen.strokeWeight(10);
			curr_screen.line(mouseX, mouseY, pmouseX, pmouseY);
			curr_screen.endDraw();
		}

		image(curr_screen, 0, 0);

		// Square color
		stroke(255);
		strokeWeight(3);
		fill(drawColor[0].get(), drawColor[1].get(), drawColor[2].get());
		rect(10, displayHeight - 60, 50, 50, 10);

		// Board number
		stroke(255);
		strokeWeight(3);
		fill(0, 0, 0);
		textSize(50);
		
		if (screen_pos.get() < 10) {
			rect(displayWidth - 60, displayHeight - 60, 50, 50, 10);
			fill(255, 255, 255);
			text(screen_pos.get(), displayWidth - 51, displayHeight - 17);
		} else {
		rect(displayWidth - 110, displayHeight - 60, 100, 50, 10);
			fill(255, 255, 255);
			text(screen_pos.get(), displayWidth - 91, displayHeight - 17);
		}
		
		// Save text
		if (saveText.get()) {
			fill(255, 255, 255);
			textSize(30);
			text("SAVED!", displayWidth - 110, displayHeight - 70);
			
			if (saveText_time.decrementAndGet() == 0)
				saveText.set(false);
		}
	}

	@Override
	public void exit() {
		controller.removeListener(listener);
		super.exit();
	}

	@Override
	public void setup() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		size(screenSize.width, screenSize.height);

		controller = new Controller();
		listener = new LeapMotionContrListener(this);
		controller.addListener(listener);

		drawColor[0] = new AtomicInteger(56);
		drawColor[1] = new AtomicInteger(78);
		drawColor[2] = new AtomicInteger(243);

		changeScreen[0] = new AtomicBoolean(false);
		changeScreen[1] = new AtomicBoolean(false);

		addAndSetNewScreen();
	}
}