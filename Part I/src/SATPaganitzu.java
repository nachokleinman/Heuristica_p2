import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

import org.jacop.core.BooleanVar;
import org.jacop.core.Store;
import org.jacop.jasat.utils.structures.IntVec;
import org.jacop.satwrapper.SatWrapper;
import org.jacop.search.DepthFirstSearch;
import org.jacop.search.IndomainMin;
import org.jacop.search.Search;
import org.jacop.search.SelectChoicePoint;
import org.jacop.search.SimpleSelect;
import org.jacop.search.SmallestDomain;


public class SATPaganitzu {


    public static void main(String[] args) throws IOException {

        // indicamos el inicio del programa
        System.out.println("Inicio");

        //Fichero captado de los argumentos pasados
        File file = new File(args[0]);

        //Lectura del fichero y guardado de este en una matriz
        int initMap[][] = readFile(file.getPath());

        // indicamos que la lectura se ha hecho correctamente
        System.out.println("Lectura del fichero:");

        // mostramos la matriz por pantalla
        showMatrix(initMap);


        // guardamos el numero de serpientes pasadas por parametro
        int numSnakes = Integer.parseInt(args[1]);

        // mostramos un mensaje de verificacion de que el numero recogido se ha hecho adecuadamente
        System.out.println("Numero de serpientes a meter: " + numSnakes + "\n");


        // guardamos Numero de filas
        int row = initMap.length;

        // guardamos Numero de columnas en la fila cero
        int col = initMap[0].length;

        // mensaje de verificacion de el numero de filas y columnas
        System.out.println("filas: " + row);
        System.out.println("columnas: " + col);


        // contador del numero de espacios en blanco que tenemos en la matriz
        int espacioBlanco = 0;

        //recorremos las filas de la matriz
        for (int i = 0; i < initMap.length; i++) {
            // recorremos las columnas de la matriz
            for (int j = 0; j < initMap[0].length; j++) {
                // si la posicion en la que nos encontramos tiene un espacio (32 en codigo ascii) sumamos una unidad a nuestro contador
                if (initMap[i][j] == 32) espacioBlanco++;
            }
        }

        // mensaje de verificacion de que se han contado bien los espacios en blanco
        System.out.println("Numero de espacios en blanco: " + espacioBlanco);

        // creamos un objeto que se utilizara para almacenar nuestras clausulas
        Store store = new Store();

        SatWrapper satWrapper = new SatWrapper();

        store.impose(satWrapper);   /* Importante: sat problem */


        //Contadores de movimiento de las variables

        int counterProtagonist = 0, counterSnake = 0;

        //Creacion de las variables booleanas. Una para protagonista y otra para las serpientes

        BooleanVar protagonist[][] = new BooleanVar[row][col];

        BooleanVar snake[][][] = new BooleanVar[row][col][numSnakes];

        /* volvemos a recorrer la matriz para saber donde podemos poner a nuestros personajes los cuales debe colocarse en espacios 		en blanco*/
        for (int i = 0; i < initMap.length; i++) {
            for (int j = 0; j < initMap[0].length; j++) { //recorremos las columnas del mapa

                if (initMap[i][j] == 32) { //si esta en espacio en blanco guardaremos esa posicion como viable

                    protagonist[i][j] = new BooleanVar(store, "Protagonista en la posición: " + i + ", " + j);

                    counterProtagonist++; // sumamos una unidad al contador de movimiento del protagonista
                }

                for (int k = 0; k < numSnakes; k++) { //bucle para que este proceso se haga con todas las serpientes

                    if (initMap[i][j] == 32) { // si hay un espacio en banco  guardamos la posicion como viable

                        snake[i][j][k] = new BooleanVar(store, "Serpiente " + k + " en la posición " + i + ", " + j);

                        counterSnake++; // sumamos una unidad al contador de movimiento de las serpientes

                    }
                }

            }
        }

        //Todas las variables: es necesario para el SimpleSelect

        //variable que nos indica la cantidad total de movimientos posibles
        int size = counterProtagonist + counterSnake;

        //contador de las variables
        int allVar = 0;

        // creamos la variable booleana almacenar a nuesrtos personajes
        BooleanVar[] allVariables = new BooleanVar[size];

        //creamos el literal del protagonista para poder hacer nuestras clausulas y restricciones del problema
        int literalProtagonist[][] = new int[row][col];


        //recorremos las filas de la variable protagonista
        for (int i = 0; i < protagonist.length; i++) {
            //recorremos las columnas de la variable protagonista
            for (int j = 0; j < protagonist[0].length; j++) {
                //si el protagonista en esa posicion es diferente a null  lo almacenamos en la variable booleana de allVars
                if (protagonist[i][j] != null) {

                    allVariables[allVar] = protagonist[i][j];

                    // Registramos las variables en el sat wrapper
                    satWrapper.register(protagonist[i][j]);

                    // Obtenemos los literales no negados de las variables
                    literalProtagonist[i][j] = satWrapper.cpVarToBoolVar(protagonist[i][j], 1, true);

                    allVar++;
                }
            }
        }

        //creamos el literal de serpientes para poder hacer las clausulas
        int literalSnake[][][] = new int[row][col][numSnakes];

        // recorremos la variable  de serpiemtes para ver con que posiciones podremos contar
        for (int i = 0; i < snake.length; i++) {
            //recorremos las columnas de la variable serpientes
            for (int j = 0; j < snake[0].length; j++) {

                for (int k = 0; k < numSnakes; k++) {

                    if (snake[i][j][k] != null) {

                        allVariables[allVar] = snake[i][j][k];

                        //Registramos las variables en el sat wrapper
                        satWrapper.register(snake[i][j][k]);

                        //Obtenemos los literales no negados de las variables
                        literalSnake[i][j][k] = satWrapper.cpVarToBoolVar(snake[i][j][k], 1, true);

                        allVar++;
                    }
                }
            }
        }



        /*

        El problema se va a definir en forma CNF, por lo tanto, tenemos que añadir una a una todas las clausulas del
        problema. Cada clausula será una disjunción de literales. Por ello, solo utilizamos los literales anteriormente
        obtenidos. Si fuese necesario utilizar un literal negado, éste se indica con un signo negativo delante.
        Ejemplo: -xLiteral

		*/


        /*

        Restricciones explicitas

            1. Al y las serpientes solo se pueden colocar en celdas vacias -> DONDE
            2. Una serpiente no puede estar en la misma fila que otra serpiente. -> DONE
            3. No puede haber ninguna serpiente ni en la misma fila ni en la misma columna que Al. -> DONDE

        Restricciones implicitas

            1. Al solo puede aparecer una vez -> DONE
            2. Es obligatorio que Al aparezca ->DONE
            3. Cada serpiente solo puede aparecer una vez -> DONE
            4. Es obligatorio que aparezcan todas las serpientes -->DONE
            5. No puede haber mas serpientes que filas --> DONE

        */


        // llamamos a los metodos que generaran cada una de nuestras clausulas
        //clausula para que las serpientes no esten en la misma fila que Al
        protagonist_snake_differentRow(satWrapper, literalSnake, literalProtagonist, protagonist, snake);

        //clausula para que las serpientes no esten en la misma columna que Al
        protagonist_snake_differentCol(satWrapper, literalSnake, literalProtagonist, protagonist, snake);

        //clausula para que las serpientes no puedan estar en mismas filas
        just_one_snake_in_each_Row(satWrapper, literalSnake, snake);

        //clausula para que las serpientes no puedan estar en la misma posicion que Al
        for (int z = 0; z < numSnakes; z++) {
            snake_protganist_not_in_theSamePlace(satWrapper, literalSnake, literalProtagonist, z, snake, protagonist);
        }
        //clausula para que cada serpiente solo aparezca una vez
        for (int i = 0; i < numSnakes; i++) {
            deploySnakeOnce(satWrapper, literalSnake, i, snake);
        }

        //clausula para que solo se ponga a Al una vez
        deployProtagonistOnce(satWrapper, literalProtagonist, protagonist);


        System.out.println("Resolucion...");

        /* Resolvemos el problema */

        Search<BooleanVar> search = new DepthFirstSearch<>();

        SelectChoicePoint<BooleanVar> select = new SimpleSelect<>(allVariables, new SmallestDomain<>(), new IndomainMin<>());

        Boolean result = search.labeling(store, select);


        if (result) { //condicion de que si exixte una solucion factible proceda a crear el mapa de resultado


            //haremos el mismo procedimiento para las serpientes
            System.out.println("snakes: ");
            //recorremos  las filas del array
            for (int i = 0; i < snake.length; i++) {
                //recorremos  las columnas del array
                for (int j = 0; j < snake[0].length; j++) {
                    // recorremos el array que nos indica con que numero de serpiente estamos trabajando
                    for (int k = 0; k < snake[0][0].length; k++) {
                        //si esa posicion es diferente de null querra decir que es parte de aquellas a las que pertenecian la serpiente
                        if (snake[i][j][k] != null) {
                            //si el valor del dominio de esa posicion es igual a 1 que es lo mismo que true imprimiremos la posicion
                            if (snake[i][j][k].dom().value() == 1) {
                                //imprimimos la posicion de la serpiente numero k
                                System.out.println(snake[i][j][k].id());

                                initMap[i][j] = 83; //Colocar una 'S' (ASCII) por cada serpiente en el mapa
                            }
                        }
                    }
                }
            }
            System.out.println("Protagonist: ");
            //recorremos las filas del array de protagonistas
            for (int i = 0; i < protagonist.length; i++) {
                //recorremos las columnas del array de protagonistas
                for (int j = 0; j < protagonist[0].length; j++) {
                    //si esa posicion es diferente de null querra decir que es parte de aquellas a las que pertenecian nuestro protagonista
                    if (protagonist[i][j] != null) {
                        //si el valor del dominio de esa posicion es igual a 1 que es lo mismo que true imprimiremos la posicion
                        if (protagonist[i][j].dom().value() == 1) {
                            //imprimimos la posicion del protagonista
                            System.out.println(protagonist[i][j].id());

                            initMap[i][j] = 65; //Colocar una 'A' (ASCII) para representar a Al en el mapa resultado
                        }

                    }
                }
            }
        } else
            System.out.println("Error, no existe solucion factible"); // si no hay solucion nos imprimira y avisa de un error

        //avisamos de que se ha llevado a cabo la resolucion
        System.out.println("resolucion hecha");

        //avisamos de la creacion del fichero
        System.out.println("creación del fichero: \n");

        // escribimos en nuestro fichero segun el mapa de entrada dado y el modificado
        writeFile(initMap, args[0]);

        //mostramos la matriz por pantalla
        showMatrix(initMap);
        //contador que nos indica la cantidad de espacios en blanco que hay en nuestro mapa
        espacioBlanco = 0;
        //recorremos las filas del mapa
        for (int i = 0; i < initMap.length; i++) {
            //recorremos las columnas del mapa
            for (int j = 0; j < initMap[0].length; j++) {
                //si encontramos un espacio en blanco, numero 32 en codigo ASCII, sumamos una unidad a nuestro contador
                if (initMap[i][j] == 32) espacioBlanco++;
            }
        }
        //imprimimos el resultado de la cantidad de espacios de blanco restantes del mapa
        System.out.println("Numero de espacios en blanco: " + espacioBlanco);
    }

    //FIN DEL METODO MAIN

    //metodo que crea las clausulas para que el protagonista y las serpientes no esten en la misma fila
    private static void protagonist_snake_differentRow(SatWrapper satWrapper, int literalSnake[][][], int literalProtagonist[][], BooleanVar protagonist[][], BooleanVar snake[][][]) {


        //recorremos una a una cada una de las serpientes que tenemos
        for (int i = 0; i < literalSnake[0][0].length; i++) {
            //recorremos las filas para hallar todos los lietarles posibles de serpientes
            for (int j = 0; j < literalSnake.length; j++) {
                //recorremos las columnas para hallar todos los lietarles posibles de serpientes
                for (int k = 0; k < literalSnake[0].length; k++) {
                    // recorremos las columnas de nuestro protagonista
                    for (int m = 0; m < literalProtagonist[0].length; m++) {

                        //comprobamos que ni los lietrales sean falsos, ni que las variables boolean sean falsas y que las columnas de los dos personajes sean diferentes
                        if (k != m && protagonist[j][m] != null && snake[j][k][i] != null) {
                            //creamos nuestras clausulas
                            IntVec clause = new IntVec(satWrapper.pool);
                            //hacemos que si el protagonista esta en una fila no este la serpiente en la misma y viceversa
                            clause.add(-literalProtagonist[j][m]);
                            clause.add(-literalSnake[j][k][i]);
                            //guardamos la clausula
                            satWrapper.addModelClause(clause.toArray());

                        }

                    }

                }
            }
        }
    }

    // metodo que hara que el protagonista no este en la misma columna que las serpientes
    private static void protagonist_snake_differentCol(SatWrapper satWrapper, int literalSnake[][][], int literalProtagonist[][], BooleanVar protagonist[][], BooleanVar snake[][][]) {

        //for que nos inidica con que numero de serpiente estamos trabajando
        for (int i = 0; i < literalSnake[0][0].length; i++) {
            //for que nos inidica con que numero de fila estamos trabajando en el literal de srrpientes
            for (int j = 0; j < literalSnake.length; j++) {
                //for que nos inidica con que numero de columna estamos trabajando en el literal de srrpientes
                for (int k = 0; k < literalSnake[0].length; k++) {
                    //for que nos inidica con que numero de fila estamos trabajando en el literal del protagonista
                    for (int l = 0; l < literalProtagonist.length; l++) {
                        //comprobaciones de que las filas sean diferentes y de que los elementos con los qie creemos las clausulas son true
                        if (j != l && protagonist[l][k] != null && snake[j][k][i] != null) {

                            IntVec clause = new IntVec(satWrapper.pool);
                            //diremos que si el protagonista esta en una columna ni puede estar la serpiente y viceversa
                            clause.add(-literalProtagonist[l][k]);
                            clause.add(-literalSnake[j][k][i]);
                            //guardamos la clausula
                            satWrapper.addModelClause(clause.toArray());

                        }

                    }

                }
            }
        }

    }

    // metodo que hara que el protagonista solo aparezca una vez en nuestra matriz
    private static void deployProtagonistOnce(SatWrapper satWrapper, int literalProtagonist[][], BooleanVar protagonist[][]) {

        IntVec clause = new IntVec(satWrapper.pool);

        //recorremos las filas de nuestro array protagonistas
        for (int i = 0; i < literalProtagonist.length; i++) {
            //recprremos las columnas de nuestri array protagonista
            for (int j = 0; j < literalProtagonist[0].length; j++) {

                //conidcion con la que comprobamos que antes de hacer la clausula nuestros elementos no tengan valor nulo
                if (protagonist[i][j] != null) {

                    //creamos la clausula que nos obliga que al salga una vez
                    clause.add(literalProtagonist[i][j]);


                }

            }


        }
        //guardamos la clausula una vez hayamos valorado todas las posibilidades de almacenamientos
        satWrapper.addModelClause(clause.toArray());
    }

    //metodo que nos crea la clausula de que cada serpiente solo puede salr una vez
    private static void deploySnakeOnce(SatWrapper satWrapper, int literalSnake[][][], int k, BooleanVar snake[][][]) {

        IntVec clause = new IntVec(satWrapper.pool);
        //recorremos las filas de la variable serpiente
        for (int i = 0; i < literalSnake.length; i++) {
            //recorremos las columnas de las variables serpientes
            for (int j = 0; j < literalSnake[0].length; j++) {
                //conidcion que hace que sol se creen las clausulas si las variables no son nulas
                if (snake[i][j][k] != null) {
                    //creamos la clausula que obligala aparicion de todas las serpientes
                    clause.add(literalSnake[i][j][k]);

                }
            }
        }
        //guardamos la clausula
        satWrapper.addModelClause(clause.toArray());

    }

    //metodo que nos crea la clausula que hace que solo haya una serpiente por fila
    private static void just_one_snake_in_each_Row(SatWrapper satWrapper, int literalSnake[][][], BooleanVar snake[][][]) {

        //contador que nos indica con que serpiente estamos trabajando
        for (int i = 0; i < literalSnake[0][0].length; i++) {
            //recorremos las filas de la variable serpiente
            for (int j = 0; j < literalSnake.length; j++) {
                //recorremos las filas de la variable serpiente
                for (int k = 0; k < literalSnake[0].length; k++) {
                    //contador que nos indica la segunda serpiente con la que estamos trabajando
                    for (int m = 0; m < literalSnake[0][0].length; m++) {
                        //vemos que no estamos comparando la misma serpiente
                        if (i != m) {
                            //si nuestros contadores indican que no estamos tratando con la misma serpiente miramos recorremos las columnas de la segunda serpiente
                            for (int l = 0; l < literalSnake[0].length; l++) {
                                //condicion que nos filtra que las serpine
                                if (snake[j][k][i] != null && snake[j][l][m] != null) {
                                    IntVec clause = new IntVec(satWrapper.pool);
                                    //clausula de que si una serpiente esta en una fila la otra no puede estar en esa misma fila
                                    clause.add(-literalSnake[j][k][i]);
                                    clause.add(-literalSnake[j][l][m]);
                                    //guardamos la clausula
                                    satWrapper.addModelClause(clause.toArray());
                                }
                            }
                        }

                    }

                }
            }
        }
    }

    //metodo que nos hace que las serpientes no se superpongan con la posicion de AL
    private static void snake_protganist_not_in_theSamePlace(SatWrapper satWrapper, int literalSnake[][][], int literalProtagonist[][], int k, BooleanVar snake[][][], BooleanVar protagonist[][]) {
        //recorremos las filas del array del literal del protagonista
        for (int i = 0; i < literalProtagonist.length; i++) {
            //recorremos las columnas del array del literal del protagonista
            for (int j = 0; j < literalProtagonist[0].length; j++) {
                //si el protagonista y la serpiente en esa posicion tienen un valor diferente a null creamos las clausulas
                if (protagonist[i][j] != null && snake[i][j][k] != null) {
                    //clausulas que hacen que haya en esa misma posicion solo uno de los personajes
                    IntVec clause1 = new IntVec(satWrapper.pool);
                    clause1.add(-literalProtagonist[i][j]);
                    clause1.add(-literalSnake[i][j][k]);

                    satWrapper.addModelClause(clause1.toArray());


                }
            }
        }
    }

    //metodo que hace que se nos muestre la matriz por pantalla
    private static void showMatrix(int resultMap[][]) {
        //recorremsos laS filas de la  matriz/mapa
        for (int[] aResultMap : resultMap) {
            //recorremos las columnas de la matriz
            for (int j = 0; j < resultMap[0].length; j++) {
                //vamos imprimiendo elemento a elemento
                System.out.print((char) aResultMap[j]);
            }
            //genera un salto de linea para que la matriz se represente como el mapa
            System.out.println();
        }
    }

    //metodo con el que leemos el fichero  de entrada
    private static int[][] readFile(String file) throws IOException {
        //creamos un objeto auxiliar con el que trabajaremos
        FileReader fileReader = new FileReader(file);
        //contadores de columnas, filas, y varibles que nos dicen si esta leido o no el fichero
        int row = 0, col = 0, counter = 0, readed = fileReader.read();

        try {
            //se seguira con el proceso siempre que no de error !=-1
            while (readed != -1) {
                if (readed == 10) { //si termina de leer una fila
                    row++; //sumamos al contador filas uno
                    col = counter; //sumamos al contador de columnas los valores del contador
                    counter = 0;
                } else {
                    counter++;
                }
                readed = fileReader.read();
            }
        } catch (FileNotFoundException e) { //filtros de errores

            System.out.println("Unable to open file '" + fileReader + "'"); //imprimimos mensaje para indicar del error

            fileReader.close(); //cerramos el fichero

        } catch (IOException e) { //filtros de errores

            System.out.println("Error reading file '" + fileReader + "'"); //imprimimos mensaje para indicar del error

            fileReader.close(); //cerramos fichero

        }

        fileReader.close(); //cerramos fichero

        FileReader fileReader2 = new FileReader(file); //objeto que guarda el fichero

        int reading = fileReader2.read(); //leemos el fichero

        int matrix[][] = new int[row][col]; //creamos la matriz donde se guardaran los elementos del mapa

        for (int i = 0; i < row; i++) { //bucle que nos indica en que fila estamos

            for (int j = 0; j < col; j++) {// recorre el bucle segun el total de columnas que haya

                if (reading == -1) { //error en la lectura
                    break;
                }

                if (reading == 10) {

                    reading = fileReader2.read();
                }

                matrix[i][j] = reading; //introducimos lo leido en la matrix

                reading = fileReader2.read(); //seguimos leyendo la matiz
            }
        }

        fileReader2.close(); //cerramos el fichero

        return matrix; //devolvemos la matriz con la que  trabajaremos
    }

    //metodo con el que escribimos en el fichero
    private static void writeFile(int[][] matrix, String file) throws IOException {

        PrintWriter writer = new PrintWriter(new FileWriter(file + ".output")); //creamos fichero de salida e imprimimos un mensaje por pantalla del nombre del mismo

        for (int i = 0; i < matrix.length; i++) { //recorremos las filas de la matriz

            for (int j = 0; j < matrix[0].length; j++) { //recorremos las columnas de la matriz

                writer.write(matrix[i][j]); //escribimos en el fcihero el valor de la matriz en la posicion i,j

            }
            writer.write("\n"); // escrinimos el salto de linea para que nos escriba el mapa en el formato deseado
        }

        writer.close(); //cerramos fichero

    }
}
