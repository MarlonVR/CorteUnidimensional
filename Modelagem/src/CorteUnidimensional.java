import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

public class CorteUnidimensional {

	private int total = 0;
	private ArrayList<Item> itens = new ArrayList<Item>();
	private ArrayList<Possibilidade> possibilidades = new ArrayList<Possibilidade>();

    MPSolver solver = MPSolver.createSolver("SCIP");
    double infinity = java.lang.Double.POSITIVE_INFINITY;
    MPVariable variaveis[];
    MPConstraint restricoes[];
    MPObjective objective = solver.objective();
    
	public CorteUnidimensional(int total, ArrayList<Item> itens) {
		this.total = total;
		this.itens = itens;
		Loader.loadNativeLibraries();
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
		for(int i = 0; i<variaveis.length; i++) {
			                        // x₁               2            ->  2x₁
			objective.setCoefficient(variaveis[i], possibilidades.get(i).getDesperdicio());
		}
		objective.setMaximization();
			
	}

	public void criarRestricoes() {
		
		Item item; 
		Possibilidade possibilidade;
		
		variaveis = new MPVariable[possibilidades.size()];
		restricoes = new MPConstraint[itens.size()];
		
		for(int i = 0; i<itens.size(); i++) {
			item = itens.get(i);
									// >=
			restricoes[i] = solver.makeConstraint(item.getDemanda(), infinity, ("c" + subscrito(i+1)));

			for(int j = 0; j<possibilidades.size(); j++) {
				possibilidade = possibilidades.get(j);
				
				if(possibilidade.getPossibilidade().containsKey(item.getTamanho())) {
					double cortes = possibilidade.getPossibilidade().get(item.getTamanho());
					
					if(variaveis[j] == null) {
						variaveis[j] = solver.makeIntVar(0.0, infinity, ("x" + subscrito(j+1)));
					}
					
					restricoes[i].setCoefficient(variaveis[j], cortes);
				}
			}
		}
	}
	
	
	public void resolverProblema() {
        criarCortes();
        imprimirPossibilidades();
        criarRestricoes();
        criarFuncaoObjetivo();

  
        System.out.println("Número de variáveis = " + solver.numVariables());
        System.out.println("Número de restrições = " + solver.numConstraints());
        MPSolver.ResultStatus resultStatus = solver.solve();

        if (resultStatus == MPSolver.ResultStatus.OPTIMAL) {
            System.out.println("Solução:");
            System.out.println("Custo da função objetivo = " + objective.value());
            for(int i = 0; i<variaveis.length; i++) {
            	System.out.println("x" + (subscrito(i+1)) + " " + variaveis[i].solutionValue());
            }
            System.out.println("Tempo de resolução = " + solver.wallTime() + " milissegundos");
            System.out.println(solver.exportModelAsLpFormat());
        } else {
            System.out.println("Solução ótima não encontrada!");
        }
	}
}