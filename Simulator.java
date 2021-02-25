import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class Simulator {
    private String fileName;
    private Pizza pizza;
    private PizzaCutter pizzaCutter;

    public Simulator(String fileName) {
        this.fileName = fileName;
    }

    public void parseInput() {
        int bufferSize = 8 * 1024;
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(this.fileName + ".in"), bufferSize);
            String line = bufferedReader.readLine();
            String[] firstLine = line.split(" ");
            pizza = new Pizza();

            pizza.rows = Integer.parseInt(firstLine[0]);
            pizza.cols = Integer.parseInt(firstLine[1]);
            pizza.rowLength = Integer.toString(pizza.rows).length();
            pizza.colLength = Integer.toString(pizza.cols).length();
            pizza.minIngredientEachPerSlice = Integer.parseInt(firstLine[2]);
            pizza.maxCellsPerSlice = Integer.parseInt(firstLine[3]);
            pizza.cells = new HashMap<>();

            for (int i = 0; i < pizza.rows; i++) {
                String l = bufferedReader.readLine();
                char[] arr = l.toCharArray();
                for (int j = 0; j < pizza.cols; j++) {
                    String cellHashKey = pizza.getCellHashKey(i, j);
                    Cell cell = new Cell();
                    cell.x = i;
                    cell.y = j;
                    cell.ingredient = arr[j];
                    pizza.cells.put(cellHashKey, cell);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void simulate() {
        pizzaCutter = new PizzaCutter(pizza);
        pizzaCutter.cutPizza();
    }

    public void printOutput() {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(this.fileName + ".out", "UTF-8");

            int noOfSlices = pizzaCutter.cutSlices.size();

            writer.print(noOfSlices);
            writer.println();

            for (int j = 0; j < noOfSlices; j++) {
                Slice cutSlice = pizzaCutter.cutSlices.get(j);
                writer.print(cutSlice.startX + " " + cutSlice.startY + " " + cutSlice.endX + " " + cutSlice.endY);
                writer.println();
            }
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        String[] inputs = { "a_example" };
        for (String in : inputs) {
            String fileName = "C:/Compete/Hash/Hash-Code2021-Practice/inputs/" + in;
            Simulator simulator = new Simulator(fileName);
            simulator.parseInput();
            simulator.simulate();
            simulator.printOutput();
        }
    }
}