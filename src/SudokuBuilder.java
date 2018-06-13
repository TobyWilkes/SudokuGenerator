import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.apache.log4j.BasicConfigurator;

import me.tobywilkes.sudoku.BuilderRunnable;
import me.tobywilkes.sudoku.DrawerRunnable;
import me.tobywilkes.sudoku.SudoSet;
import me.tobywilkes.sudoku.SudoSet.Difficulty;
import me.tobywilkes.sudoku.ui.TaskAddPanel;

public class SudokuBuilder {
	Logger logger = Logger.getLogger(SudokuBuilder.class.getSimpleName());
	private JFrame gui = new JFrame();
	private TaskAddPanel taskPanel;
	
	private static final int MAX_PUZZLES = 100;
	ArrayBlockingQueue<SudoSet> puzzles = new ArrayBlockingQueue<SudoSet>(MAX_PUZZLES);
	Semaphore userRequests = new Semaphore(0); 
	
	ExecutorService pool;
	
	public SudokuBuilder() {
		BasicConfigurator.configure();
		logger.info("--Started Sudoku Builder--");
		
		pool = Executors.newFixedThreadPool(4);
		pool.execute(new BuilderRunnable(userRequests, puzzles));
		pool.execute(new DrawerRunnable(puzzles, Difficulty.HARD));
		pool.execute(new DrawerRunnable(puzzles, Difficulty.MEDIUM));
		pool.execute(new DrawerRunnable(puzzles, Difficulty.EASY));
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				buildGui();
			}
		});
	}
	
	public void buildGui() {
		taskPanel = new TaskAddPanel(userRequests);
		
		gui.setTitle("Sudoku Builder");
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		gui.add(taskPanel);
		gui.pack();
		
		gui.setVisible(true);
	}
	
	public static void main(String[] args) {
		new SudokuBuilder();
	}
}
