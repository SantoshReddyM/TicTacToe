# TicTacToe

This is a simple TicTacToe game implemented using basic Java WebSockets and very basic HTML/JS

Overview:

The goal is to implement a TicTacToe game where the computer never loses even a single game when played against any opponent.


Rules to implement TicTacToe:
In every move that the computer makes, it follows the below rules.

1. If we can make a move that can result in a win in the current chance, we will make it.
2. If the opponent is about to win the game in the next move, block the opponent.
3. If we can setup a 'double win' move, then do it
4. If there exists any row/column/diagonal with one of cells already taken by us and if it is a 'Safe' move, then make it.
5. If Center is available, then select the center cell
6. If any of the corners is available, then select it.
7. Select any of the remaining cells.

Note:

Double Win : Consider the below example -

o - x <br> 
o - - <br>
x - -

If we can place an 'x' at (3,3), then it is a Double Win  move. We will win in the next chance as the opponent will not be able to block both the cells


Safe: A move is considered 'Safe' if by making that move, we do not allow the opponet to make a 'Double Win' move in their next chance.
