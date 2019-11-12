import java.io.*;
import java.util.*;

public class AstarPaganitzu {

    //Variables final que pueden ser utilizadas en programa
    public static final int blanco = 32;
    public static final int muro = 37;
    public static final int al = 65;
    public static final int salida = 69;
    public static final int llave = 75;
    public static final int roca = 79;
    public static final int serpiente = 83;

    //
    public static final int Coste_Celda_Roca = 4;
    public static final int Coste_Celda_Vacia = 2;


    //Posiciones de incio y final de nodos
    static int startI, startJ;
    static int endI, endJ;

    //Variables globales pertinentes a la heuristica
    static int costeTotal = 0;
    static int distancia = 0;
    static int nodos_Expandidos = 0;
    static int contador = 0;

    //
    static String heuristicaIntroducida;

	//Cola abierta y cerrada para los nodos
    static Nodo[][] grid = new Nodo[0][0];
    static PriorityQueue<Nodo> open;
    static boolean closed[][];

    public static void main(String[] args) throws IOException {

		//tratamiento de errores para los parametros
        if(args.length !=2){
            System.out.println("ERROR:el numero de parametros introducidos es incorrecto!!");
            return;
        }

        //creamos un fichero de prueba para analizar el fichero de entrada
        File fichero = new File(args[0]);
        //leemos el fichero de entrada
        int prueba[][] = readFile(fichero.getPath());

        //vemos cuales son sus dimensiones
        int dimensionFila, dimensionColumna;

        //si la matriz no tiene ninguna dimension, devuelve error
        if(prueba.length==0){

            //informamos del tipo de error
            System.out.println("ERROR:las dimensiones de la matriz no pueden ser cero!!");
            return;
        }

        //Lectura del fichero y guardado de este en una matriz

        int mapa[][] = readFile(args[0]);


        contador++;

        String heuristicaEntrante = args[1];

        //Inicializacion las variables

        long tiempo_inicial, tiempo_final;
		
		
		//leemos la heuristica introducida desde argumentos 1 y la tratamos para el problema
        heuristicaIntroducida = funcionHeuristica(heuristicaEntrante);

        int resultadoParcial = 999999;

        int filas = mapa.length;
        int columnas = mapa[0].length;
        char solucionMapa[][] = new char[filas][columnas];
        char matrixAux[][]= new char [filas][columnas];


        int llaves = 0;
        int contador_protagonista = 0;

        //recorremos la matriz de inicio y contabilizamos el numero de llaves

        for (int i = 0; i < filas; i++) {

            for (int j = 0; j < columnas; j++) {

                if (mapa[i][j] == llave) {

                    llaves++;
                }
                if (mapa[i][j] == al) contador_protagonista++;

                if (mapa[i][j] != llave && mapa[i][j] != al && mapa[i][j] != muro && mapa[i][j] != roca && mapa[i][j] != serpiente && mapa[i][j] != salida && mapa[i][j] != blanco) {
                    System.out.println("ERROR: HAY UN INTRUSO EN EL MAPA");
                    return;
                }
            }
        }

		//tratamiento de error para el numero de protagonistas
        if (contador_protagonista != 1) {
            System.out.println("ERROR: HAY UN FALLO CON EL NUMERO DE PROTAGONISTAS EXISTENTES EN EL MAPA");
            return;
        }

        //recorremos la matriz inicial y la copiamos en la matriz solucion en formato char

        for (int i = 0; i < filas; i++) {

            for (int j = 0; j < columnas; j++) {

                solucionMapa[i][j] = (char) mapa[i][j];
                matrixAux[i][j] = (char) mapa[i][j];


            }
        }

        //Inicio del cronometro

        tiempo_inicial = System.currentTimeMillis();


        for (int i = 1; i <= llaves + 1; i++) {

            // showMatrix2(solucionMapa);

            System.out.println("AL está en: ");

            //Posicion de inicio de Al
            inicioNodo(solucionMapa);


            //Fijamos la posicion de la llave objetivo, calculo de la heuristica a esa llave y nodo final
            finalNodo(solucionMapa, resultadoParcial);

            //ejecucion de la solucion
            solucionMapa = ejecucion(solucionMapa, matrixAux);

            if (solucionMapa == null) return;


            writeFile1(mapa, args[0], matrixAux);


            contador++;
        }


        if (solucionMapa != null) System.out.println("Enhorabuena, camino encontrado!!!");


        //Fin tiempo de ejecucion
        tiempo_final = System.currentTimeMillis();

        //Estadisitcas de  ejecucion
        writeStatics(tiempo_inicial, tiempo_final, args[0]);


    }


    //Resto de Metodos


    //Comprueba de que heuristica se trata

    public static String funcionHeuristica(String heuristica) {

        if (heuristica.equals("Manhattan")) {

            heuristica = "heuristicaManhattan";
            return heuristica;

        } else if (heuristica.equals("Euclidea")) {

            heuristica = "heuristicaEuclidea";
            return heuristica;

        } else {

            System.out.println("Error: no se ha introducido ninguna heuristica");
            return null;
        }
    }

    //Distancia manhattan

    public static int heuristicaManhattan(int i, int j, int endi, int endj) {

        int Hi = Math.abs(endi - i) * 2;
        int Hj = Math.abs(endj - j) * 2;
        return Hi + Hj;

    }

    //Distancia Euclidea
    public static int heuristicaEuclidea(int i, int j, int endi, int endj) {

        int Hi = Math.abs(endi - i) * 2;
        int Hj = Math.abs(endj - j) * 2;
        Hi = (int) Math.pow(Hi, 2);
        Hj = (int) Math.pow(Hj, 2);
        return (int) Math.sqrt(Hi + Hj);

    }

    //Algoritmo de busqueda A*

    public static void aStar(char[][] solucionMatriz) {


        //Añade la posicion inicial al principio de la lista abierta

        open.add(grid[startI][startJ]);

        System.out.println("Nodo inicial i : " + startI + ", J: " + startJ);
        System.out.println("Nodo final i : " + endI + ", J: " + endJ);

        Nodo actual;

        astarLoop:
        while (true) {
            actual = open.poll();
			//si algun nodo es null (%) entonces, salimos del bucle
            if (actual == null) {
                break;
            }
			
			//en la matriz de boolean ponemos a true los que vayan entrando
            closed[actual.i][actual.j] = true;

			//cuando llegamos al final, salimos
            if (actual.equals(grid[endI][endJ])) {
                return;
            }


            //Identificar celdas contiguas y actualizar costes

            Nodo comparado;

            /** Retrocedemos una fila **/

            if (actual.i - 1 < grid.length) {

                comparado = grid[actual.i - 1][actual.j];

                //La celda que analizamos o esta en blanco o hay una llave
                if (solucionMatriz[actual.i - 1][actual.j] == blanco || solucionMatriz[actual.i - 1][actual.j] == solucionMatriz[endI][endJ]) {
					//miramos en la fila por la izquierda
                    for (int k = actual.j; k >= 0; k--) {
						//Si encontramos roca o muro podemos pasar
                        if (solucionMatriz[actual.i - 1][k] == roca || solucionMatriz[actual.i - 1][k] == muro) break;
						//si hay serpiente entonces salimos
                        else if (solucionMatriz[actual.i - 1][k] == serpiente) continue astarLoop;
                    }
					//miramos en la fila por la derecha
                    for (int k = actual.j; k < solucionMatriz[0].length; k++) {
                        if (solucionMatriz[actual.i - 1][k] == roca || solucionMatriz[actual.i - 1][k] == muro) break;
                        else if (solucionMatriz[actual.i - 1][k] == serpiente) continue astarLoop;
                    }
					//actualizamos costes
                    checkAndUpdateCost(actual, comparado, actual.finalCost + Coste_Celda_Vacia);
                }
            }
			
            /** Retrocedemos una columna **/
            if (actual.j - 1 < grid[0].length) {
                comparado = grid[actual.i][actual.j - 1];
				//si encontramos una casilla en blanco o una llave
                if (solucionMatriz[actual.i][actual.j - 1] == blanco || solucionMatriz[actual.i][actual.j - 1] == solucionMatriz[endI][endJ]) {
                    checkAndUpdateCost(actual, comparado, actual.finalCost + Coste_Celda_Vacia);
					//si hay una roca y despues un blanco podemos mover la roca
                } else if (solucionMatriz[actual.i][actual.j - 1] == roca) {
                    if (solucionMatriz[actual.i][actual.j - 2] == blanco) {
                        checkAndUpdateCost(actual, comparado, actual.finalCost + Coste_Celda_Roca);
                    }
                }
				//en caso de encontrarse una salida
                if (solucionMatriz[actual.i][actual.j - 1] == salida) {
                    checkAndUpdateCost(actual, comparado, actual.finalCost + Coste_Celda_Vacia);
                }

            }
			
            /** Avanzamos una fila **/
            if (actual.i + 1 < grid.length) {
                comparado = grid[actual.i + 1][actual.j];
                //La celda que analizamos o esta en blanco o hay una llave
                if (solucionMatriz[actual.i + 1][actual.j] == blanco || solucionMatriz[actual.i + 1][actual.j] == solucionMatriz[endI][endJ]) {
					//miramos por la izquierda
                    for (int k = actual.j; k >= 0; k--) {
                        if (solucionMatriz[actual.i + 1][k] == roca || solucionMatriz[actual.i + 1][k] == muro) break;
                        else if (solucionMatriz[actual.i + 1][k] == serpiente) continue astarLoop;
                    }
					//miramos por la derehca
                    for (int k = actual.j; k < solucionMatriz[0].length; k++) {
                        if (solucionMatriz[actual.i + 1][k] == roca || solucionMatriz[actual.i + 1][k] == muro) break;
                        else if (solucionMatriz[actual.i + 1][k] == serpiente) continue astarLoop;
                    }
					//actualizamos costes
                    checkAndUpdateCost(actual, comparado, actual.finalCost + Coste_Celda_Vacia);
                }
            }


            /** Avanzamos una columna **/
            if (actual.j + 1 < grid[0].length) {
                comparado = grid[actual.i][actual.j + 1];
				//si hay blanco o llave podemos ir con un coste de vacio (2)
                if (solucionMatriz[actual.i][actual.j + 1] == blanco || solucionMatriz[actual.i][actual.j + 1] == solucionMatriz[endI][endJ]) {
                    checkAndUpdateCost(actual, comparado, actual.finalCost + Coste_Celda_Vacia);
					//si hay roca miramos en la segunda casilla si esta libre para mover roca
                } else if (solucionMatriz[actual.i][actual.j + 1] == roca) {
                    if (solucionMatriz[actual.i][actual.j + 2] == blanco) {
                        checkAndUpdateCost(actual, comparado, actual.finalCost + Coste_Celda_Roca);
                    }
                }
                //comprobamos la salida
                if (solucionMatriz[actual.i][actual.j + 1] == salida) {
                    checkAndUpdateCost(actual, comparado, actual.finalCost + Coste_Celda_Vacia);
                }
            }
        }
    }


	//método que cordina la ejecuci´´on de los nodos y el A*
    public static char[][] ejecucion(char[][] solucionMatriz, char matrixAux[][]) {

        int filas = solucionMatriz.length;
        int columnas = solucionMatriz[0].length;
		
        //Inicializamos el grid de tipo celda con el tamaño de la matriz solucion
        grid = new Nodo[filas][columnas];

        //Inicializamos la lista abierta y cerrada
        closed = new boolean[filas][columnas];
		
		//ordenamos los nodos abiertos por costes
        open = new PriorityQueue<>((Object o1, Object o2) -> {

            Nodo c1 = (Nodo) o1;
            Nodo c2 = (Nodo) o2;
			//los nodos más costosos se ponen detras de los menos costosos
            return c1.finalCost - c2.finalCost;
        });

        //Es asiganado el coste heuristico de cada celda
        for (int i = 0; i < filas; ++i) {
            for (int j = 0; j < columnas; ++j) {
                grid[i][j] = new Nodo(i, j);

                //asignar el coste heuristico al nodo
                if (heuristicaIntroducida.equals("heuristicaManhattan")) {
                    grid[i][j].heuristicCost = heuristicaManhattan(i, j, endI, endJ);
                }
                if (heuristicaIntroducida.equals("heuristicaEuclidea")) {
                    grid[i][j].heuristicCost = heuristicaEuclidea(i, j, endI, endJ);
                }
            }
        }

        //el coste del nodo inicial es cero
        grid[startI][startJ].finalCost = 0;

        //Las celdas que son muros no son consideradas, se bloquean
        celdaMuro(solucionMatriz);

        //Busqueda Heuristica A*
        aStar(solucionMatriz);

        //Unicamente si la celda objetivo esta en la lista cerrada se empieza a generar la matriz solucion
        if (closed[endI][endJ]) {

            Nodo current;
			
            for (int i = 0; i < filas; ++i) {
                for (int j = 0; j < columnas; ++j) {
                    current = grid[endI][endJ];

                    while (current.parent != null) {
						
                        if (i == current.parent.i && j == current.parent.j) {
							//guardamos el recorrido de AL
                            matrixAux[i][j]=88;
                            j++;
                            distancia = distancia + 1;

                            current = grid[endI][endJ];

                        } else {
                            current = current.parent;
                        }
                    }

                    if (i == endI && j == endJ) {
                        solucionMatriz[endI][endJ] = al;
                        solucionMatriz[startI][startJ] = blanco;
                        distancia = distancia + 1;
                    }
                }
            }
            return solucionMatriz;

        } else {
            System.out.println("No existe camino viable");
            return null;
        }
    }

    //Establecer la posiicion de inicio de Al
    public static void inicioNodo(char[][] solucionMatriz) {

        for (int i = 0; i < solucionMatriz.length; i++) {
            for (int j = 0; j < solucionMatriz[0].length; j++) {
                if (solucionMatriz[i][j] == al) {
                    startI = i;
                    startJ = j;
                }
            }
        }
    }

    /* Coste heuristico del camino desde Al hasta cada una de las llaves. Objetivo: Escoger la llave con menor coste*/

    public static void finalNodo(char[][] solucionMatriz, int resultadoParcial) {
        int resultado = 0;
        for (int i = 0; i < solucionMatriz.length; i++) {
            for (int j = 0; j < solucionMatriz[0].length; j++) {

                //Si se encuentra una llave
                if (solucionMatriz[i][j] == llave) {
                    resultado = 0;

                    if (heuristicaIntroducida.equals("heuristicaManhattan")) {
                        resultado = heuristicaManhattan(startI, startJ, i, j);
                    }

                    if (heuristicaIntroducida.equals("heuristicaEuclidea")) {
                        resultado = heuristicaEuclidea(startI, startJ, i, j);
                    }
                    
                    if (resultado < resultadoParcial) {

                        endI = i;
                        endJ = j;

                        resultadoParcial = resultado;
                    }
                }
            }
        }
        if (resultado == 0) {
            for (int i = 0; i < solucionMatriz.length; i++) {

                for (int j = 0; j < solucionMatriz[0].length; j++) {

                    if (solucionMatriz[i][j] == salida) {
                        resultado = 0;

                        if (heuristicaIntroducida.equals("heuristicaManhattan")) {
                            resultado = heuristicaManhattan(startI, startJ, i, j);      
                        }

                        if (heuristicaIntroducida.equals("heuristicaEuclidea")) {
                            resultado = heuristicaEuclidea(startI, startJ, i, j);
                        }

                        if (resultado < resultadoParcial) {

                            endI = i;
                            endJ = j;
                            resultadoParcial = resultado;
                        }
                    }
                }
            }
        }
    }

	//si hay muro cancelamos esa celda
    public static void celdaMuro(char[][] matrix_solution) {

        for (int i = 0; i < matrix_solution.length; i++) {

            for (int j = 0; j < matrix_solution[0].length; j++) {

                //si la celda contiene un muro (%) entonces se desestima
                if (matrix_solution[i][j] == muro) {
                    grid[i][j] = null;
                }
            }
        }

    }


    /* Actualiza los costes de cada una de las celdas usando su coste heuristico y teniendo
     en cuenta el coste que supone pasar por cada tipo de celda */

    static void checkAndUpdateCost(Nodo actual, Nodo t, int coste) {

        if (t == null || closed[t.i][t.j]) {
            return;
        }

        int t_final_cost = t.heuristicCost + coste;

        boolean inOpen = open.contains(t);

        if (!inOpen || t_final_cost < t.finalCost) {

            if (actual.finalCost == 0) {
                t.finalCost = t_final_cost;
            } else {
                t.finalCost = t_final_cost - actual.heuristicCost;
            }

            t.parent = actual;

            nodos_Expandidos++;

            if (t.heuristicCost == 0) {
                costeTotal = costeTotal + t.finalCost;
            }

            if (!inOpen) {
                open.add(t);
            }
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

    private static void writeFile1(int matrix[][], String file,char matrixAux[][]) throws IOException {

        PrintWriter writer = new PrintWriter(new FileWriter(file + ".output")); //creamos fichero de salida e imprimimos un mensaje por pantalla del nombre del mismo

        writer.write("Mapa Original:\n");


        for (int i = 0; i < matrix.length; i++) { //recorremos las filas de la matriz

            for (int j = 0; j < matrix[0].length; j++) { //recorremos las columnas de la matriz

                writer.write(matrix[i][j]); //escribimos en el fcihero el valor de la matriz en la posicion i,j

            }
            writer.write("\n"); // escrinimos el salto de linea para que nos escriba el mapa en el formato deseado
        }


        writer.write("\nCAMINO: \n");

        writer.write("( " +endI+" , "+endJ+") -->");


        for (int i = 0 ; i < matrix.length ; i++) { //recorremos las filas de la matriz

            for (int j = 0; j <  matrix[0].length ; j++) { //recorremos las columnas de la matriz

                if (matrixAux[i][j]==88){
                    writer.write("( " +i+" , "+j+") -->");
                }
            }

        }


        writer.close();
    }
    //Estadisticas de ejecucion para mostrar tiempos y nodos asi como la longitud

    public static void writeStatics(long tiempoInicial, long tiempoFinal, String file) throws IOException {
	
		//creamos fichero de salida e imprimimos un mensaje por pantalla del nombre del mismo
	   PrintWriter writer = new PrintWriter(new FileWriter(file + ".statistics")); 
	   
        writer.write("Tiempo total: " + (tiempoFinal - tiempoInicial) + " ms\n");
        writer.write("Coste total: " + costeTotal + "\n");
        writer.write("Longitud de la ruta: " + distancia + "\n");
        writer.write("Nodos expandidos: " + nodos_Expandidos + "\n");

        writer.close();
    }
}
