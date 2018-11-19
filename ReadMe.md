<h2>1. Objetivo</h2>

El objetivo de esta práctica es que el alumno aprenda a modelar y resolver problemas tanto de satisfacibili- dad lógica (SAT) como de búsqueda heurística.

<h2>2. Enunciado del problema</h2>
Paganitzu 1 es un videojuego arcade lanzado en los años 90, que está basado en el clásico juego del Sokoban 2. En él, el protagonista llamado Alabama o “Al” Smith se encuentra en un laberinto tipo grid. En la versión simplificada del juego que consideraremos, el objetivo es conseguir que Al recorra el laberinto recogiendo todas las llaves y, una vez recogidas, salga del laberinto. Para conseguir este objetivo, Al puede empujar las rocas en forma de bolas que aparecen y que obstaculizan sus movimientos. En el laberinto, también aparecen serpientes que disparan de forma horizontal si el protagonista cruza por delante, aunque no disparan si tienen un obstáculo (llave o roca) que obstaculice su visión. Por lo tanto, estas rocas también se pueden colocar delante de las serpientes para evitar ser disparados. Para entender mejor la dinámica del juego se recomienda jugar algunas partidas 3, aunque, como se ha comentado, en esta pra ́ctica consideraremos la versión simplificada del juego anteriormente descrita (i.e., no se consideran gemas, arañas, ni demás elementos del juego).

El laberinto está conformado por diferentes celdas, y en cada celda se puede encontrar un u ́nico elemento del juego: el protagonista, una serpiente, una roca, una pared o un obstáculo que no podemos atravesar, las llaves, la salida, o una celda vacía. En los mapas que se elaboren en esta práctica cada uno de estos elementos se va a representar con una letra diferente: ’A’ para el protagonista, ’S’ para las serpientes, ’O’ para las rocas, ’K’ para las llaves, ’E’ para la salida del laberinto, ’%’ para los obsta ́culos que no podemos atravesar, y un espacio en blanco para las celdas vacías.

<h3>2.1. Parte1: Diseño de laberintos con SAT</h3>
En el diseço de los laberintos, uno de los problemas que surgen es dónde situar inicialmente los diferentes elementos del juego de forma que el laberinto sea atractivo para los jugadores. Para ello, se ha decidido automatizar la colocación del protagonista y las serpientes en un laberinto dado (i.e., las paredes, rocas, llaves, y la salida vienen definidos inicialmente). Las restricciones a tener en cuenta en este proceso son:
<ol>
<li>Al y las serpientes so ́lo se pueden colocar en celdas vac ́ıas.</li>
<li>Una serpiente no puede estar en la misma fila que otra serpiente.</li>
<li>No puede haber ninguna serpiente ni en la misma fila ni en la misma columna que Al.</li>
</ol>
Para esta parte se pide:
<ul>

<li>Modelar el problema como un problema de satisfacibilidad lógica, en Forma Normal Conjuntiva (CNF).<li>
Utilizando JaCoP, desarrollar un programa que codifique el modelo anterior y determine dónde colocar a Al y las serpientes. La implementación desarrollada se deberá ejecutar desde una consola o terminal con el siguiente comando:<br>
```
java SATPaganitzu <laberinto> n
```
<br> donde:
<ul>
<li>laberinto: es el nombre del fichero que contiene un laberinto en el formato indicado en la Figura 2 pero vacío, es decir, sólo indicando las paredes, llaves, salida y las rocas. Un ejemplo de nombre para este fichero podría ser lab1 parte1.lab.</li>
<li>n: número de serpientes a colocar en el laberinto.
En el caso de que el problema sea satisfacible, el programa generará como salida un laberinto que se escribirá en un fichero utilizando el formato de la Figura 2 que contendrá, además de los elementos iniciales, las n serpientes y a Al. El nombre del fichero generado debe ser el nombre del fichero de entrada con extensión .output (por ejemplo, lab1 parte1.lab.output). En el caso de que el problema no sea satisfacible, se imprimirá un mensaje por pantalla informando de este hecho.</li>
<br>Los alumnos deben generar sus propios casos de prueba, es decir, sus propios laberintos vacíos de varios tamaños y formas y con un número de serpientes diferente a incluir en ellos.