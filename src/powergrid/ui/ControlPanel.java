package powergrid.ui;

import powergrid.model.*;
import powergrid.game.*;
import powergrid.algorithms.*;
import powergrid.utils.Constants;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ControlPanel extends JPanel {
    private GamePanel gamePanel;
    private JButton wireButton, transformerButton;
    private JButton bfsButton, dfsButton;
    private JButton disasterButton, nextLevelButton;
    private JLabel levelLabel;
    
    public ControlPanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        setLayout(new GridLayout(12, 1, 5, 5));
        setBorder(BorderFactory.createTitledBorder("أدوات التحكم"));
        setBackground(new Color(240, 248, 255));
        
        initComponents();
    }
    
    private void initComponents() {
        // عنوان المستوى
        levelLabel = new JLabel("المستوى: 1", SwingConstants.CENTER);
        levelLabel.setFont(new Font("Arial", Font.BOLD, 16));
        levelLabel.setForeground(new Color(0, 51, 102));
        add(levelLabel);
        
        // أزرار الأدوات
        wireButton = createStyledButton("🔌 وضع سلك (10$)", new Color(160, 82, 45));
        transformerButton = createStyledButton("🔄 وضع محول (100$)", new Color(30, 144, 255));
        
        // أزرار الخوارزميات
        bfsButton = createStyledButton("🧭 تشغيل BFS", new Color(60, 179, 113));
        dfsButton = createStyledButton("🔍 تشغيل DFS", new Color(255, 140, 0));
        
        // أزرار خاصة
        disasterButton = createStyledButton("⚠️ كارثة عشوائية", new Color(220, 53, 69));
        nextLevelButton = createStyledButton("🚀 المستوى التالي", new Color(147, 112, 219));
        
        // إضافة المكونات
        add(wireButton);
        add(transformerButton);
        add(new JSeparator());
        add(bfsButton);
        add(dfsButton);
        add(new JSeparator());
        add(disasterButton);
        add(nextLevelButton);
        
        // إضافة المستمعين للأحداث
        addButtonListeners();
        
        // بدء مؤقت لتحديث المستوى
        startLevelUpdateTimer();
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
    
    private void addButtonListeners() {
        wireButton.addActionListener(e -> {
            gamePanel.setSelectedTool(1);
            JOptionPane.showMessageDialog(this,
                "تم اختيار أداة الأسلاك\n"
                + "انقر على خلية فارغة لوضع سلك\n"
                + "التكلفة: 10$",
                "أداة الأسلاك",
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        transformerButton.addActionListener(e -> {
            gamePanel.setSelectedTool(2);
            JOptionPane.showMessageDialog(this,
                "تم اختيار أداة المحولات\n"
                + "انقر على خلية فارغة لوضع محول\n"
                + "التكلفة: 100$",
                "أداة المحولات",
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        bfsButton.addActionListener(e -> {
            // عرض معلومات عن خوارزمية BFS
            String bfsInfo = "<html><div style='text-align: right; direction: rtl; font-family: Arial;'>" +
                "<h3>🧭 خوارزمية BFS (Breadth-First Search)</h3>" +
                "<hr>" +
                "<p><b>الاستخدام في اللعبة:</b> نشر الكهرباء من المصدر إلى المنازل</p>" +
                "<p><b>طريقة العمل:</b> البحث في العرض أولاً</p>" +
                "<p><b>المزايا:</b> تضمن الوصول لأقرب المنازل أولاً</p>" +
                "<p><b>التعقيد الزمني:</b> O(V + E)</p>" +
                "<hr>" +
                "<p>في هذه اللعبة، BFS تستخدم لتوصيل الكهرباء من مصادر الطاقة إلى جميع المنازل المتصلة عبر الأسلاك.</p>" +
                "</div></html>";
            
            JOptionPane.showMessageDialog(this, bfsInfo,
                "معلومات عن خوارزمية BFS",
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        dfsButton.addActionListener(e -> {
            // فحص الاتصال باستخدام DFS
            boolean connected = DFS.isGridConnected(gamePanel.getGameEngine().getGrid());
            
            String message = connected ? 
                "<html><div style='text-align: right; direction: rtl;'>" +
                "<h3>✅ الشبكة متصلة</h3>" +
                "<hr>" +
                "<p>جميع الأجزاء متصلة بشكل صحيح</p>" +
                "<p>يمكن للكهرباء الانتقال بحرية</p>" +
                "</div></html>" :
                
                "<html><div style='text-align: right; direction: rtl;'>" +
                "<h3>⚠️ انتبه! هناك أجزاء غير متصلة</h3>" +
                "<hr>" +
                "<p>بعض أجزاء الشبكة معزولة</p>" +
                "<p>تحقق من الأسلاك والوصلات</p>" +
                "</div></html>";
            
            JOptionPane.showMessageDialog(this, message,
                "نتيجة فحص DFS",
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        disasterButton.addActionListener(e -> {
            String[] disasters = {"عاصفة ⛈️", "زلزال 🌋", "فيضان 🌊"};
            String choice = (String) JOptionPane.showInputDialog(
                this,
                "اختر نوع الكارثة:",
                "كارثة طبيعية",
                JOptionPane.QUESTION_MESSAGE,
                null,
                disasters,
                disasters[0]);
            
            if (choice != null) {
                String disasterType = choice.split(" ")[0]; // أخذ الاسم فقط
                gamePanel.getGameEngine().triggerDisaster(disasterType.toUpperCase());
                gamePanel.repaint();
                
                JOptionPane.showMessageDialog(this,
                    "حدثت كارثة " + choice + "!\n"
                    + "تحقق من تلف الشبكة.",
                    "كارثة طبيعية",
                    JOptionPane.WARNING_MESSAGE);
            }
        });
        
        // ============ زر المستوى التالي (الأهم) ============
        nextLevelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // الحصول على GameEngine
                GameEngine gameEngine = gamePanel.getGameEngine();
                Grid grid = gameEngine.getGrid();
                
                // التحقق من إكمال المستوى الحالي
                int poweredHouses = grid.countPoweredHouses();
                int totalHouses = grid.getHouses().size();
                
                if (totalHouses == 0) {
                    JOptionPane.showMessageDialog(ControlPanel.this,
                        "لا توجد منازل في هذا المستوى!",
                        "خطأ",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (poweredHouses < totalHouses) {
                    String message = "<html><div style='text-align: right; direction: rtl;'>" +
                        "<h3>⚠️ لم تكتمل المهمة!</h3>" +
                        "<hr>" +
                        "<p>يجب توصيل جميع المنازل أولاً.</p>" +
                        "<p>المنازل الموصولة: " + poweredHouses + "/" + totalHouses + "</p>" +
                        "<p>أنت تحتاج إلى توصيل " + (totalHouses - poweredHouses) + " منازل أخرى.</p>";
                    
                    if (totalHouses - poweredHouses == 1) {
                        message += "<p>أنت قريب من النجاح! منزل واحد متبقي.</p>";
                    }
                    
                    message += "</div></html>";
                    
                    JOptionPane.showMessageDialog(ControlPanel.this, message,
                        "المستوى غير مكتمل",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // محاولة الانتقال للمستوى التالي
                boolean success = gameEngine.nextLevel();
                
                if (success) {
                    // إعادة رسم اللعبة
                    gamePanel.repaint();
                    
                    // تحديث تسمية المستوى
                    updateLevelLabel();
                    
                    // تحديث الإحصائيات
                    //if (gamePanel.getStatsPanel() != null) {
                   //     gamePanel.getStatsPanel().updateStats();
                    //}
                }
                // إذا كانت success = false، فهذا يعني أن اللعبة انتهت
                // وقد عرضت GameEngine رسالة النهاية بالفعل
            }
        });
    }
    
    private void startLevelUpdateTimer() {
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateLevelLabel();
            }
        });
        timer.start();
    }
    
    private void updateLevelLabel() {
        GameEngine gameEngine = gamePanel.getGameEngine();
        Grid grid = gameEngine.getGrid();
        
        int currentLevel = gameEngine.getCurrentLevelNumber();
        int poweredHouses = grid.countPoweredHouses();
        int totalHouses = grid.getHouses().size();
        
        String levelText = String.format("المستوى: %d/5 - 🏠 %d/%d", 
            currentLevel, poweredHouses, totalHouses);
        
        // تلوين النص حسب الحالة
        if (poweredHouses == totalHouses && totalHouses > 0) {
            levelLabel.setForeground(new Color(0, 128, 0)); // أخضر - مكتمل
            levelLabel.setText(levelText + " ✓");
        } else if (poweredHouses > 0) {
            levelLabel.setForeground(new Color(255, 140, 0)); // برتقالي - جزئي
            levelLabel.setText(levelText);
        } else {
            levelLabel.setForeground(new Color(0, 51, 102)); // أزرق - مبتدئ
            levelLabel.setText(levelText);
        }
    }
    
    // دالة مساعدة للوصول من GamePanel
    public void updateControlPanel() {
        updateLevelLabel();
    }
}