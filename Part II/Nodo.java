public class Nodo {

    //Coste de la heuristica
    int heuristicCost = 0;

    //F= G+H
    int finalCost = 0;

    int i, j;

    Nodo parent;

    Nodo (int i, int j){
        this.i = i;
        this.j = j;
    }

    @Override
    public String toString() {
        return "("+this.i+", "+this.j+")";
    }

    public String toStringEv() {
        return "("+this.i+", "+this.j+"), " + finalCost + ", " + heuristicCost + ", " + (parent != null ? parent.toString() : "no parent") + ")";
    }

    public String toStringEv2() {
        return "("+this.i+", "+this.j+")";
    }

}
