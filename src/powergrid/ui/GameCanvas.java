package powergrid.ui;

import powergrid.model.*;
import powergrid.manager.EffectManager;
import powergrid.utils.Constants;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class GameCanvas extends JPanel {
    private Grid grid;
    private EffectManager effectManager;
    private String selectionMode;
    private int selectionValue;
    private Point earthquakeEpicenter;
    private List<VisualEffect> visualEffects;
    private Map<Point, DamageIndicator> damageIndicators;
    private long lastUpdateTime;
    private BufferedImage gridTexture;
    private boolean texturesInitialized = false;
    
    // ألوان محسنة
    private final Color GRID_BG = new Color(25, 25, 35);
    private final Color GRID_LINE = new Color(50, 50, 70);
    private final Color WIRE_COLOR = new Color(255, 255, 150);
    private final Color POWERED_WIRE_COLOR = new Color(255, 255, 0);
    private final Color TRANSFORMER_COLOR = new Color(255, 140, 0);
    private final Color HOUSE_COLOR = new Color(100, 200, 100);
    private final Color POWER_SOURCE_COLOR = new Color(220, 60, 60);
    private final Color FACTORY_COLOR = new Color(65, 105, 225);
    private final Color WATER_COLOR = new Color(30, 144, 255);
    private final Color MOUNTAIN_COLOR = new Color(139, 137, 137);
    private final Color OBSTACLE_COLOR = new Color(40, 40, 40);
    private final Color RUBBLE_COLOR = new Color(139, 69, 19);
    private final Color FLOODED_COLOR = new Color(30, 144, 255, 150);
    private final Color CRACK_COLOR = new Color(160, 82, 45);
    private final Color BROKEN_WIRE_COLOR = new Color(128, 0, 0);
    
    // تأثيرات بصرية
    private class VisualEffect {
        int x, y;
        String type;
        long startTime;
        int duration;
        float intensity;
        Color color;
        
        VisualEffect(int x, int y, String type, int duration, float intensity, Color color) {
            this.x = x;
            this.y = y;
            this.type = type;
            this.startTime = System.currentTimeMillis();
            this.duration = duration;
            this.intensity = intensity;
            this.color = color;
        }
        
        boolean isActive() {
            return System.currentTimeMillis() - startTime < duration;
        }
        
        float getProgress() {
            long elapsed = System.currentTimeMillis() - startTime;
            return Math.min(1.0f, (float)elapsed / duration);
        }
    }
    
    // مؤشرات التلف
    private class DamageIndicator {
        int x, y;
        int damageLevel;
        long startTime;
        boolean visible;
        
        DamageIndicator(int x, int y, int damageLevel) {
            this.x = x;
            this.y = y;
            this.damageLevel = damageLevel;
            this.startTime = System.currentTimeMillis();
            this.visible = true;
        }
        
        void update() {
            if (System.currentTimeMillis() - startTime > 10000) {
                visible = false;
            }
        }
    }
    
    public GameCanvas(Grid grid, EffectManager effectManager) {
        this.grid = grid;
        this.effectManager = effectManager;
        this.selectionMode = null;
        this.visualEffects = new ArrayList<>();
        this.damageIndicators = new HashMap<>();
        this.lastUpdateTime = System.currentTimeMillis();
        
        setPreferredSize(new Dimension(1000, 700));
        setBackground(GRID_BG);
        
        // إعداد مؤقت للرسوم المتحركة
        Timer animationTimer = new Timer(30, e -> {
            updateAnimations();
            repaint();
        });
        animationTimer.start();
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleCanvasClick(e.getX(), e.getY());
            }
        });
        
        // تهيئة القوام
        initTextures();
    }
    
    private void initTextures() {
        gridTexture = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = gridTexture.createGraphics();
        
        // خلفية مظلمة مع نمط شبكة خفيف
        g2d.setColor(new Color(30, 30, 40));
        g2d.fillRect(0, 0, 64, 64);
        
        g2d.setColor(new Color(40, 40, 50));
        for (int i = 0; i < 64; i += 8) {
            g2d.drawLine(i, 0, i, 64);
            g2d.drawLine(0, i, 64, i);
        }
        
        g2d.dispose();
        texturesInitialized = true;
    }
    
    private void updateAnimations() {
        long currentTime = System.currentTimeMillis();
        float deltaTime = (currentTime - lastUpdateTime) / 1000.0f;
        lastUpdateTime = currentTime;
        
        // تحديث التأثيرات البصرية
        visualEffects.removeIf(effect -> !effect.isActive());
        
        // تحديث مؤشرات التلف
        damageIndicators.values().forEach(DamageIndicator::update);
        damageIndicators.entrySet().removeIf(entry -> !entry.getValue().visible);
        
        // تحديث الخلايا
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                Cell cell = grid.getCell(x, y);
                if (cell != null && cell.isDamaged()) {
                    Point key = new Point(x, y);
                    if (!damageIndicators.containsKey(key)) {
                        damageIndicators.put(key, new DamageIndicator(x, y, cell.getDamageLevel()));
                        addVisualEffect(x, y, "damage", 1500, 1.0f, new Color(255, 50, 50, 150));
                    }
                }
            }
        }
    }
    
    public void addVisualEffect(int x, int y, String type, int duration, float intensity, Color color) {
        visualEffects.add(new VisualEffect(x, y, type, duration, intensity, color));
    }
    
    public void triggerEarthquakeVisual(int epicenterX, int epicenterY, int magnitude) {
        this.earthquakeEpicenter = new Point(epicenterX, epicenterY);
        
        // تأثير اهتزاز الشاشة
        addVisualEffect(epicenterX, epicenterY, "earthquake_shake", 3000, magnitude / 10.0f, new Color(255, 100, 100, 150));
        
        // موجات الصدمة
        for (int i = 1; i <= magnitude * 2; i++) {
            final int radius = i;
            Timer timer = new Timer(i * 80, e -> addShockwave(epicenterX, epicenterY, radius, magnitude));
            timer.setRepeats(false);
            timer.start();
        }
        
        repaint();
    }
    
    private void addShockwave(int centerX, int centerY, int radius, int magnitude) {
        int points = radius * 12;
        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points;
            int x = (int)(centerX + radius * Math.cos(angle));
            int y = (int)(centerY + radius * Math.sin(angle));
            
            if (x >= 0 && x < grid.getWidth() && y >= 0 && y < grid.getHeight()) {
                addVisualEffect(x, y, "shockwave", 600, 0.7f, new Color(255, 200, 0, 100));
            }
        }
    }
    
    private void handleCanvasClick(int mouseX, int mouseY) {
        int cellSize = 50;
        int gridX = mouseX / cellSize;
        int gridY = mouseY / cellSize;
        
        if (selectionMode != null && grid.isValidPosition(gridX, gridY)) {
            switch(selectionMode) {
                case "earthquake":
                    earthquakeEpicenter = new Point(gridX, gridY);
                    triggerEarthquakeVisual(gridX, gridY, selectionValue);
                    
                    Timer delayTimer = new Timer(500, e -> {
                        if (effectManager != null) {
                            effectManager.triggerEarthquake(gridX, gridY, selectionValue);
                        }
                    });
                    delayTimer.setRepeats(false);
                    delayTimer.start();
                    
                    selectionMode = null;
                    setCursor(Cursor.getDefaultCursor());
                    break;
            }
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // إعدادات الجودة
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        
        // رسم خلفية القوام
        if (texturesInitialized) {
            TexturePaint texture = new TexturePaint(gridTexture, new Rectangle(0, 0, 64, 64));
            g2d.setPaint(texture);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
        
        int cellSize = 50;
        
        // رسم الخلايا
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                Cell cell = grid.getCell(x, y);
                drawCell(g2d, x, y, cellSize, cell);
            }
        }
        
        // رسم التأثيرات البصرية
        drawVisualEffects(g2d, cellSize);
        
        // رسم مركز الزلزال
        if (earthquakeEpicenter != null) {
            drawEarthquakeEpicenter(g2d, earthquakeEpicenter.x, earthquakeEpicenter.y, cellSize);
        }
        
        // رسم مؤشرات التلف
        drawDamageIndicators(g2d, cellSize);
        
        // رسم واجهة المستخدم العلوية
        drawHUD(g2d);
    }
    
    private void drawCell(Graphics2D g2d, int x, int y, int cellSize, Cell cell) {
        int screenX = x * cellSize;
        int screenY = y * cellSize;
        
        // تدرج لوني للخلية
        Color baseColor = getCellColor(cell);
        Color lightColor = baseColor.brighter().brighter();
        Color darkColor = baseColor.darker().darker();
        
        // تأثير ثلاثي الأبعاد
        GradientPaint gradient = new GradientPaint(
            screenX, screenY, lightColor,
            screenX + cellSize, screenY + cellSize, darkColor
        );
        
        // رسم الخلية مع حواف مستديرة
        RoundRectangle2D cellShape = new RoundRectangle2D.Float(
            screenX + 2, screenY + 2, 
            cellSize - 4, cellSize - 4, 
            10, 10
        );
        
        g2d.setPaint(gradient);
        g2d.fill(cellShape);
        
        // حدود الخلية
        g2d.setColor(new Color(255, 255, 255, 50));
        g2d.setStroke(new BasicStroke(1));
        g2d.draw(cellShape);
        
        // إذا كانت الخلية موصلة بالطاقة
        if (cell.isPowered() && !cell.isDamaged()) {
            drawPowerEffect(g2d, screenX, screenY, cellSize);
        }
        
        // إذا كانت الخلية متضررة
        if (cell.isDamaged()) {
            drawDamageEffects(g2d, screenX, screenY, cellSize, cell.getDamageLevel());
        }
        
        // رسم أيقونة الخلية
        drawCellIcon(g2d, screenX, screenY, cellSize, cell);
        
        // رسم معلومات إضافية
        if (cell.getDamageLevel() > 0) {
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 11));
            String damageText = "تلف: " + cell.getDamageLevel();
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(damageText);
            g2d.drawString(damageText, screenX + (cellSize - textWidth)/2, screenY + cellSize - 5);
        }
    }
    
    private Color getCellColor(Cell cell) {
        if (cell.isDamaged()) {
            int damageLevel = cell.getDamageLevel();
            if (damageLevel > 7) return new Color(139, 0, 0);
            if (damageLevel > 4) return new Color(205, 92, 92);
            return new Color(255, 200, 200);
        }
        
        switch(cell.getType()) {
            case Constants.WIRE:
                return cell.isPowered() ? POWERED_WIRE_COLOR : new Color(150, 150, 150);
            case Constants.TRANSFORMER:
                return TRANSFORMER_COLOR;
            case Constants.HOUSE:
                return cell.isPowered() ? HOUSE_COLOR : new Color(100, 100, 100);
            case Constants.POWER_SOURCE:
                return POWER_SOURCE_COLOR;
            case Constants.FACTORY:
                return FACTORY_COLOR;
            case Constants.WATER:
                return WATER_COLOR;
            case Constants.MOUNTAIN:
                return MOUNTAIN_COLOR;
            case Constants.OBSTACLE:
                return OBSTACLE_COLOR;
            case Constants.RUBBLE:
                return RUBBLE_COLOR;
            case Constants.FLOODED:
                return FLOODED_COLOR;
            case Constants.CRACK:
                return CRACK_COLOR;
            case Constants.BROKEN_WIRE:
                return BROKEN_WIRE_COLOR;
            default:
                return new Color(80, 80, 80);
        }
    }
    
    private void drawPowerEffect(Graphics2D g2d, int x, int y, int size) {
        // تأثير توهج للطاقة
        long time = System.currentTimeMillis();
        float pulse = (float)(0.5 + 0.5 * Math.sin(time / 300.0));
        
        RadialGradientPaint glow = new RadialGradientPaint(
            x + size/2, y + size/2, size/2,
            new float[]{0.0f, 1.0f},
            new Color[]{new Color(255, 255, 150, (int)(100 * pulse)), new Color(255, 255, 0, 0)}
        );
        
        g2d.setPaint(glow);
        g2d.fillOval(x, y, size, size);
        
        // خطوط الطاقة المتحركة
        g2d.setColor(new Color(255, 255, 200, 100));
        g2d.setStroke(new BasicStroke(2));
        for (int i = 0; i < 4; i++) {
            double angle = time / 500.0 + i * Math.PI / 2;
            int x1 = x + size/2 + (int)(Math.cos(angle) * size/3);
            int y1 = y + size/2 + (int)(Math.sin(angle) * size/3);
            int x2 = x + size/2 + (int)(Math.cos(angle) * size/2);
            int y2 = y + size/2 + (int)(Math.sin(angle) * size/2);
            g2d.drawLine(x1, y1, x2, y2);
        }
    }
    
    private void drawDamageEffects(Graphics2D g2d, int x, int y, int size, int damageLevel) {
        // تأثير تشققات
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.setStroke(new BasicStroke(1 + damageLevel/2));
        
        for (int i = 0; i < damageLevel * 2; i++) {
            int x1 = x + (int)(Math.random() * size);
            int y1 = y + (int)(Math.random() * size);
            int x2 = x1 + (int)(Math.random() * 15 - 7);
            int y2 = y1 + (int)(Math.random() * 15 - 7);
            g2d.drawLine(x1, y1, x2, y2);
        }
        
        // تأثير دخان للتلف الشديد
        if (damageLevel > 6) {
            long time = System.currentTimeMillis();
            float smokeAlpha = (float)(0.3 + 0.2 * Math.sin(time / 1000.0));
            g2d.setColor(new Color(50, 50, 50, (int)(100 * smokeAlpha)));
            
            for (int i = 0; i < 3; i++) {
                int smokeSize = (int)(size * 0.5 + Math.random() * size * 0.3);
                int smokeX = x + (int)(Math.random() * size/2);
                int smokeY = y + (int)(Math.random() * size/2);
                g2d.fillOval(smokeX, smokeY, smokeSize, smokeSize);
            }
        }
    }
    
    private void drawCellIcon(Graphics2D g2d, int x, int y, int size, Cell cell) {
        String icon = getCellIcon(cell);
        Color iconColor = getIconColor(cell);
        
        // ظل للأيقونة
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        FontMetrics fm = g2d.getFontMetrics();
        int iconWidth = fm.stringWidth(icon);
        int iconHeight = fm.getHeight();
        g2d.drawString(icon, x + (size - iconWidth)/2 + 1, y + (size + iconHeight)/2 - 3);
        
        // الأيقونة
        g2d.setColor(iconColor);
        g2d.drawString(icon, x + (size - iconWidth)/2, y + (size + iconHeight)/2 - 4);
    }
    
    private String getCellIcon(Cell cell) {
        switch(cell.getType()) {
            case Constants.WIRE: return "〰️";
            case Constants.TRANSFORMER: return "⚡";
            case Constants.HOUSE: return "🏠";
            case Constants.POWER_SOURCE: return "🔋";
            case Constants.FACTORY: return "🏭";
            case Constants.WATER: return "💧";
            case Constants.MOUNTAIN: return "⛰️";
            case Constants.OBSTACLE: return "🚧";
            case Constants.BROKEN_WIRE: return "💥";
            case Constants.RUBBLE: return "🧱";
            case Constants.FLOODED: return "🌊";
            case Constants.CRACK: return "🌋";
            default: return "⬜";
        }
    }
    
    private Color getIconColor(Cell cell) {
        if (cell.isDamaged()) return Color.WHITE;
        if (cell.isPowered()) return Color.YELLOW;
        return Color.WHITE;
    }
    
    private void drawVisualEffects(Graphics2D g2d, int cellSize) {
        long currentTime = System.currentTimeMillis();
        
        for (VisualEffect effect : visualEffects) {
            float progress = effect.getProgress();
            float alpha = 1.0f - progress;
            
            int x = effect.x * cellSize;
            int y = effect.y * cellSize;
            
            switch(effect.type) {
                case "earthquake_shake":
                    drawEarthquakeShake(g2d, x, y, cellSize, progress, effect.intensity, effect.color);
                    break;
                case "shockwave":
                    drawShockwave(g2d, x, y, cellSize, progress, alpha, effect.color);
                    break;
                case "damage":
                    drawDamageFlash(g2d, x, y, cellSize, progress, alpha, effect.color);
                    break;
            }
        }
    }
    
    private void drawEarthquakeShake(Graphics2D g2d, int x, int y, int size, float progress, float intensity, Color color) {
        // اهتزاز
        float shakeX = (float)Math.sin(progress * 25) * intensity * 8;
        float shakeY = (float)Math.cos(progress * 20) * intensity * 8;
        
        // دوائر متحدة المركز
        g2d.setStroke(new BasicStroke(2));
        for (int i = 1; i <= 3; i++) {
            float waveProgress = progress * i / 3;
            if (waveProgress > 1) continue;
            
            int waveSize = (int)(size * (1 + waveProgress * 4));
            int alpha = (int)(150 * (1 - waveProgress));
            
            g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
            g2d.drawOval(
                x + (size - waveSize)/2 + (int)shakeX,
                y + (size - waveSize)/2 + (int)shakeY,
                waveSize, waveSize
            );
        }
    }
    
    private void drawShockwave(Graphics2D g2d, int x, int y, int size, float progress, float alpha, Color color) {
        int waveSize = (int)(size * progress * 3);
        g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(alpha * 100)));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(x + (size - waveSize)/2, y + (size - waveSize)/2, waveSize, waveSize);
    }
    
    private void drawDamageFlash(Graphics2D g2d, int x, int y, int size, float progress, float alpha, Color color) {
        // وميض أحمر
        g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(alpha * 150)));
        g2d.fillRect(x, y, size, size);
        
        // علامة تعجب وامضة
        if (progress < 0.3) {
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 28));
            FontMetrics fm = g2d.getFontMetrics();
            String exclamation = "!";
            int textWidth = fm.stringWidth(exclamation);
            g2d.drawString(exclamation, x + (size - textWidth)/2, y + size/2 + 10);
        }
    }
    
    private void drawEarthquakeEpicenter(Graphics2D g2d, int gridX, int gridY, int cellSize) {
        int x = gridX * cellSize;
        int y = gridY * cellSize;
        
        long time = System.currentTimeMillis();
        float pulse = (float)(0.5 + 0.5 * Math.sin(time / 200.0));
        
        // دائرة حمراء نابضة
        RadialGradientPaint epicenterGlow = new RadialGradientPaint(
            x + cellSize/2, y + cellSize/2, cellSize/2,
            new float[]{0.0f, 0.7f, 1.0f},
            new Color[]{
                new Color(255, 0, 0, (int)(200 * pulse)),
                new Color(255, 100, 0, (int)(100 * pulse)),
                new Color(255, 200, 0, 0)
            }
        );
        
        g2d.setPaint(epicenterGlow);
        g2d.fillOval(x - cellSize/2, y - cellSize/2, cellSize * 2, cellSize * 2);
        
        // علامة X
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(x + 5, y + 5, x + cellSize - 5, y + cellSize - 5);
        g2d.drawLine(x + cellSize - 5, y + 5, x + 5, y + cellSize - 5);
        
        // نص "Epicenter"
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        String text = "مركز";
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        g2d.drawString(text, x + (cellSize - textWidth)/2, y - 5);
    }
    
    private void drawDamageIndicators(Graphics2D g2d, int cellSize) {
        for (DamageIndicator indicator : damageIndicators.values()) {
            if (indicator.visible) {
                int x = indicator.x * cellSize;
                int y = indicator.y * cellSize;
                
                long time = System.currentTimeMillis();
                float pulse = (float)(0.5 + 0.5 * Math.sin(time / 400.0));
                
                // دائرة وامضة
                g2d.setColor(new Color(255, 50, 50, (int)(150 * pulse)));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawOval(x - 5, y - 5, cellSize + 10, cellSize + 10);
                
                // نص التلف
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 11));
                String damageText = "تلف: " + indicator.damageLevel;
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(damageText);
                g2d.drawString(damageText, x + (cellSize - textWidth)/2, y - 10);
            }
        }
    }
    
    private void drawHUD(Graphics2D g2d) {
        // حساب الإحصائيات
        int damagedCells = 0;
        int poweredCells = 0;
        
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                Cell cell = grid.getCell(x, y);
                if (cell.isDamaged()) damagedCells++;
                if (cell.isPowered()) poweredCells++;
            }
        }
        
        // رسم لوحة معلومات شبه شفافة
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRoundRect(10, 10, 300, 100, 15, 15);
        
        // حدود اللوحة
        g2d.setColor(new Color(100, 150, 255, 200));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(10, 10, 300, 100, 15, 15);
        
        // نص المعلومات
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        
        String[] lines = {
            "⚡ نظام إدارة الطاقة - شبكة الطاقة",
            "📍 الخلايا النشطة: " + poweredCells,
            "⚠️ الخلايا المتضررة: " + damagedCells,
            earthquakeEpicenter != null ? 
                "🌍 مركز الزلزال: (" + earthquakeEpicenter.x + "," + earthquakeEpicenter.y + ")" : 
                "✅ لا توجد كوارث نشطة"
        };
        
        for (int i = 0; i < lines.length; i++) {
            g2d.drawString(lines[i], 20, 40 + i * 20);
        }
    }
    
    public void setSelectionMode(boolean active, String mode, int value) {
        if (active) {
            this.selectionMode = mode;
            this.selectionValue = value;
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        } else {
            this.selectionMode = null;
            setCursor(Cursor.getDefaultCursor());
        }
    }
    
    public void notifyDamage(int x, int y, int damageLevel) {
        Point key = new Point(x, y);
        damageIndicators.put(key, new DamageIndicator(x, y, damageLevel));
        addVisualEffect(x, y, "damage", 1500, 1.0f, new Color(255, 50, 50, 150));
        repaint();
    }
}