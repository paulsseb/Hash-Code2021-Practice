import java.util.HashMap;

public class CityPlan {

    int simulation;
    int intersections;
    int streets;
    int cars;
    HashMap<String, Street> streetInfo;
    int rowLength;
    int colLength;

    public String getCellHashKey(int x, int y) {
        return String.format("%0" + rowLength + "d", x) + String.format("%0" + colLength + "d", y);
    }
}