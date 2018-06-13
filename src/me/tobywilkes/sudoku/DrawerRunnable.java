package me.tobywilkes.sudoku;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import me.tobywilkes.sudoku.SudoSet.Difficulty;

public class DrawerRunnable implements Runnable {
	Logger logger = Logger.getLogger(DrawerRunnable.class.getSimpleName());
	final static int IMAGE_WIDTH = 200;
	final static int IMAGE_HEIGHT = 200;
	final static int SQUARE_SIZE = Math.min(IMAGE_WIDTH, IMAGE_HEIGHT) / 9;
	final static String OUTPUT_FOLDER = "sudokus/";
	
	ArrayBlockingQueue<SudoSet> input;
	Difficulty difficulty;
	BufferedImage image;

	public DrawerRunnable(ArrayBlockingQueue<SudoSet> input, Difficulty difficulty) {
		this.input = input;
		this.difficulty = difficulty;
		
		File directory = new File(OUTPUT_FOLDER);
		if(!directory.exists()) {
			logger.info("Creating sudoku directory");
			directory.mkdirs();
		} else {
			logger.info("Images directory at" + directory.getAbsolutePath());
		}
	}
	
	private BufferedImage sudoToImage(SudoSet sudo) {
		BufferedImage image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
		Graphics graphics = image.getGraphics();
		
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
		graphics.setColor(Color.RED);
		
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				Integer cell = sudo.get(j, i);
				String s = (cell == null) ? " " : cell.toString();
				graphics.drawRect(SQUARE_SIZE * j, SQUARE_SIZE * i, SQUARE_SIZE, SQUARE_SIZE);
				
				graphics.drawString(s, j * SQUARE_SIZE + 6, i * SQUARE_SIZE + 16);
			}
		}
		
		return image;
	}
	
	@Override
	public void run() {
		/*SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SudoSet s = new SudoSet();
				s.setMask(SudoSet.Difficulty.EASY);
				JFrame frame = new JFrame();
				BufferedImage b = sudoToImage(s);
				JPanel panel = new JPanel() {
					protected void paintComponent(Graphics g) {
						super.paintComponent(g);
						g.drawImage(b, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT, null);
					}
				};
				panel.setPreferredSize(new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT));
				frame.add(panel);
				frame.pack();
				frame.setVisible(true);
			}
		});*/
		
		
		for(;;) {
			try {
				SudoSet s = input.take();
				Long fileTime = System.currentTimeMillis();
				s.setMask(difficulty);
				BufferedImage b = sudoToImage(s);
				File outputFile = new File(OUTPUT_FOLDER + fileTime.toString() + "-" + difficulty.label() + ".jpg");
				ImageIO.write(b, "jpg", outputFile);
				logger.info("Output file");
			} catch (InterruptedException e) { } catch (IOException e) {
				e.printStackTrace();
			}
			
		}//
	}
	
}
