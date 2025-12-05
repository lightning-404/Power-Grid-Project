package powergrid.algorithms;

import powergrid.model.*;
import powergrid.utils.Constants;
import java.util.*;

public class BFS {
    
    public static List<Cell> findPath(Grid grid, int startX, int startY, int targetX, int targetY) {
        boolean[][] visited = new boolean[grid.getWidth()][grid.getHeight()];
        Cell[][] parent = new Cell[grid.getWidth()][grid.getHeight()];
        Queue<Cell> queue = new LinkedList<>();
        
        Cell startCell = grid.getCell(startX, startY);
        queue.add(startCell);
        visited[startX][startY] = true;
        
        int[] dx = {0, 1, 0, -1}; // اتجاهات الحركة
        int[] dy = {1, 0, -1, 0};
        
        while (!queue.isEmpty()) {
            Cell current = queue.poll();
            
            // إذا وصلنا للهدف
            if (current.getX() == targetX && current.getY() == targetY) {
                return reconstructPath(parent, startCell, current);
            }
            
            // استكشاف الجيران
            for (int i = 0; i < 4; i++) {
                int newX = current.getX() + dx[i];
                int newY = current.getY() + dy[i];
                
                if (grid.isValidPosition(newX, newY) && !visited[newX][newY]) {
                    Cell neighbor = grid.getCell(newX, newY);
                    
                    // يمكن المرور عبر الخلية
                    if (neighbor.isPassable() || neighbor.getType() == Constants.HOUSE) {
                        visited[newX][newY] = true;
                        parent[newX][newY] = current;
                        queue.add(neighbor);
                    }
                }
            }
        }
        
        return new ArrayList<>(); // لا يوجد مسار
    }
    
    private static List<Cell> reconstructPath(Cell[][] parent, Cell start, Cell end) {
        List<Cell> path = new ArrayList<>();
        Cell current = end;
        
        while (current != start) {
            path.add(current);
            current = parent[current.getX()][current.getY()];
        }
        
        Collections.reverse(path);
        return path;
    }
    
    public static void spreadPower(Grid grid, int startX, int startY) {
        boolean[][] visited = new boolean[grid.getWidth()][grid.getHeight()];
        Queue<Cell> queue = new LinkedList<>();
        
        Cell startCell = grid.getCell(startX, startY);
        queue.add(startCell);
        visited[startX][startY] = true;
        
        int[] dx = {0, 1, 0, -1};
        int[] dy = {1, 0, -1, 0};
        
        while (!queue.isEmpty()) {
            Cell current = queue.poll();
            
            // توصيل الكهرباء للخلية الحالية
            current.setPowered(true);
            
            // إذا كانت منزلاً، نقوم بتشغيله
            if (current.getType() == Constants.HOUSE) {
                for (House house : grid.getHouses()) {
                    if (house.getX() == current.getX() && house.getY() == current.getY()) {
                        house.setPowered(true);
                        break;
                    }
                }
            }
            
            // استكشاف الجيران
            for (int i = 0; i < 4; i++) {
                int newX = current.getX() + dx[i];
                int newY = current.getY() + dy[i];
                
                if (grid.isValidPosition(newX, newY) && !visited[newX][newY]) {
                    Cell neighbor = grid.getCell(newX, newY);
                    
                    // يمكن للكهرباء الانتقال عبر الأسلاك أو المحولات
                    if (neighbor.getType() == Constants.WIRE || 
                        neighbor.getType() == Constants.TRANSFORMER ||
                        neighbor.getType() == Constants.HOUSE) {
                        
                        visited[newX][newY] = true;
                        queue.add(neighbor);
                    }
                }
            }
        }
    }
}