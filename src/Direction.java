/**
 * The class that will represent the direction. It has many handy methods that
 * can be used to simplify the codes.
 *
 * @author zhangq2. Created Nov 8, 2014.
 */
public enum Direction {
	NORTH(0), EAST(1), SOUTH(2), WEST(3);
	final double rotateAngle;
	private int x;
	private int y;
	private int n;

	Direction(int i) {
		rotateAngle = (i) * Math.PI / 2;
		n = i;
		x = 0;
		y = 0;
		switch (i) {
		case 0:
			y = -1;
			break;
		case 1:
			x = 1;
			break;
		case 2:
			y = 1;
			break;
		case 3:
			x = -1;
			break;
		default:
			break;
		}
	}

	public double getRotateAngle() {
		return rotateAngle;
	}

	/**
	 *
	 * @return whether a move in this direction would cause a change in x
	 *         coordinate; 1 if it increases x, -1 if it decreases x.
	 */
	public int dx() {
		return x;
	}

	/**
	 * @return whether a move in this direction would cause a change in x
	 *         coordinate; 1 if it increases y, -1 if it decreases y.
	 */
	public int dy() {
		return y;
	}

	/**
	 * 
	 * 
	 * @return true if they are in the same line
	 */
	public boolean sameAxis(Direction b) {
		return 0 != dx() * b.dx() + dy() * b.dy();
	}

	public int toNum() {
		return n;
	}

	public static Direction reverse(Direction d) {
		if ( d == null)
			return null;
		switch (d) {
		case SOUTH:
			return NORTH;
		case WEST:
			return EAST;
		case NORTH:
			return SOUTH;
		case EAST:
			return WEST;
		default:
			throw new RuntimeException();
		}
	}

	public static Direction fromNum(int i) {
		switch (i) {
		case 0:
			return NORTH;
		case 1:
			return EAST;
		case 2:
			return SOUTH;
		case 3:
			return WEST;
		default:
			return null;
		}
	}
}
