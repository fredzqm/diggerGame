import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

/**
 * The little bullet shot by the hero to kill enemy. It requires some recharge
 * time.
 *
 * @author zhangq2. Created Nov 8, 2014.
 */
public class Weapon extends Element {
	private static final Color WEAPON_COLOR = Color.red;
	private static final double SPEED = 0.2 * Main.UPDATE_INTERVAL / 10;

	protected Weapon(Position s, Direction d) {
		super(s.getSquare());
		setP(s);
		setDirection(d);
	}

	@Override
	public void drawOn(Graphics2D g) {
		Path2D path = new Path2D.Double();
		path.moveTo(0, -0.5 * DiggerComponent.w);
		path.lineTo(-0.2 * DiggerComponent.w, 0.3 * DiggerComponent.w);
		path.lineTo(0.2 * DiggerComponent.w, 0.3 * DiggerComponent.w);
		path.closePath();
		g.setColor(WEAPON_COLOR);
		g.fill(path);
	}

	@Override
	public void move() {
		Position designated = getP().findPosition(getDirection(), SPEED);
		Square square = designated.getSquare();
		if (square == null || square.hasDirtAtCenter()) {
			setLive(false);
		} else {
			setP(designated);
		}
	}

	@Override
	public void restart() {
		die();
	}

	@Override
	public char toChar() {
		return 'O';
	}
}
