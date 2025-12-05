package powergrid.ui;

import powergrid.manager.EffectManager;
import powergrid.manager.EffectListener;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ObstaclePanel extends JPanel {
    private EffectManager effectManager;
    private GameCanvas gameCanvas;
    
    // مكونات الواجهة
    private JButton earthquakeButton;
    private JSlider magnitudeSlider;
    private JLabel statusLabel;
    private JTextArea logArea;
    private JLabel magnitudeValueLabel;
    
    public ObstaclePanel(EffectManager effectManager, GameCanvas canvas) {
        this.effectManager = effectManager;
        this.gameCanvas = canvas;
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(45, 45, 55));
        
        initUI();
    }
    
    private void initUI() {
        // لوحة العنوان
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);
        
        // لوحة التحكم
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.CENTER);
        
        // لوحة السجل
        JPanel logPanel = createLogPanel();
        add(logPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(60, 60, 70));
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(100, 100, 120), 1),
            new EmptyBorder(10, 15, 10, 15)
        ));
        
        JLabel titleLabel = new JLabel("🛠️ أدوات الكوارث");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(220, 220, 255));
        
        JLabel subtitleLabel = new JLabel("تحكم في الكوارث الطبيعية وتأثيراتها");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(180, 180, 200));
        
        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(subtitleLabel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 1, 10, 10));
        panel.setBackground(new Color(55, 55, 65));
        panel.setBorder(new EmptyBorder(20, 10, 20, 10));
        
        // زر الزلزال
        earthquakeButton = createStyledButton("⚡ تفعيل زلزال", 
            new Color(255, 100, 100), new Color(220, 80, 80));
        earthquakeButton.addActionListener(this::onEarthquakeClick);
        
        // شريط تحديد القوة
        JPanel magnitudePanel = new JPanel(new BorderLayout(10, 5));
        magnitudePanel.setBackground(new Color(55, 55, 65));
        
        JLabel magnitudeLabel = new JLabel("قوة الزلزال:");
        magnitudeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        magnitudeLabel.setForeground(Color.WHITE);
        
        magnitudeSlider = new JSlider(1, 10, 5);
        magnitudeSlider.setMajorTickSpacing(1);
        magnitudeSlider.setPaintTicks(true);
        magnitudeSlider.setPaintLabels(true);
        magnitudeSlider.setBackground(new Color(55, 55, 65));
        magnitudeSlider.setForeground(Color.WHITE);
        
        magnitudeValueLabel = new JLabel("5", SwingConstants.CENTER);
        magnitudeValueLabel.setFont(new Font("Arial", Font.BOLD, 16));
        magnitudeValueLabel.setForeground(new Color(255, 200, 100));
        magnitudeValueLabel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 120), 1));
        magnitudeValueLabel.setPreferredSize(new Dimension(40, 25));
        
        magnitudeSlider.addChangeListener(e -> {
            int value = magnitudeSlider.getValue();
            magnitudeValueLabel.setText(String.valueOf(value));
            magnitudeValueLabel.setForeground(getMagnitudeColor(value));
        });
        
        magnitudePanel.add(magnitudeLabel, BorderLayout.WEST);
        magnitudePanel.add(magnitudeSlider, BorderLayout.CENTER);
        magnitudePanel.add(magnitudeValueLabel, BorderLayout.EAST);
        
        // زر الزلزال العشوائي
        JButton randomButton = createStyledButton("🎲 زلزال عشوائي", 
            new Color(100, 150, 255), new Color(80, 130, 235));
        randomButton.addActionListener(e -> {
            effectManager.triggerRandomEarthquake();
            logMessage("🔀 تم تفعيل زلزال عشوائي");
        });
        
        // زر الفيضان
        JButton floodButton = createStyledButton("🌊 تفعيل فيضان", 
            new Color(64, 164, 223), new Color(54, 154, 213));
        floodButton.addActionListener(e -> {
            logMessage("🌊 تفعيل الفيضان غير متوفر حالياً");
        });
        
        // زر العاصفة
        JButton stormButton = createStyledButton("⛈️ تفعيل عاصفة", 
            new Color(150, 150, 255), new Color(130, 130, 235));
        stormButton.addActionListener(e -> {
            logMessage("⛈️ تفعيل العاصفة غير متوفر حالياً");
        });
        
        panel.add(earthquakeButton);
        panel.add(magnitudePanel);
        panel.add(randomButton);
        panel.add(floodButton);
        panel.add(stormButton);
        
        return panel;
    }
    
    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(50, 50, 60));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 120), 1),
            "📝 سجل الأحداث",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12),
            new Color(220, 220, 255)
        ));
        
        logArea = new JTextArea(8, 25);
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        logArea.setForeground(new Color(240, 240, 240));
        logArea.setBackground(new Color(40, 40, 50));
        logArea.setCaretColor(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 90)));
        scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        
        // زر مسح السجل
        JButton clearButton = new JButton("مسح السجل");
        clearButton.setFont(new Font("Arial", Font.PLAIN, 11));
        clearButton.setBackground(new Color(70, 70, 80));
        clearButton.setForeground(Color.WHITE);
        clearButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        clearButton.addActionListener(e -> logArea.setText(""));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(50, 50, 60));
        buttonPanel.add(clearButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JButton createStyledButton(String text, Color bgColor, Color hoverColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(hoverColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(hoverColor);
                } else {
                    g2.setColor(bgColor);
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                g2.setColor(bgColor.brighter());
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                
                g2.dispose();
                
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        
        return button;
    }
    
    private Color getMagnitudeColor(int magnitude) {
        if (magnitude <= 3) return new Color(100, 200, 100);   // أخضر
        if (magnitude <= 6) return new Color(255, 200, 100);   // برتقالي
        return new Color(255, 100, 100);                      // أحمر
    }
    
    private void onEarthquakeClick(ActionEvent e) {
        int magnitude = magnitudeSlider.getValue();
        
        String message = "<html><div style='width:250px;text-align:center'>" +
                        "<b>تفعيل زلزال</b><br><br>" +
                        "قوة الزلزال: " + magnitude + "<br>" +
                        "انقر على الخريطة لتحديد مركز الزلزال" +
                        "</div></html>";
        
        JOptionPane.showMessageDialog(this, message, "تفعيل الزلزال", 
            JOptionPane.INFORMATION_MESSAGE);
        
        if (gameCanvas != null) {
            gameCanvas.setSelectionMode(true, "earthquake", magnitude);
        }
        
        logMessage("📍 اختر مركز الزلزال على الخريطة");
    }
    
    public void logMessage(String message) {
        String timestamp = String.format("[%tT]", new java.util.Date());
        String formattedMessage = String.format("%s %s\n", timestamp, message);
        
        logArea.append(formattedMessage);
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
    
    // كلاس مخصص لشريط التمرير
    class CustomScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = new Color(100, 100, 120);
            this.trackColor = new Color(60, 60, 70);
        }
        
        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }
        
        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }
        
        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }
        
        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2.setColor(thumbColor);
            g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + 2, 
                           thumbBounds.width - 4, thumbBounds.height - 4, 8, 8);
            
            g2.setColor(thumbColor.brighter());
            g2.drawRoundRect(thumbBounds.x + 2, thumbBounds.y + 2, 
                           thumbBounds.width - 4, thumbBounds.height - 4, 8, 8);
            
            g2.dispose();
        }
        
        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2.setColor(trackColor);
            g2.fillRoundRect(trackBounds.x, trackBounds.y, 
                           trackBounds.width, trackBounds.height, 4, 4);
            
            g2.dispose();
        }
    }
}