import java.awt.Color;

/**
 * 
 * The digger. The only character the player can control.
 */
public class Hero extends Fighter {
	static final DirectionKeyManager KEY_MANAGER = new DirectionKeyManager();
	private static final Color HERO_Color = Color.BLUE;
	private static final int TIME_TO_COOL = 10000/Main.UPDATE_INTERVAL;
	private final Square originSquare;
	private int coolTime;

	/**
	 * Creates the hero at a particular Square in the game.
	 */
	protected Hero(Square s) {
		super(s);
		coolTime = 0;
		originSquare = s;
		setSpeed(0.04 * Main.UPDATE_INTERVAL/10); // set the default speed of hero
	}

	@Override
	public void restart() {
		setP(originSquare.getCenterPosition());
		setLive(true);
	}


	@Override
	public Color getColor(){
		return HERO_Color;
	}

	@Override
	public void die() {
		Main.myGame.heroDie();
	}

	public void resetPositon() {
		setP(originSquare.getCenterPosition());
	}

	@Override
	public void move() {
		updateAnimation();
		coolTime--;
		Direction ordered = KEY_MANAGER.getOrderedDirection();
		if (ordered != null) {
			moveWithDirection(ordered, getSpeed());
			pushGoldBags();
			dig();
		}
	}

	@Override
	public char toChar() {
		return 'I';
	}
	
	
	public void shoot() {
		if (coolTime < 0) {
			Main.myGame.addElement(new Weapon(getP(), getDirection()));
			coolTime = TIME_TO_COOL;
		}
	}

	/**
	 * 
	 * Direciton manager smartly handle the input. So we don't have to release
	 * all the buttons and let the digger stops for a while before the new
	 * command. Direction manger knows the status of the key, so won't realse
	 * all the direcition when the player only realse one of many direction keys
	 * he was pressing.
	 * 
	 * @author zhangq2. Created Nov 8, 2014.
	 */
	static class DirectionKeyManager {
		int[] keys;

		DirectionKeyManager() {
			keys = new int[4];
		}

		/**
		 * 
		 * Returns the direction that was most recently pressed.
		 * 
		 * @return
		 */
		public Direction getOrderedDirection() {
			int max = keys[0];
			int k = 0;
			for (int i = 1; i < 4; i++) {
				if (keys[i] > max) {
					max = keys[i];
					k = i;
				}
			}
			if (max == 0)
				return null;
			return Direction.fromNum(k);
		}

		/**
		 * Release all the direciton keys. This method will be called whenever
		 * there is a pop up screen, when the direction manger do not get
		 * notified when the direction keys are pressed or release.
		 * 
		 */
		public void releaseAll() {
			keys = new int[4];
		}

		/**
		 * When a key for a certain direction is released, the value in the keys
		 * array that corresponds to that direction is set to 0.
		 * 
		 * @param d
		 */
		public void releaseKey(Direction d) {
			keys[d.toNum()] = 0;
		}

		/**
		 * Finds the maximum value in the keys array and assigns that value plus
		 * one to the element of the array that corresponds to the direction
		 * passed to the method. The result is that the direction that was most
		 * recently pressed will have the highest corresponding value in the
		 * keys array, even if another key is currently being pressed.
		 * 
		 * @param d
		 */
		public void pressKey(Direction d) {
			keys[d.toNum()] = Math.max(Math.max(keys[0], keys[1]),
					Math.max(keys[2], keys[3])) + 1;
		}

	}

}
