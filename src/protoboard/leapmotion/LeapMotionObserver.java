package protoboard.leapmotion;

/**
 * Interface that a class must implement to receive cooked actions captured from
 * the Leap Motion hardware.
 * 
 */
public interface LeapMotionObserver {
	public void onDownSwipe();
	
	public void onKeyTap();

	public void onLeftCircle();

	public void onLeftSwipe();

	public void onRighCircle();

	public void onRightSwipe();
	
	public void onScreenTap();

	public void onUpSwipe();
}