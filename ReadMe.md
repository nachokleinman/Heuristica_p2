<h2>Heuristic exercise: logical satisfaction. JaCop </h1>

<h2>1. Objective </h2> 

The objective of this practice is to learn to model and solve problems of both logical satisfaction (SAT) and heuristic search.

<h2>2. Statement of the problem </h2>
Paganitzu 1 is an arcade video game released in the 90's, which is based on the classic game of Sokoban 2. In it, the protagonist called Alabama or "Al" Smith is in a grid-like labyrinth. In the simplified version of the game we'll consider, the goal is to get Al to walk through the maze collecting all the keys and, once they're collected, get out of the maze. To achieve this goal, Al can push the rocks in the form of balls that appear and obstruct his movements. In the labyrinth, there are also snakes that shoot horizontally if the protagonist crosses in front, although they do not shoot if they have an obstacle (key or rock) that hinders their vision. Therefore, these rocks can also be placed in front of the snakes to avoid being shot. To better understand the dynamics of the game it is recommended to play some games 3, although, as mentioned above, in this practice we will consider the simplified version of the game described above (i.e., they are not considered gems, spiders, or other elements of the game).<br>



The labyrinth is made up of different cells, and in each cell you can find an element of the game: the protagonist, a snake, a rock, a wall or an obstacle that we cannot cross, the keys, the exit, or an empty cell. In the maps elaborated in this practice each of these elements will be represented with a different letter: 'A' for the protagonist, 'S' for the snakes, 'O' for the rocks, 'K' for the keys, 'E' for the exit of the labyrinth, '%' for the obsta ́culos that we cannot cross, and a blank space for the empty cells.<br>

<h3>2.1. Part 1: Labyrinth design with SAT</h3>>
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
