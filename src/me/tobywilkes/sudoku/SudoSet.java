package me.tobywilkes.sudoku;

import java.util.Random;

public class SudoSet {
	private static enum Form { SQUARE, ROW, COL }
	
	public static enum Difficulty {
		EASY (40, "easy"), 
		MEDIUM (30, "medium"), 
		HARD (20, "hard"), 
		ALL (89, "all");
		private int maxHints;
		private String label;
		Difficulty(int maxHints, String label) {
			this.maxHints = maxHints;
			this.label = label;
		}
		public String label() {
			return label;
		}
		public int maxHints() {
			return maxHints;
		}
	}
	
	private Integer[][] grid = new Integer[9][9];
	private boolean[][] mask = new boolean[9][9];
	
	public SudoSet() { 
		randomize(); 
	}
	
	public SudoSet(SudoSet sudoSet) {
		grid = sudoSet.toArray();
	}
	
	public void setMask(Difficulty maskLevel) {
		for(boolean[] row : mask) {
			for(int i = 0; i < row.length; i++) {
				if(maskLevel == Difficulty.ALL)
					row[i] = true;
				else
					row[i] = false;
			}
		}
		
		// We don't need to continue if we're showing all.
		if(maskLevel == Difficulty.ALL)
			return;
		
		Random r = new Random();
		int hints = r.nextInt(maskLevel.maxHints());
		
		for(int i = 0; i < hints; i++) {
			mask[r.nextInt(9)][r.nextInt(9)] = true;
		}
	}
	
	private void randomize() {
		int [][] offsets = new int[9][9];
		
		Random r = new Random();
		int row = 0;
		int col = 0;
		int curBase = 0;
		while(row < 9) {
			boolean rollback = false;
			
			while(col < 9) {
				int squareIndex = (Math.floorDiv(row, 3) * 3) + Math.floorDiv(col, 3); 
				int testValue = 0;
				
				// Get the value for this attempt.
				if (offsets[row][col] == 0) {
					// First try on this square, try a random value.
					testValue = curBase = r.nextInt(9) + 1;
				} else if(offsets[row][col] == 9) {
					// Impossible. All values tried. Roll back.
					offsets[row][col] = 0;
					grid[row][col] = (Integer) null;
					
					// Roll back to previous position.
					col--;
					if(col < 0) {
						rollback = true;
						row--;
						col = 8;
						break;
					} else {
						continue;
					}
				} else {
					// Not the first try, try original random number + offset.
					testValue = (curBase + offsets[row][col]) % 9 + 1;
				}

				// Check if the value works in all conditions.
				if(!contains(Form.ROW, row, testValue) 
						&& !contains(Form.COL, col, testValue)
						&& !contains(Form.SQUARE, squareIndex, testValue)) {
					setByForm(Form.ROW, row, col, testValue);
					col++;
				} else {
					// Increase the offset and try again on failure.
					offsets[row][col]++;
				}
			}
			
			// Roll back to previous row.
			if(! rollback) {
				row++;
				col = 0;
			}
		}
	}
	
	private Integer getByForm(Form form, int formIndex, int index) {
		switch(form) {
			case SQUARE:
				int x = formIndex % 3;
				int y = (formIndex - x) / 3;
				int offsetX = index % 3;
				int offsetY = (index - offsetX) / 3;
				
				return grid[(y*3) + offsetY][(x*3) + offsetX];
			case ROW:
				return grid[formIndex][index];
			case COL:
				return grid[index][formIndex];
			default:
				return 0;
		}
	}
	
	private void setByForm(Form form, int formIndex, int index, int value) {
		switch(form) {
			case SQUARE:
				int x = formIndex % 3;
				int y = (formIndex - x) / 3;
				int offsetX = index % 3;
				int offsetY = (index - offsetX) / 3;
				
				grid[(y*3) + offsetY][(x*3) + offsetX] = value;
				break;
			case ROW:
				grid[formIndex][index] = value;
				break;
			case COL:
				grid[index][formIndex] = value;
				break;
		}
	}
	
	private boolean contains(Form form, int formIndex, int number) {
		for(int i = 0; i < 9; i++) {
			Integer out = getByForm(form, formIndex, i);
			if(new Integer(number).equals(out)) {
				return true;
			}
		}
		return false;
	}
	
	private void clear(Form form, int formIndex) {
		for(int i = 0; i < 9; i++) {
			setByForm(form, formIndex, i, (Integer) null);
		}
	}
	
	public void clear() {
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				grid[i][j] = (Integer) null;
			}
		}
	}
	
	public Integer[][] toArray(){
		return grid;
	}
	
	public Integer[][] toMaskedArray() {
		Integer[][] output = new Integer[9][9];
		
		for(int row = 0; row < output.length; row++) {
			for(int col = 0; col < output[row].length; col++) {
				output[row][col] = (mask[row][col]) ? grid[row][col] : null;
			}
		}
		
		return output;
	}
	
	public Integer get(int x, int y) {
		return (mask[y][x]) ? grid[y][x] : (Integer) null;
	}
	
	public String toString() {
		String output = "";

		// Print the grid
		for(Integer[] row : grid) {
			for(Integer col : row) {
				output += ((col == null) ? "-" : col) + " ";
			}
			output += "\n";
		}
		
		return output;
	}
}
