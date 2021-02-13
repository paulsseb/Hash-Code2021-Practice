import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PizzaCutter {

    Pizza pizza;
    ArrayList<Slice> cutSlices = new ArrayList<>();

    public PizzaCutter(Pizza pizza) {
        this.pizza = pizza;
    }

    public void cutPizza() {
        for (int i = 0; i < pizza.rows; i++) {
            for (int j = 0; j < pizza.cols; j++) {
                String cellKey = pizza.getCellHashKey(i, j);
                Cell cell = pizza.cells.get(cellKey);
                if (cell.cutOut) {
                    continue;
                }
                ArrayList<Cell> sliceCells = this.expandFromCell(cell);
                if (!sliceCells.isEmpty()) {
                    if (doesSliceSatisfyMaximumAreaRule(sliceCells) && allCellsAreNotPreviouslyCut(sliceCells)
                            && doesSliceSatisfyMinimumIngredientRule(sliceCells)) {
                        this.markAllSliceCellsAsCut(sliceCells);
                        Cell firstSlicedCell = getLeastCell(sliceCells);
                        Cell lastSlicedCell = getMaxCell(sliceCells);
                        Slice slice = new Slice();
                        slice.startX = firstSlicedCell.x;
                        slice.startY = firstSlicedCell.y;
                        slice.endX = lastSlicedCell.x;
                        slice.endY = lastSlicedCell.y;
                        cutSlices.add(slice);
                    }
                }
            }
        }
    }

    private void markAllSliceCellsAsCut(ArrayList<Cell> sliceCells) {
        for (int i = 0; i < sliceCells.size(); i++) {
            Cell cell = sliceCells.get(i);
            cell.cutOut = true;
            String cellHashKey = pizza.getCellHashKey(cell.x, cell.y);
            pizza.cells.put(cellHashKey, cell);
        }
    }

    private boolean allCellsAreNotPreviouslyCut(ArrayList<Cell> sliceCells) {
        for (int i = 0; i < sliceCells.size(); i++) {
            Cell cell = sliceCells.get(i);
            if (cell == null) {
                return false;
            }
            if (cell.cutOut) {
                return false;
            }
        }
        return true;
    }

    private boolean doesSliceSatisfyMinimumIngredientRule(ArrayList<Cell> sliceCells) {
        int mushroomsCount = 0;
        int tomatoCount = 0;
        for (int i = 0; i < sliceCells.size(); i++) {
            Cell cell = sliceCells.get(i);
            if (cell.ingredient == 'T') {
                tomatoCount++;
            } else {
                mushroomsCount++;
            }
        }
        return (mushroomsCount >= pizza.minIngredientEachPerSlice && tomatoCount >= pizza.minIngredientEachPerSlice);
    }

    private Cell getLeastCell(ArrayList<Cell> slicedCells) {
        int minimumCellNumber = Integer.MAX_VALUE;
        int size = slicedCells.size();
        Cell leastCell = null;
        for (int i = 0; i < size; i++) {
            Cell cell = slicedCells.get(i);
            String cellKey = pizza.getCellHashKey(cell.x, cell.y);
            int cellKeyIntValue = Integer.parseInt(cellKey);
            if (cellKeyIntValue < minimumCellNumber) {
                minimumCellNumber = cellKeyIntValue;
                leastCell = cell;
            }
        }
        return leastCell;
    }

    private Cell getMaxCell(ArrayList<Cell> slicedCells) {
        int maxCellNumber = 0;
        int size = slicedCells.size();
        Cell maxCell = null;
        for (int i = 0; i < size; i++) {
            Cell cell = slicedCells.get(i);
            String cellKey = pizza.getCellHashKey(cell.x, cell.y);
            int cellKeyIntValue = Integer.parseInt(cellKey);
            if (cellKeyIntValue > maxCellNumber) {
                maxCellNumber = cellKeyIntValue;
                maxCell = cell;
            }
        }
        return maxCell;
    }

    public ArrayList<Cell> expandFromCell(Cell cell) {
        HashMap<String, Cell> focusCells = new HashMap();
        focusCells.put(pizza.getCellHashKey(cell.x, cell.y), cell);

        HashMap<String, Cell> cellsToSlice = new HashMap();

        while (allCellsAreNotCutOut(focusCells)
                && ((focusCells.size() + cellsToSlice.size()) <= pizza.maxCellsPerSlice)) {
            if (focusCells.isEmpty()) {
                break;
            }
            cellsToSlice.putAll(focusCells);
            focusCells = expandFocusCells(focusCells);
        }
        return new ArrayList<>(cellsToSlice.values());
    }

    private HashMap<String, Cell> expandFocusCells(HashMap<String, Cell> focusCells) {
        HashMap<String, Cell> expandResultCells = new HashMap();
        for (Map.Entry<String, Cell> entry : focusCells.entrySet()) {
            Cell cell = entry.getValue();
            if (cell == null) {
                continue;
            }

            if (cell.y + 1 <= pizza.cols - 1) {
                String rightCellKey = pizza.getCellHashKey(cell.x, cell.y + 1);
                if (!focusCells.containsKey(rightCellKey)) {
                    Cell rightCell = pizza.cells.get(rightCellKey);
                    expandResultCells.put(rightCellKey, rightCell);
                }
            }

            if (cell.y + 1 <= pizza.cols - 1 && cell.x <= pizza.rows - 1) {
                String diagonalCellKey = pizza.getCellHashKey(cell.x + 1, cell.y + 1);
                if (!focusCells.containsKey(diagonalCellKey)) {
                    Cell diagonalCell = pizza.cells.get(diagonalCellKey);
                    expandResultCells.put(diagonalCellKey, diagonalCell);
                }
            }

            if (cell.x + 1 <= pizza.rows - 1) {
                String downCellKey = pizza.getCellHashKey(cell.x + 1, cell.y);
                if (!focusCells.containsKey(downCellKey)) {
                    Cell downCell = pizza.cells.get(downCellKey);
                    expandResultCells.put(downCellKey, downCell);
                }
            }
        }

        return expandResultCells;
    }

    private boolean allCellsAreNotCutOut(HashMap<String, Cell> sliceCells) {
        for (Map.Entry<String, Cell> entry : sliceCells.entrySet()) {
            Cell cell = entry.getValue();
            if (cell == null || cell.cutOut) {
                return false;
            }
        }
        return true;
    }

    private boolean doesSliceSatisfyMaximumAreaRule(ArrayList<Cell> sliceCells) {
        return sliceCells.size() <= pizza.maxCellsPerSlice;
    }
}