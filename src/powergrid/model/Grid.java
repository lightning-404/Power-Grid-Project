package powergrid.model;

import java.util.ArrayList;
import java.util.List;

public class Grid {
    private Cell[][] cells;
    private int width, height;
    private List<Cell> damagedCells;
    
    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
        this.cells = new Cell[width][height];
        this.damagedCells = new ArrayList<>();
        
        initializeGrid();
    }
    
    private void initializeGrid() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // إنشاء خريطة أولية متنوعة
                int type = Constants.EMPTY;
                if (x % 5 == 0 && y % 5 == 0) type = Constants.POWER_SOURCE;
                else if (x % 3 == 0 && y % 3 == 0) type = Constants.HOUSE;
                else if (x % 2 == 0 && y % 2 == 0) type = Constants.WIRE;
                else if (Math.random() < 0.1) type = Constants.WATER;
                else if (Math.random() < 0.05) type = Constants.MOUNTAIN;
                
                cells[x][y] = new Cell(x, y, type);
            }
        }
        
        // توصيل بعض المنازل بمصادر الطاقة
        connectInitialPower();
    }
    
    private void connectInitialPower() {
        // منطق توصيل أولي
    }
    
    public Cell getCell(int x, int y) {
        if (isValidPosition(x, y)) {
            return cells[x][y];
        }
        return null;
    }
    
    public void setCell(int x, int y, Cell cell) {
        if (isValidPosition(x, y)) {
            cells[x][y] = cell;
        }
    }
    
    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
    
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    
    public void addDamagedCell(Cell cell) {
        if (!damagedCells.contains(cell)) {
            damagedCells.add(cell);
        }
    }
    
    public void removeDamagedCell(Cell cell) {
        damagedCells.remove(cell);
    }
    
    public List<Cell> getDamagedCells() {
        return new ArrayList<>(damagedCells);
    }
    
    public int getDamageRepairCost() {
        int totalCost = 0;
        for (Cell cell : damagedCells) {
            totalCost += cell.getDamageLevel() * 100;
        }
        return totalCost;
    }
    
    public void updateAllCells() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cells[x][y].updateEffects();
            }
        }
    }
}