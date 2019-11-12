<h2>Heuristic exercise: logical satisfaction. JaCop </h1>

<h2>1. Objective </h2>

The objective of this practice is to learn to model and solve problems of both logical satisfaction (SAT) and heuristic search.

<h2>2. Statement of the problem </h2>
Paganitzu 1 is an arcade video game released in the 90's, which is based on the classic game of Sokoban 2. In it, the protagonist called Alabama or "Al" Smith is in a grid-like labyrinth. In the simplified version of the game we'll consider, the goal is to get Al to walk through the maze collecting all the keys and, once they're collected, get out of the maze. To achieve this goal, Al can push the rocks in the form of balls that appear and obstruct his movements. In the labyrinth, there are also snakes that shoot horizontally if the protagonist crosses in front, although they do not shoot if they have an obstacle (key or rock) that hinders their vision. Therefore, these rocks can also be placed in front of the snakes to avoid being shot. To better understand the dynamics of the game it is recommended to play some games 3, although, as mentioned above, in this practice we will consider the simplified version of the game described above (i.e., they are not considered gems, spiders, or other elements of the game).<br>

The labyrinth is made up of different cells, and in each cell you can find an element of the game: the protagonist, a snake, a rock, a wall or an obstacle that we cannot cross, the keys, the exit, or an empty cell. In the maps elaborated in this practice each of these elements will be represented with a different letter: 'A' for the protagonist, 'S' for the snakes, 'O' for the rocks, 'K' for the keys, 'E' for the exit of the labyrinth, '%' for the obsta ́culos that we cannot cross, and a blank space for the empty cells.<br>

<h3> 2.1. Part 1: Labyrinth design with SAT</h3>
In the design of labyrinths, one of the problems that arise is where to initially place the different elements of the game so that the labyrinth is attractive to players. To do this, it has been decided to automate the placement of the protagonist and snakes in a given labyrinth (i.e., the walls, rocks, keys, and exit are initially defined). The restrictions to take into account in this process are:
<ol>
<li>Al and snakes so ́lo can be placed in vac cells ́ıas.</li>
<li>A snake cannot be in the same row as another snake.</li>
<li>There can be no snake in the same row or column as Al.</li>
</ol>
For this part it is requested:
<ul>

<li>The objective of the exercise is to model the problem as a problem of logical satisfaction, in Conjunctive Normal Form (CNF).</li>
<li>Using JaCoP, develop a program that codes the previous model and determines where to place Al and the snakes. The implementation developed must be executed from a console or terminal with the following command:</li>

``
java SATPaganitzu <maze> n
``<br>

where:
<ul>
<li>labyrinth: is the name of the file that contains a labyrinth in the format indicated in Figure 2 but empty, that is, only indicating the walls, keys, exit and rocks. An example name for this file could be lab1 part1.lab.</li>
<li>n: number of snakes to place in the labyrinth.
In the case that the problem is satisfactory, the program will generate as output a labyrinth that will be written in a file using the format of Figure 2 that will contain, in addition to the initial elements, the n snakes and Al. The name of the generated file must be the name of the input file with extension .output (for example, lab1 part1.lab.output). If the problem is not satisfactory, a message will be printed on the screen informing of this fact.</li><br>
</ul></ul>
In addition, test cases should be generated, i.e. some empty labyrinths of various sizes and shapes and with a different number of snakes to include in them.


<h3> 2.2. Part 2: Heuristic Search.</h3>


The problem proposed in this part is to calculate the minimum cost route that allows Al to collect all the keys and get out of the labyrinth, while avoiding being shot by snakes. To do this, Al can move between two contiguous horizontal or vertical cells as long as it is not prevented by a wall or obstacle that cannot pass through. In addition, you can push the rocks that stand in your way, or you can push them to place them in the line of sight of snakes and avoid being shot. To be able to push a rock, Al must be in an adjacent cell, and the cell to which you want to move the rock must be empty. When Al pushes a rock he goes on to occupy the cell that occupied the rock. The costs that are contemplated are the following:
<ul>
<li>An empty cell can be passed through at cost 2.</li>
<li>Moving a rock costs 4.</li>
</ul>

In this part you ask:

Model the problem of route calculation proposed as a heuristic search problem.
To implement the __algorithm A*__ that allows to solve the problem and to implement two heuristic functions informed and admissible that estimate the remaining cost, so that they serve as guide for the algorithm. The developed implementation must be executed from a console or terminal with the following command:

``
java AstarPaganitzu <labyrinth> <heuristics>
``<br>
 where:

 <ul>
 <li>Labyrinth: Name of the file containing the labyrinth in the format specified in Figure 2. Example: lab1 part2.map.</li>
 <li>Heuristica: Name of the heuristics.</li>
 </ul>


The possible values for the heuristic parameter must be detailed in the memory and in the help of the developed implementation. The program should generate two output files. Both must be generated in the same directory where the input labyrinth is located and must have the same name of the labyrinth (extension included) ma ́s an additional extension. The files are the following:


<ul>
<li>Labyrinth of exit: Must contain the route made by Al to collect all the keys and reach the exit. To do this, the entry labyrinth will be shown first, and immediately below the path followed by Al, for example, (0,0)→(0,1)→(1,1)→ . . ., where each of the positions (x, y) indicates the row x and column y where Al is. The extension ́n of this file must be '.output'. Example: lab1 part2.lab.output.</li>
<li>Statistics file. This file must contain information related to the search process, such as total time, total cost, route length, expanded nodes, etc. For example,
                  Total time: 145
                  Total cost: 54
                  Route length: 27
                  Expanded nodes: 132
The extension of this file must be '.statistics'. Example: lab1parte2.lab.statistics.</li>

<li>Propose test cases on various labyrinths between those generated in the first part and those that are expressly generated for this part, and resolve them with the implementation developed. These cases should be generated reasonably depending on the efficiency achieved in the implementation.</li>


<li>Perform a comparative study using the two heuristics implemented (number of expanded nodes, computation time, etc.).</li>

<li>As an optional part of the problem, it is proposed to model and implement the diagonal movement between two contiguous boxes, implement a heuristic that considers this movement and a brief comparative analysis as a complement to the previous one. </li>
</ul>
