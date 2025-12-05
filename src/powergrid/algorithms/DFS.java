package powergrid.algorithms;

import powergrid.model.*;
import powergrid.utils.Constants;
import java.util.*;

public class DFS {
    
    public static boolean isGridConnected(Grid grid) {
        boolean[][] visited = new boolean[grid.getWidth()][grid.getHeight()];
        int componentCount = 0;
        
        // البحث عن مصادر الطاقة كنقاط بداية
        for (PowerSource source : grid.getPowerSources()) {
            if (!visited[source.getX()][source.getY()]) {
                dfsVisit(grid, source.getX(), source.getY(), visited);
                componentCount++;
            }
        }
        
        return componentCount <= 1; // متصل إذا كان هناك مكون واحد فقط
    }
    
    private static void dfsVisit(Grid grid, int x, int y, boolean[][] visited) {
        Stack<Cell> stack = new Stack<>();
        stack.push(grid.getCell(x, y));
        
        int[] dx = {0, 1, 0, -1};
        int[] dy = {1, 0, -1, 0};
        
        while (!stack.isEmpty()) {
            Cell current = stack.pop();
            visited[current.getX()][current.getY()] = true;
            
            for (int i = 0; i < 4; i++) {
                int newX = current.getX() + dx[i];
                int newY = current.getY() + dy[i];
                
                if (grid.isValidPosition(newX, newY) && !visited[newX][newY]) {
                    Cell neighbor = grid.getCell(newX, newY);
                    
                    // يمكن الانتقال عبر الأسلاك والمحولات
                    if (neighbor.getType() == Constants.WIRE || 
                        neighbor.getType() == Constants.TRANSFORMER ||
                        neighbor.getType() == Constants.HOUSE ||
                        neighbor.getType() == Constants.POWER_SOURCE) {
                        
                        stack.push(neighbor);
                    }
                }
            }
        }
    }
    
    public static List<Cell> detectShortCircuit(Grid grid) {
        List<Cell> shortCircuitPoints = new ArrayList<>();
        boolean[][] visited = new boolean[grid.getWidth()][grid.getHeight()];
        
        for (int i = 0; i < grid.getWidth(); i++) {
            for (int j = 0; j < grid.getHeight(); j++) {
                Cell cell = grid.getCell(i, j);
                
                if ((cell.getType() == Constants.WIRE || cell.getType() == Constants.TRANSFORMER) 
                    && !visited[i][j]) {
                    
                    if (hasMultiplePowerSources(grid, i, j, visited)) {
                        shortCircuitPoints.add(cell);
                    }
                }
            }
        }
        
        return shortCircuitPoints;
    }
    
    private static boolean hasMultiplePowerSources(Grid grid, int x, int y, boolean[][] visited) {
        // تنفيذ DFS للتحقق من اتصال سلك بمصدرين طاقة أو أكثر
        // (مبسّط للاختصار)
        return false;
    }
}