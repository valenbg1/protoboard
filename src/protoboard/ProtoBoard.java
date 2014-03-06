package protoboard;

import java.awt.Dimension;
import java.awt.Toolkit;
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
		drawColor[0].set((drawColor[0].get() - 15) % 256);
		drawColor[1].set((drawColor[1].get() - 25) % 256);
		drawColor[2].set((drawColor[2].get() - 35) % 256);
	}

	public synchronized void changeDrawColorForth() {
		drawColor[0].set((drawColor[0].get() + 15) % 256);
		drawColor[1].set((drawColor[1].get() + 25) % 256);
		drawColor[2].set((drawColor[2].get() + 35) % 256);
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
		textSize(64);
		fill(255, 255, 255);
		text(screen_pos.get(), displayWidth - 90, displayHeight - 20);
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

		for (int i = 0; i < drawColor.length; ++i)
			drawColor[i] = new AtomicInteger(126);

		for (int i = 0; i < changeScreen.length; ++i)
			changeScreen[i] = new AtomicBoolean(false);

		addAndSetNewScreen();
	}
}