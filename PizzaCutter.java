import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PizzaCutter {

    CityPlan pizza;
    ArrayList<Slice> cutSlices = new ArrayList<>();

    public PizzaCutter(CityPlan pizza) {
        this.pizza = pizza;
    }

    public void cutPizza() {
        for (int i = 0; i < pizza.rows; i++) {
            for (int j = 0; j < pizza.cols; j++) {
                String cellKey = pizza.getCellHashKey(i, j);
                Street cell = pizza.streetInfo.get(cellKey);
                if (cell.cutOut) {
                    continue;
                }
                ArrayList<Street> sliceCells = this.expandFromCell(cell);
                if (!sliceCells.isEmpty()) {
                    if (doesSliceSatisfyMaximumAreaRule(sliceCells) && allCellsAreNotPreviouslyCut(sliceCells)
                            && doesSliceSatisfyMinimumIngredientRule(sliceCells)) {
                        this.markAllSliceCellsAsCut(sliceCells);
                        Street firstSlicedCell = getLeastCell(sliceCells);
                        Street lastSlicedCell = getMaxCell(sliceCells);
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

    private void markAllSliceCellsAsCut(ArrayList<Street> sliceCells) {
        for (int i = 0; i < sliceCells.size(); i++) {
            Street cell = sliceCells.get(i);
            cell.cutOut = true;
            String cellHashKey = pizza.getCellHashKey(cell.x, cell.y);
            pizza.streetInfo.put(cellHashKey, cell);
        }
    }

    private boolean allCellsAreNotPreviouslyCut(ArrayList<Street> sliceCells) {
        for (int i = 0; i < sliceCells.size(); i++) {
            Street cell = sliceCells.get(i);
            if (cell == null) {
                return false;
            }
            if (cell.cutOut) {
                return false;
            }
        }
        return true;
    }

    private boolean doesSliceSatisfyMinimumIngredientRule(ArrayList<Street> sliceCells) {
        int mushroomsCount = 0;
        int tomatoCount = 0;
        for (int i = 0; i < sliceCells.size(); i++) {
            Street cell = sliceCells.get(i);
            if (cell.ingredient == 'T') {
                tomatoCount++;
            } else {
                mushroomsCount++;
            }
        }
        return (mushroomsCount >= pizza.minIngredientEachPerSlice && tomatoCount >= pizza.minIngredientEachPerSlice);
    }

    private Street getLeastCell(ArrayList<Street> slicedCells) {
        int minimumCellNumber = Integer.MAX_VALUE;
        int size = slicedCells.size();
        Street leastCell = null;
        for (int i = 0; i < size; i++) {
            Street cell = slicedCells.get(i);
            String cellKey = pizza.getCellHashKey(cell.x, cell.y);
            int cellKeyIntValue = Integer.parseInt(cellKey);
            if (cellKeyIntValue < minimumCellNumber) {
                minimumCellNumber = cellKeyIntValue;
                leastCell = cell;
            }
        }
        return leastCell;
    }

    private Street getMaxCell(ArrayList<Street> slicedCells) {
        int maxCellNumber = 0;
        int size = slicedCells.size();
        Street maxCell = null;
        for (int i = 0; i < size; i++) {
            Street cell = slicedCells.get(i);
            String cellKey = pizza.getCellHashKey(cell.x, cell.y);
            int cellKeyIntValue = Integer.parseInt(cellKey);
            if (cellKeyIntValue > maxCellNumber) {
                maxCellNumber = cellKeyIntValue;
                maxCell = cell;
            }
        }
        return maxCell;
    }

    public ArrayList<Street> expandFromCell(Street cell) {
        HashMap<String, Street> focusCells = new HashMap();
        focusCells.put(pizza.getCellHashKey(cell.x, cell.y), cell);

        HashMap<String, Street> cellsToSlice = new HashMap();

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

    private HashMap<String, Street> expandFocusCells(HashMap<String, Street> focusCells) {
        HashMap<String, Street> expandResultCells = new HashMap();
        for (Map.Entry<String, Street> entry : focusCells.entrySet()) {
            Street cell = entry.getValue();
            if (cell == null) {
                continue;
            }

            if (cell.y + 1 <= pizza.cols - 1) {
                String rightCellKey = pizza.getCellHashKey(cell.x, cell.y + 1);
                if (!focusCells.containsKey(rightCellKey)) {
                    Street rightCell = pizza.streetInfo.get(rightCellKey);
                    expandResultCells.put(rightCellKey, rightCell);
                }
            }

            if (cell.y + 1 <= pizza.cols - 1 && cell.x <= pizza.rows - 1) {
                String diagonalCellKey = pizza.getCellHashKey(cell.x + 1, cell.y + 1);
                if (!focusCells.containsKey(diagonalCellKey)) {
                    Street diagonalCell = pizza.streetInfo.get(diagonalCellKey);
                    expandResultCells.put(diagonalCellKey, diagonalCell);
                }
            }

            if (cell.x + 1 <= pizza.rows - 1) {
                String downCellKey = pizza.getCellHashKey(cell.x + 1, cell.y);
                if (!focusCells.containsKey(downCellKey)) {
                    Street downCell = pizza.streetInfo.get(downCellKey);
                    expandResultCells.put(downCellKey, downCell);
                }
            }
        }

        return expandResultCells;
    }

    private boolean allCellsAreNotCutOut(HashMap<String, Street> sliceCells) {
        for (Map.Entry<String, Street> entry : sliceCells.entrySet()) {
            Street cell = entry.getValue();
            if (cell == null || cell.cutOut) {
                return false;
            }
        }
        return true;
    }

    private boolean doesSliceSatisfyMaximumAreaRule(ArrayList<Street> sliceCells) {
        return sliceCells.size() <= pizza.maxCellsPerSlice;
    }
}