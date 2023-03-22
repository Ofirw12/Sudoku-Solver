import java.util.Scanner;

public class SUDOKU2022 {

	public static void main(String[] args) {
		welcome();
		int[][] board = buildBoard();
		board = placeUserInput(board);
		int[][] boardCopy = copyBoard(board); // Keep track of initial board with user input.
		solveBoard(board,boardCopy);		
	}
	
	/**
	 * Description - Solves the board and prints the board if there is one. 
	 * @param board - Working board for solution.
	 * @param boardCopy - Initial board.
	 */
	public static void solveBoard(int[][]board, int[][] boardCopy) {
		System.out.print("The given numbers are:");
		printBoard(board);
		System.out.println("");
		int row = 0, col = 0; // Pointing on top left
		int[] last = new int[2]; // Keeps track of last position that was changed.
		System.out.println("");
		System.out.println("");
		System.out.println("Looking for a solution...");
		while (true) {
			if (canFillCell(board, boardCopy, row, col)) {
				last[0] = row;
				last[1] = col;
				if (row == 8 && col == 8) { // Filled the last cell of the board.
					System.out.println("");
					System.out.println("The solution is:");
					printBoard(board);
					break;
				} else if (col == 8) { // End of line.
					col = 0;
					row++;
				} else {
					col++;
				}
			} else {
				last = backToLast(board, boardCopy, row, col);
				if (last[0] == -1) {
					System.out.println("");
					System.out.println("There is no valid solution");
					// Failed to complete the board.
					break;
				} else {
					row = last[0];
					col = last[1];
				}
			}
		}
	}
	
	public static void welcome() {
		Scanner sc = new Scanner(System.in);
		String enter = " ";
		while (enter != "") {
			System.out.println("Welcome to SUDOKU Solver 2022. To start, press enter");
			enter = sc.nextLine();
		}
	}
	
	/**
	 * Description - Initializes the board.
	 * @return board - Working board for solution.
	 */
	public static int[][] buildBoard(){
		int[][] board = new int[9][9];
		for (int row = 0; row < 9; row++) { 
			for (int col = 0; col < 9; col++) {
					board[row][col] = 0;
			}
		}
		return board;
	}
	
	/**
	 * Description - Gets position and value from user and tries to place it.
	 * @param board - Working board for solution.
	 * @return - board.
	 */
	public static int[][] placeUserInput(int[][] board) {
		Scanner sc = new Scanner(System.in);
		boolean firstRun = true;
		System.out.println("Please enter a given number and its location,");
		System.out.println("following the LL-N pattern, L for location and N for number.");
		String placement = sc.nextLine();
		while (placement.compareTo("00-0") != 0) {
			while (!canPlace(placement, board)) {
				System.out.println("The input is not valid. Please try again");
				placement = sc.nextLine();
			}
			placeNum(board, placement);
			firstRun = false;
			if (!firstRun) {
				System.out.println("Please enter a given number and its location.");
				placement = sc.nextLine();
			}
		}
		return board;
	}
	
	/**
	 * Description - Checks if the input and location are valid.
	 * @param place - Row, column and value.
	 * @param board - Working board for solution.
	 * @return - Can place or not.
	 */
	public static boolean canPlace(String place, int[][] board) {
		if (isValidInput(place) && okLocation(board, place)) {
			return true;
		} else
			return false;
	}
	
	/**
	 * Description - Creates and array to reference row(pos[0]), column(pos[1]) and value(pos[2]).
	 * @param place - Row, column and value.
	 * @return - pos - The created array.
	 */
	public static int[] makeTemp(String place) {
		int[] pos = new int[3];
		pos[0] = Integer.parseInt(place.substring(0, 1));
		pos[1] = Integer.parseInt(place.substring(1, 2));
		pos[2] = Integer.parseInt(place.substring(3));
		return pos;
	}
	
	/**
	 * Description - Checks if the input is valid.
	 * @param place - Row, column and value.
	 * @return - Valid input or not.
	 */
	public static boolean isValidInput(String place) {
		boolean dash = (place.substring(2, 3).compareTo("-") == 0);
		boolean length = (place.length() == 4);
		if (length && dash) {
			int[] pos = makeTemp(place); // Create array to reference row(pos[0]), column(pos[1]) and value(pos[2]).
			if (hvLimit(pos) && valLimit(pos)) // Checks if row, column and value are not over the limit.
			{
				return true;
			}		
		}
		return false;
	}
	
	/**
	 * Description - Checks if the row and column are between 0 and 8.
	 * @param pos - Array to reference row(pos[0]), column(pos[1]) and value(pos[2]).
	 * @return - Over the limit or not for row or column.
	 */
	public static boolean hvLimit(int[] pos) {
		if (pos[0] < 0 || pos[0] > 8 || pos[1] < 0 || pos[1] > 8)
			return false;
		return true;
	}
	
	/**
	 * Description - Checks if the value between 1 and 9.
	 * @param pos - Array to reference row(pos[0]), column(pos[1]) and value(pos[2]).
	 * @return - Value between 1 and 9 or not.
	 */
	public static boolean valLimit(int[] pos) {
		if (pos[2] < 1 || pos[2] > 9)
			return false;
		return true;
	}
	
	/**
	 * Description - Checks if the value is present in the row column or inner square.
	 * @param board - Working board for solution.
	 * @param place - Row, column and value.
	 * @return - Value in the inner square, column or row or not.
	 */
	public static boolean okLocation(int[][] board, String place) {
		boolean hasNumber = false;
		int[] pos = makeTemp(place); // Array to reference row(pos[0]), column(pos[1]) and value(pos[2]).
		for (int row = 0; row < 9; row++) { // first run check
			for (int col = 0; col < 9; col++) {
				if (board[row][col] != 0) {
					hasNumber = true;
				}
			}
		}
		if (hasNumber) {
			if (verCheck(board, pos) && horCheck(board, pos) && squareCheck(board, pos)) {
				return true;
			} else
				return false;
		}
		return true;
	}
	
	/**
	 * Description - Checks if the value is present in the column.
	 * @param board - Working board for solution.
	 * @param pos - Array to reference row(pos[0]), column(pos[1]) and value(pos[2]).
	 * @return - Duplicate value in the column or not.
	 */
	public static boolean verCheck(int[][] board, int[] pos) {
		for (int h = 0; h < 9; h++) {
			if (pos[2] == board[pos[0]][h]) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Description - Checks if the number is present in the row.
	 * @param board - Working board for solution.
	 * @param pos - Temporary array to reference row, column and value.
	 * @return - Duplicate value in the row or not.
	 */
	public static boolean horCheck(int[][] board, int[] pos) {
		for (int v = 0; v < 9; v++) {
			if (pos[2] == board[v][pos[1]]) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Description - Checks in which inner square the location, and then if there's a value identical to the given value.
	 * @param board - Working board for solution.
	 * @param pos - Temporary array to reference row, column and value.
	 * @return - Duplicate value in the inner square or not.
	 */
	public static boolean squareCheck(int[][] board, int[] pos) {
		int row = 0, col = 0;
		if (pos[0] < 3) {
			row = 0;
			if (pos[1] < 3)
				col = 0;
			else if (pos[1] > 5) {
				col = 6;
			} else {
				col = 3;
			}
		} else if (pos[0] > 5) {
			row = 6;
			if (pos[1] < 3)
				col = 0;
			else if (pos[1] > 5) {
				col = 6;
			} else {
				col = 3;
			}
		} else {
			row = 3;
			if (pos[1] < 3)
				col = 0;
			else if (pos[1] > 5) {
				col = 6;
			} else {
				col = 3;
			}
		}

		if (innerSquare(board, pos, row, col)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Description - Checks if there's a number the same as the given value.
	 * @param board - Working board for solution.
	 * @param pos - Temporary array to reference row, column and value.
	 * @param row - Top row of the inner square.
	 * @param col - left column of the inner square.
	 * @return - Duplicate value in the inner square.
	 */
	public static boolean innerSquare(int[][] board, int[] pos, int row, int col) {
		for (int i = row; i < row + 3; i++)
			for (int j = col; j < col + 3; j++) {
				if (pos[2] == board[i][j]) {
					return false;
				}
			}
		return true;
	}
	
	/**
	 * Description - Places the value in the position on the board.
	 * @param board - Working board for solution.
	 * @param placement - Row, column and value.
	 */
	public static void placeNum(int[][] board, String placement) {
		int[] pos = makeTemp(placement); // Array to reference row(pos[0]), column(pos[1]) and value(pos[2]).
		board[pos[0]][pos[1]] = pos[2];
	}

	/**
	 * Description - Checks if can place a value in the row and column, and places if can.
	 * @param board - Working board for solution.
	 * @param boardCopy - Initial board.
	 * @param row
	 * @param col
	 * @return - Can place a value or not.
	 */
	public static boolean canFillCell(int[][] board, int[][] boardCopy, int row, int col) {
		if (board[row][col] == boardCopy[row][col] && board[row][col] != 0)
			return true;
		int cellValue = board[row][col] + 1;
		while (true) {
			if (cellValue > 9) {
				board[row][col] = 0;
				return false;
			}
			String temp = "" + row + col + "-" + cellValue;
			if (okLocation(board, temp)) {
				placeNum(board, temp);
				return true;
			} else
				cellValue++;
		}
	}
	
	/**
	 * Description - Returns to the last location that had changed its value.
	 * @param board - Working board for solution.
	 * @param copy - Initial board.
	 * @param row
	 * @param col
	 * @return - Last changed value location.
	 */
	public static int[] backToLast(int[][] board, int[][] copy, int row, int col) {
		int[] last = { -1, -1 };
		for (int a = row; a >= 0; a--) {
			for (int b = col - 1; b >= 0; b--) {
				if (b == -1) {
					b = 0;
				}
				if (board[a][b] != 0 && board[a][b] != copy[a][b]) {
					last[0] = a;
					last[1] = b;
					return last;
				}
				if (b <= 0) {
					b = 8;
					a--;
				}
			}
		}
		return last;
	}
	
	/**
	 * @param board - Working board for solution.
	 * @return - Copy of the board with user inputs.
	 */
	public static int[][] copyBoard(int[][] board) {
		int[][] copy = new int[9][9];
		for (int row = 0; row < 9; row++) {
			for (int col = 0; col < 9; col++) {
				copy[row][col] = board[row][col];
			}
		}
		return copy;
	}
	
	/**
	 * @param board - Working board for solution.
	 */
	public static void printBoard(int[][] board) {
		for (int i = 0; i < 9; i++) {
			System.out.print("\n");
			if (i % 3 == 0)
				System.out.print("\n");
			for (int j = 0; j < 9; j++) {
				if (j % 3 == 0)
					System.out.print(" ");
				if (board[i][j] == 0)
					System.out.print(". ");
				if (board[i][j] == 1)
					System.out.print("1 ");
				if (board[i][j] == 2)
					System.out.print("2 ");
				if (board[i][j] == 3)
					System.out.print("3 ");
				if (board[i][j] == 4)
					System.out.print("4 ");
				if (board[i][j] == 5)
					System.out.print("5 ");
				if (board[i][j] == 6)
					System.out.print("6 ");
				if (board[i][j] == 7)
					System.out.print("7 ");
				if (board[i][j] == 8)
					System.out.print("8 ");
				if (board[i][j] == 9)
					System.out.print("9 ");
			}
		}
	}
}
