package parte1;

import java.io.*;
import java.util.*;

public class AstarPaganitzuExtra {

	    static int startI, startJ;
	    static int endI, endJ;
	    static int costeTotal = 0;
	    static int distancia = 0;
	    static int nodos_Expandidos = 0;
	    static int contador = 0;

	    static String heuristicaIntroducida;

	    public static final int Coste_Celda_Roca = 4;
	    public static final int Coste_Celda_Vacia = 2;

	    public static final int blanco = 32;
	    public static final int muro = 37;
	    public static final int al = 65;
	    public static final int salida = 69;
	    public static final int llave = 75;
	    public static final int roca = 79;
	    public static final int serpiente = 83;


	    static Nodo[][] grid = new Nodo[0][0];
	    static PriorityQueue<Nodo> open;
	    static boolean closed[][];

	    public static void printClosed(boolean[][] closed) {
	        System.out.println();
	        for (boolean[] arr : closed) {
	            for (boolean bool : arr) {
	                System.out.print(bool ? "·" : "X");
	                System.out.print(" ");
	            }
	            System.out.println();
	        }
	        System.out.println();
	    }


	    public static void main(String[] args) throws IOException {


	        //Lectura del fichero y guardado de este en una matriz

	        int mapa[][] = readFile(args[0]);

	        writeFile1(mapa, args[0]);
	        contador++;

	        String heuristicaEntrante = args[1];

	        //Inicializacion las variables

	        long tiempo_inicial, tiempo_final;

	        heuristicaIntroducida = funcionHeuristica(heuristicaEntrante);


	        int resultadoParcial = 999999;

	        int filas = mapa.length;
	        int columnas = mapa[0].length;
	        char solucionMapa[][] = new char[filas][columnas];

	        int llaves = 0;
	        int rocas = 0;


	        //recorremos la matriz de inicio y contabilizamos el numero de llaves

	        for (int i = 0; i < filas; i++) {

	            for (int j = 0; j < columnas; j++) {

	                if (mapa[i][j] == llave) {
	                    llaves++;
	                }
	            }
	        }

	        //recorremos la matriz inicial y la copiamos en la matriz solucion en formato char

	        for (int i = 0; i < filas; i++) {

	            for (int j = 0; j < columnas; j++) {

	                solucionMapa[i][j] = (char) mapa[i][j];
	            }
	        }

	        //Inicio del cronometro

	        tiempo_inicial = System.currentTimeMillis();


	        for (int i = 1; i <= llaves+1; i++) {

	            // showMatrix2(solucionMapa);

	            System.out.println("AL está en: ");

	            //Posicion de inicio de Al
	            inicioNodo(solucionMapa);


	            //Fijamos la posicion de la llave objetivo, calculo de la heuristica a esa llave y nodo final
	            finalNodo(solucionMapa, resultadoParcial);

	            //ejecucion de la solucion
	            solucionMapa = ejecucion(solucionMapa);
	            if(solucionMapa == null) return;

	            contador++;
	        }

	        if(solucionMapa != null) System.out.println("Enhorabuena, camino encontrado!!!");

	        //writeFile2();


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

	        int Hi = Math.abs(endi - i);
	        int Hj = Math.abs(endj - j);
	        Hi = (int) Math.pow(Hi, 2);
	        Hj = (int) Math.pow(Hj, 2);
	        return (int) Math.sqrt(Hi + Hj);

	    }

	    // ALGORITMO de busqueda A*

	    public static void aStar(char[][] solucionMatriz) {

	        //Añade la posicion inicial al principio de la lista abierta

	        open.add(grid[startI][startJ]);

	        System.out.println("Nodo inicial i : " + startI+ ", J: "+startJ);
	        System.out.println("Nodo final i : " + endI+ ", J: "+endJ);

	        Nodo actual;

	        while (true) {
	            actual = open.poll();
	            //System.out.println(actual.toStringEv());

	       if (actual == null){

	                break;
	            }

	            closed[actual.i][actual.j] = true;
	            System.out.println("CAMINO : " + actual.toStringEv());

	       if (actual.equals(grid[endI][endJ])) {
	                return;
	            }

	            //Identificar celdas contiguas y actualizar costes

	            Nodo comparado; // nos ayuda a la hora de comparar dos celdas y saber si son celdas contiguas o no lo son 

	            
	            /** Retrocedemos una FILA **/

	       if (actual.i - 1 < grid.length) {
	            	comparado = grid[actual.i - 1][actual.j];

	                //La celda que analizamos o esta en blanco o hay una llave
	            			if (solucionMatriz[actual.i - 1][actual.j] == blanco || solucionMatriz[actual.i - 1][actual.j] == solucionMatriz[endI][endJ]) {
	            					for (int k = 0; k <= solucionMatriz[0].length - 1; k++) {
	                        //si hay serpiente
	            							if (solucionMatriz[actual.i - 1][k] == serpiente) {
	            									for (int l = 0; l < k; l++) {
	                        //si entre medias existe algo mas, no afecta
	            									if (solucionMatriz[actual.i - 1][l] != blanco || solucionMatriz[actual.i - 1][l] != llave) {
	            												checkAndUpdateCost(actual, comparado, actual.finalCost + Coste_Celda_Vacia);
	            												return;
	            									} else return; //no podemos ir porque hay una serpiente
	            								}
	            						  }
	                        
	            			if (k == solucionMatriz[0].length - 1) {
	                            	checkAndUpdateCost(actual, comparado, actual.finalCost + Coste_Celda_Vacia);
	                        }
	                    } // corchete de cierre del -for línea 222
	                    //System.out.println(" Retrocedemos una fila: "+comparado.toStringEv());
	               } // corchete de cierre del -if línea 221

	         }


	            /** Retrocedemos una COLUMNA **/

	      if (actual.j - 1 < grid[0].length ) {
	                	comparado = grid[actual.i][actual.j - 1];
	                			if (solucionMatriz[actual.i][actual.j - 1] == blanco || solucionMatriz[actual.i][actual.j - 1] ==  solucionMatriz[endI][endJ] ) {
	                				checkAndUpdateCost(actual, comparado, actual.finalCost + Coste_Celda_Vacia);

	                			} else if (solucionMatriz[actual.i][actual.j - 1] == roca) {

	                					if (solucionMatriz[actual.i][actual.j - 2] == blanco) {
	                						checkAndUpdateCost(actual, comparado, actual.finalCost + Coste_Celda_Roca);
	                					}
	                    //System.out.println(" Retrocedemos una columna: "+comparado.toStringEv());
	                			}
	            	} // corchete de cierre del -if línea 256 


	            /** Avanzamos una FILA **/

	      if (actual.i + 1 < grid.length ) {
	               comparado = grid[actual.i + 1][actual.j];
	               // System.out.println("Avanzamos una fila");

	                //La celda que analizamos o esta en blanco o hay una llave
	                		if (solucionMatriz[actual.i + 1][actual.j] == blanco || solucionMatriz[actual.i + 1][actual.j] == solucionMatriz[endI][endJ]) {
	                				for (int k = 0; k <= solucionMatriz[0].length - 1; k++) {
	                						//si hay serpiente
	                						if (solucionMatriz[actual.i + 1][k] == serpiente) {
	                								System.out.println("Serpiente");
	                								for (int l = 0; l < k; l++) {
	                										//si entre medias existe algo mas, no afecta
	                										if (solucionMatriz[actual.i + 1][l] != blanco || solucionMatriz[actual.i + 1][l] != solucionMatriz[endI][endJ]) {
	                											checkAndUpdateCost(actual, comparado, actual.finalCost + Coste_Celda_Vacia);
	                											return;

	                										} else {
	                												return;
	                										}
	                								} // corchete de cierre del -for línea 286 
	                						}
	                						
	                       if (k == solucionMatriz[0].length - 1 ) {
	                            checkAndUpdateCost(actual, comparado, actual.finalCost + Coste_Celda_Vacia);

	                        }
	                    }
	                    //System.out.println("Avanzamos una fila: "+comparado.toStringEv());
	               }
	           }


	            /** Avanzamos una COLUMNA **/

	         if (actual.j + 1 < grid[0].length ) {
	               	comparado = grid[actual.i][actual.j + 1];
	                		if (solucionMatriz[actual.i][actual.j + 1] == blanco || solucionMatriz[actual.i][actual.j + 1] != solucionMatriz[endI][endJ]) {
	                			checkAndUpdateCost(actual, comparado, actual.finalCost + Coste_Celda_Vacia);

	                		} else if (solucionMatriz[actual.i][actual.j + 1] == roca) {

	                		if (solucionMatriz[actual.i][actual.j + 2] == blanco) {
	                			checkAndUpdateCost(actual, comparado, actual.finalCost + Coste_Celda_Roca);
	                		}
	              	
	                } // corchete de cierre del else-if línea 323	
	                			
	                //System.out.println("Avanzamos una columna: "+comparado.toStringEv());

	                //comprobamos la salida
	                		if (solucionMatriz[actual.i][actual.j+1] == salida) {
	                    		checkAndUpdateCost(actual, comparado, actual.finalCost + Coste_Celda_Vacia);
	                		}
	           }       

	        } // corchete de cierre del WHILE línea 194
    	    
	    	/************** PARTE EXTRA ******************/ 
	    
	        Nodo comparado; 
	        if(actual.i-1>=0){
	        		comparado = grid[actual.i-1][actual.j];
	        				if(solucionMatriz[actual.i-1][actual.j]==79){
	        					checkAndUpdateCost(actual, comparado, actual.finalCost+Coste_Celda_Roca);
              	
	        				}
	        				else{
              					checkAndUpdateCost(actual, comparado, actual.finalCost+Coste_Celda_Vacia); 
	        				}
	        				
            if(actual.j-1>=0){                      
                  comparado = grid[actual.i-1][actual.j-1];
                  			if(solucionMatriz[actual.i-1][actual.j-1]==79){
                  				checkAndUpdateCost(actual, comparado, actual.finalCost+Coste_Celda_Roca);
                  	
                  			}
                  			else{
                  				checkAndUpdateCost(actual, comparado, actual.finalCost+Coste_Celda_Vacia); 	
                  			}
                  
            }

             if(actual.j+1<grid[0].length){
                  comparado = grid[actual.i-1][actual.j+1];
                  			if(solucionMatriz[actual.i-1][actual.j+1]==79){
                  				checkAndUpdateCost(actual, comparado, actual.finalCost+Coste_Celda_Roca);
                  			}
                  			else{
                  				checkAndUpdateCost(actual, comparado, actual.finalCost+Coste_Celda_Vacia); 
                  			}
             }
      
	    }  // cierre de corchete del primer -if despues de declarar la variable: Nodo comparado en la línea 342 
	    
	      		/*..... CONTINUA....*/ 
	      
	      if(actual.i+1<grid.length){
              comparado = grid[actual.i+1][actual.j];
              			if(solucionMatriz[actual.i+1][actual.j]==79){
              					checkAndUpdateCost(actual, comparado, actual.finalCost+Coste_Celda_Roca);             
              			}
              				else{
              					checkAndUpdateCost(actual, comparado, actual.finalCost+Coste_Celda_Vacia);              	
              				}
              
          if(actual.j-1>=0){                      
                  comparado = grid[actual.i+1][actual.j-1];
                  			if(solucionMatriz[actual.i+1][actual.j-1]==79){
                  				checkAndUpdateCost(actual, comparado, actual.finalCost+Coste_Celda_Roca);	
                  			}
                  				else{
                  					checkAndUpdateCost(actual, comparado, actual.finalCost+Coste_Celda_Vacia); 
                  				}               
         } 

         if(actual.j+1<grid[0].length){
                  comparado = grid[actual.i+1][actual.j+1];
                  			if(solucionMatriz[actual.i+1][actual.j+1]==79){
                  					checkAndUpdateCost(actual, comparado, actual.finalCost+Coste_Celda_Roca); 	
                  			}
                  				else{
                  					checkAndUpdateCost(actual, comparado, actual.finalCost+Coste_Celda_Vacia); 
                  				}
          
          }
         
       } // corchete de cierre del -if línea 382
    
	    
	} // corchete de cierre de la línea 183 
	    
			/************** FIN PARTE EXTRA ******************/ 
	    			

	    public static char[][] ejecucion(char[][] solucionMatriz) {


	        int filas = solucionMatriz.length;
	        int columnas = solucionMatriz[0].length;
	        char matrix_auxiliar[][] = new char[filas][columnas];


	        //Inicializamos el grid de tipo celda con el tamaño de la matriz solucion
	        grid = new Nodo[filas][columnas];

	        //Inicializamos la lista abierta y cerrada
	        closed = new boolean[filas][columnas];

	        open = new PriorityQueue<>((Object o1, Object o2) -> {

	            Nodo c1 = (Nodo) o1;
	            Nodo c2 = (Nodo) o2;

	            return c1.finalCost < c2.finalCost ? -1 : c1.finalCost > c2.finalCost ? 1 : 0;
	        });



	        //Es asiganado el coste heuristico de cada celda

	        for (int i = 0; i < filas; ++i) {

	            for (int j = 0; j < columnas; ++j) {

	                grid[i][j] = new Nodo(i, j);

	                //asignar el coste heuristico al nodo
	                if (heuristicaIntroducida.equals("heuristicaManhattan")) {
	                    grid[i][j].heuristicCost = heuristicaManhattan(i, j, endI, endJ);
	                    //System.out.print("Nodo:"+ grid[i][j]+", -> " );
	                   // System.out.println("COSTE:"+ grid[i][j].heuristicCost);
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

	        printClosed(closed);


	        /*TODO:

	        1. Heuristica es calculada sin tener en cuenta los %
	        2. Salida
	        3. Serpientes en misma fila con obstanculo
	         */



	        aStar(solucionMatriz);



	        printClosed(closed);


	        //Unicamente si la celda objetivo esta en la lista cerrada se empieza a generar la matriz solucion


	        if (closed[endI][endJ]) {

	            Nodo current;
	            for (int i = 0; i < filas; ++i) {
	                for (int j = 0; j < columnas; ++j) {
	                    current = grid[endI][endJ];

	                    while (current.parent != null) {
	                        if (i == current.parent.i && j == current.parent.j) {


	                            j++;
	                            distancia = distancia + 1;

	                            current = grid[endI][endJ];
	                        } else {
	                            current = current.parent;
	                        }

	                    }

	                    if (i == endI && j == endJ) {

	                        solucionMatriz[endI][endJ] = al;

	                        solucionMatriz[startI][startJ] = 32;

	                        distancia = distancia + 1;
	                    }

	                   // else if (solucionMatriz[i][j] == llave) solucionMatriz[i][j] = llave;

	                   // else if (grid[i][j] == null) solucionMatriz[i][j] = muro;

	                   // else solucionMatriz[i][j] = 32;


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

	                    System.out.println("i incio: " + i);
	                    System.out.println("j incio: " + j);

	                }
	            }
	        }
	    }


	    /* Coste heuristico del camino desde Al hasta cada una de las llaves. Objetivo: Escoger la llave con menor coste*/

	    public static void finalNodo(char[][] solucionMatriz, int resultadoParcial) {
	;

	        int resultado=0;

	        for (int i = 0; i < solucionMatriz.length; i++) {

	            for (int j = 0; j < solucionMatriz[0].length; j++) {

	                //Si se encuentra una llave


	                if (solucionMatriz[i][j] == llave) {

	                    resultado = 0;

	                    if (heuristicaIntroducida.equals("heuristicaManhattan")) {

	                        resultado = heuristicaManhattan(startI, startJ, i, j);
	                        //System.out.println("heuristica:"+resultado);

	                    }

	                    if (heuristicaIntroducida.equals("heuristicaEuclidea")) {

	                        resultado = heuristicaEuclidea(startI, startJ, i, j);

	                    }
	                    //System.out.println("RESULTADO: "+resultado);
	                    //System.out.println("RESULTADO PARCIAL: "+resultadoParcial);

	                    if (resultado < resultadoParcial) {

	                        endI = i;
	                        endJ = j;

	                        //System.out.println("FINAL NODO: "+i+" , "+j);

	                        resultadoParcial = resultado;
	                    }
	                }


	            }

	        }
	        if(resultado == 0 ){
	            for (int i = 0; i < solucionMatriz.length; i++) {

	                for (int j = 0; j < solucionMatriz[0].length; j++) {

	                    if(solucionMatriz[i][j]==69){
	                        endI=i;
	                        endJ=j;

	                    }
	                }

	                }


	        }
	    }

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
	     en cuenta el coste que supone pasar por cada tipo de celda (con capsula o vacia) */

	    static void checkAndUpdateCost(Nodo actual, Nodo t, int coste) {

	        if (t == null || closed[t.i][t.j]) {
	            //System.out.println("Nodo cerrado -> ("+ t.i+ ", " +t.j+ ")");
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
	            if (!inOpen) open.add(t);
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

	    private static void writeFile1(int matrix[][], String file) throws IOException {

	        PrintWriter writer = new PrintWriter(new FileWriter(file + ".output")); //creamos fichero de salida e imprimimos un mensaje por pantalla del nombre del mismo

	        writer.write("Mapa Original:\n");

	        for (int i = 0; i < matrix.length; i++) { //recorremos las filas de la matriz

	            for (int j = 0; j < matrix[0].length; j++) { //recorremos las columnas de la matriz

	                writer.write(matrix[i][j]); //escribimos en el fcihero el valor de la matriz en la posicion i,j

	            }
	            writer.write("\n"); // escrinimos el salto de linea para que nos escriba el mapa en el formato deseado
	        }
	        writer.close(); //cerramos fichero

	    }

	  /*  private static void writeFile2(Nodo nodo, String file) throws IOException {

	        PrintWriter writer = new PrintWriter(new FileWriter(file + ".output")); //creamos fichero de salida e imprimimos un mensaje por pantalla del nombre del mismo
	        writer.write("pasos dados:\n");


	        System.out.println("CAMINO : "+nodo.toStringEv());

	        writer.close();


	    }*/


	    //Estadisticas de ejecucion

	    public static void writeStatics(long tiempoInicial, long tiempoFinal, String file) throws IOException {

	        PrintWriter writer = new PrintWriter(new FileWriter(file + ".statistics")); //creamos fichero de salida e imprimimos un mensaje por pantalla del nombre del mismo

	        writer.write("Tiempo total: " + (tiempoFinal - tiempoInicial) + " ms\n");
	        writer.write("Coste total: " + costeTotal + "\n");
	        writer.write("Longitud de la ruta: " + distancia + "\n");
	        writer.write("Nodos expandidos: " + nodos_Expandidos + "\n");

	        writer.close();
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

	    //metodo que hace que se nos muestre la matriz por pantalla
	    private static void showMatrix2(char resultMap[][]) {
	        //recorremsos laS filas de la  matriz/mapa
	        for (char[] aResultMap : resultMap) {
	            //recorremos las columnas de la matriz
	            for (int j = 0; j < resultMap[0].length; j++) {
	                //vamos imprimiendo elemento a elemento
	                System.out.print((char) aResultMap[j]);
	            }
	            //genera un salto de linea para que la matriz se represente como el mapa
	            System.out.println();
	        }
	    }

	}



