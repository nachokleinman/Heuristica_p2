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


    public static void main (String[] args) throws IOException {

        System.out.println("Inicio");


        //Fichero captado de los argumentos
        File file = new File(args[0]);


        //Lectura del fichero que es guardado en una matriz
        int initMap[][] = readFile(file.getPath());

        System.out.println("lectura del fichero2");

        showMatrix(initMap);




        //Numero de serpientes pasadas por parametro
        int numSnakes = Integer.parseInt(args[1]);

        //Numero de filas
        int row = initMap.length;

        //Numero de columnas en la fila cero
        int col = initMap[0].length;



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

                    // Registramos las variables en el sat wrapper
                    satWrapper.register (protagonist [i][j]);

                    // Obtenemos los literales no negados de las variables
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

                        //Registramos las variables en el sat wrapper
                        satWrapper.register(snake[i][j][k]);

                        //Obtenemos los literales no negados de las variables
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

        System.out.println("restriccion serpiente");

        // 3. No puede haber ninguna serpiente ni en la misma fila ni en la misma columna que Al.
        deployAlSnakes (satWrapper, literalSnake, literalProtagonist);


        //Restricciones implicitas


        // 1. Al solo puede aparecer una vez
        deployProtagonistOnce (satWrapper, literalProtagonist);

        // 3. Cada serpiente solo puede aparecer una vez

        for (int i = 0; i < numSnakes; i ++) {

            deploySnakeOnce (satWrapper, literalSnake, i);
        }


        System.out.println("resolucion");

        /* Resolvemos el problema */
        Search <BooleanVar> search = new DepthFirstSearch<>();

        SelectChoicePoint <BooleanVar> select = new SimpleSelect<>(allVariables, new SmallestDomain<>(), new IndomainMin<>());

        Boolean result = search.labeling (store, select);

        if (result) {

            System.out.println ("Solution: ");

            for (BooleanVar[] aProtagonist : protagonist) {

                for (int j = 0; j < protagonist[0].length; j++) {

                    if (aProtagonist[j] != null) {

                        if (aProtagonist[j].dom().value() == 1) {

                            System.out.println(aProtagonist[j].id());
                        }
                    }
                }
            }

            for (BooleanVar[][] aSnake : snake) {

                for (int j = 0; j < snake[0].length; j++) {

                    for (int k = 0; k < snake[0][0].length; k++) {

                        if (aSnake[j][k] != null) {

                            if (aSnake[j][k].dom().value() == 1) {

                                System.out.println(aSnake[j][k].id());
                            }
                        }
                    }
                }
            }
        } else System.out.println ("Error");

        System.out.println("resolucion hecha");

        writeFile (initMap, args[0]);
        System.out.println("impresion: ");

        showMatrix(initMap);

        //end of the main method

    }




    private static void showMatrix(int resultMap[][]) {

        for (int[] aResultMap : resultMap) {

            for (int j = 0; j < resultMap[0].length; j++) {

                System.out.print((char) aResultMap[j]);
            }

            System.out.println();
        }
    }


    private static int[][] readFile (String file) throws IOException {

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

            System.out.println ("Unable to open file '" + fileReader + "'");

            fileReader.close();

        } catch (IOException e) {

            System.out.println ("Error reading file '" + fileReader + "'");

            fileReader.close();

        }

        fileReader.close();

        FileReader fileReader2 = new FileReader(file);

        int reading = fileReader2.read();

        int matrix [][] = new int [row] [col];

        for (int i = 0; i < row; i ++) {

            for (int j =0; j < col; j ++) {

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

        String d = file.concat (".output");

        FileWriter fileWriter = new FileWriter (d);

        BufferedWriter bufferWriter = new BufferedWriter (fileWriter);

        PrintWriter writer = new PrintWriter (bufferWriter);

        for (int[] aMatrix : matrix) {

            for (int j = 0; j < matrix[0].length; j++) {

                writer.write(aMatrix[j]);
            }
            writer.write("\n");
        }
        writer.close();
    }

    private static void deployProtagonistOnce(SatWrapper satWrapper, int literal[][]) {

        IntVec clause = new IntVec (satWrapper.pool);

        for (int[] aLiteral : literal) {

            for (int j = 0; j < literal[0].length; j++) {

                if (aLiteral[j] != 0) {

                    clause.add(aLiteral[j]);
                }
            }
        }
        satWrapper.addModelClause(clause.toArray());
    }

    private static void deploySnakeOnce(SatWrapper satWrapper, int literal[][][], int k){

        IntVec clause = new IntVec(satWrapper.pool);

        for (int[][] aLiteral : literal) {

            for (int j = 0; j < literal[0].length; j++) {

                if (aLiteral[j][k] != 0) {

                    clause.add(aLiteral[j][k]);
                }
            }
        }
        satWrapper.addModelClause(clause.toArray());
    }


    private static void snakesForRow(SatWrapper satWrapper, int literalSnake[][][]) {

        for (int i = 0; i < literalSnake[0][0].length; i ++){ //Recorrido de una serpiente

            for (int[][] aLiteralSnake : literalSnake) { //Recorrido de la posicion "x" de la primera serpiente

                for (int k = 0; k < literalSnake[0].length; k++) { //Recorrido de la posicion "y" de la primera serpiente

                    for (int l = 0; l < literalSnake[0][0].length; l++) { //Recorrido de otra serpiente de la seguna serpiente

                        if (l != i) { //Comprobacion de que la segunda serpiente sea distinta a la primera

                            for (int m = 0; m < literalSnake[0].length; m++) { //Recorrido de la posicion "y" de la seguna serpiente

                                IntVec clause = new IntVec(satWrapper.pool);

                                //Una serpiente no puede estar en la misma fila que otra distinta

                                clause.add(-aLiteralSnake[k][i]); //primera serpiente en la posición j(x), k(y), i(primera serpiente)

                                clause.add(-aLiteralSnake[m][l]); //primera serpiente en la posición j(x), m(y), l(segunda serpiente)

                                satWrapper.addModelClause(clause.toArray()); //añadir
                            }
                        }
                    }
                }
            }
        }
    }


    private static void deployAlSnakes(SatWrapper satWrapper, int literalSnake[][][], int literalProtagonist[][]) {

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
