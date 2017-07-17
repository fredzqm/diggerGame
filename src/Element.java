import java.awt.Graphics2D;
import java.util.ArrayList;

/**
 * the super class of all the elements within this game It is both drawable and
 * movable. For those imovable subclass, like Emerald, we implement the move()
 * method, but leave it empty
 */
public abstract class Element {
	private Position p;
	private Direction direction;
	private boolean live;

	/**
	 * Creates the Mover object. (Sets direction to north by default.)
	 */
	protected Element(Square s) {
		setP(new Position(s));
		setDirection(Direction.NORTH);
		live = true;
	}

	/**
	 * Gets the element's current location.
	 * 
	 * @return the Square where the element is currently located
	 */
	public Position getP() {
		return p;
	}

	/**
	 * set the position of element, also revise the Square, if necessary
	 * 
	 * @param p
	 */
	public void setP(Position p) {
		this.p = p;
	}

	/**
	 * 
	 *
	 * @return the direction this element will move or is moving toward.
	 */
	public Direction getDirection() {
		return direction;
	}

	/**
	 *
	 * @return the direction, based on which this object will be drawn
	 */
	public Direction getFaceDirection() {
		return getDirection();
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	/**
	 * 
	 * @return the square this element's center is at
	 */
	public Square getSquare() {
		return getP().getSquare();
	}

	/**
	 * 
	 * 
	 * @param b
	 * @return true if two elements are close within distance samller than
	 *         {@link Square.W}.
	 */
	public boolean hasMeet(Element b) {
		return getP().distance(b.getP()) < 1;
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @return Elements in that Square
	 */
	public ArrayList<Element> getElementAround() {
		ArrayList<Element> elements = Main.myGame.getAllElements();
		for (Element i : elements) {
			if (hasMeet(i))
				elements.add(i);
		}
		return elements;
	}

	/**
	 * usually called by enemy or hero; move with an aimed direction
	 * 
	 * @param aimedDirection
	 * @param s
	 */
	protected void moveWithDirection(Direction aimedDirection, double s) {
		if (aimedDirection != null && s > 0) {
			if (aimedDirection.sameAxis(getDirection())) {
				setDirection(aimedDirection);
				Position destine = getP().findPosition(aimedDirection, s);
				if (destine.withinFrame()) {
					setP(destine);
				} else {
					setP(getSquare().getCenterPosition());
				}
			} else {
				if (getP().almostReachNextCenter(getDirection(), s)) {
					setDirection(aimedDirection);
					Position center = getSquare().getCenterPosition();
					double d = getP().distance(center);
					setP(center);
					Position destine = getP().findPosition(aimedDirection, s - d);
					if (destine.withinFrame())
						setP(destine);
				} else {
					setP(getP().findPosition(getDirection(), s));
				}
			}
		}
	}

	/**
	 * put this element at the center of this square
	 * 
	 * @param s
	 */
	public void setSquare(Square s) {
		setP(new Position(s));
	}

	public void setLive(boolean b) {
		live = b;
	}

	public boolean hasDie() {
		return !live;
	}

	/**
	 * 
	 * remove this element from the frame; For hero, it calls heroDie() in
	 * DiggerGame
	 */
	public void die() {
		Main.myGame.removeElement(this);
	}

	

	@Override
	public String toString() {
		return getClass() + " at " + getP().toString();
	}

	/**
	 * 
	 * action performed to restart the game when the hero just dies
	 */
	public abstract void restart();

	/**
	 * Moves the element according to its different type.
	 */
	public abstract void move();

	/**
	 * Draws the element.
	 * 
	 * The element will be centered at the current drawing origin and will point
	 * up (in the negative y direction).
	 * 
	 * (Access the element's getDirection() method to draw the element with it's
	 * current orientation.)
	 * 
	 * @param g
	 */
	public abstract void drawOn(Graphics2D g);

	/**
	 * 
	 * @return the char that can represent this element in saved file
	 */
	public abstract char toChar();

}
