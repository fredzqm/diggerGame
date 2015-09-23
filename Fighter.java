import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public abstract class Fighter extends Element {
	public static final double ANIMATION_INVERTAL = 500 / Main.UPDATE_INTERVAL;
	private int animation;
	private double speed;

	protected Fighter(Square s) {
		super(s);
		animation = 0;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	protected void pushGoldBags() {
		for (GoldBag goldBag : Main.myGame.getGoldBags()) {
			if (hasMeet(goldBag) && isFaceBag(goldBag)) {
				goldBag.addPusher(this);
			}
		}
	}

	/**
	 * 
	 * dig the dirt according to the position and direction the this element
	 * right now
	 */
	protected void dig() {
		getP().findPosition(getDirection(), 2.5 / (2 * Square.U + 1))
				.digAround();
	}

	@Override
	public void drawOn(Graphics2D g) {
		g.setColor(getColor());
		final double WIDTH = DiggerComponent.w;
		if (animation < ANIMATION_INVERTAL) {
			g.fill(new Ellipse2D.Double(-WIDTH * 0.5, -WIDTH * 0.5, WIDTH,
					WIDTH));
			g.setColor(Color.yellow);
			g.fill(new Rectangle2D.Double(-WIDTH * 0.05, -WIDTH / 2,
					WIDTH * 0.1, WIDTH / 2));
		} else if (animation < ANIMATION_INVERTAL * 2) {
			g.fill(new Arc2D.Double(-WIDTH * 0.5, -WIDTH * 0.5, WIDTH, WIDTH,
					-240, 300, 2));
			g.setColor(Color.yellow);
			g.fill(new Arc2D.Double(-WIDTH * 0.5, -WIDTH * 0.5, WIDTH, WIDTH,
					55, 20, 2));
			g.fill(new Arc2D.Double(-WIDTH * 0.5, -WIDTH * 0.5, WIDTH, WIDTH,
					105, 20, 2));
		}
		g.setColor(Color.black);
		if (getDirection() == Direction.EAST)
			g.fill(new Ellipse2D.Double(-0.55 * WIDTH, -0.35 * WIDTH,
					0.4 * WIDTH, 0.4 * WIDTH));
		else
			g.fill(new Ellipse2D.Double(0.15 * WIDTH, -0.35 * WIDTH,
					0.4 * WIDTH, 0.4 * WIDTH));
	}

	protected void updateAnimation() {
		animation++;
		if (animation == 2 * ANIMATION_INVERTAL)
			animation = 0;
	}

	private boolean isFaceBag(GoldBag goldBag) {
		double dx = (goldBag.getP().getX() - getP().getX());
		double dy = (goldBag.getP().getY() - getP().getY());
		return (dx == 0 && dy * getDirection().dy() > 0)
				|| (dy == 0 && dx * getDirection().dx() > 0);

	}

	public int getPushDirection(GoldBag goldBag) {
		if (goldBag.getP().getX() - getP().getX() > 0)
			return 1;
		else if (goldBag.getP().getX() - getP().getX() < 0)
			return -1;
		return 0;
	}

	public abstract Color getColor();
}
