package powergrid.game;

public class Level {
    private int levelNumber;
    private int housesToPower;
    private int maxBudget;
    private String terrainType;
    private boolean hasDisasters;
    
    public Level(int levelNumber) {
        this.levelNumber = levelNumber;
        setupLevelParameters();
    }
    
    private void setupLevelParameters() {
        switch(levelNumber) {
            case 1:
                housesToPower = 3;
                maxBudget = 1000;
                terrainType = "PLAIN";
                hasDisasters = false;
                break;
            case 2:
                housesToPower = 5;
                maxBudget = 1500;
                terrainType = "URBAN";
                hasDisasters = false;
                break;
            case 3:
                housesToPower = 8;
                maxBudget = 2000;
                terrainType = "MOUNTAIN";
                hasDisasters = true;
                break;
            // إضافة مستويات إضافية
        }
    }
    
    // Getters
    public int getLevelNumber() { return levelNumber; }
    public int getHousesToPower() { return housesToPower; }
    public int getMaxBudget() { return maxBudget; }
    public String getTerrainType() { return terrainType; }
    public boolean hasDisasters() { return hasDisasters; }
}