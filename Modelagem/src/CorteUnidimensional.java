import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class CorteUnidimensional {

	private int total = 0;
	private ArrayList<Item> itens = new ArrayList<Item>();
	private ArrayList<Possibilidade> possibilidades = new ArrayList<Possibilidade>();
	private int funcaoObjetivo[];
	private ArrayList<Restricao> restricoes = new ArrayList<Restricao>();
	
	public CorteUnidimensional(int total, ArrayList<Item> itens) {
		this.total = total;
		this.itens = itens;
	}

	public void criarCortes() {	
		ordenarItens();
		for(int i = 0; i<itens.size(); i++) {
			criarCortes(i, i, total, new HashMap<Integer, Integer>());		
		}
	}
	
	public void criarCortes(int indiceAtual, int indiceInicial, int desperdicio, HashMap<Integer, Integer> cortes) {
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
		
		possibilidades.add(new Possibilidade (new HashMap<Integer, Integer>(cortes), desperdicio));

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
	
	public void imprimirPossibilidades() {
		for(Possibilidade x : possibilidades) {
			for (Entry<Integer, Integer> entry : x.getPossibilidade().entrySet()) {
				System.out.print(entry.getValue() + " de " + entry.getKey() + "  ");
			}
			System.out.println("desperdicio = " + x.getDesperdicio());
		}
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
	
	// deixa o numero pequeno no lugar de ficar x1, fica x₁
	public String subscrito(int n) {
	    StringBuilder sb = new StringBuilder();
	    String digits = String.valueOf(n);
	    for (int i = 0; i < digits.length(); i++) {
	        char c = digits.charAt(i);
	        char subscript = (char) ('\u2080' + (c - '0'));
	        sb.append(subscript);
	    }
	    return sb.toString();
	}

	public void criarFuncaoObjetivo() {
		funcaoObjetivo = new int[possibilidades.size()];
		//elemento na posição 0 é o x1, na posição 1 é o x2, e assim por diante
		for(int i = 0; i<funcaoObjetivo.length; i++) {
			funcaoObjetivo[i] = possibilidades.get(i).getDesperdicio();
		}
			
	}

	public void imprimirFuncaoObjetivo() {
		System.out.print("\nFunção objetivo -> ");
		for(int i = 0; i<funcaoObjetivo.length; i++) {
			if(i < funcaoObjetivo.length-1) {
				System.out.printf("%dx%s + ", funcaoObjetivo[i], subscrito(i+1));
			} else {
				System.out.printf("%dx%s%n", funcaoObjetivo[i], subscrito(i+1));
			}
		}
	}

	public void criarRestricoes() {
		Item item; 
		Possibilidade possibilidade;
		ArrayList<String> expressao = new ArrayList<String>();
		
		for(int i = 0; i<itens.size(); i++) {
			item = itens.get(i);
			for(int j = 0, cortes; j<possibilidades.size(); j++) {
				possibilidade = possibilidades.get(j);
				
				if(possibilidade.getPossibilidade().containsKey(item.getTamanho())) {
					// cortes = quantidade de cortes do item
					// se no hashmap da possibilidade tiver {30=2} por exemplo,
					// a variavel corte vai ser 2
					cortes = possibilidade.getPossibilidade().get(item.getTamanho());
					
					if(cortes == 1) {
						expressao.add(String.format("x%s", subscrito(j+1)));
					}else {
						expressao.add(String.format("%dx%s", cortes, subscrito(j+1)));
					}
				}
			}
			
			restricoes.add(new Restricao(new ArrayList<String>(expressao), item.getDemanda()));
			expressao.clear();
		}
	}
	
	public void imprimirRestricoes() {
		System.out.println("Restrições:");
		for(Restricao restricao : restricoes) {
			ArrayList<String> expressao = restricao.getExpressao();		
			for (int i = 0; i<expressao.size(); i++) {
				if(i < expressao.size() - 1) {
					System.out.print(expressao.get(i) + " + ");
				}
				else {
					System.out.println(expressao.get(i) + " >= " + restricao.getConstante());
				}
			}
		}
		System.out.printf("x%s ... x%s >= 0%n", subscrito(1), subscrito(possibilidades.size()));
	}
}