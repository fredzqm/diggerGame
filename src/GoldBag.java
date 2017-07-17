import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * Gold bag can drop, can be pushed by nobbin or enemy.
 *
 * @author zhangq2. Created Nov 8, 2014.
 */
public class GoldBag extends Element {
	private static final int DROP_DELAY_IN_FRAMES = 700 / Main.UPDATE_INTERVAL;
	private static final double DROP_SPEED = 0.1 * Main.UPDATE_INTERVAL / 10;
	private static final double SLIDE_SPEED = 0.025 * Main.UPDATE_INTERVAL / 10;
	static final Color GOLD_BAG = Color.yellow;
	private int framesUntilDrop;
	private ArrayList<Fighter> pushers;

	protected GoldBag(Square s) {
		super(s);
		framesUntilDrop = DROP_DELAY_IN_FRAMES;
		pushers = new ArrayList<Fighter>();
	}

	/**
	 * Returns true if the gold bag is falling and, therefore, dangerous.
	 */
	public boolean dangerous() {
		return framesUntilDrop < 0;
	}

	public void addPusher(Fighter fighter) {
		pushers.add(fighter);
	}

	@Override
	public Direction getFaceDirection() {
		return Direction.NORTH;
	}

	@Override
	public void drawOn(Graphics2D g) {
		g.setColor(GOLD_BAG);
		g.fill(new Rectangle2D.Double(-DiggerComponent.w * 0.35,
				-DiggerComponent.w * 0.1, DiggerComponent.w * 0.7,
				DiggerComponent.w * 0.6));

		g.fill(new Ellipse2D.Double(-DiggerComponent.w * 0.22,
				-DiggerComponent.w * 0.4, DiggerComponent.w * 0.4,
				DiggerComponent.w * 0.4));
	}

	@Override
	public void move() {

		double speed = SLIDE_SPEED;

		if (pushers.size() > 0 && framesUntilDrop >= 0) {

			if (pushForce() > 0) {
				setDirection(Direction.EAST);
				speed = SLIDE_SPEED * pushForce();
			} else if (pushForce() < 0) {
				setDirection(Direction.WEST);
				speed = -SLIDE_SPEED * pushForce();
			} else {
				if (pushDown()) {
					setDirection(Direction.SOUTH);
					speed = SLIDE_SPEED;
				} else
					speed = 0;
			}
		}

		if (canDrop() && getP().getX() - getSquare().getX() == 0)
			framesUntilDrop--;

		if (framesUntilDrop < 0) {// the bag is dropping
			if (canDrop()) {// it continues to drop
				moveWithDirection(Direction.SOUTH, DROP_SPEED);
				getP().digAround();
			} else {// it drops to ground
				if (-framesUntilDrop * DROP_SPEED > 1.5) {
					Main.myGame.addElement(new Gold(this.getSquare(),
							-framesUntilDrop * DROP_SPEED * 100));
					setLive(false);
				} else {
					setP(getSquare().getCenterPosition());
					setDirection(Direction.NORTH);
					framesUntilDrop = DROP_DELAY_IN_FRAMES;
				}
			}
		} else {
			if (getSquare().getCenterPosition().equals(getP())) {
				if (!canDrop()) {
					if (getDirection() != Direction.NORTH) {
						moveWithDirection(getDirection(), speed);
					}
				}
			} else {
				framesUntilDrop = 0;
				if (canDrop()) {
					moveWithDirection(Direction.SOUTH, speed);
				} else {
					if (getDirection() == Direction.SOUTH) {
						moveWithDirection(Direction.EAST, speed);
						if (getDirection() == Direction.EAST) {
							setP(getSquare().getCenterPosition());
							if (!canDrop()) {
								setDirection(Direction.NORTH);
								framesUntilDrop = DROP_DELAY_IN_FRAMES;
							}
						}
					} else {
						moveWithDirection(Direction.NORTH, speed);
						if (getDirection() == Direction.NORTH) {
							setP(getSquare().getCenterPosition());
							framesUntilDrop = DROP_DELAY_IN_FRAMES;
						}
					}
				}
			}
			for (Fighter i : pushers) {
				if (getDirection().sameAxis(i.getDirection())) {
					i.setP(getP().findPosition(
							Direction.reverse(i.getDirection()), 1));
					i.dig();
				}
			}
		}
		pushers.clear();
	}

	private boolean pushDown() {
		for (Fighter i : pushers) {
			if (i.getDirection() == Direction.SOUTH)
				return true;
		}
		return false;
	}

	private int pushForce() {
		int d = 0;
		for (Fighter i : pushers) {
			d += i.getPushDirection(this);
		}
		return d;
	}

	private boolean canDrop() {
		Square square = getP().findPosition(Direction.SOUTH, 0.5).getSquare();
		return square != null && !square.hasDirtAtCenter();
	}

	@Override
	public void restart() {
		// if (dangerous())
		// die();
	}

	@Override
	public char toChar() {
		if (dangerous())
			return 'O';
		if (getSquare().hasDirtAtCenter())
			return 'b';
		return 'B';
	}

}
