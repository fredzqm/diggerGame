import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Main starts the game, and handle all the input. myGame is a static variable
 * of Main, because we will only create one DiggerGame in the whole game and
 * making is static can help other Class acess its data
 *
 * @author zhangq2. Created Oct 27, 2014.
 */
public class Main {
	public static final int UPDATE_INTERVAL = 10;
	public static final int WIDTH = 1300;
	public static final int HEIGHT = 800;

	static JFrame myFrame;
	static DiggerGame myGame;
	static DiggerComponent myComponent;

	/**
	 * Main function creats the frame, game and component. Those three are
	 * static because we only have one game runing in this program.
	 * 
	 * It read all the files from txt documents and store those level
	 * information in {@link DiggerGame.levels}.
	 * 
	 * 
	 * @param args
	 *            ignored
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		readLevels();
		myFrame = new JFrame("Digger Game Level 1");
		myFrame.addKeyListener(new KeyBoardListener());
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myFrame.setSize(new Dimension(WIDTH, HEIGHT));
		myGame = new DiggerGame();
		myComponent = new DiggerComponent(myGame);
		myFrame.add(myComponent);
		new Thread(new Updator()).start();
		myFrame.setVisible(true);

		Clip clip = AudioSystem.getClip();
		AudioInputStream ais = AudioSystem.getAudioInputStream(new File(
				"fight.wav"));
		clip.open(ais);
		clip.loop(Clip.LOOP_CONTINUOUSLY);

	}

	/**
	 * read all the level files we and store them in an array list in DiggerGame
	 *
	 */
	private static void readLevels() {
		ArrayList<String> levels = new ArrayList<String>();
		String s;
		for (int i = 1;; i++) {
			try {
				s = readExistingFile(i);
			} catch (FileNotFoundException exception1) {
				break;
			}
			levels.add(s);
		}
		DiggerGame.levels = levels;
	}

	/**
	 * read files and store them into the Digger.levels
	 * 
	 * @param n
	 * @return
	 * @throws FileNotFoundException
	 */
	private static String readExistingFile(int n) throws FileNotFoundException {
		File inputFile = new File("Level " + n + ".txt");
		String s = "";
		Scanner input;
		input = new Scanner(inputFile);
		while (input.hasNextLine()) {
			s += input.nextLine() + '\n';
		}
		input.close();
		return s;
	}

	/**
	 * this method will try to return a proper scanner of an input. Then the
	 * input will be processed in the loadFile if it is not null. If it is null,
	 * this means that the user are not able to find a proper.
	 * 
	 * @return a proper input resource, null if user cannot find a good
	 *         resource.
	 */
	public static String openOtherFile() {
		JFileChooser chooser = new JFileChooser();
		String s = "";
		if (chooser.showOpenDialog(myComponent) != JFileChooser.APPROVE_OPTION) {
			return s;
		}
		try {
			Scanner input = new Scanner(chooser.getSelectedFile());
			while (input.hasNextLine()) {
				s += input.nextLine() + '\n';
			}
			input.close();
		} catch (FileNotFoundException exception) {
			int output = JOptionPane.showConfirmDialog(myFrame,
					"Do you want to try to open another file?",
					"Error: This file cannot be found!!",
					JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
			if (output == JOptionPane.YES_OPTION) {
				return openOtherFile();
			}
		}
		return s;
	}

	/**
	 * this method will save the file as txt into a selected directory
	 *
	 */
	public static void saveFile() {
		myGame.setActive(false);
		JFileChooser chooser = new JFileChooser();
		if (chooser.showOpenDialog(myComponent) != JFileChooser.APPROVE_OPTION)
			return;
		Hero.KEY_MANAGER.releaseAll();
		PrintWriter out = null;
		try {
			out = new PrintWriter(chooser.getSelectedFile());
			out.println(myGame.toString());
		} catch (FileNotFoundException exception) {
			exception.printStackTrace();
		} finally {
			out.close();
		}

	}

	/**
	 * handles all the key command, and call the designated methods.
	 *
	 * @author zhangq2. Created Nov 6, 2014.
	 */
	private static class KeyBoardListener implements KeyListener {

		@Override
		public void keyPressed(KeyEvent e) {
			int n = e.getKeyCode();
			switch (n) {
			case KeyEvent.VK_UP:
				Hero.KEY_MANAGER.pressKey(Direction.NORTH);
				break;
			case KeyEvent.VK_DOWN:
				Hero.KEY_MANAGER.pressKey(Direction.SOUTH);
				break;
			case KeyEvent.VK_LEFT:
				Hero.KEY_MANAGER.pressKey(Direction.WEST);
				break;
			case KeyEvent.VK_RIGHT:
				Hero.KEY_MANAGER.pressKey(Direction.EAST);
				break;
			case KeyEvent.VK_SPACE:
				myGame.getHero().shoot();
				break;
			case KeyEvent.VK_O:
				myGame.setActive(false);
				String s = openOtherFile();
				if (s.length() > 0)
					myGame.intializeTheGame(s);
				myGame.setActive(false);
				myComponent.repaint();
				break;
			case KeyEvent.VK_S:
				saveFile();
				break;
			case KeyEvent.VK_R:
				myGame.restartTheGame();
				break;
			default:
				break;
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			int n = e.getKeyCode();
			switch (n) {
			case KeyEvent.VK_UP:
				Hero.KEY_MANAGER.releaseKey(Direction.NORTH);
				break;
			case KeyEvent.VK_DOWN:
				Hero.KEY_MANAGER.releaseKey(Direction.SOUTH);
				break;
			case KeyEvent.VK_LEFT:
				Hero.KEY_MANAGER.releaseKey(Direction.WEST);
				break;
			case KeyEvent.VK_RIGHT:
				Hero.KEY_MANAGER.releaseKey(Direction.EAST);
				break;
			case KeyEvent.VK_P:
				myGame.setActive(!myGame.isActive());
				break;
			case KeyEvent.VK_ENTER:
				myGame.setActive(true);
				break;
			case KeyEvent.VK_U:
				myGame.nextLevel();
				break;
			case KeyEvent.VK_D:
				myGame.previousLevel();
				break;
			default:
				break;
			}
		}

		@Override
		public void keyTyped(KeyEvent e) {
			// nothing involved here
		}
	}

	/**
	 * the clock of this game. We only have one thread to update all the
	 * elements within the game. when the game is active, updator will update
	 * every element, handle their collision and repaint the JComponent.
	 *
	 * @author zhangq2. Created Nov 6, 2014.
	 */
	private static class Updator implements Runnable {
		@Override
		public void run() {
			try {
				while (true) {
					if (myGame.isActive()) {
						myGame.update();
						myGame.handleCollision();
						myComponent.repaint();
					}
					Thread.sleep(UPDATE_INTERVAL);
				}
			} catch (InterruptedException exception) {
				throw new RuntimeException();
			}
		}
	}

}
