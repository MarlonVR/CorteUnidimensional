import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws IOException {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        ArrayList<Item> itens = new ArrayList<Item>();

        System.out.println("Digite o tamanho do item no qual será cortado:");
        int total = Integer.parseInt(input.readLine());

        System.out.println("Digite quantidade de itens da demanda:");
        int n = Integer.parseInt(input.readLine());

        for(int i = 0; i<n; i++) {
            Item item = new Item();
            System.out.printf("Digite o tamanho do item %d:%n", i+1);
            item.setTamanho(Integer.parseInt(input.readLine()));

            System.out.println("Digite a demanda:");
            item.setDemanda(Integer.parseInt(input.readLine()));

            itens.add(item);
        }

        Modelagem mod = new Modelagem(total, itens);
        mod.ordenarItens();

        System.out.println("\nTamanho do item de corte -> " + total);

        for(int i = 0; i<itens.size(); i++) {
            System.out.printf("item %d -> quantidade %d -> tamanho -> %d%n", i+1, itens.get(i).getDemanda(), itens.get(i).getTamanho());
        }
        System.out.println();

        mod.criarCortes();

    }

}