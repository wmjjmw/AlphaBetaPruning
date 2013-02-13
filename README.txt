Description of project:
A new board game called "wizwoz" is developed. Two players, red(referred as r) and gold(referred as g). An n*n board is created with k "r" and k "g" randomly placed on the board.
Starting with player r, each player puts his/her letter in one of the empty squares on the board. After the board is filled, each player's score is equal to the largest connected region on the board filled with that player's color. The player with the highest score wins, and is awarded with the difference between his/her score and the score of the other player.

Given any starting configuration, the program will output the best move for the current player, i.e, the score that maximizes the points awarded to that player.

Input:
There is a file called input.txt in the current directory. it consists of multiple test cases. Each test case starts with a line containing a positive integer n(<= 8) indicating the size of the board. Next will come n lines describing the current board layout. Each of these lines will contain n characters taken from 'r', 'g', '.', where '.' represents an empty square.

Output:
The result will be saved in output.txt, and pruning information will be printed out in the console.

Compile:
javac assignment3Tester.java

Run:
java assignment3Tester

Heuristic function:
heuristic function is defined by the award of current player based on current board configuration.
