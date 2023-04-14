import java.util.HashMap;

public class Possibilidade {
    private HashMap<Integer, Integer> possibilidade = new HashMap<Integer, Integer>();
    private int desperdicio;

    public Possibilidade(HashMap<Integer, Integer> possibilidade, int desperdicio) {
    	this.possibilidade = possibilidade;
        this.desperdicio = desperdicio;
    }

    public HashMap<Integer, Integer> getPossibilidade() {
        return possibilidade;
    }

    public int getDesperdicio() {
        return desperdicio;
    }

}