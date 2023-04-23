import java.util.ArrayList;

public class Restricao {
	private ArrayList<String> expressao;
	private int constante;
	
	public Restricao(ArrayList<String> expressao, int constante) {
		this.expressao = expressao;
		this.constante = constante;
	}

	public ArrayList<String> getExpressao() {
		return expressao;
	}

	public int getConstante() {
		return constante;
	}

}
