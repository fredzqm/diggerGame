/**
 * It will adds an enemy automatically at a fixed interval in a fixed square of
 * the game
 * 
 * @author zhangq2. Created Nov 8, 2014.
 */
public class EnemyAdder {
	private final Square square;
	private final int timeInterval; // every .. secound
	private final int secound;
	private int timer;

	public EnemyAdder(Square s, int d) {
		square = s;
		secound = d;
		timeInterval = d * 1000 / Main.UPDATE_INTERVAL;
		timer = d;
	}

	public void run() {
		timer--;
		if (timer == 0) {
			Main.myGame.addElement(new Enemy(square, false));
			timer = timeInterval;
		}
	}

	public Square getSquare() {
		return square;
	}

	public int getInterval() {
		return secound;
	}
}
