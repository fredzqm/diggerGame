import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JOptionPane;

/**
 * 
 * The Class of the game. It deals with the interactions between the element in
 * the game.
 * 
 * @author zhangq2.
 */
public class DiggerGame {
	protected static ArrayList<String> levels;
	protected static ArrayList<DiggerGame> savedGames;
	private int level;
	private int score;
	private boolean active;
	private Square[][] squares;

	private Hero me;
	private int livesOfHero;
	private ArrayList<Emerald> emeralds;
	private ArrayList<GoldBag> goldBags;
	private ArrayList<Enemy> enemies;
	private ArrayList<Weapon> weapons;
	private ArrayList<EnemyAdder> adders;

	public DiggerGame() {
		restartTheGame();
	}

	/**
	 * initialize the game to the very first level of the game
	 *
	 */
	protected void restartTheGame() {
		intializeTheGame(levels.get(0));
		score = 0;
		livesOfHero = 3;
	}

	/**
	 * 
	 * initilize the game with the provided text
	 * 
	 * @param s
	 */
	protected void intializeTheGame(String s) {
		active = false;
		emeralds = new ArrayList<Emerald>();
		goldBags = new ArrayList<GoldBag>();
		enemies = new ArrayList<Enemy>();
		weapons = new ArrayList<Weapon>();
		adders = new ArrayList<EnemyAdder>();
		// plans = new ArrayList<DiggerGame.Plan>();
		ArrayList<String> rows = new ArrayList<String>();

		int k = 0;
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == '\n') {
				String a = s.substring(k, i);
				rows.add(a);
				k = i + 1;
			}
		}

		String infor = rows.get(0);
		int t = infor.indexOf('/');
		int p = infor.indexOf('-');
		if (t == -1 || p == -1)
			throw new RuntimeException();
		level = Integer.parseInt(infor.substring(0, t));
		Main.myFrame.setTitle("Digger Game Level " + level);

		if (t < p - 1) {
			score = Integer.parseInt(infor.substring(t + 1, p));
		}

		if (p < infor.length() - 1) {
			livesOfHero = Integer.parseInt(infor.substring(p + 1));
		}

		int colNum = rows.get(1).length();
		int rowNum = rows.size() - 1;

		Character[][] map = new Character[colNum][rowNum];
		squares = new Square[colNum][rowNum];

		for (int i = 0; i < rowNum; i++) {
			if (rows.get(i + 1).length() != colNum)
				throw new RuntimeException("" + i + " row "
						+ rows.get(i + 1).length() + " is not equal to colNum "
						+ colNum);
			for (int j = 0; j < colNum; j++) {
				map[j][i] = rows.get(i + 1).charAt(j);
			}
		}

		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++) {
				if (Character.isDigit(map[i][j])) {
					squares[i][j] = new Square(i, j, false);
					adders.add(new EnemyAdder(squares[i][j], Character.digit(
							map[i][j], 10)));
				} else if (Character.isLowerCase(map[i][j])) {
					squares[i][j] = new Square(i, j, true);
					switch (map[i][j]) {
					case 'x':
						break;// it's an empty Square with dirt
					case 'b':
						goldBags.add(new GoldBag(squares[i][j]));
						break;
					case 'e':
						emeralds.add(new Emerald(squares[i][j]));
						break;
					default:
						throw new RuntimeException("Wrong symbol: " + map[i][j]
								+ " at " + squares[i][j].toString());
					}
				} else {
					squares[i][j] = new Square(i, j, false);
					switch (map[i][j]) {
					case 'O':
						break;
					case 'I':
						me = new Hero(squares[i][j]);
						break;
					case 'H':
						enemies.add(new Enemy(squares[i][j], true));
						break;
					case 'N':
						enemies.add(new Enemy(squares[i][j], false));
						break;
					case 'B':
						goldBags.add(new GoldBag(squares[i][j]));
						break;
					case 'G':
						emeralds.add(new Gold(squares[i][j], 100));
						break;
					default:
						throw new RuntimeException("Wrong symbol: " + map[i][j]
								+ " at " + squares[i][j].toString());
					}
				}
			}
		}
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++) {
				if (!squares[i][j].hasDirtAtCenter()) {
					for (Direction d : Direction.values()) {
						if (0 <= i + d.dx() && i + d.dx() < squares.length
								&& 0 <= j + d.dy()
								&& j + d.dy() < squares[0].length)
							if (squares[i + d.dx()][j + d.dy()]
									.hasDirtAtCenter())
								squares[i][j].setDirt(d);

					}
				}
			}
		}
		if (me == null)
			throw new RuntimeException("There must be one hero");
		active = true;
	}

	@Override
	public String toString() {
		String s = "" + level + '/';
		s += score + "-";
		s += livesOfHero + "\n";
		for (int i = 0; i < squares[0].length; i++) {
			for (int j = 0; j < squares.length; j++) {
				s += squares[j][i].getDiscription();
			}
			s += "\n";
		}
		return s.substring(0, s.length() - 1);
	}

	/**
	 * 
	 * start the next level of the game
	 */
	public void nextLevel() {
		if (level < levels.size()) {
			String s = levels.get(level);
			level++;
			intializeTheGame(s);
		} else {
			setActive(false);
			JOptionPane.showConfirmDialog(Main.myFrame, "This is already "
					+ "the Highest Level. You cannot go to a higher level!",
					"Error", JOptionPane.CLOSED_OPTION,
					JOptionPane.INFORMATION_MESSAGE);
			Hero.KEY_MANAGER.releaseAll();
		}
	}

	/**
	 * 
	 * go to previous level of the game
	 */
	public void previousLevel() {
		if (level > 1) {
			String s = levels.get(level - 2);
			level--;
			intializeTheGame(s);
		} else {
			setActive(false);
			JOptionPane.showConfirmDialog(Main.myFrame, "This is already "
					+ "the first Level. You cannot go to a lower level!",
					"Error", JOptionPane.CLOSED_OPTION,
					JOptionPane.INFORMATION_MESSAGE);
			Hero.KEY_MANAGER.releaseAll();
		}
	}

	public int getScore() {
		return score;
	}

	public int getLifesOfHero() {
		return livesOfHero;
	}

	public Hero getHero() {
		return me;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean a) {
		active = a;
	}

	public ArrayList<EnemyAdder> getAdders() {
		return adders;
	}

	public Square[][] getAllSquares() {
		return squares;
	}

	/**
	 * 
	 *
	 * @param x
	 * @param y
	 * @return the square in the game with the corresponding coordinate
	 */
	public Square getSquare(int x, int y) {
		if (x >= 0 && x < squares.length && y >= 0 && y < squares[0].length)
			return squares[x][y];
		return null;
	}

	/**
	 * 
	 *
	 * @return all the elements in this game
	 */
	public ArrayList<Element> getAllElements() {
		ArrayList<Element> allElements = new ArrayList<Element>();
		allElements.addAll(emeralds);
		allElements.addAll(weapons);
		allElements.addAll(enemies);
		allElements.add(me);
		allElements.addAll(goldBags); // goldBags should be last one, so it
										// will be executed last
		return allElements;
	}

	/**
	 * 
	 * @return an array list of all gold bags in the game
	 */
	public ArrayList<GoldBag> getGoldBags() {
		return this.goldBags;
	}

	/**
	 * 
	 * remove the designated eleemnt from the game
	 * 
	 * @param element
	 */
	public void removeElement(Element element) {
		if (element instanceof GoldBag)
			goldBags.remove(element);
		else if (element instanceof Emerald)
			emeralds.remove(element);
		else if (element instanceof Enemy)
			enemies.remove(element);
		else if (element instanceof Weapon)
			weapons.remove(element);
	}

	/**
	 * 
	 * add an element into this game
	 * 
	 * @param element
	 */
	public void addElement(Element element) {
		if (element instanceof GoldBag)
			goldBags.add((GoldBag) element);
		else if (element instanceof Emerald)
			emeralds.add((Emerald) element);
		else if (element instanceof Enemy)
			enemies.add((Enemy) element);
		else if (element instanceof Weapon)
			weapons.add((Weapon) element);
	}

	/**
	 * 
	 * move all the movable once, update all the information
	 */
	public void update() {

		Enemy.updateMap();
		for (Element i : getAllElements()) {
			i.move();
		}
		for (EnemyAdder i : adders) {
			i.run();
		}
	}

	/**
	 * 
	 * deal with the collisions between the element, each time the game update
	 *
	 */
	public void handleCollision() {

		for (Enemy i : enemies) {
			if (i.hasMeet(me)) {
				me.setLive(false);
			}
		}

		for (Emerald i : emeralds) {
			if (i.hasMeet(me)) {
				score += i.getValue();
				i.setLive(false);
			}
			for (Enemy j : enemies) {
				if (i.hasMeet(j) && j.isHobbing())
					i.setLive(false);
			}
		}

		for (GoldBag i : goldBags) {
			if (!i.dangerous()) {
				for (Enemy j : enemies) {
					if (i.hasMeet(j) && j.isHobbing())
						i.setLive(false);
				}
			} else {
				ArrayList<Element> fighters = new ArrayList<Element>(enemies);
				fighters.add(me);
				for (Element j : fighters) {
					if (i.hasMeet(j) && i.getP().getY() < j.getP().getY()) {
						j.setLive(false);
					}
				}
			}
		}

		for (Weapon j : weapons) {
			for (Enemy i : enemies) {
				if (j.hasMeet(i)) {
					i.setLive(false);
					// j.setLive(false);
				}
			}
		}

		ArrayList<Element> all = getAllElements();
		for (Element i : all) {
			if (i.hasDie())
				i.die();
		}

		for (Emerald i : emeralds) {
			if (i.getClass() == Emerald.class)
				return;
		}
		nextLevel();
	}

	/**
	 * freeze the screen for a while when hero dies. The game will resume if
	 * some one press "P" or "Enter"; If the player haven't done anything for
	 * ten secound, a dialogue will pop up.
	 *
	 */
	public void heroDie() {
		livesOfHero--;
		setActive(false);

		// Pause before displaying the resume or game over dialogue
		try {
			Thread.sleep(500);
		} catch (InterruptedException exception) {
			exception.printStackTrace();
		}

		// If game over is reached, the game is reset or ended
		if (livesOfHero == 0) {
			Main.myGame.gameOver();
			return;
		}

		// This is only reached if game over has not occurred
		int i = JOptionPane.showConfirmDialog(Main.myFrame,
				"Do you want to resume the game?", "Resume?",
				JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
		if (i == JOptionPane.YES_OPTION)
			setActive(true);
		else
			System.exit(0);
		Hero.KEY_MANAGER.releaseAll();

		getHero().resetPositon();
		for (Element e : Main.myGame.getAllElements()) {
			e.restart();
		}
	}

	/**
	 * 
	 * game over, so pop up a dialogue and ask whether we need to restart the
	 * game; we haven't made the highest scores board yet.
	 */
	public void gameOver() {
		Main.myFrame.setTitle("Game Over!");
		setActive(false);

		displayLeaderboard();

		int i = JOptionPane.showConfirmDialog(Main.myFrame,
				"Do you want to restart?", "Game Over",
				JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
		if (i == JOptionPane.YES_OPTION) {
			restartTheGame();
		} else {
			System.exit(0);
		}
		Hero.KEY_MANAGER.releaseAll();
	}

	/**
	 * Display the high scores leaderboard
	 */
	private void displayLeaderboard() {
		ArrayList<ScoreRecord> scoreRecords = new ArrayList<ScoreRecord>();

		// Deserialize the Leaderboard.txt file. and populate scoreRecords
		File leaderboardFile = new File("Leaderboard.txt");
		try {
			Scanner scanner = new Scanner(leaderboardFile);
			while (scanner.hasNextLine()) {
				String currentName = scanner.nextLine();
				int currentScore = Integer.parseInt(scanner.nextLine());
				scoreRecords.add(new ScoreRecord(currentName, currentScore));
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(
					"The Leaderboard.txt file could not be found");
		}

		// Determine where on the list the new score should go.
		// Assume the list is already sorted in descending order.
		// If newScoreIndex is -1, the score has not made it onto the list.
		int newScoreIndex = -1;
		for (int i = 0; i < scoreRecords.size(); i++) {
			if (this.score > scoreRecords.get(i).getScore()) {
				newScoreIndex = i;
				break;
			}
		}

		// Insert a new score onto the board with a temporary player name
		if (newScoreIndex > -1) {
			ScoreRecord newRecord = new ScoreRecord("YOUR NAME HERE",
					this.score);
			scoreRecords.add(newScoreIndex, newRecord);
			scoreRecords.remove(scoreRecords.size() - 1);
		}

		// Determine what text the leaderboard should display
		String leaderboardText = "High Scores:\n";
		for (ScoreRecord record : scoreRecords) {
			leaderboardText += (record.getPlayerName() + ":  "
					+ record.getScore() + "\n");
		}

		// If the score did not make it onto the list, just show the list
		if (newScoreIndex < 0) {
			JOptionPane.showMessageDialog(Main.myFrame, leaderboardText,
					"Game Over", JOptionPane.PLAIN_MESSAGE);
			// If the score made it onto the list, ask for and record the
			// player's name
		} else {
			leaderboardText += "\nEnter your name:\n";
			String playerName = (String) JOptionPane.showInputDialog(
					Main.myFrame, leaderboardText, "Game Over",
					JOptionPane.PLAIN_MESSAGE, null, null, null);
			if (playerName == null)
				playerName = "unknown player";
			if (playerName.contains("\\"))
				playerName.replace("\\", "");
			scoreRecords.get(newScoreIndex).setPlayerName(playerName);

			// Write the new high scores list to Leaderboard.txt
			try {
				PrintWriter printWriter = new PrintWriter(leaderboardFile);
				for (ScoreRecord record : scoreRecords) {
					printWriter.println(record.getPlayerName());
					printWriter.println("" + record.getScore() + "");
				}
				// printWriter.println();
				printWriter.close();
			} catch (FileNotFoundException e) {
				throw new RuntimeException(
						"The Leaderboard.txt file could not be found");
			}

		}

	}

}
