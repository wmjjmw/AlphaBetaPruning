import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;


public class assignment3Tester {
	public static void main(String[] args){
		ArrayList<Integer> sizes = new ArrayList<Integer>();
		ArrayList<int[][]> tables = new ArrayList<int[][]>();
		
		File fileName = new File("input.txt");  
	    Scanner inFile;
	    try {
			inFile = new Scanner(fileName);
			while(inFile.hasNextInt()){
				int size = inFile.nextInt();
				inFile.nextLine();

				sizes.add(new Integer(size));
				int[][] table = new int[size][size];
				
				for(int i = 0; i < size; i++){
					String currentLine = inFile.nextLine();
					for (int j = 0; j< size; j++){
						if(currentLine.charAt(j) == 'r'){
							table[i][j] = 1;
						}
						else if(currentLine.charAt(j) == 'g'){
							table[i][j] = 0;
						}
						else {
							table[i][j] = -1;
						}
					}				
				}
				tables.add(table);
			}
			
			// File writer
			FileWriter fstream = new FileWriter("output.txt");
			BufferedWriter out = new BufferedWriter(fstream);
			
			Assignment3 alphaBeta = new Assignment3(sizes, tables);
			alphaBeta.alphaBetaPruning(out);
			out.close();
	    } catch (FileNotFoundException e) {
	    	System.out.println("Cannot find input file");
	    }catch (Exception e){
	    	System.err.println("Error: " + e.getMessage());
	    }
	}
}
