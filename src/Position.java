/**
 * An immutable class made to represent the position of each element. like
 * String, object of this class can never be changed and this class does not
 * represent anything on the board. It only signals the position of element, but
 * it can do much more than simply int x and int y.
 * 
 * @author zhangq2. Created Nov 3, 2014.
 */
public class Position {
	private final double x;
	private final double y;

	/**
	 * 
	 * none of x or y can be changed, after Square is created.
	 * 
	 * @param x
	 *            the x value of the Square created
	 * @param y
	 *            the y value of the Square created
	 * @param b
	 * @param g
	 *            the gem in this Square, null if there is no Gem
	 */
	public Position(Square s) {
		x = s.getX();
		y = s.getY();
	}

	public Position(double d, double e) {
		x = d;
		y = e;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	/**
	 * Returns the position's x value rounded to the closest integer. (0.5
	 * rounds up.)
	 * 
	 * @return
	 */
	public int getRoundX() {
		return (int) (x + 0.5);
	}

	/**
	 * Returns the position's y value rounded to the closest integer. (0.5
	 * rounds up.)
	 * 
	 * @return
	 */
	public int getRoundY() {
		return (int) (y + 0.5);
	}

	public boolean equals(Position b) {
		return x == b.x && y == b.y;
	}

	public Square getSquare() {
		return Main.myGame.getSquare(getRoundX(), getRoundY());
	}

	@Override
	public String toString() {
		return "[" + x + "," + y + "]";
	}

	public double distance(Position b) {
		return Math.sqrt((b.x - x) * (b.x - x) + (b.y - y) * (b.y - y));
	}

	/**
	 * 
	 * @return if an element at the center of this position is within frame
	 *
	 */
	public boolean withinFrame() {
		return getX() >= 0 && getY() >= 0
				&& getX() <= Main.myGame.getAllSquares().length - 1
				&& getY() <= Main.myGame.getAllSquares()[0].length - 1;
	}

	/**
	 * 
	 * return true if the center of the element is less than given distance from
	 * the center of its current square and it is moving towards the center.
	 * 
	 * @param d
	 * @param distanceThreshold
	 * @return
	 */
	public boolean almostReachNextCenter(Direction d, double distanceThreshold) {
		double a = (d.dx() * (getRoundX() - getX()) + d.dy()
				* (getRoundY() - getY()));
		return a >= 0 && a <= distanceThreshold;
	}

	/**
	 * 
	 * @return
	 */
	public Direction directonToCenter() {
		for (Direction i : Direction.values()) {
			if (i.dx() * (getRoundX() - getX()) > 0)
				return i;
			if (i.dy() * (getRoundY() - getY()) > 0)
				return i;
		}
		return null; // right at the center
	}

	/**
	 *
	 * @param d
	 * @param distance
	 * @return
	 */
	protected Position findPosition(Direction d, double distance) {
		if (d == null)
			return this;
		return new Position(getX() + distance * d.dx(), getY() + distance
				* d.dy());
	}

	/**
	 * 
	 * digs the square the position in inside, as well as the next closest
	 * square
	 * 
	 */
	public void digAround() {
		getSquare().dig(this);
		Square adjacent = findPosition(Direction.reverse(directonToCenter()),
				0.5).getSquare();
		if (adjacent != null)
			adjacent.dig(this);
	}
}
