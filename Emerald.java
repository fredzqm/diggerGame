import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

/**
 * The green gem collected for points.
 * 
 */
public class Emerald extends Element {
	private static final Color GEM_COLOR = Color.green;
	private static final int GEMVALUE = 100;

	public Emerald(Square s) {
		super(s);
	}

	@Override
	public void drawOn(Graphics2D g) {
		g.setColor(GEM_COLOR);
		final double WIDTH = DiggerComponent.w;
		g.fill(new Ellipse2D.Double(-0.35 * WIDTH, -0.35 * WIDTH, 0.7 * WIDTH,
				0.7 * WIDTH));
	}

	@Override
	public void move() {
		// Emerald never moves.
	}

	public int getValue() {
		return GEMVALUE;
	}

	@Override
	public void restart() {
		// it remains after the game restart
	}

	@Override
	public char toChar() {
		return 'e';
	}

}
