package powergrid.ui;

import powergrid.manager.GameManager;
import powergrid.manager.GameManager.GameStateListener;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.*;

public class GameInfoPanel extends JPanel implements GameStateListener {
    private GameManager gameManager;
    
    // المكونات
    private JLabel scoreLabel, moneyLabel, dayLabel;
    private JLabel powerLabel, housesLabel, crewsLabel;
    private JProgressBar powerBar;
    private JTextArea objectivesArea;
    
    // الألوان
    private final Color PANEL_BG = new Color(45, 45, 55, 220);
    private final Color TEXT_COLOR = new Color(240, 240, 255);
    private final Color VALUE_COLOR = new Color(255, 255, 150);
    
    public GameInfoPanel(GameManager gameManager) {
        this.gameManager = gameManager;
        gameManager.addStateListener(this);
        
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setOpaque(false);
        
        initUI();
        updateDisplay();
    }
    
    private void initUI() {
        // لوحة الإحصائيات
        JPanel statsPanel = createStatsPanel();
        
        // لوحة كفاءة الطاقة
        JPanel powerPanel = createPowerPanel();
        
        // لوحة الأهداف
        JPanel objectivesPanel = createObjectivesPanel();
        
        // تجميع المكونات
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);
        mainPanel.add(statsPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(powerPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(objectivesPanel);
        
        add(mainPanel, BorderLayout.NORTH);
    }
    
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setOpaque(false);
        panel.setBorder(createTitledBorder("📊 إحصائيات اللعبة"));
        
        // النقاط
        panel.add(createLabel("النقاط:"));
        scoreLabel = createValueLabel("0");
        panel.add(scoreLabel);
        
        // المال
        panel.add(createLabel("المال:"));
        moneyLabel = createValueLabel("$0");
        panel.add(moneyLabel);
        
        // اليوم
        panel.add(createLabel("اليوم:"));
        dayLabel = createValueLabel("1");
        panel.add(dayLabel);
        
        return panel;
    }
    
    private JPanel createPowerPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setOpaque(false);
        panel.setBorder(createTitledBorder("⚡ حالة الطاقة"));
        
        // شريط التقدم
        powerBar = new JProgressBar(0, 100);
        powerBar.setStringPainted(true);
        powerBar.setFont(new Font("Arial", Font.BOLD, 11));
        powerBar.setForeground(new Color(100, 200, 100));
        powerBar.setBackground(new Color(50, 50, 60));
        powerBar.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 120), 1));
        
        // تخصيص مظهر شريط التقدم
        powerBar.setUI(new javax.swing.plaf.basic.BasicProgressBarUI() {
            @Override
            protected void paintDeterminate(Graphics g, JComponent c) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Insets b = powerBar.getInsets();
                int barRectWidth = powerBar.getWidth() - b.right - b.left;
                int barRectHeight = powerBar.getHeight() - b.top - b.bottom;
                
                if (barRectWidth <= 0 || barRectHeight <= 0) {
                    return;
                }
                
                int amountFull = getAmountFull(b, barRectWidth, barRectHeight);
                
                // رسم الخلفية
                g2d.setColor(powerBar.getBackground());
                g2d.fillRoundRect(b.left, b.top, barRectWidth, barRectHeight, 8, 8);
                
                // رسم المقدار الممتلئ
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(100, 200, 100),
                    barRectWidth, 0, new Color(50, 150, 50)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(b.left, b.top, amountFull, barRectHeight, 8, 8);
                
                // رسم الحدود
                g2d.setColor(new Color(100, 100, 120));
                g2d.drawRoundRect(b.left, b.top, barRectWidth, barRectHeight, 8, 8);
                
                // رسم النص
                if (powerBar.isStringPainted()) {
                    paintString(g, b.left, b.top, barRectWidth, barRectHeight, amountFull, b);
                }
            }
        });
        
        panel.add(powerBar, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createObjectivesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(createTitledBorder("🎯 الأهداف"));
        
        objectivesArea = new JTextArea(4, 20);
        objectivesArea.setEditable(false);
        objectivesArea.setLineWrap(true);
        objectivesArea.setWrapStyleWord(true);
        objectivesArea.setFont(new Font("Arial", Font.PLAIN, 12));
        objectivesArea.setForeground(TEXT_COLOR);
        objectivesArea.setBackground(new Color(60, 60, 70, 200));
        objectivesArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        objectivesArea.setText("1. وصل الطاقة إلى 5 منازل\n2. وفر $2000 للإصلاحات\n3. احتفظ بـ3 أطقم إصلاح");
        
        JScrollPane scrollPane = new JScrollPane(objectivesArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 120), 1));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        label.setForeground(TEXT_COLOR);
        return label;
    }
    
    private JLabel createValueLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(VALUE_COLOR);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        return label;
    }
    
    private TitledBorder createTitledBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 120), 1),
            title,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12),
            new Color(220, 220, 255)
        );
        border.setTitlePosition(TitledBorder.ABOVE_TOP);
        return border;
    }
    
    private void updateDisplay() {
        SwingUtilities.invokeLater(() -> {
            scoreLabel.setText(String.valueOf(gameManager.getScore()));
            moneyLabel.setText("$" + gameManager.getMoney());
            dayLabel.setText(String.valueOf(gameManager.getDay()));
            
            // تحديث شريط الطاقة
            double efficiency = gameManager.getPowerEfficiency();
            int percentage = (int)(efficiency * 100);
            powerBar.setValue(Math.min(100, percentage));
            powerBar.setString("كفاءة الطاقة: " + percentage + "%");
            
            // تغيير اللون حسب الكفاءة
            if (efficiency >= 1.0) {
                powerBar.setForeground(new Color(100, 200, 100));
            } else if (efficiency >= 0.7) {
                powerBar.setForeground(new Color(255, 200, 100));
            } else {
                powerBar.setForeground(new Color(255, 100, 100));
            }
        });
    }
    
    @Override
    public void onScoreChanged(int newScore) {
        SwingUtilities.invokeLater(() -> {
            scoreLabel.setText(String.valueOf(newScore));
        });
    }
    
    @Override
    public void onMoneyChanged(int newMoney) {
        SwingUtilities.invokeLater(() -> {
            moneyLabel.setText("$" + newMoney);
        });
    }
    
    @Override
    public void onDayChanged(int newDay) {
        SwingUtilities.invokeLater(() -> {
            dayLabel.setText(String.valueOf(newDay));
        });
    }
    
    @Override
    public void onPowerUpdate(int demand, int supply) {
        SwingUtilities.invokeLater(() -> {
            double efficiency = (demand > 0) ? (double) supply / demand : 0;
            int percentage = (int)(efficiency * 100);
            powerBar.setValue(Math.min(100, percentage));
            powerBar.setString("كفاءة الطاقة: " + percentage + "%");
            
            if (efficiency >= 1.0) {
                powerBar.setForeground(new Color(100, 200, 100));
            } else if (efficiency >= 0.7) {
                powerBar.setForeground(new Color(255, 200, 100));
            } else {
                powerBar.setForeground(new Color(255, 100, 100));
            }
        });
    }
    
    @Override
    public void onGameOver(boolean win, String message) {
        SwingUtilities.invokeLater(() -> {
            String title = win ? "🎉 فوز!" : "💀 خسارة!";
            int messageType = win ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE;
            
            JOptionPane.showMessageDialog(this, message, title, messageType);
        });
    }
    
    @Override
    public void onDisasterWarning(String disasterType, int severity) {
        SwingUtilities.invokeLater(() -> {
            String icon = "🌍";
            if (disasterType.contains("عاصفة")) icon = "⛈️";
            if (disasterType.contains("فيضان")) icon = "🌊";
            if (disasterType.contains("حريق")) icon = "🔥";
            
            String warning = icon + " تحذير: " + disasterType + " (شدة: " + severity + ")";
            objectivesArea.append("\n" + warning);
        });
    }
    
    @Override
    public void onNewObjective(String objective) {
        SwingUtilities.invokeLater(() -> {
            objectivesArea.append("\n🎯 " + objective);
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // رسم خلفية شفافة مع تدرج
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(45, 45, 55, 220),
            getWidth(), getHeight(), new Color(35, 35, 45, 220)
        );
        g2d.setPaint(gradient);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        
        // حدود مضيئة
        g2d.setColor(new Color(100, 150, 255, 100));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 15, 15);
        
        g2d.dispose();
        super.paintComponent(g);
    }
    
    public void cleanup() {
        gameManager.removeStateListener(this);
    }
}