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

        int literalSnake [][]= new int [row][col];

        for (int i = 0; i < snake.length; i ++) {

            for (int j = 0; j < snake[0].length; j ++) {

                for (int k = 0; k < numSnakes; k ++) {

                    if (snake [i][j][k] != null) {

                        allVariables[allVar] = snake[i][j][k];

                        /* Registramos las variables en el sat wrapper */
                        satWrapper.register(snake[i][j][k]);

                        /* Obtenemos los literales no negados de las variables */
                        literalSnake[i][j] = satWrapper.cpVarToBoolVar(snake[i][j][k], 1, true);
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
            2. Una serpiente no puede estar en la misma fila que otra serpiente.
            3. No puede haber ninguna serpiente ni en la misma fila ni en la misma columna que Al.

        Restricciones implicitas

            1. Al solo puede aparecer una vez
            2. Es obligatorio que Al aparezca
            3. Cada serpiente solo puede aparecer una vez
            4. Es obligatorio que aparezcan todas las serpientes
            5. No puede haber mas serpientes que filas

        */






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
}
