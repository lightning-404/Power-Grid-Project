package powergrid.game;

import powergrid.model.*;
import powergrid.utils.Constants;
import javax.swing.*;
import java.util.Random;

/**
 * مدير المستويات - يتحكم في إنشاء وتغيير المستويات
 */
public class LevelManager {
    private int currentLevel;
    private Random random;
    
    public LevelManager() {
        this.currentLevel = 1;
        this.random = new Random();
    }
    
    /**
     * تهيئة مستوى جديد على الشبكة
     */
    public void setupLevel(Grid grid, int level) {
        // تنظيف الشبكة أولاً
        clearGrid(grid);
        
        switch(level) {
            case 1:
                setupLevel1(grid);
                break;
            case 2:
                setupLevel2(grid);
                break;
            case 3:
                setupLevel3(grid);
                break;
            case 4:
                setupLevel4(grid);
                break;
            case 5:
                setupLevel5(grid);
                break;
            default:
                setupCustomLevel(grid, level);
        }
        
        this.currentLevel = level;
    }
    
    /**
     * المستوى 1: تعليمي
     */
    private void setupLevel1(Grid grid) {
        grid.addPowerSource(0, 0);
        grid.addHouse(4, 4);
        grid.addHouse(8, 8);
        grid.addHouse(12, 12);
    }
    
    /**
     * المستوى 2: عوائق بسيطة
     */
    private void setupLevel2(Grid grid) {
        grid.addPowerSource(0, 0);
        
        // منازل
        grid.addHouse(7, 7);
        grid.addHouse(3, 10);
        grid.addHouse(10, 3);
        grid.addHouse(14, 14);
        
        // عوائق
        for (int i = 3; i < 8; i++) {
            grid.addObstacle(i, 5, Constants.OBSTACLE);
        }
    }
    
    /**
     * المستوى 3: تضاريس
     */
    private void setupLevel3(Grid grid) {
        grid.addPowerSource(0, 0);
        
        // منازل
        grid.addHouse(14, 14);
        grid.addHouse(4, 12);
        grid.addHouse(12, 4);
        grid.addHouse(7, 7);
        grid.addHouse(10, 10);
        
        // ماء
        for (int i = 5; i < 10; i++) {
            for (int j = 5; j < 7; j++) {
                grid.addObstacle(i, j, Constants.WATER);
            }
        }
        
        // جبال
        for (int i = 8; i < 12; i++) {
            grid.addObstacle(i, 8, Constants.MOUNTAIN);
        }
    }
    
    /**
     * المستوى 4: متقدم
     */
    private void setupLevel4(Grid grid) {
        grid.addPowerSource(0, 0);
        grid.addPowerSource(14, 0); // مصدر ثاني
        
        // منازل عشوائية
        for (int i = 0; i < 8; i++) {
            int x, y;
            do {
                x = random.nextInt(12) + 1;
                y = random.nextInt(12) + 1;
            } while (grid.getCell(x, y).getType() != Constants.EMPTY);
            grid.addHouse(x, y);
        }
        
        // عوائق عشوائية
        for (int i = 0; i < 10; i++) {
            int x = random.nextInt(Constants.GRID_SIZE);
            int y = random.nextInt(Constants.GRID_SIZE);
            int type = random.nextInt(3);
            switch(type) {
                case 0: grid.addObstacle(x, y, Constants.OBSTACLE); break;
                case 1: grid.addObstacle(x, y, Constants.WATER); break;
                case 2: grid.addObstacle(x, y, Constants.MOUNTAIN); break;
            }
        }
    }
    
    /**
     * المستوى 5: متاهة
     */
    private void setupLevel5(Grid grid) {
        grid.addPowerSource(7, 7); // في المنتصف
        
        // منازل في الزوايا
        grid.addHouse(0, 0);
        grid.addHouse(0, 14);
        grid.addHouse(14, 0);
        grid.addHouse(14, 14);
        grid.addHouse(7, 0);
        grid.addHouse(0, 7);
        grid.addHouse(14, 7);
        grid.addHouse(7, 14);
        
        // متاهة
        createMaze(grid);
    }
    
    /**
     * مستوى مخصص (للإضافات المستقبلية)
     */
    private void setupCustomLevel(Grid grid, int level) {
        grid.addPowerSource(0, 0);
        for (int i = 0; i < Math.min(level * 2, 20); i++) {
            int x = random.nextInt(Constants.GRID_SIZE);
            int y = random.nextInt(Constants.GRID_SIZE);
            if (grid.getCell(x, y).getType() == Constants.EMPTY) {
                grid.addHouse(x, y);
            }
        }
    }
    
    /**
     * إنشاء متاهة بسيطة
     */
    private void createMaze(Grid grid) {
        for (int i = 2; i < 13; i += 2) {
            for (int j = 2; j < 13; j += 2) {
                grid.addObstacle(i, j, Constants.OBSTACLE);
            }
        }
    }
    
    /**
     * تنظيف الشبكة
     */
    private void clearGrid(Grid grid) {
        for (int i = 0; i < grid.getWidth(); i++) {
            for (int j = 0; j < grid.getHeight(); j++) {
                grid.getCell(i, j).setType(Constants.EMPTY);
            }
        }
        
        grid.getHouses().clear();
        grid.getPowerSources().clear();
    }
    
    /**
     * الانتقال للمستوى التالي
     */
    public boolean goToNextLevel(Grid grid) {
        if (currentLevel >= 5) {
            showGameComplete();
            return false;
        }
        
        currentLevel++;
        setupLevel(grid, currentLevel);
        showLevelMessage();
        return true;
    }
    
    /**
     * إعادة تعيين للمستوى الحالي
     */
    public void resetCurrentLevel(Grid grid) {
        setupLevel(grid, currentLevel);
    }
    
    /**
     * عرض رسالة المستوى الجديد
     */
    private void showLevelMessage() {
        String[] messages = {
            "مستوى تعليمي: تعلم أساسيات اللعبة",
            "مستوى المدينة: تجنب العوائق الحضرية",
            "مستوى الطبيعة: تعامل مع التضاريس الصعبة",
            "مستوى الخبير: تحديات متعددة",
            "المستوى النهائي: اختبار جميع مهاراتك"
        };
        
        int index = Math.min(currentLevel - 1, messages.length - 1);
        
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null,
                "<html><div style='text-align: right; direction: rtl;'>"
                + "<h2>🎮 المستوى " + currentLevel + " 🎮</h2>"
                + "<hr>"
                + "<p>" + messages[index] + "</p>"
                + "<br>"
                + "<p><b>التحدي:</b> توصيل جميع المنازل بالكهرباء</p>"
                + "</div></html>",
                "مستوى جديد",
                JOptionPane.INFORMATION_MESSAGE);
        });
    }
    
    /**
     * عرض رسالة إكمال اللعبة
     */
    private void showGameComplete() {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null,
                "<html><div style='text-align: center;'>"
                + "<h1>🎊 مبروك! 🎊</h1>"
                + "<h2>لقد أكملت جميع مستويات Power Grid</h2>"
                + "<hr>"
                + "<p>لقد أظهرت مهارة رائعة في خوارزميات البحث</p>"
                + "<p>BFS و DFS أصبحت تحت سيطرتك!</p>"
                + "<br>"
                + "<p style='color: blue;'>⚡ مشروع ناجح ⚡</p>"
                + "</div></html>",
                "إكمال اللعبة",
                JOptionPane.INFORMATION_MESSAGE);
        });
    }
    
    /**
     * التحقق من إكمال المستوى
     */
    public boolean isLevelComplete(Grid grid) {
        return grid.countPoweredHouses() == grid.getHouses().size() 
               && !grid.getHouses().isEmpty();
    }
    
    // ============ دوال الوصول ============
    
    public int getCurrentLevel() {
        return currentLevel;
    }
    
    public void setCurrentLevel(int level) {
        this.currentLevel = Math.max(1, Math.min(level, 5));
    }
    
    public String getLevelInfo() {
        return "المستوى " + currentLevel + "/5";
    }
    
    public String getLevelDescription() {
        switch(currentLevel) {
            case 1: return "تعليمي (3 منازل)";
            case 2: return "مدني (4 منازل + عوائق)";
            case 3: return "طبيعي (5 منازل + تضاريس)";
            case 4: return "متقدم (8 منازل + تحديات)";
            case 5: return "نهائي (8 منازل + متاهة)";
            default: return "مستوى " + currentLevel;
        }
    }
}