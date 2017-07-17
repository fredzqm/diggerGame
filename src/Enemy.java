import java.awt.Color;

/**
 * the enemy class can be either nobbin or hobbin. It will select its behavior
 * acoording to H_N field of this class. TODO Put here a description of what
 * this class does.
 * 
 * @author zhangq2. Created Nov 8, 2014.
 */
public class Enemy extends Fighter {
	private static final Color HOBBIN_COLOR = Color.MAGENTA;
	private static final Color NOBBIN_COLOR = Color.RED;
	private static final double TRANSFORM_H_TO_N = 0.001 * Main.UPDATE_INTERVAL / 10;
	private static final double TRANSFORM_N_TO_H = 0.0001 * Main.UPDATE_INTERVAL / 10;
	private static Map nobbinMap;

	private boolean H_N;

	protected Enemy(Square s, boolean b) {
		super(s);
		H_N = b;
		setSpeed(0.045 * Main.UPDATE_INTERVAL / 10); // set default speed
	}

	public boolean isHobbing() {
		return H_N;
	}

	@Override
	public void restart() {
		die();
	}

	@Override
	public Color getColor() {
		if (H_N) {
			return HOBBIN_COLOR;
		}
		return NOBBIN_COLOR;
	}

	@Override
	public void move() {
		updateAnimation();
		if (H_N) {
			if (Math.random() < TRANSFORM_H_TO_N)
				H_N = false;
		} else {
			if (Math.random() < TRANSFORM_N_TO_H)
				H_N = true;
		}
		Direction aimedDirection;
		if (H_N) {
			aimedDirection = hobbinThinkOfDirection();
			moveWithDirection(aimedDirection, getSpeed());
			dig();
			destroyBags();
		} else {
			aimedDirection = nobbinThinkOfDirection();
			moveWithDirection(aimedDirection, getSpeed());
			pushGoldBags();
		}

	}

	protected void destroyBags() {
		for (GoldBag goldBag : Main.myGame.getGoldBags()) {
			if (hasMeet(goldBag)) {
				goldBag.setLive(false);
			}
		}
	}

	/**
	 * the direction nobbin would choose to chase hero
	 *
	 * @param map
	 * @return
	 */
	private Direction nobbinThinkOfDirection() {

		int[] score = new int[4];
		score[0] = getMapResult(Direction.NORTH);
		score[1] = getMapResult(Direction.EAST);
		score[3] = getMapResult(Direction.WEST);
		score[2] = getMapResult(Direction.SOUTH);
		int supposed = getMapResult(null) - 1;

		for (Direction i : Direction.values()) {
			if (score[i.toNum()] == supposed) {
				Square x = getSquare().getAdjacentSquare(i);
				if (!(x.hasDirtAtCenter() || x.hasDirtAtEdge(i))) {
					return i;
				}
			}
		}

		return null;
	}

	/**
	 * 
	 *
	 * @param d
	 *            the square in which direction, null if we want to get the
	 *            result at current square.
	 * @return
	 * @return
	 */
	private int getMapResult(Direction d) {
		if (d == null)
			return nobbinMap.getValue(getSquare());
		Square a = getSquare().getAdjacentSquare(d);
		return nobbinMap.getValue(a);
	}

	/**
	 * the direction hobbin would choose to chase hero
	 *
	 * @return
	 */
	private Direction hobbinThinkOfDirection() {
		double dx = Main.myGame.getHero().getP().getX() - getP().getX();
		double dy = Main.myGame.getHero().getP().getY() - getP().getY();
		if (dx > 0) {
			if (dy > Math.abs(dx))
				return Direction.SOUTH;
			else if (dy < -Math.abs(dx))
				return Direction.NORTH;
			else
				return Direction.EAST;
		}
		if (dx < 0) {
			if (dy > Math.abs(dx))
				return Direction.SOUTH;
			else if (dy < -Math.abs(dx))
				return Direction.NORTH;
			else
				return Direction.WEST;
		}
		if (dy > 0)
			return Direction.SOUTH;
		return Direction.NORTH;
	}

	@Override
	public char toChar() {
		if (H_N)
			return 'H';
		return 'N';
	}

	/**
	 * 
	 * Map class will calculate the minimus step need for a nobbing to reach the
	 * hero from all the squares without dirt in the game. This class is the
	 * reason our nobbing is so smart
	 * 
	 * @author zhangq2. Created Nov 8, 2014.
	 */
	private static class Map {
		private int[][] map;

		/**
		 * creates a Map according to the situation right now
		 *
		 */
		Map() {
			map = new int[Main.myGame.getAllSquares().length][Main.myGame
					.getAllSquares()[0].length];
			Square heroSquare = Main.myGame.getHero().getSquare();
			// set all the square with dirt with -1
			for (Square[] j : Main.myGame.getAllSquares()) {
				for (Square i : j) {
					if (i.hasDirtAtCenter())
						setValue(i, -1);
				}
			}
			upDateNeighbors(heroSquare, 0);
			setValue(heroSquare, 0);

			for (int i = 0; i < map[0].length; i++) {
				for (int j = 0; j < map.length; j++) {
					String s = "" + getValue(Main.myGame.getSquare(j, i));
					s = s.substring(0, 1);
				}
			}
		}

		private void upDateNeighbors(Square c, int distance) {
			int d = distance + 1;
			updateDistance(c, d, Direction.NORTH);
			updateDistance(c, d, Direction.SOUTH);
			updateDistance(c, d, Direction.EAST);
			updateDistance(c, d, Direction.WEST);
		}

		private void updateDistance(Square heroSquare, int distance, Direction d) {
			Square x = heroSquare.getAdjacentSquare(d);
			if (x != null
					&& (getValue(x) == 0 || distance < getValue(x))
					&& !(x.hasDirtAtCenter() || x.hasDirtAtEdge(d) || heroSquare
							.hasDirtAtEdge(Direction.reverse(d)))) {
				setValue(x, distance);
				upDateNeighbors(x, distance);
			}
		}

		private int getValue(Square a) {
			if (a == null)
				return -1;
			return map[a.getX()][a.getY()];
		}

		private void setValue(Square a, int v) {
			map[a.getX()][a.getY()] = v;
		}

		@Override
		public String toString() {
			String s = "";
			for (int i = 0; i < map[0].length; i++) {
				for (int j = 0; j < map.length; j++) {
					String temp;
					if (map[j][i] > 9) {
						s += map[j][i] + " ";
					} else {
						temp = "" + map[j][i];
						if (temp.length() > 1) {
							temp = temp.substring(0, 1);
						}
						s += temp + "  ";
					}
				}
				s += "\n";
			}
			return s + "\n";
		}
	}

	public static void updateMap() {
		nobbinMap = new Map();
	}

}
