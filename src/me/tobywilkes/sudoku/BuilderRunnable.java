package me.tobywilkes.sudoku;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;

public class BuilderRunnable implements Runnable {
	ArrayBlockingQueue<SudoSet> output;
	Semaphore input;
	
	public BuilderRunnable(Semaphore input, ArrayBlockingQueue<SudoSet> output) {
		this.output = output;
		this.input = input;
	}

	@Override
	public void run() {
		for(;;) {
			try {
				input.acquire();
				SudoSet s = new SudoSet();
				output.put(s);
				//Thread.sleep(100);
			} catch (InterruptedException e) { }
		}
	}
}
