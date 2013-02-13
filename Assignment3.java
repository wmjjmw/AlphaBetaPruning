import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;


public class Assignment3 {
	
	public static class MaxHeap implements Comparator<queueElement> { 
        public int compare(queueElement one, queueElement two) {
            return two.getHeuristicValue() - one.getHeuristicValue();
        }
    }
	public static class MinHeap implements Comparator<queueElement> {		 
	    public int compare(queueElement one, queueElement two) {
	        return one.getHeuristicValue() - two.getHeuristicValue();
	    }
    }
	
	public static class queueElement{
		private int heuristicValue;
		private int[] pos;
		
		public queueElement(int heuristic, int[] currentPos){
			setHeuristicValue(heuristic);
			setPos(currentPos);
		}
		
		public void setHeuristicValue(int heuristicValue) {
			this.heuristicValue = heuristicValue;
		}
		public int getHeuristicValue() {
			return heuristicValue;
		}
		public void setPos(int[] pos) {
			this.pos = pos;
		}
		public int[] getPos() {
			return pos;
		}
		
	}
	
	public static class nextStep{
		private int bestPoint;
		private int[] bestMove;
		
		public nextStep(int point, int[] move){
			setBestPoint(point);
			setBestMove(move);
		}

		public void setBestPoint(int bestPoint) {
			this.bestPoint = bestPoint;
		}

		public int getBestPoint() {
			return bestPoint;
		}

		public void setBestMove(int[] bestMove) {
			this.bestMove = bestMove;
		}

		public int[] getBestMove() {
			return bestMove;
		}
	}
	
	// global variables
	private boolean redTurn;
	private ArrayList<Integer> sizes;
	private ArrayList<int[][]> tables;
	
	public Assignment3(ArrayList<Integer> sizeArray, ArrayList<int[][]> tableArray){
		sizes = sizeArray;
		tables = tableArray;
	}
	
	public void alphaBetaPruning(BufferedWriter out) throws IOException{		
		
		// go through all the elements
		for (int i = 0; i < sizes.size(); i++){
			// check whose turn and build empty position list
			int redNum = 0, greenNum = 0;
			int size = sizes.get(i);
			int[][] table = tables.get(i);		
			for(int m = 0; m < size; m++){
				for(int n = 0; n < size; n++){
					// red
					if (table[m][n] == 1){
						redNum++;
					}
					// green
					else if (table[m][n] == 0){
						greenNum++;
					}
				}
			}
			// red's turn
			if(redNum == greenNum){
				redTurn = true;
			}
			// green's turn
			else{
				redTurn = false;
			}
			
			nextStep optimalStep = firstMaxValue(table, size, Integer.MIN_VALUE, Integer.MAX_VALUE);
			int[] bestMove = optimalStep.getBestMove();
			
			String buffer = "(" + bestMove[0] + "," + bestMove[1] + ")";
			out.write(buffer);
			buffer = "" + optimalStep.getBestPoint();
			out.write(buffer);
			out.newLine();		
		}
	}
	
	private void copy2DimArray(int[][] destArray, int[][] origArray, int size){
		for(int m = 0; m < size; m++){
			for(int n = 0; n< size; n++) {
				destArray[m][n] = origArray[m][n];
			}
		}
	}
	
	private ArrayList<int[]> getEmptyPos(int[][] table, int size){
		// emptyPos
		ArrayList<int[]> emptyPos =  new ArrayList<int[]>();			
		for(int m = 0; m < size; m++){
			for(int n = 0; n < size; n++){
				if (table[m][n] == -1){
					int[] pos = {m, n};
					emptyPos.add(pos);
				}
			}
		}
		return emptyPos;
	}
	
	private nextStep firstMaxValue(int[][] table, int size, int alpha, int beta){
		int[] ties = new int[2];
		StringBuffer allMove = new StringBuffer();
		StringBuffer prunedMove = new StringBuffer();
		String whosTurn= redTurn ? "r" : "g"; 
		
		// emptyPos
		ArrayList<int[]> emptyPos =  getEmptyPos(table, size);			
		
		// base case
		if(emptyPos.size() == 0){
			return new nextStep(getAward(table, size), ties);
		}
		
		// rank
		MaxHeap maxSort = new MaxHeap();
		PriorityQueue<queueElement> maxHeap = new PriorityQueue<queueElement>(emptyPos.size(), maxSort);
		for (int[] pos : emptyPos){
			allMove.append("<" + pos[0] + "," + pos[1] + ">,");
			// copy table
			int[][] tableCopy = new int[size][size];
			copy2DimArray(tableCopy, table, size);
			// red turn
			if(redTurn){
				tableCopy[pos[0]][pos[1]] = 1;
			}
			// green turn
			else{
				tableCopy[pos[0]][pos[1]] = 0;
			}
			
			// get heuristic value
			int heuristicValue = getAward(tableCopy, size);
			maxHeap.offer(new queueElement(heuristicValue, pos));
		}
		
		// all the cases
		while(maxHeap.size() > 0){			
			// copy
			int[][] tableCopy = new int[size][size];
			copy2DimArray(tableCopy, table, size);
			int[] pos = maxHeap.poll().getPos();
			prunedMove.append("<" + pos[0] + "," + pos[1] + ">,");
			
			// red turn
			if(redTurn){
				tableCopy[pos[0]][pos[1]] = 1;
			}
			// green turn
			else{
				tableCopy[pos[0]][pos[1]] = 0;
			}
			
			// update alpha
			int newAlpha = minValue(tableCopy, size, alpha, beta);
			if (alpha < newAlpha){
				alpha = newAlpha;
				ties = pos;
			}
			
			if (alpha >= beta){
				System.out.println("Player " + whosTurn + "'s turn: alpha = " + alpha + "; beta = " + beta + "; all moves: " + allMove.deleteCharAt(allMove.length()-1).toString() + ";"
						+ " pruned moves: " + prunedMove.deleteCharAt(prunedMove.length()-1).toString());
				return new nextStep(beta, ties);
			}
		}
		
		// printing		
		System.out.println("Player " + whosTurn + "'s turn: alpha = " + alpha + "; beta = " + beta + "; all moves: " + allMove.deleteCharAt(allMove.length()-1).toString() + ";"
				+ " pruned moves: " + prunedMove.deleteCharAt(prunedMove.length()-1).toString());
		
		return new nextStep(alpha, ties);
	}
	
	private int maxValue(int[][] table, int size, int alpha, int beta){
		// printing
		StringBuffer allMove = new StringBuffer();
		StringBuffer prunedMove = new StringBuffer();
		String whosTurn= redTurn ? "r" : "g"; 	
		
		// emptyPos
		ArrayList<int[]> emptyPos =  getEmptyPos(table, size);
		
		// base case
		if(emptyPos.size() == 0){
			return getAward(table, size);
		}
		
		// rank
		MaxHeap maxSort = new MaxHeap();
		PriorityQueue<queueElement> maxHeap = new PriorityQueue<queueElement>(emptyPos.size(), maxSort);
		for (int[] pos : emptyPos){
			allMove.append("<" + pos[0] + "," + pos[1] + ">,");
			// copy table
			int[][] tableCopy = new int[size][size];
			copy2DimArray(tableCopy, table, size);
			// red turn
			if(redTurn){
				tableCopy[pos[0]][pos[1]] = 1;
			}
			// green turn
			else{
				tableCopy[pos[0]][pos[1]] = 0;
			}
			
			// get heuristic value
			int heuristicValue = getAward(tableCopy, size);
			maxHeap.offer(new queueElement(heuristicValue, pos));
		}
		
		// all the cases
		while(maxHeap.size() > 0){
			// copy
			int[][] tableCopy = new int[size][size];
			copy2DimArray(tableCopy, table, size);
			int[] pos = maxHeap.poll().getPos();
			prunedMove.append("<" + pos[0] + "," + pos[1] + ">,");
			
			// red turn
			if(redTurn){
				tableCopy[pos[0]][pos[1]] = 1;
			}
			// green turn
			else{
				tableCopy[pos[0]][pos[1]] = 0;
			}
			
			// update alpha
			int newAlpha = minValue(tableCopy, size, alpha, beta);
			if (alpha < newAlpha){
				alpha = newAlpha;
			}
			
			if (alpha >= beta){
				// printing	
				System.out.println("Player " + whosTurn + "'s turn: alpha = " + alpha + "; beta = " + beta + "; all moves: " + allMove.deleteCharAt(allMove.length()-1).toString() + ";"
						+ " pruned moves: " + prunedMove.deleteCharAt(prunedMove.length()-1).toString());
				return beta;
			}
		}
		
		// printing	
		System.out.println("Player " + whosTurn + "'s turn: alpha = " + alpha + "; beta = " + beta + "; all moves: " + allMove.deleteCharAt(allMove.length()-1).toString() + ";"
				+ " pruned moves: " + prunedMove.deleteCharAt(prunedMove.length()-1).toString());
		return alpha;
	}
	
	
	private int minValue(int[][] table, int size, int alpha, int beta){
		// printing
		StringBuffer allMove = new StringBuffer();
		StringBuffer prunedMove = new StringBuffer();
		String whosTurn= redTurn ? "g" : "r"; 
		
		// emptyPos
		ArrayList<int[]> emptyPos =  getEmptyPos(table, size);
		
		// base case
		if(emptyPos.size() == 0){
			return getAward(table, size);
		}
		
		// rank
		MinHeap minSort = new MinHeap();
		PriorityQueue<queueElement> minHeap = new PriorityQueue<queueElement>(emptyPos.size(), minSort);
		for (int[] pos : emptyPos){
			allMove.append("<" + pos[0] + "," + pos[1] + ">,");
			// copy table
			int[][] tableCopy = new int[size][size];
			copy2DimArray(tableCopy, table, size);
			// red turn
			if(redTurn){
				tableCopy[pos[0]][pos[1]] = 0;
			}
			// green turn
			else{
				tableCopy[pos[0]][pos[1]] = 1;
			}
			
			// get heuristic value
			int heuristicValue = getAward(tableCopy, size);
			minHeap.offer(new queueElement(heuristicValue, pos));
		}
		
		// all the cases
		while(minHeap.size() > 0){
			// copy
			int[][] tableCopy = new int[size][size];
			copy2DimArray(tableCopy, table, size);
			int[] pos = minHeap.poll().getPos();
			prunedMove.append("<" + pos[0] + "," + pos[1] + ">,");
			
			// red turn
			if(redTurn){
				tableCopy[pos[0]][pos[1]] = 0;
			}
			// green turn
			else{
				tableCopy[pos[0]][pos[1]] = 1;
			}
			
			// update alpha
			int newBeta = maxValue(tableCopy, size, alpha, beta);
			if (beta > newBeta){
				beta = newBeta;
			}
			
			if (alpha >= beta){
				// printing	
				System.out.println("Player " + whosTurn + "'s turn: alpha = " + alpha + "; beta = " + beta + "; all moves: " + allMove.deleteCharAt(allMove.length()-1).toString() + ";"
						+ " pruned moves: " + prunedMove.deleteCharAt(prunedMove.length()-1).toString());
				return alpha;
			}
		}

		// printing	
		System.out.println("Player " + whosTurn + "'s turn: alpha = " + alpha + "; beta = " + beta + "; all moves: " + allMove.deleteCharAt(allMove.length()-1).toString() + ";"
				+ " pruned moves: " + prunedMove.deleteCharAt(prunedMove.length()-1).toString());
		return beta;
	}
	
	private int getAward(int[][] table, int size){
		int[][] tableCopy = new int[size][size];
		copy2DimArray(tableCopy, table, size);
		
		// score for red and green
		int redMax = 0;
		int greenMax = 0;
		for (int i = 0; i < size; i++){
			for (int j = 0; j < size; j++){
				// search
				if (tableCopy[i][j] == 1){
					int currentRed = getRegionSize(tableCopy, size, i, j, 1);
					if(currentRed > redMax){
						redMax = currentRed;
					}
				}
				
				else if (tableCopy[i][j] == 0){
					int currentGreen = getRegionSize(tableCopy, size, i, j, 0);
					if(currentGreen > greenMax){
						greenMax = currentGreen;
					}
				}
			}
		}
		
		// red turn
		if(redTurn){
			return redMax - greenMax;
		}
		else {
			return greenMax - redMax;
		}
	}
	
	public int getRegionSize(int[][] table, int size, int x, int y, int player){
		// base case
		if(x < 0 || x >= size || y < 0 || y>= size){
			return 0;
		}
		
		if(table[x][y] != player){
			return 0;
		}
		
		table[x][y] = -1;
		return 1 + getRegionSize(table, size, x, y-1, player) + getRegionSize(table, size, x, y+1, player) + getRegionSize(table, size, x-1, y, player) + getRegionSize(table, size, x+1, y, player);
	}
	
}
