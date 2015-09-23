import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * Square represent the squares in the game. It can have dirt or not. It can
 * have dirt on its border, so nobbin cannot go through. There is a 2D-array of
 * squares stored in DiggerGame
 *
 * @author zhangq2. Created Nov 8, 2014.
 */
public class Square {
	/**
	 * separate the big square into (2*U + 1 ) by(2*U + 1 ) small grids
	 */
	static final int U = 20;
	/**
	 * width of small dirt
	 */
	static double w;
	private final int x;
	private final int y;
	private boolean[][] dirt;

	/**
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
	public Square(int x, int y, boolean b) {
		this.x = x;
		this.y = y;
		dirt = new boolean[2 * U + 1][2 * U + 1];
		if (b) {
			for (int i = -U; i <= U; i++) {
				for (int j = -U; j <= U; j++) {
					dirt[i + U][j + U] = true;
				}
			}
		}
	}

	/**
	 * 
	 * only used to initialize modifie the cell
	 * 
	 * @param direction
	 *            the side of the cell on which the dirt will be put
	 */
	protected void setDirt(Direction direction) {
		switch (direction) {
		case NORTH:
			for (int i = 0; i < 2 * U + 1; i++)
				dirt[i][0] = true;
			break;
		case EAST:
			for (int i = 0; i < 2 * U + 1; i++)
				dirt[2 * U][i] = true;
			break;
		case SOUTH:
			for (int i = 0; i < 2 * U + 1; i++)
				dirt[i][2 * U] = true;
			break;
		case WEST:
			for (int i = 0; i < 2 * U + 1; i++)
				dirt[0][i] = true;
			break;
		default:
			break;
		}
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	/**
	 * 
	 * @return true if the center of the squre has dirt
	 */
	public boolean hasDirtAtCenter() {
		return dirt[U][U];
	}

	/**
	 *
	 * @param d
	 *            the direction the nobbin is moving towards
	 * @return true if the nobbin will be blocked by the thin layer of dirt in
	 *         this direction
	 */
	public boolean hasDirtAtEdge(Direction d) {
		switch (d) {
		case SOUTH:
			return dirt[U][0];
		case WEST:
			return dirt[2 * U][U];
		case NORTH:
			return dirt[U][U * 2];
		case EAST:
			return dirt[0][U];
		default:
			return hasDirtAtCenter();
		}
	}

	/**
	 * 
	 * @return the position of its center
	 */
	public Position getCenterPosition() {
		return new Position(x, y);
	}

	/**
	 * draw this Square in the frame
	 *
	 * @param g
	 */
	public void drawOn(Graphics2D g) {
		if (allDirt()) {
			g.fill(new Rectangle2D.Double(-DiggerComponent.w / 2,
					-DiggerComponent.w / 2, DiggerComponent.w,
					DiggerComponent.w));
		} else {
			for (int i = -U; i <= U; i++) {
				for (int j = -U; j <= U; j++) {
					if (dirt[i + U][j + U]) {
						g.translate(i * w, j * w);
						g.fill(new Rectangle2D.Double(-w / 2, -w / 2, w, w));
						g.translate(-i * w, -j * w);
					}
				}
			}
		}
	}

	private boolean allDirt() {
		return hasDirtAtEdge(Direction.NORTH) && hasDirtAtEdge(Direction.SOUTH)
				&& hasDirtAtEdge(Direction.WEST)
				&& hasDirtAtEdge(Direction.EAST);
	}

	/**
	 * 
	 * dig this dirt given the position. All the dirt within U*w will be dug
	 * When the hero or hobbin goes from one square to the other, it also digs
	 * the corner of the squares
	 * 
	 * @param pt
	 */
	public void dig(Position pt) {
		// dx and dy represent the distance of a given point from the center of
		// the square,
		// measured in sub-cells
		double dx = (pt.getX() - getX()) * (2 * U + 1);
		double dy = (pt.getY() - getY()) * (2 * U + 1);

		// when dx and dy are used in the following distance formula, it is best
		// to think
		// of the variables as coordinates relative to an origin at the center
		// of the square
		for (int j = -U; j <= U; j++) {
			for (int i = -U; i <= U; i++) {
				if ((dx - i) * (dx - i) + (dy - j) * (dy - j) < U * U)
					dirt[i + U][j + U] = false;
			}
		}
		// remove dirt in the corners
		if ((dx >= -U - 2 && dx <= -U + 1) || (dy <= -U + 1 && dy >= -U - 2)) {
			dirt[0][0] = false;
		}
		if ((dx <= U + 2 && dx >= U - 1) || (dy <= -U - 1 && dy >= -U - 2)) {
			dirt[2 * U][0] = false;
		}
		if ((dx >= -U - 2 && dx <= -U + 1) || (dy >= U - 1 && dy <= U + 2)) {
			dirt[0][2 * U] = false;
		}
		if ((dx <= U + 2 && dx >= U - 1) || (dy >= U - 1 && dy <= U + 2)) {
			dirt[2 * U][2 * U] = false;
		}
	}

	/**
	 * 
	 * @return the char that can represents the state of this square right now
	 *         (as saved in file)
	 */
	public char getDiscription() {
		for (EnemyAdder i : Main.myGame.getAdders()) {
			if (equals(i.getSquare()))
				return (char) (48 + (i.getInterval()));
		}
		ArrayList<Element> inSquare = new ArrayList<Element>();
		for (Element i : Main.myGame.getAllElements()) {
			if (equals(i.getSquare()))
				inSquare.add(i);
		}
		if (inSquare.size() == 0) {
			if (hasDirtAtCenter())
				return 'x';
			return 'O';
		}
		return inSquare.get(0).toChar();
	}

	/**
	 * 
	 * Very useful method to get the adjacent Square.
	 *
	 * If you pass a null to this method, and it will just return a null.
	 *
	 * @param d
	 *            in what direction.
	 * @return the Square right next to this element in the given direction,
	 *         null if its at the border and no more Square can be found
	 */
	public Square getAdjacentSquare(Direction d) {
		return Main.myGame.getSquare(getX() + d.dx(), getY() + d.dy());
	}

	@Override
	public String toString() {
		return "[" + x + "," + y + "]";
	}
}
