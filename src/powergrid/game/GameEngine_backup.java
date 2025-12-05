package powergrid.game;

import powergrid.model.*;
import powergrid.algorithms.*;
import powergrid.utils.Constants;
import javax.swing.*;
import java.util.Random;

public class GameEngine {
    private Grid grid;
    private int budget;
    private int score;
    private int currentLevelNumber;
    private boolean gameRunning;
    private Random random;
    private int totalHousesConnected;
    
    public GameEngine() {
        this.budget = Constants.INITIAL_BUDGET;
        this.score = 0;
        this.currentLevelNumber = 1;
        this.gameRunning = true;
        this.random = new Random();
        this.totalHousesConnected = 0;
        
        // إنشاء الشبكة وتهيئة المستوى الأول
        grid = new Grid(Constants.GRID_SIZE, Constants.GRID_SIZE);
        initializeLevel(currentLevelNumber);
    }
    
    private void initializeLevel(int levelNumber) {
        // تنظيف الشبكة أولاً
        clearGrid();
        
        // إعداد المستوى حسب الرقم
        switch(levelNumber) {
            case 1:
                // مستوى تعليمي بسيط
                grid.addPowerSource(0, 0);
                grid.addHouse(4, 4);
                grid.addHouse(8, 8);
                break;
                
            case 2:
                // مستوى مع عوائق
                grid.addPowerSource(0, 0);
                grid.addHouse(7, 7);
                grid.addHouse(3, 10);
                grid.addHouse(10, 3);
                
                // إضافة عوائق
                for (int i = 3; i < 8; i++) {
                    grid.addObstacle(i, 5, Constants.OBSTACLE);
                }
                break;
                
            case 3:
                // مستوى مع تضاريس
                grid.addPowerSource(0, 0);
                grid.addHouse(12, 12);
                grid.addHouse(4, 12);
                grid.addHouse(12, 4);
                grid.addHouse(7, 7);
                
                // إضافة ماء
                for (int i = 5; i < 10; i++) {
                    for (int j = 5; j < 7; j++) {
                        grid.addObstacle(i, j, Constants.WATER);
                    }
                }
                break;
                
            case 4:
                // مستوى متقدم بمصدرين
                grid.addPowerSource(0, 0);
                grid.addPowerSource(14, 0);
                
                // 6 منازل عشوائية
                for (int i = 0; i < 6; i++) {
                    int x, y;
                    do {
                        x = random.nextInt(12) + 1;
                        y = random.nextInt(12) + 1;
                    } while (grid.getCell(x, y).getType() != Constants.EMPTY);
                    grid.addHouse(x, y);
                }
                break;
                
            case 5:
                // المستوى النهائي
                grid.addPowerSource(7, 7); // في المنتصف
                
                // منازل في الزوايا والأطراف
                grid.addHouse(2, 2);
                grid.addHouse(2, 12);
                grid.addHouse(12, 2);
                grid.addHouse(12, 12);
                grid.addHouse(7, 2);
                grid.addHouse(2, 7);
                grid.addHouse(12, 7);
                grid.addHouse(7, 12);
                break;
        }
        
        // تحديث توزيع الكهرباء
        updatePowerDistribution();
    }
    
    private void clearGrid() {
        // إعادة تعيين جميع الخلايا
        for (int i = 0; i < grid.getWidth(); i++) {
            for (int j = 0; j < grid.getHeight(); j++) {
                grid.getCell(i, j).setType(Constants.EMPTY);
            }
        }
        
        // مسح القوائم
        grid.getHouses().clear();
        grid.getPowerSources().clear();
    }
    
    public boolean placeWire(int x, int y) {
        if (budget >= Constants.WIRE_COST && grid.getCell(x, y).getType() == Constants.EMPTY) {
            grid.addWire(x, y);
            budget -= Constants.WIRE_COST;
            updatePowerDistribution();
            return true;
        }
        return false;
    }
    
    public boolean placeTransformer(int x, int y) {
        if (budget >= Constants.TRANSFORMER_COST && grid.getCell(x, y).getType() == Constants.EMPTY) {
            grid.addTransformer(x, y);
            budget -= Constants.TRANSFORMER_COST;
            updatePowerDistribution();
            return true;
        }
        return false;
    }
    
    private void updatePowerDistribution() {
        // إعادة تعيين الكهرباء
        grid.resetPower();
        
        // نشر الكهرباء من مصادر الطاقة باستخدام BFS
        for (PowerSource source : grid.getPowerSources()) {
            if (source.isActive()) {
                BFS.spreadPower(grid, source.getX(), source.getY());
            }
        }
        
        // حساب النقاط والإحصائيات
        calculateScore();
    }
    
    private void calculateScore() {
        int poweredHouses = grid.countPoweredHouses();
        score = poweredHouses * Constants.HOUSE_REWARD;
        
        // مكافأة إضافية إذا تم توصيل جميع المنازل
        if (poweredHouses == grid.getHouses().size() && grid.getHouses().size() > 0) {
            score += 500; // مكافأة إكمال المستوى
            totalHousesConnected += poweredHouses;
        }
    }
    
    public boolean nextLevel() {
        // التحقق من إكمال المستوى الحالي
        int poweredHouses = grid.countPoweredHouses();
        int totalHouses = grid.getHouses().size();
        
        if (totalHouses == 0 || poweredHouses < totalHouses) {
            // لم يكتمل المستوى
            return false;
        }
        
        // الانتقال للمستوى التالي
        if (currentLevelNumber < 5) {
            currentLevelNumber++;
            
            // مكافأة إكمال المستوى
            budget += 300;
            score += 500;
            
            // إنشاء المستوى الجديد
            initializeLevel(currentLevelNumber);
            
            // عرض رسالة نجاح
            showLevelCompleteMessage();
            return true;
        } else {
            // لعبة مكتملة
            showGameCompleteMessage();
            return false;
        }
    }
    
    private void showLevelCompleteMessage() {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null,
                "<html><div style='text-align: center;'>"
                + "<h2>🎉 مستوى مكتمل! 🎉</h2>"
                + "<hr>"
                + "<p>تم الانتقال للمستوى " + currentLevelNumber + "</p>"
                + "<p>مكافأة: +500 نقطة</p>"
                + "<p>ميزانية إضافية: +300$</p>"
                + "<br>"
                + "<p>🏠 المنازل الجديدة: " + grid.getHouses().size() + "</p>"
                + "<p>⚡ مصادر الطاقة: " + grid.getPowerSources().size() + "</p>"
                + "</div></html>",
                "تهانينا!",
                JOptionPane.INFORMATION_MESSAGE);
        });
    }
    
    private void showGameCompleteMessage() {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null,
                "<html><div style='text-align: center;'>"
                + "<h1>🏆 فوز! 🏆</h1>"
                + "<h3>لقد أكملت جميع مستويات اللعبة!</h3>"
                + "<hr>"
                + "<p>النقاط النهائية: " + score + "</p>"
                + "<p>آخر ميزانية: $" + budget + "</p>"
                + "<p>إجمالي المنازل الموصولة: " + totalHousesConnected + "</p>"
                + "<br>"
                + "<p>شكراً للعب <b>Power Grid</b>!</p>"
                + "<p>لقد أتقنت خوارزميات BFS و DFS بنجاح.</p>"
                + "</div></html>",
                "إكمال اللعبة",
                JOptionPane.INFORMATION_MESSAGE);
        });
    }
    
    public void triggerDisaster(String disasterType) {
        switch(disasterType) {
            case "STORM":
                // تعطيل مصادر طاقة عشوائية
                for (PowerSource source : grid.getPowerSources()) {
                    if (random.nextDouble() < 0.3) {
                        source.setActive(false);
                    }
                }
                break;
                
            case "EARTHQUAKE":
                // إضافة عوائق عشوائية
                for (int i = 0; i < 5; i++) {
                    int x = random.nextInt(grid.getWidth());
                    int y = random.nextInt(grid.getHeight());
                    grid.addObstacle(x, y, Constants.OBSTACLE);
                }
                break;
        }
        
        updatePowerDistribution();
    }
    
    // ============ دوال الوصول (Getters) ============
    
    public Grid getGrid() { return grid; }
    public int getBudget() { return budget; }
    public int getScore() { return score; }
    public boolean isGameRunning() { return gameRunning; }
    public int getCurrentLevelNumber() { return currentLevelNumber; }
    public int getTotalHousesConnected() { return totalHousesConnected; }
}