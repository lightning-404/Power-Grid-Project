package powergrid.ui;

import powergrid.model.*;
import powergrid.game.*;
import powergrid.utils.Constants;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel implements MouseListener, MouseMotionListener {
    private GameEngine gameEngine;
    private int selectedTool; // 1: سلك، 2: محول
    private Cell hoveredCell;
    
    public GamePanel() {
        gameEngine = new GameEngine();
        selectedTool = 1; // السلك هو الأداة الافتراضية
        
        setPreferredSize(new Dimension(
            Constants.GRID_SIZE * Constants.CELL_SIZE,
            Constants.GRID_SIZE * Constants.CELL_SIZE
        ));
        
        setBackground(Color.WHITE);
        addMouseListener(this);
        addMouseMotionListener(this);
        
        // مؤقت لتحديث الواجهة
        Timer timer = new Timer(100, e -> repaint());
        timer.start();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        drawGrid(g2d);
        drawCells(g2d);
        drawHoverEffect(g2d);
        drawPowerFlow(g2d);
    }
    
    private void drawGrid(Graphics2D g2d) {
        g2d.setColor(Color.LIGHT_GRAY);
        
        for (int i = 0; i <= Constants.GRID_SIZE; i++) {
            // خطوط أفقية
            g2d.drawLine(0, i * Constants.CELL_SIZE,
                        Constants.GRID_SIZE * Constants.CELL_SIZE,
                        i * Constants.CELL_SIZE);
            
            // خطوط عمودية
            g2d.drawLine(i * Constants.CELL_SIZE, 0,
                        i * Constants.CELL_SIZE,
                        Constants.GRID_SIZE * Constants.CELL_SIZE);
        }
    }
    
    private void drawCells(Graphics2D g2d) {
        Grid grid = gameEngine.getGrid();
        
        for (int i = 0; i < grid.getWidth(); i++) {
            for (int j = 0; j < grid.getHeight(); j++) {
                Cell cell = grid.getCell(i, j);
                int x = i * Constants.CELL_SIZE;
                int y = j * Constants.CELL_SIZE;
                
                switch(cell.getType()) {
                    case Constants.EMPTY:
                        if (cell.isPowered()) {
                            g2d.setColor(new Color(255, 255, 200)); // أصفر فاتح
                            g2d.fillRect(x, y, Constants.CELL_SIZE, Constants.CELL_SIZE);
                        }
                        break;
                        
                    case Constants.HOUSE:
                        g2d.setColor(Constants.COLOR_HOUSE);
                        g2d.fillRect(x + 2, y + 2, 
                                   Constants.CELL_SIZE - 4, Constants.CELL_SIZE - 4);
                        
                        if (cell.isPowered()) {
                            g2d.setColor(Color.GREEN);
                            g2d.fillOval(x + 10, y + 10, 20, 20);
                        } else {
                            g2d.setColor(Color.RED);
                            g2d.fillOval(x + 10, y + 10, 20, 20);
                        }
                        break;
                        
                    case Constants.POWER_SOURCE:
                        g2d.setColor(Constants.COLOR_POWER_SOURCE);
                        g2d.fillRect(x, y, Constants.CELL_SIZE, Constants.CELL_SIZE);
                        
                        // رسم رمز البرق
                        g2d.setColor(Color.BLACK);
                        int[] xPoints = {x+10, x+20, x+15, x+25, x+10};
                        int[] yPoints = {y+10, y+15, y+20, y+25, y+30};
                        g2d.drawPolyline(xPoints, yPoints, 5);
                        break;
                        
                    case Constants.WIRE:
                        g2d.setColor(Constants.COLOR_WIRE);
                        g2d.fillRect(x + 10, y + 10, 20, 20);
                        
                        if (cell.isPowered()) {
                            g2d.setColor(Color.YELLOW);
                            g2d.drawRect(x + 10, y + 10, 20, 20);
                        }
                        break;
                        
                    case Constants.TRANSFORMER:
                        g2d.setColor(Constants.COLOR_TRANSFORMER);
                        g2d.fillRect(x + 5, y + 5, 30, 30);
                        
                        g2d.setColor(Color.WHITE);
                        g2d.drawString("T", x + 15, y + 25);
                        break;
                        
                    case Constants.OBSTACLE:
                        g2d.setColor(Constants.COLOR_OBSTACLE);
                        g2d.fillRect(x, y, Constants.CELL_SIZE, Constants.CELL_SIZE);
                        break;
                        
                    case Constants.WATER:
                        g2d.setColor(Color.BLUE);
                        g2d.fillRect(x, y, Constants.CELL_SIZE, Constants.CELL_SIZE);
                        break;
                        
                    case Constants.MOUNTAIN:
                        g2d.setColor(Color.GRAY);
                        Polygon mountain = new Polygon();
                        mountain.addPoint(x, y + Constants.CELL_SIZE);
                        mountain.addPoint(x + Constants.CELL_SIZE/2, y);
                        mountain.addPoint(x + Constants.CELL_SIZE, y + Constants.CELL_SIZE);
                        g2d.fillPolygon(mountain);
                        break;
                }
                
                // رسم حدود الخلية
                g2d.setColor(Color.BLACK);
                g2d.drawRect(x, y, Constants.CELL_SIZE, Constants.CELL_SIZE);
            }
        }
    }
    
    private void drawHoverEffect(Graphics2D g2d) {
        if (hoveredCell != null) {
            g2d.setColor(new Color(0, 255, 0, 100)); // أخضر شفاف
            int x = hoveredCell.getX() * Constants.CELL_SIZE;
            int y = hoveredCell.getY() * Constants.CELL_SIZE;
            g2d.fillRect(x, y, Constants.CELL_SIZE, Constants.CELL_SIZE);
            
            // عرض نوع الأداة المحددة
            g2d.setColor(Color.BLACK);
            String tool = (selectedTool == 1) ? "سلك" : "محول";
            g2d.drawString(tool, x + 5, y + 15);
        }
    }
    
    private void drawPowerFlow(Graphics2D g2d) {
        // رسم خطوط تدفق الكهرباء (مبسّط)
        Grid grid = gameEngine.getGrid();
        g2d.setColor(new Color(255, 255, 0, 100));
        g2d.setStroke(new BasicStroke(2));
        
        // ... (رسم خطوط بين الخلايا الموصلة)
    }
    
    // MouseListener methods
    @Override
    public void mouseClicked(MouseEvent e) {
        int gridX = e.getX() / Constants.CELL_SIZE;
        int gridY = e.getY() / Constants.CELL_SIZE;
        
        if (gameEngine.getGrid().isValidPosition(gridX, gridY)) {
            Cell cell = gameEngine.getGrid().getCell(gridX, gridY);
            
            if (cell.getType() == Constants.EMPTY) {
                if (selectedTool == 1) {
                    gameEngine.placeWire(gridX, gridY);
                } else if (selectedTool == 2) {
                    gameEngine.placeTransformer(gridX, gridY);
                }
                repaint();
            }
        }
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
        int gridX = e.getX() / Constants.CELL_SIZE;
        int gridY = e.getY() / Constants.CELL_SIZE;
        
        if (gameEngine.getGrid().isValidPosition(gridX, gridY)) {
            hoveredCell = gameEngine.getGrid().getCell(gridX, gridY);
            repaint();
        }
    }
    
    // باقي دوال MouseListener و MouseMotionListener
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    @Override public void mouseDragged(MouseEvent e) {}
    
    // Getters and Setters
    public void setSelectedTool(int tool) { this.selectedTool = tool; }
    public GameEngine getGameEngine() { return gameEngine; }
}