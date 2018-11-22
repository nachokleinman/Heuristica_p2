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

        System.out.println("Inicio");

        //Fichero captado de los argumentos
        File file = new File(args[0]);

        //Lectura del fichero que es guardado en una matriz
        int initMap[][] = readFile(file.getPath());

        System.out.println("Lectura del fichero:");

        showMatrix(initMap);

        //Numero de serpientes pasadas por parametro
        int numSnakes = Integer.parseInt(args[1]);

        System.out.println("Numero de serpientes a meter: " + numSnakes + "\n");


        //Numero de filas
        int row = initMap.length;

        //Numero de columnas en la fila cero
        int col = initMap[0].length;

        System.out.println("filas" + row);
        System.out.println("columnas" + col);

        int espacioBlanco = 0;

        for (int i = 0; i < initMap.length; i++) {

            for (int j = 0; j < initMap[0].length; j++) {

                if (initMap[i][j] == 32) espacioBlanco++;
            }
        }
        System.out.println("Numero de espacios en blanco: " + espacioBlanco);

        Store store = new Store();

        SatWrapper satWrapper = new SatWrapper();

        store.impose(satWrapper);   /* Importante: sat problem */


        //Contadores de movimiento de las variables

        int counterProtagonist = 0, counterSnake = 0;

        //Creacion de las variables booleanas. Una para protagonista y otra para las serpientes

        BooleanVar protagonist[][] = new BooleanVar[row][col];

        BooleanVar snake[][][] = new BooleanVar[row][col][numSnakes];

        for (int i = 0; i < initMap.length; i++) {

            for (int j = 0; j < initMap[0].length; j++) {


                if (initMap[i][j] == 32) { //si está en espacio en blanco

                    protagonist[i][j] = new BooleanVar(store, "Protagonista en la posición: " + i + ", " + j);


                    counterProtagonist++;
                }

                for (int k = 0; k < numSnakes; k++) {

                    if (initMap[i][j] == 32) {

                        snake[i][j][k] = new BooleanVar(store, "Serpiente " + k + " en la posición " + i + ", " + j);

                        counterSnake++;

                    }
                }

            }
        }

        //Todas las variables: es necesario para el SimpleSelect

        int size = counterProtagonist + counterSnake;

        int allVar = 0;

        BooleanVar[] allVariables = new BooleanVar[size];

        int literalProtagonist[][] = new int[row][col];

        for (int i = 0; i < protagonist.length; i++) {

            for (int j = 0; j < protagonist[0].length; j++) {

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

        int literalSnake[][][] = new int[row][col][numSnakes];

        for (int i = 0; i < snake.length; i++) {

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

            1. Al y las serpientes solo se pueden colocar en celdas vacias.
            2. Una serpiente no puede estar en la misma fila que otra serpiente. -> DONE
            3. No puede haber ninguna serpiente ni en la misma fila ni en la misma columna que Al. ->

        Restricciones implicitas

            1. Al solo puede aparecer una vez -> DONE
            2. Es obligatorio que Al aparezca ->
            3. Cada serpiente solo puede aparecer una vez -> DONE
            4. Es obligatorio que aparezcan todas las serpientes
            5. No puede haber mas serpientes que filas

        */


        deployProtagonistOnce(satWrapper, literalProtagonist);


       for (int i = 0; i < numSnakes; i++) {

            deploySnakeOnce(satWrapper, literalSnake, i);
        }

        snakesForRow(satWrapper, literalSnake, literalProtagonist);


        differentColFil (satWrapper, literalSnake, literalProtagonist);


        System.out.println("Resolucion...");

        /* Resolvemos el problema */

        Search<BooleanVar> search = new DepthFirstSearch<>();

        SelectChoicePoint<BooleanVar> select = new SimpleSelect<>(allVariables, new SmallestDomain<>(), new IndomainMin<>());

        Boolean result = search.labeling(store, select);


        if (result) {

            System.out.println("Protagonist: ");

            for (int i = 0; i < protagonist.length; i++) {

                for (int j = 0; j < protagonist[0].length; j++) {

                    if (protagonist[i][j] != null) {

                        if (protagonist[i][j].dom().value() == 1) {

                            System.out.println(protagonist[i][j].id());

                            initMap[i][j] = 65; //Colocar una 'A' (ASCII) para representar a Al
                        }

                    }
                }
            }

            System.out.println("snakes: ");

            for (int i = 0; i < snake.length; i++) {

                for (int j = 0; j < snake[0].length; j++) {

                    for (int k = 0; k < snake[0][0].length; k++) {

                        if (snake[i][j][k] != null) {

                            if (snake[i][j][k].dom().value() == 1) {

                                System.out.println(snake[i][j][k].id());

                                initMap[i][j] = 83; //Colocar una 'S' (ASCII) por cada serpiente
                            }


                        }
                    }
                }
            }
        } else System.out.println("Error");

        System.out.println("resolucion hecha");


        System.out.println("creación del fichero: \n");

        writeFile(initMap, args[0]);

        showMatrix(initMap);

        espacioBlanco = 0;

        for (int i = 0; i < initMap.length; i++) {

            for (int j = 0; j < initMap[0].length; j++) {

                if (initMap[i][j] == 32) espacioBlanco++;
            }
        }

        System.out.println("Numero de espacios en blanco: " + espacioBlanco);
    }

    //FIN DEL METODO MAIN


    public static void differentColFil(SatWrapper satWrapper, int literalSnake[][][], int literalProtagonist[][]){

        for (int j = 0; j < literalSnake.length; j++) {

            for (int k = 0; k < literalSnake[0].length; k++) {

                for (int l = 0; l < literalProtagonist.length; l++) {

                    for (int m = 0; m < literalProtagonist[0].length; m++) {

                        for (int i = 0; i < literalSnake[0][0].length; i++) {

                            if (k != m ) {

                                IntVec clause = new IntVec(satWrapper.pool);

                                clause.add(-literalSnake[j][k][i]);

                                clause.add(-literalProtagonist[j][m]);

                                satWrapper.addModelClause(clause.toArray());

                            }

                            if (l != j ) {

                                IntVec clause2 = new IntVec(satWrapper.pool);

                                clause2.add(-literalSnake[j][k][i]);

                                clause2.add(-literalProtagonist[j][k]);

                                satWrapper.addModelClause(clause2.toArray());

                            }
                        }

                    }
                }
            }
        }

    }


    private static void deployProtagonistOnce(SatWrapper satWrapper, int literalProtagonist[][]) {

        IntVec clause = new IntVec(satWrapper.pool);

        for (int i = 0; i < literalProtagonist.length; i++) {

            for (int j = 0; j < literalProtagonist[0].length; j++) {

                    if (literalProtagonist[i][j] != 0) {

                        clause.add(literalProtagonist[i][j]);
                        satWrapper.addModelClause(clause.toArray());

                }
            }
        }
    }

    private static void deploySnakeOnce(SatWrapper satWrapper, int literalSnake[][][], int k) {

        IntVec clause = new IntVec(satWrapper.pool);

        for (int[][] aLiteral : literalSnake) {

            for (int j = 0; j < literalSnake[0].length; j++) {

                if (aLiteral[j][k] != 0) {

                    clause.add(aLiteral[j][k]);
                }
            }
        }
        satWrapper.addModelClause(clause.toArray());

    }


    private static void snakesForRow(SatWrapper satWrapper, int literalSnake[][][], int literalProtagonist[][]) {

        for (int i=0; i < literalSnake[0][0].length; i ++) {

            for (int j=0; j < literalSnake.length; j ++){

                for (int k=0; k < literalSnake[0].length; k ++) {

                    for (int m=0; m < literalSnake[0][0].length; m ++) {

                        if (i != m) {

                            for (int l = 0; l < literalSnake[0].length; l++) {
                                IntVec clause = new IntVec(satWrapper.pool);

                                clause.add(-literalSnake[j][k][i]);
                                clause.add(-literalSnake[j][l][m]);

                                satWrapper.addModelClause(clause.toArray());
                            }
                        }
                    }
                    IntVec clause2 = new IntVec(satWrapper.pool);

                    clause2.add(-literalProtagonist[j][k]);
                    clause2.add(-literalSnake[j][k][i]);

                    satWrapper.addModelClause(clause2.toArray());
                }
            }
        }
    }




    private static void showMatrix(int resultMap[][]) {

        for (int[] aResultMap : resultMap) {

            for (int j = 0; j < resultMap[0].length; j++) {

                System.out.print((char) aResultMap[j]);
            }

            System.out.println();
        }
    }


    private static int[][] readFile(String file) throws IOException {

        FileReader fileReader = new FileReader(file);

        int row = 0, col = 0, counter = 0, readed = fileReader.read();

        try {
            while (readed != -1) {
                if (readed == 10) {
                    row++;
                    col = counter;
                    counter = 0;
                } else {
                    counter++;
                }
                readed = fileReader.read();
            }
        } catch (FileNotFoundException e) {

            System.out.println("Unable to open file '" + fileReader + "'");

            fileReader.close();

        } catch (IOException e) {

            System.out.println("Error reading file '" + fileReader + "'");

            fileReader.close();

        }

        fileReader.close();

        FileReader fileReader2 = new FileReader(file);

        int reading = fileReader2.read();

        int matrix[][] = new int[row][col];

        for (int i = 0; i < row; i++) {

            for (int j = 0; j < col; j++) {

                if (reading == -1) {
                    break;
                }

                if (reading == 10) {

                    reading = fileReader2.read();
                }

                matrix[i][j] = reading;

                reading = fileReader2.read();
            }
        }

        fileReader2.close();

        return matrix;
    }

    private static void writeFile(int[][] matrix, String file) throws IOException {

        PrintWriter writer = new PrintWriter(new FileWriter(file + ".output"));

        for (int i = 0; i < matrix.length; i++) {

            for (int j = 0; j < matrix[0].length; j++) {

                writer.write(matrix[i][j]);

            }
            writer.write("\n");
        }

        writer.close();

    }
}
