import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class Simulator {
    private String fileName;
    private CityPlan cityInstances;
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
            cityInstances = new CityPlan();

            cityInstances.simulation = Integer.parseInt(firstLine[0]);
            cityInstances.intersections = Integer.parseInt(firstLine[1]);
            cityInstances.rowLength = Integer.toString(cityInstances.simulation).length();
            cityInstances.colLength = Integer.toString(cityInstances.intersections).length();
            cityInstances.streets = Integer.parseInt(firstLine[2]);
            cityInstances.cars = Integer.parseInt(firstLine[3]);
            cityInstances.streetInfo = new HashMap<>();

            for (int i = 0; i < cityInstances.streets; i++) {
                String l = bufferedReader.readLine();
                String[] streetDetails = l.split(" ");
                // char[] arr = l.toCharArray();
                // for (int j = 0; j < cityInstances.intersections; j++) {
                String cellHashKey = cityInstances.getCellHashKey(Integer.parseInt(streetDetails[0]),
                        Integer.parseInt(streetDetails[1]));
                Street street = new Street();
                street.startIntersection = Integer.parseInt(streetDetails[0]);
                street.endIntersection = Integer.parseInt(streetDetails[1]);
                street.name = streetDetails[2];
                street.timeL = Integer.parseInt(streetDetails[3]);

                cityInstances.streetInfo.put(cellHashKey, street);
                // }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void simulate() {
        pizzaCutter = new PizzaCutter(cityInstances);
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