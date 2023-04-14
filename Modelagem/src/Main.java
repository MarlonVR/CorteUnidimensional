import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws IOException {
        BufferedReader entrada = new BufferedReader(new InputStreamReader(System.in));

        ArrayList<Item> itens = new ArrayList<Item>();

        System.out.println("Digite o tamanho do item de corte:");
        int total = Integer.parseInt(entrada.readLine());

        System.out.println("Digite quantidade de itens:");
        int n = Integer.parseInt(entrada.readLine());

        String y[];
        
        for(int i = 0; i<n; i++) {
            Item item = new Item();
            System.out.printf("Digite o tamanho e a demanda do item %d:%n", i+1);
            y = entrada.readLine().split(" ");
            item.setTamanho(Integer.parseInt(y[0]));
            item.setDemanda(Integer.parseInt(y[1]));
            itens.add(item);
        }

        Modelagem mod = new Modelagem(total, itens);

        System.out.println("\nItem de corte = " + total);
        
        System.out.println("Item      Demanda      Tamanho");
        for(int i = 0; i<itens.size(); i++) {
            System.out.printf("  %d          %d           %d%n", i+1, itens.get(i).getDemanda(), itens.get(i).getTamanho());
        }
        System.out.println("\n");

        mod.criarCortes();
        mod.imprimirPossibilidades();

    }

}