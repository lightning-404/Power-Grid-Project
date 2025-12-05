package powergrid.ui;

import powergrid.model.*;
import powergrid.game.GameEngine;
import powergrid.algorithms.PathFinder;
import powergrid.utils.Constants;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * لوحة الإحصائيات والمعلومات - تعرض حالة اللعبة الحالية
 * تحتوي على: النقاط، الميزانية، الوقت، المنازل الموصولة، إحصائيات الخوارزميات
 */
public class StatsPanel extends JPanel {
    private GamePanel gamePanel;
    private GameEngine gameEngine;
    
    // العلامات للإحصائيات
    private JLabel scoreLabel;
    private JLabel budgetLabel;
    private JLabel timeLabel;
    private JLabel housesLabel;
    private JLabel algorithmLabel;
    private JLabel pathLengthLabel;
    private JLabel efficiencyLabel;
    
    // شريط التقدم
    private JProgressBar progressBar;
    
    // أزرار التحكم
    private JButton statsButton;
    private JButton algorithmInfoButton;
    
    // مؤقت لتحديث الإحصائيات
    private Timer updateTimer;
    private long startTime;
    private int elapsedSeconds;
    
    // ألوان
    private final Color PANEL_COLOR = new Color(240, 248, 255); // لون فاتح
    private final Color TEXT_COLOR = new Color(0, 51, 102); // أزرق داكن
    private final Color POSITIVE_COLOR = new Color(0, 128, 0); // أخضر
    private final Color NEGATIVE_COLOR = new Color(204, 0, 0); // أحمر
    
    public StatsPanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        this.gameEngine = gamePanel.getGameEngine();
        this.startTime = System.currentTimeMillis();
        this.elapsedSeconds = 0;
        
        initComponents();
        setupLayout();
        startUpdateTimer();
    }
    
    private void initComponents() {
        // تعيين خلفية اللوحة
        setBackground(PANEL_COLOR);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("📊 لوحة الإحصائيات"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // تهيئة العلامات
        scoreLabel = createStyledLabel("النقاط: 0", TEXT_COLOR, Font.BOLD, 14);
        budgetLabel = createStyledLabel("الميزانية: $" + Constants.INITIAL_BUDGET, TEXT_COLOR, Font.BOLD, 14);
        timeLabel = createStyledLabel("الوقت: 00:00", TEXT_COLOR, Font.PLAIN, 12);
        housesLabel = createStyledLabel("المنازل: 0/0 موصولة", TEXT_COLOR, Font.PLAIN, 12);
        algorithmLabel = createStyledLabel("الخوارزمية: BFS", new Color(0, 102, 204), Font.BOLD, 12);
        pathLengthLabel = createStyledLabel("طول المسار: -", TEXT_COLOR, Font.PLAIN, 12);
        efficiencyLabel = createStyledLabel("الكفاءة: 0%", TEXT_COLOR, Font.PLAIN, 12);
        
        // شريط التقدم لتقدم المستوى
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(0, 153, 76)); // أخضر
        progressBar.setBackground(new Color(220, 220, 220));
        progressBar.setString("تقدم المستوى: 0%");
        
        // أزرار التحكم
        statsButton = createStyledButton("📈 إحصائيات متقدمة", new Color(70, 130, 180));
        algorithmInfoButton = createStyledButton("ℹ️ معلومات الخوارزميات", new Color(100, 149, 237));
        
        // إضافة المستمعين للأزرار
        addButtonListeners();
    }
    
    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;
        
        // الصف 1: النقاط والميزانية
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(scoreLabel, gbc);
        
        gbc.gridx = 1;
        add(budgetLabel, gbc);
        
        // الصف 2: الوقت والمنازل
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(timeLabel, gbc);
        
        gbc.gridx = 1;
        add(housesLabel, gbc);
        
        // الصف 3: معلومات الخوارزميات
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(algorithmLabel, gbc);
        
        gbc.gridx = 1;
        add(pathLengthLabel, gbc);
        
        // الصف 4: الكفاءة
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(efficiencyLabel, gbc);
        
        // الصف 5: شريط التقدم
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.BOTH;
        add(progressBar, gbc);
        
        // الصف 6: الأزرار
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setBackground(PANEL_COLOR);
        buttonPanel.add(statsButton);
        buttonPanel.add(algorithmInfoButton);
        add(buttonPanel, gbc);
    }
    
    private void startUpdateTimer() {
        updateTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateStats();
            }
        });
        updateTimer.start();
    }
    
    /**
     * تحديث جميع الإحصائيات
     */
    public void updateStats() {
        if (gameEngine == null || gameEngine.getGrid() == null) return;
        
        // تحديث الوقت المنقضي
        elapsedSeconds = (int)((System.currentTimeMillis() - startTime) / 1000);
        int minutes = elapsedSeconds / 60;
        int seconds = elapsedSeconds % 60;
        timeLabel.setText(String.format("الوقت: %02d:%02d", minutes, seconds));
        
        // تحديث النقاط والميزانية
        scoreLabel.setText("النقاط: " + gameEngine.getScore());
        budgetLabel.setText("الميزانية: $" + gameEngine.getBudget());
        
        // تحديث حالة المنازل
        Grid grid = gameEngine.getGrid();
        int totalHouses = grid.getHouses().size();
        int poweredHouses = grid.countPoweredHouses();
        
        housesLabel.setText(String.format("المنازل: %d/%d موصولة", poweredHouses, totalHouses));
        
        // تحديث لون المنازل بناءً على الحالة
        if (poweredHouses == totalHouses && totalHouses > 0) {
            housesLabel.setForeground(POSITIVE_COLOR);
            housesLabel.setFont(new Font(housesLabel.getFont().getName(), Font.BOLD, 12));
        } else if (poweredHouses > 0) {
            housesLabel.setForeground(new Color(255, 140, 0)); // برتقالي
        } else {
            housesLabel.setForeground(NEGATIVE_COLOR);
        }
        
        // تحديث معلومات الخوارزميات
        updateAlgorithmStats();
        
        // تحديث شريط التقدم
        updateProgressBar(poweredHouses, totalHouses);
        
        // تحديث الكفاءة
        updateEfficiency();
        
        // إعادة رسم اللوحة
        revalidate();
        repaint();
    }
    
    /**
     * تحديث إحصائيات الخوارزميات
     */
    private void updateAlgorithmStats() {
        Grid grid = gameEngine.getGrid();
        
        // إذا كان هناك منازل ومصادر طاقة
        if (!grid.getHouses().isEmpty() && !grid.getPowerSources().isEmpty()) {
            // استخدام الخوارزمية الأولى (BFS) كمثال
            House firstHouse = grid.getHouses().get(0);
            PowerSource firstSource = grid.getPowerSources().get(0);
            
            // حساب المسار باستخدام BFS
            List<Cell> path = PathFinder.findShortestPathBFS(
                grid, 
                firstSource.getX(), firstSource.getY(),
                firstHouse.getX(), firstHouse.getY()
            );
            
            if (!path.isEmpty()) {
                algorithmLabel.setText("الخوارزمية: BFS");
                pathLengthLabel.setText("طول المسار: " + path.size() + " خلية");
                
                // عرض نوع المسار بناءً على الطول
                if (path.size() < 5) {
                    pathLengthLabel.setForeground(POSITIVE_COLOR);
                } else if (path.size() < 10) {
                    pathLengthLabel.setForeground(new Color(255, 140, 0)); // برتقالي
                } else {
                    pathLengthLabel.setForeground(NEGATIVE_COLOR);
                }
            } else {
                algorithmLabel.setText("الخوارزمية: -");
                pathLengthLabel.setText("طول المسار: لا يوجد مسار");
                pathLengthLabel.setForeground(NEGATIVE_COLOR);
            }
        } else {
            algorithmLabel.setText("الخوارزمية: بانتظار المدخلات");
            pathLengthLabel.setText("طول المسار: -");
        }
    }
    
    /**
     * تحديث شريط التقدم
     */
    private void updateProgressBar(int poweredHouses, int totalHouses) {
        if (totalHouses > 0) {
            int progress = (int)((poweredHouses * 100.0) / totalHouses);
            progressBar.setValue(progress);
            progressBar.setString("تقدم المستوى: " + progress + "%");
            
            // تغيير لون شريط التقدم بناءً على التقدم
            if (progress >= 80) {
                progressBar.setForeground(POSITIVE_COLOR);
            } else if (progress >= 50) {
                progressBar.setForeground(new Color(255, 165, 0)); // برتقالي
            } else {
                progressBar.setForeground(NEGATIVE_COLOR);
            }
        } else {
            progressBar.setValue(0);
            progressBar.setString("لا توجد منازل");
        }
    }
    
    /**
     * تحديث كفاءة الشبكة
     */
    private void updateEfficiency() {
        Grid grid = gameEngine.getGrid();
        int totalHouses = grid.getHouses().size();
        int poweredHouses = grid.countPoweredHouses();
        
        if (totalHouses > 0) {
            int efficiency = (int)((poweredHouses * 100.0) / totalHouses);
            efficiencyLabel.setText("الكفاءة: " + efficiency + "%");
            
            // تغيير لون الكفاءة
            if (efficiency >= 80) {
                efficiencyLabel.setForeground(POSITIVE_COLOR);
                efficiencyLabel.setFont(new Font(efficiencyLabel.getFont().getName(), Font.BOLD, 12));
            } else if (efficiency >= 50) {
                efficiencyLabel.setForeground(new Color(255, 140, 0));
            } else {
                efficiencyLabel.setForeground(NEGATIVE_COLOR);
            }
        } else {
            efficiencyLabel.setText("الكفاءة: 0%");
            efficiencyLabel.setForeground(TEXT_COLOR);
        }
    }
    
    /**
     * إضافة مستمعي الأحداث للأزرار
     */
    private void addButtonListeners() {
        statsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAdvancedStats();
            }
        });
        
        algorithmInfoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAlgorithmInfo();
            }
        });
    }
    
    /**
     * عرض إحصائيات متقدمة
     */
    private void showAdvancedStats() {
        Grid grid = gameEngine.getGrid();
        StringBuilder stats = new StringBuilder();
        
        stats.append("<html><div style='text-align: right; direction: rtl; font-family: Arial;'>");
        stats.append("<h2 style='color: #0066cc;'>📊 الإحصائيات المتقدمة</h2>");
        stats.append("<hr>");
        
        // معلومات الشبكة
        stats.append("<h3>معلومات الشبكة:</h3>");
        stats.append("<table style='width:100%; border-collapse: collapse;'>");
        stats.append("<tr><td>حجم الشبكة:</td><td><b>").append(grid.getWidth()).append("×").append(grid.getHeight()).append("</b></td></tr>");
        stats.append("<tr><td>عدد المنازل:</td><td><b>").append(grid.getHouses().size()).append("</b></td></tr>");
        stats.append("<tr><td>منازل موصولة:</td><td><b style='color: ").append(grid.countPoweredHouses() > 0 ? "green" : "red").append(";'>")
             .append(grid.countPoweredHouses()).append("</b></td></tr>");
        stats.append("<tr><td>مصادر الطاقة:</td><td><b>").append(grid.getPowerSources().size()).append("</b></td></tr>");
        stats.append("<tr><td>الأسلاك المثبتة:</td><td><b>").append(countWires(grid)).append("</b></td></tr>");
        stats.append("</table>");
        
        // إحصائيات الأداء
        stats.append("<h3>إحصائيات الأداء:</h3>");
        stats.append("<table style='width:100%; border-collapse: collapse;'>");
        stats.append("<tr><td>الوقت المنقضي:</td><td><b>").append(elapsedSeconds).append(" ثانية</b></td></tr>");
        stats.append("<tr><td>النقاط الحالية:</td><td><b>").append(gameEngine.getScore()).append("</b></td></tr>");
        stats.append("<tr><td>الميزانية المتبقية:</td><td><b style='color: ").append(gameEngine.getBudget() > 500 ? "green" : "orange").append(";'>$")
             .append(gameEngine.getBudget()).append("</b></td></tr>");
        stats.append("<tr><td>كفاءة الشبكة:</td><td><b>").append(calculateEfficiency(grid)).append("%</b></td></tr>");
        stats.append("</table>");
        
        // معلومات الخوارزميات
        stats.append("<h3>معلومات الخوارزميات:</h3>");
        stats.append("<ul>");
        stats.append("<li><b>BFS:</b> البحث في العرض أولاً - يستخدم لتوزيع الكهرباء</li>");
        stats.append("<li><b>DFS:</b> البحث في العمق أولاً - يستخدم للتحقق من الاتصال</li>");
        stats.append("<li><b>A*:</b> إيجاد أقصر مسار مع تكلفة</li>");
        stats.append("<li><b>UCS:</b> البحث بأقل تكلفة</li>");
        stats.append("</ul>");
        
        stats.append("</div></html>");
        
        JOptionPane.showMessageDialog(this, stats.toString(), 
            "الإحصائيات المتقدمة", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * عرض معلومات عن الخوارزميات
     */
    private void showAlgorithmInfo() {
        String info = "<html><div style='text-align: right; direction: rtl; font-family: Arial; line-height: 1.6;'>" +
            "<h2 style='color: #0066cc;'>ℹ️ معلومات الخوارزميات المستخدمة</h2>" +
            "<hr>" +
            
            "<h3>🎯 خوارزمية BFS (Breadth-First Search):</h3>" +
            "<p><b>الاستخدام:</b> توزيع الكهرباء على المنازل</p>" +
            "<p><b>المبدأ:</b> البحث في العرض أولاً - تفحص جميع الجيران قبل الانتقال للعمق</p>" +
            "<p><b>المزايا:</b> تضمن الوصول لأقرب المنازل أولاً، توزيع متساوٍ</p>" +
            "<p><b>التعقيد الزمني:</b> O(V + E) حيث V عدد الرؤوس و E عدد الأضلاع</p>" +
            
            "<h3>🔍 خوارزمية DFS (Depth-First Search):</h3>" +
            "<p><b>الاستخدام:</b> التحقق من اتصال الشبكة</p>" +
            "<p><b>المبدأ:</b> البحث في العمق أولاً - تستكشف كل فرع حتى النهاية</p>" +
            "<p><b>المزايا:</b> كشف الأعطال في الشبكة، اكتشاف العيوب</p>" +
            "<p><b>التعقيد الزمني:</b> O(V + E)</p>" +
            
            "<h3>⭐ خوارزمية A* (A-Star):</h3>" +
            "<p><b>الاستخدام:</b> إيجاد أقصر مسار مع مراعاة التكلفة</p>" +
            "<p><b>المبدأ:</b> تجمع بين تكلفة المسار وتقدير المسافة للهدف</p>" +
            "<p><b>المزايا:</b> فعالة في إيجاد المسار الأمثل، سريعة</p>" +
            
            "<h3>💰 خوارزمية UCS (Uniform Cost Search):</h3>" +
            "<p><b>الاستخدام:</b> إيجاد المسار بأقل تكلفة مالية</p>" +
            "<p><b>المبدأ:</b> تختار دائماً المسار الأقل تكلفة</p>" +
            "<p><b>المزايا:</b> تضمن أقل تكلفة ممكنة</p>" +
            
            "<hr>" +
            "<p style='color: #666; font-size: 12px;'>" +
            "📚 هذه الخوارزميات جزء من مادة خوارزميات البحث الذكية<br>" +
            "💡 يمكن تغيير الخوارزمية حسب احتياجات اللعبة" +
            "</p>" +
            "</div></html>";
        
        JTextPane textPane = new JTextPane();
        textPane.setContentType("text/html");
        textPane.setText(info);
        textPane.setEditable(false);
        textPane.setPreferredSize(new Dimension(500, 600));
        
        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setPreferredSize(new Dimension(550, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, 
            "معلومات الخوارزميات", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * دوال مساعدة
     */
    private JLabel createStyledLabel(String text, Color color, int style, int size) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", style, size));
        label.setForeground(color);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        return label;
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // تأثير عند المرور فوق الزر
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    private int countWires(Grid grid) {
        int count = 0;
        Cell[][] cells = grid.getCells();
        for (int i = 0; i < grid.getWidth(); i++) {
            for (int j = 0; j < grid.getHeight(); j++) {
                if (cells[i][j].getType() == Constants.WIRE) {
                    count++;
                }
            }
        }
        return count;
    }
    
    private int calculateEfficiency(Grid grid) {
        int totalHouses = grid.getHouses().size();
        if (totalHouses == 0) return 0;
        
        int poweredHouses = grid.countPoweredHouses();
        return (poweredHouses * 100) / totalHouses;
    }
    
    /**
     * دوال عامة للوصول من الخارج
     */
    public void setGameEngine(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
        updateStats();
    }
    
    public void resetTimer() {
        startTime = System.currentTimeMillis();
        elapsedSeconds = 0;
        updateStats();
    }
    
    public void stopTimer() {
        if (updateTimer != null) {
            updateTimer.stop();
        }
    }
    
    public int getElapsedSeconds() {
        return elapsedSeconds;
    }
}