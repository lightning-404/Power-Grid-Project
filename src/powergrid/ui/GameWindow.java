package powergrid.ui;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {
    
    public GameWindow() {
        setTitle("Power Grid - لعبة توصيل الكهرباء");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // إنشاء المكونات الأساسية
        GamePanel gamePanel = new GamePanel();
        
        try {
            ControlPanel controlPanel = new ControlPanel(gamePanel);
            add(controlPanel, BorderLayout.WEST);
        } catch (Exception e) {
            System.out.println("ControlPanel error: " + e.getMessage());
            // استمر بدون ControlPanel
        }
        
        try {
            StatsPanel statsPanel = new StatsPanel(gamePanel);
            add(statsPanel, BorderLayout.SOUTH);
        } catch (Exception e) {
            System.out.println("StatsPanel error: " + e.getMessage());
            // استمر بدون StatsPanel
        }
        
        add(gamePanel, BorderLayout.CENTER);
        
        pack();
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}