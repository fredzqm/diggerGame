import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

/**
 * 
 * When gold bags breaks open, it turns into gold. Gold behaves like Emerald
 * because it does not move and can be collected for ponts, so it is a subclass
 * of Emerald
 * 
 * However, we don't have to collect all Gold to enter next level
 * 
 * @author zhangq2. Created Nov 8, 2014.
 */
public class Gold extends Emerald {

	private int value;

	public Gold(Square s, double d) {
		super(s);
		value = (int) d;
	}

	@Override
	public void drawOn(Graphics2D g) {
		g.setColor(Color.yellow);
		g.fill(new Ellipse2D.Double(-DiggerComponent.w * 0.5,
				DiggerComponent.w * 0.2, DiggerComponent.w * 0.6,
				DiggerComponent.w * 0.3));
		g.fill(new Ellipse2D.Double(-DiggerComponent.w * 0.1,
				DiggerComponent.w * 0.2, DiggerComponent.w * 0.6,
				DiggerComponent.w * 0.3));
//		g.fill(new Ellipse2D.Double(-DiggerComponent.w * 0.3,
//				-DiggerComponent.w * 0.5, DiggerComponent.w * 0.6,
//				DiggerComponent.w * 0.6));
	}

	@Override
	public int getValue() {
		return value;
	}

	@Override
	public char toChar() {
		return 'G';
	}
}
