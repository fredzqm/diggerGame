import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JComponent;

/**
 * 
 * The class that is responsible for pain the screen.
 * 
 * @author zhangq2. Created Nov 8, 2014.
 */
@SuppressWarnings("serial")
public class DiggerComponent extends JComponent {
	private static final Color Square_COLOR = new Color(139, 71, 20);
	private static final Color BACKGROUND_COLOR = new Color(20, 20, 20);
	//private static final Color TEXT_COLOR = new Color.WHITE;
	static double w;
	private final DiggerGame myGame;

	public DiggerComponent(DiggerGame game) {
		this.myGame = game;
		w = 50;
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		Rectangle2D.Double background = new Rectangle2D.Double(0, 0, this.getWidth(), this.getHeight());
		g2.setColor(BACKGROUND_COLOR);
		g2.fill(background);
		
		double a = Main.myFrame.getWidth()
				/ (Main.myGame.getAllSquares().length + 3);
		double b = Main.myFrame.getHeight()
				/ (Main.myGame.getAllSquares()[0].length + 3);
		w = Math.min(a, b);
		g2.translate(2 * w, 2 * w);// center of left up corner

		g2.setColor(Square_COLOR);
		Square.w = w / (Square.U * 2 + 1);
		Square[][] SquareGrid = myGame.getAllSquares();
		for (Square[] i : SquareGrid) {
			for (Square j : i) {
				g2.translate(j.getX() * w, j.getY() * w);
				j.drawOn(g2);
				g2.translate(-j.getX() * w, -j.getY() * w);
			}
		}

		ArrayList<Element> drawables = myGame.getAllElements();
		for (Element i : drawables) {
			g2.translate(i.getP().getX() * w, i.getP().getY() * w);
			g2.rotate(i.getFaceDirection().getRotateAngle());
			i.drawOn(g2);
			g2.rotate(-i.getFaceDirection().getRotateAngle());
			g2.translate(-i.getP().getX() * w, -i.getP().getY() * w);
		}

		paintTheScoreBoard(g2);
	}

	private void paintTheScoreBoard(Graphics2D g) {
		int score = myGame.getScore();
		String s = "Your Score is :  " + score;
		g.setColor(Color.gray);
		g.setFont(new Font("Serif", Font.PLAIN, (int) (w*0.8)));
		g.drawString(s, -(int) w, (int) -w);


		int lifeLeft = myGame.getLifesOfHero();
		s = "You have  " + lifeLeft + "  lives left!";

		g.setColor(Color.gray);
		g.setFont(new Font("Serif", Font.PLAIN, (int) (w*0.8)));
		g.drawString(s, (int) (8 * w), -(int) w);

	}

}
