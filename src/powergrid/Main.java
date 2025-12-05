package powergrid;

import powergrid.manager.*;
import powergrid.model.*;
import powergrid.ui.*;
import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // 1. إنشاء الشبكة
                Grid grid = new Grid(20, 15);
                
                // 2. إنشاء مدير التأثيرات
                EffectManager effectManager = new EffectManager(grid);
                
                // 3. إنشاء مدير اللعبة
                GameManager gameManager = new GameManager(grid, effectManager);
                
                // 4. إنشاء النافذة الرئيسية
                JFrame frame = new JFrame("⚡ Power Grid - محاكاة إدارة الشبكة الكهربائية");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLayout(new BorderLayout(5, 5));
                
                // 5. إنشاء وحدات واجهة المستخدم
                GameCanvas gameCanvas = new GameCanvas(grid, effectManager);
                ObstaclePanel obstaclePanel = new ObstaclePanel(effectManager);
                GameInfoPanel gameInfoPanel = new GameInfoPanel(gameManager);
                
                // 6. شريط القائمة
                JMenuBar menuBar = createMenuBar(gameManager);
                frame.setJMenuBar(menuBar);
                
                // 7. إضافة المكونات
                frame.add(gameCanvas, BorderLayout.CENTER);
                
                JPanel sidePanel = new JPanel(new BorderLayout());
                sidePanel.add(gameInfoPanel, BorderLayout.NORTH);
                sidePanel.add(obstaclePanel, BorderLayout.CENTER);
                frame.add(sidePanel, BorderLayout.EAST);
                
                // 8. إعداد النافذة
                frame.pack();
                frame.setSize(1400, 800);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                
                // 9. رسالة بدء
                JOptionPane.showMessageDialog(frame,
                    "مرحبًا بك في محاكاة إدارة الشبكة الكهربائية!\n\n" +
                    "🎯 الأهداف:\n" +
                    "1. وفر الطاقة للمنازل والمصانع\n" +
                    "2. تعامل مع الكوارث الطبيعية\n" +
                    "3. أصلح الأضرار بسرعة\n" +
                    "4. حافظ على استقرار الشبكة\n\n" +
                    "استخدم لوحة العوائق لتفعيل الزلازل واختبر قدراتك!",
                    "تعليمات اللعبة",
                    JOptionPane.INFORMATION_MESSAGE);
                
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "خطأ في تشغيل المحاكاة: " + e.getMessage(), 
                    "خطأ", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    private static JMenuBar createMenuBar(GameManager gameManager) {
        JMenuBar menuBar = new JMenuBar();
        
        // قائمة اللعبة
        JMenu gameMenu = new JMenu("🎮 اللعبة");
        
        JMenuItem newGameEasy = new JMenuItem("لعبة جديدة - سهل");
        JMenuItem newGameMedium = new JMenuItem("لعبة جديدة - متوسط");
        JMenuItem newGameHard = new JMenuItem("لعبة جديدة - صعب");
        JMenuItem exitItem = new JMenuItem("خروج");
        
        newGameEasy.addActionListener(e -> 
            gameManager.startNewGame(GameManager.Difficulty.EASY));
        newGameMedium.addActionListener(e -> 
            gameManager.startNewGame(GameManager.Difficulty.MEDIUM));
        newGameHard.addActionListener(e -> 
            gameManager.startNewGame(GameManager.Difficulty.HARD));
        exitItem.addActionListener(e -> System.exit(0));
        
        gameMenu.add(newGameEasy);
        gameMenu.add(newGameMedium);
        gameMenu.add(newGameHard);
        gameMenu.addSeparator();
        gameMenu.add(exitItem);
        
        // قائمة الإحصائيات
        JMenu statsMenu = new JMenu("📊 إحصائيات");
        JMenuItem showStats = new JMenuItem("عرض الإحصائيات");
        
        showStats.addActionListener(e -> {
            String stats = "📈 إحصائيات اللعبة:\n\n" +
                          "النقاط: " + gameManager.getScore() + "\n" +
                          "اليوم: " + gameManager.getDay() + "\n" +
                          "الزلازل: " + gameManager.getEarthquakesTriggered() + "\n" +
                          "الإصلاحات: " + gameManager.getRepairsCompleted() + "\n" +
                          "تكلفة الأضرار: $" + gameManager.getTotalDamageCost() + "\n" +
                          "كفاءة الطاقة: " + String.format("%.1f%%", gameManager.getPowerEfficiency() * 100);
            
            JOptionPane.showMessageDialog(null, stats, "الإحصائيات", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        statsMenu.add(showStats);
        
        menuBar.add(gameMenu);
        menuBar.add(statsMenu);
        
        return menuBar;
    }
}