import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

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

        //Fichero captado de los argumentos
        File file = new File(args[0]);

        //Lectura del fichero que es guardado en una matriz
        int initMap[][] = readFile(file.getPath());

        //Matriz copiada de la matriz incial con la que se trabara en el programa
        int resultMap[][] = initMap;

        //Numero de serpientes pasadas por parametro
        int numSnakes = Integer.parseInt(args[1]);

        //Numero de filas
        int row = initMap.length;

        //Numero de columnas en la fila cero
        int col = initMap[0].length;


        showMatrix(resultMap);

        Store store = new Store();

        SatWrapper satWrapper = new SatWrapper();

        store.impose(satWrapper);   /* Importante: sat problem */


        //contadores de movimiento de las variables
        int counterProtagonist =0, counterSnake = 0;

        //Creacion de las variables booleanas. Una para protagonista y otra para las serpientes

        BooleanVar protagonist[][] = new BooleanVar[row][col];

        BooleanVar snake[][][] = new BooleanVar[row][col][numSnakes];

        for (int i = 0; i < initMap.length; i ++) {

            for (int j = 0; j < initMap[0].length; j ++) {

                protagonist[i][j] = new BooleanVar(store, "Posición: " + i + ", " + j);
                counterProtagonist ++;

                for (int k = 0; k < numSnakes; k ++) {

                    snake[i][j][k] = new BooleanVar(store, "Serpiente " + k + " en la posición" + i + ", " + j);
                    counterSnake ++;
                }
            }
        }

        //Todas las variables: es necesario para el SimpleSelect
        int size = counterProtagonist + counterSnake;

        int allVar = 0;

        BooleanVar[] allVariables = new BooleanVar[size];

        int literalProtagonist [][] = new int[row][col];

        for (int i = 0; i < protagonist.length; i ++) {

            for (int j = 0; j < protagonist[0].length; j ++) {

                if (protagonist [i][j] != null) {

                    allVariables[allVar] = protagonist[i][j];

                    /* Registramos las variables en el sat wrapper */
                    satWrapper.register (protagonist [i][j]);

                    /* Obtenemos los literales no negados de las variables */
                    literalProtagonist [i][j] = satWrapper.cpVarToBoolVar (protagonist[i][j], 1, true);
                }
            }
        }

        int literalSnake [][][]= new int [row][col][numSnakes];

        for (int i = 0; i < snake.length; i ++) {

            for (int j = 0; j < snake[0].length; j ++) {

                for (int k = 0; k < numSnakes; k ++) {

                    if (snake [i][j][k] != null) {

                        allVariables[allVar] = snake[i][j][k];

                        /* Registramos las variables en el sat wrapper */
                        satWrapper.register(snake[i][j][k]);

                        /* Obtenemos los literales no negados de las variables */
                        literalSnake[i][j][k] = satWrapper.cpVarToBoolVar(snake[i][j][k], 1, true);
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

            1. Al y las serpientes solo se pueden colocar en celdas vacias.
            2. Una serpiente no puede estar en la misma fila que otra serpiente. -> DONE
            3. No puede haber ninguna serpiente ni en la misma fila ni en la misma columna que Al. -> DONE

        Restricciones implicitas

            1. Al solo puede aparecer una vez -> DONE
            2. Es obligatorio que Al aparezca
            3. Cada serpiente solo puede aparecer una vez -> DONE
            4. Es obligatorio que aparezcan todas las serpientes
            5. No puede haber mas serpientes que filas

        */


        //Restricciones explicitas

        // 2. Una serpiente no puede estar en la misma fila que otra serpiente
        snakesForRow(satWrapper, literalSnake);

        // 3. No puede haber ninguna serpiente ni en la misma fila ni en la misma columna que Al.
        deployAlSnakes (satWrapper, literalSnake, literalProtagonist);


        //Restricciones implicitas


        // 1. Al solo puede aparecer una vez
        deployProtagonistOnce (satWrapper, literalProtagonist);

        // 3. Cada serpiente solo puede aparecer una vez

        for (int i = 0; i < numSnakes; i ++) {

            deploySnakeOnce (satWrapper, literalSnake, i);
        }





    }




    public static void showMatrix (int resultMap [][]) {

        int row = resultMap.length;

        int col = resultMap[0].length;

        for (int i = 0; i < row; i ++) {

            for (int j = 0; j < col; j++) {

            }
            System.out.println();
        }
    }


    public static int[][] readFile(String file) throws IOException {
      return ;
    }

    public static void deployProtagonistOnce (SatWrapper satWrapper, int literal[][]) {

        IntVec clause = new IntVec (satWrapper.pool);

        for (int i = 0; i < literal.length; i ++) {

            for (int j = 0; j < literal[0].length; j ++) {

                if (literal [i][j] != 0) {

                    clause.add(literal[i][j]);
                }
            }
        }
        satWrapper.addModelClause(clause.toArray());
    }

    public static void deploySnakeOnce (SatWrapper satWrapper, int literal[][][], int k){

        IntVec clause = new IntVec(satWrapper.pool);

        for (int i = 0; i < literal.length; i ++) {

            for (int j = 0; j < literal[0].length; j ++) {

                if (literal[i][j][k] != 0) {

                    clause.add(literal[i][j][k]);
                }
            }
        }
        satWrapper.addModelClause(clause.toArray());
    }


    public static void snakesForRow (SatWrapper satWrapper, int literalSnake[][][]) {

        for (int i = 0; i < literalSnake[0][0].length; i ++){ //Recorrido de una serpiente

            for (int j = 0; j < literalSnake.length; j ++) { //Recorrido de la posicion "x" de la primera serpiente

                for (int k = 0; k < literalSnake[0].length; k ++) { //Recorrido de la posicion "y" de la primera serpiente

                    for (int l = 0; l < literalSnake[0][0].length; l ++) { //Recorrido de otra serpiente de la seguna serpiente

                        if (l != i) { //Comprobacion de que la segunda serpiente sea distinta a la primera

                            for (int m = 0; m < literalSnake[0].length; m ++) { //Recorrido de la posicion "y" de la seguna serpiente

                                IntVec clause = new IntVec (satWrapper.pool);

                                //Una serpiente no puede estar en la misma fila que otra distinta

                                clause.add (-literalSnake[j][k][i]); //primera serpiente en la posición j(x), k(y), i(primera serpiente)

                                clause.add (-literalSnake[j][m][l]); //primera serpiente en la posición j(x), m(y), l(segunda serpiente)

                                satWrapper.addModelClause (clause.toArray()); //añadir
                            }
                        }
                    }
                }
            }
        }
    }


    public static void deployAlSnakes (SatWrapper satWrapper, int literalSnake[][][], int literalProtagonist[][]) {

        for (int i = 0; i < literalSnake[0][0].length; i++) { //Recorrido de una serpiente

            for (int j = 0; j < literalSnake.length; j++) { //Recorrido de la posicion "x" de la primera serpiente

                //Recorrido de la posicion "y" de la primera serpiente
                for (int k = 0; k < literalSnake[0].length; k++) {

                    for (int l = 0; l < literalSnake[0][0].length; l++) { //Recorrido de otra serpiente de la seguna serpiente

                        IntVec clause1 = new IntVec(satWrapper.pool);

                        //una serpiente no puede estar ni en la misma fila ni columna que el protagonista

                        clause1.add(-literalSnake[j][k][l]);

                        clause1.add(-literalProtagonist[j][k]);

                        satWrapper.addModelClause(clause1.toArray());
                    }
                }
            }
        }
    }
}
