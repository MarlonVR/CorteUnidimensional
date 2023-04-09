import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class Modelagem {

	private int total = 0;
	private ArrayList<Item> itens = new ArrayList<Item>();
	//private ArrayList<Possibilidade> possibilidades = new ArrayList<Possibilidade>();
	
	public Modelagem(int total, ArrayList<Item> itens) {
		this.total = total;
		this.itens = itens;
	}

	public void criarCortes() {	
		for(int i = 0; i<itens.size(); i++) {
			criarCortes(i, i, total, new HashMap<Integer, Integer>());		
		}
	}
	
	public void criarCortes(int indiceAtual, int indiceInicial, int desperdicio, HashMap<Integer, Integer> cortes) {
		//HashMap  key = tamanho   value = quantidade
		if(desperdicio >= itens.get(indiceAtual).getTamanho()) {
			adicionarUnidade(indiceAtual, cortes); 
			desperdicio -= itens.get(indiceAtual).getTamanho();
			criarCortes(indiceAtual, indiceInicial, desperdicio, cortes);
			return;
		}else {
			for(int j = indiceAtual+1; j<itens.size(); j++) {
				if(itens.get(j).getTamanho() <= desperdicio) {
					criarCortes(j, indiceInicial, desperdicio, cortes);
					return;
				}
			}
		}
		
		imprimirPossibilidade(cortes, desperdicio); // metodo teste

		//verifica se tem outro na frente
		if(indiceAtual+1 < itens.size()) {
			removerUnidade(indiceAtual, cortes); 
			desperdicio += itens.get(indiceAtual).getTamanho(); 
			criarCortes(indiceAtual+1, indiceInicial, desperdicio, cortes);
		}
		else if(indiceInicial+1 < itens.size()) {
			if(removerUnidade(indiceInicial, cortes)) {return;}
			desperdicio += itens.get(indiceInicial).getTamanho();
			criarCortes(indiceInicial+1, indiceInicial, desperdicio, cortes);
		}
		
	}
	
	public void adicionarUnidade(int i, HashMap<Integer, Integer> cortes) {
		if(cortes.get(itens.get(i).getTamanho()) == null) {
			cortes.put(itens.get(i).getTamanho(), 1);
		}
		else {
			cortes.put(itens.get(i).getTamanho(), cortes.get(itens.get(i).getTamanho()) + 1);
		}
	}
	
	public boolean removerUnidade(int i, HashMap<Integer, Integer> cortes) {
		if(cortes.get(itens.get(i).getTamanho()) != null){
			if(cortes.get(itens.get(i).getTamanho()) > 1) {
				cortes.put(itens.get(i).getTamanho(), cortes.get(itens.get(i).getTamanho()) - 1);
			}
			else {
				cortes.remove(itens.get(i).getTamanho());
				return true;
			}
		}
		
		return false;
		
	}
	
	public void imprimirPossibilidade(HashMap<Integer, Integer> cortes, int desperdicio) {
		for (Entry<Integer, Integer> entry : cortes.entrySet()) {
			System.out.print(entry.getValue() + " de " + entry.getKey() + "  ");
		}
		System.out.println("desperdicio = " + desperdicio);
	}
	
	public void ordenarItens() {
		Item aux = new Item();
		
		for(int i = 0; i<itens.size()-1; i++) {
			for(int j = i+1; j<itens.size(); j++) {
				if(itens.get(i).getTamanho() < itens.get(j).getTamanho()) {
					aux = itens.get(i);
					itens.set(i, itens.get(j));
					itens.set(j, aux);
				}
			}
		}
	}
	
}