package powergrid.algorithms;

import powergrid.model.*;
import powergrid.utils.Constants;
import java.util.*;

/**
 * كلاس متخصص في إيجاد المسارات في الشبكة الكهربائية
 * يجمع بين خوارزميات متعددة للبحث عن أفضل المسارات
 */
public class PathFinder {
    
    /**
     * إيجاد أقصر مسار بين نقطتين باستخدام BFS
     * @param grid الشبكة
     * @param startX نقطة البداية X
     * @param startY نقطة البداية Y
     * @param targetX نقطة الهدف X
     * @param targetY نقطة الهدف Y
     * @return قائمة بالخلايا المكونة للمسار
     */
    public static List<Cell> findShortestPathBFS(Grid grid, int startX, int startY, 
                                                 int targetX, int targetY) {
        if (!grid.isValidPosition(startX, startY) || !grid.isValidPosition(targetX, targetY)) {
            return new ArrayList<>();
        }
        
        // إذا كانت النقطتان نفسها
        if (startX == targetX && startY == targetY) {
            List<Cell> path = new ArrayList<>();
            path.add(grid.getCell(startX, startY));
            return path;
        }
        
        boolean[][] visited = new boolean[grid.getWidth()][grid.getHeight()];
        Cell[][] parent = new Cell[grid.getWidth()][grid.getHeight()];
        Queue<Cell> queue = new LinkedList<>();
        
        Cell startCell = grid.getCell(startX, startY);
        queue.add(startCell);
        visited[startX][startY] = true;
        
        // اتجاهات الحركة (أعلى، يمين، أسفل، يسار)
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        
        while (!queue.isEmpty()) {
            Cell current = queue.poll();
            
            // إذا وصلنا للهدف
            if (current.getX() == targetX && current.getY() == targetY) {
                return reconstructPath(parent, startCell, current);
            }
            
            // استكشاف الجيران
            for (int[] dir : directions) {
                int newX = current.getX() + dir[0];
                int newY = current.getY() + dir[1];
                
                if (grid.isValidPosition(newX, newY) && !visited[newX][newY]) {
                    Cell neighbor = grid.getCell(newX, newY);
                    
                    // يمكن المرور عبر الخلية
                    if (isPassable(neighbor)) {
                        visited[newX][newY] = true;
                        parent[newX][newY] = current;
                        queue.add(neighbor);
                    }
                }
            }
        }
        
        return new ArrayList<>(); // لا يوجد مسار
    }
    
    /**
     * إيجاد المسار بأقل تكلفة باستخدام Uniform Cost Search
     * @param grid الشبكة
     * @param startX نقطة البداية X
     * @param startY نقطة البداية Y
     * @param targetX نقطة الهدف X
     * @param targetY نقطة الهدف Y
     * @return المسار بأقل تكلفة
     */
    public static List<Cell> findCheapestPathUCS(Grid grid, int startX, int startY,
                                                 int targetX, int targetY) {
        // تنفيذ UCS
        PriorityQueue<Node> frontier = new PriorityQueue<>(Comparator.comparingInt(n -> n.cost));
        boolean[][] visited = new boolean[grid.getWidth()][grid.getHeight()];
        Cell[][] parent = new Cell[grid.getWidth()][grid.getHeight()];
        int[][] costSoFar = new int[grid.getWidth()][grid.getHeight()];
        
        // تهيئة المصفوفات
        for (int i = 0; i < grid.getWidth(); i++) {
            Arrays.fill(costSoFar[i], Integer.MAX_VALUE);
        }
        
        Cell startCell = grid.getCell(startX, startY);
        frontier.add(new Node(startCell, 0));
        costSoFar[startX][startY] = 0;
        
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        
        while (!frontier.isEmpty()) {
            Node current = frontier.poll();
            Cell currentCell = current.cell;
            
            if (visited[currentCell.getX()][currentCell.getY()]) {
                continue;
            }
            
            visited[currentCell.getX()][currentCell.getY()] = true;
            
            // إذا وصلنا للهدف
            if (currentCell.getX() == targetX && currentCell.getY() == targetY) {
                return reconstructPath(parent, startCell, currentCell);
            }
            
            // استكشاف الجيران
            for (int[] dir : directions) {
                int newX = currentCell.getX() + dir[0];
                int newY = currentCell.getY() + dir[1];
                
                if (grid.isValidPosition(newX, newY) && !visited[newX][newY]) {
                    Cell neighbor = grid.getCell(newX, newY);
                    
                    if (isPassable(neighbor)) {
                        int newCost = costSoFar[currentCell.getX()][currentCell.getY()] 
                                    + getMoveCost(currentCell, neighbor);
                        
                        if (newCost < costSoFar[newX][newY]) {
                            costSoFar[newX][newY] = newCost;
                            parent[newX][newY] = currentCell;
                            frontier.add(new Node(neighbor, newCost));
                        }
                    }
                }
            }
        }
        
        return new ArrayList<>();
    }
    
    /**
     * إيجاد المسار باستخدام خوارزمية A*
     * @param grid الشبكة
     * @param startX نقطة البداية X
     * @param startY نقطة البداية Y
     * @param targetX نقطة الهدف X
     * @param targetY نقطة الهدف Y
     * @return المسار باستخدام A*
     */
    public static List<Cell> findPathAStar(Grid grid, int startX, int startY,
                                           int targetX, int targetY) {
        // تنفيذ A* Star
        PriorityQueue<AStarNode> openSet = new PriorityQueue<>(
            Comparator.comparingInt(n -> n.fCost)
        );
        
        boolean[][] closedSet = new boolean[grid.getWidth()][grid.getHeight()];
        Cell[][] cameFrom = new Cell[grid.getWidth()][grid.getHeight()];
        int[][] gScore = new int[grid.getWidth()][grid.getHeight()];
        int[][] fScore = new int[grid.getWidth()][grid.getHeight()];
        
        // تهيئة
        for (int i = 0; i < grid.getWidth(); i++) {
            Arrays.fill(gScore[i], Integer.MAX_VALUE);
            Arrays.fill(fScore[i], Integer.MAX_VALUE);
        }
        
        Cell startCell = grid.getCell(startX, startY);
        Cell targetCell = grid.getCell(targetX, targetY);
        
        gScore[startX][startY] = 0;
        fScore[startX][startY] = heuristic(startCell, targetCell);
        
        openSet.add(new AStarNode(startCell, fScore[startX][startY]));
        
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0},
                             {1, 1}, {1, -1}, {-1, 1}, {-1, -1}}; // مع القطر
        
        while (!openSet.isEmpty()) {
            AStarNode current = openSet.poll();
            Cell currentCell = current.cell;
            
            if (currentCell.getX() == targetX && currentCell.getY() == targetY) {
                return reconstructPath(cameFrom, startCell, currentCell);
            }
            
            closedSet[currentCell.getX()][currentCell.getY()] = true;
            
            for (int[] dir : directions) {
                int newX = currentCell.getX() + dir[0];
                int newY = currentCell.getY() + dir[1];
                
                // تجاهل الحركات القطرية إذا كان أحد الجوانب غير سالك
                if (Math.abs(dir[0]) == 1 && Math.abs(dir[1]) == 1) {
                    if (!grid.isValidPosition(currentCell.getX() + dir[0], currentCell.getY()) ||
                        !grid.isValidPosition(currentCell.getX(), currentCell.getY() + dir[1])) {
                        continue;
                    }
                }
                
                if (grid.isValidPosition(newX, newY) && !closedSet[newX][newY]) {
                    Cell neighbor = grid.getCell(newX, newY);
                    
                    if (isPassable(neighbor)) {
                        int tentativeGScore = gScore[currentCell.getX()][currentCell.getY()] 
                                            + getMoveCost(currentCell, neighbor);
                        
                        if (tentativeGScore < gScore[newX][newY]) {
                            cameFrom[newX][newY] = currentCell;
                            gScore[newX][newY] = tentativeGScore;
                            fScore[newX][newY] = gScore[newX][newY] 
                                               + heuristic(neighbor, targetCell);
                            
                            openSet.add(new AStarNode(neighbor, fScore[newX][newY]));
                        }
                    }
                }
            }
        }
        
        return new ArrayList<>();
    }
    
    /**
     * البحث عن جميع المنازل القابلة للوصول من مصدر طاقة
     * @param grid الشبكة
     * @param sourceX موقع مصدر الطاقة X
     * @param sourceY موقع مصدر الطاقة Y
     * @return قائمة بالمنازل القابلة للوصول
     */
    public static List<House> findReachableHouses(Grid grid, int sourceX, int sourceY) {
        List<House> reachableHouses = new ArrayList<>();
        boolean[][] visited = new boolean[grid.getWidth()][grid.getHeight()];
        Queue<Cell> queue = new LinkedList<>();
        
        Cell start = grid.getCell(sourceX, sourceY);
        queue.add(start);
        visited[sourceX][sourceY] = true;
        
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        
        while (!queue.isEmpty()) {
            Cell current = queue.poll();
            
            // إذا كانت الخلية منزلاً، أضفه للقائمة
            if (current.getType() == Constants.HOUSE) {
                for (House house : grid.getHouses()) {
                    if (house.getX() == current.getX() && house.getY() == current.getY()) {
                        reachableHouses.add(house);
                        break;
                    }
                }
            }
            
            // استكشاف الجيران
            for (int[] dir : directions) {
                int newX = current.getX() + dir[0];
                int newY = current.getY() + dir[1];
                
                if (grid.isValidPosition(newX, newY) && !visited[newX][newY]) {
                    Cell neighbor = grid.getCell(newX, newY);
                    
                    // يمكن للكهرباء المرور عبر الأسلاك والمحولات والمنازل الفارغة
                    if (isPowerTransmittable(neighbor)) {
                        visited[newX][newY] = true;
                        queue.add(neighbor);
                    }
                }
            }
        }
        
        return reachableHouses;
    }
    
    /**
     * التحقق مما إذا كان المنزل معزولاً (لا يمكن الوصول له)
     * @param grid الشبكة
     * @param house المنزل
     * @return true إذا كان معزولاً
     */
    public static boolean isHouseIsolated(Grid grid, House house) {
        // البحث عن أقرب مصدر طاقة
        for (PowerSource source : grid.getPowerSources()) {
            List<Cell> path = findShortestPathBFS(grid, source.getX(), source.getY(), 
                                                  house.getX(), house.getY());
            if (!path.isEmpty()) {
                return false; // تم العثور على مسار، المنزل ليس معزولاً
            }
        }
        return true; // لا يوجد مسار لأي مصدر طاقة
    }
    
    /**
     * حساب تكلفة توصيل منزل بمصدر طاقة
     * @param grid الشبكة
     * @param house المنزل
     * @param source مصدر الطاقة
     * @return التكلفة الإجمالية
     */
    public static int calculateConnectionCost(Grid grid, House house, PowerSource source) {
        List<Cell> path = findShortestPathBFS(grid, source.getX(), source.getY(), 
                                              house.getX(), house.getY());
        
        if (path.isEmpty()) {
            return Integer.MAX_VALUE; // لا يوجد مسار
        }
        
        int totalCost = 0;
        for (Cell cell : path) {
            totalCost += getTerrainCost(cell);
        }
        
        return totalCost;
    }
    
    // ============ الدوال المساعدة ============
    
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
    
    private static boolean isPassable(Cell cell) {
        if (cell == null) return false;
        
        return cell.getType() == Constants.EMPTY ||
               cell.getType() == Constants.WIRE ||
               cell.getType() == Constants.TRANSFORMER ||
               cell.getType() == Constants.HOUSE ||
               cell.getType() == Constants.POWER_SOURCE;
    }
    
    private static boolean isPowerTransmittable(Cell cell) {
        if (cell == null) return false;
        
        return cell.getType() == Constants.WIRE ||
               cell.getType() == Constants.TRANSFORMER ||
               cell.getType() == Constants.HOUSE ||
               cell.getType() == Constants.EMPTY ||
               cell.getType() == Constants.POWER_SOURCE;
    }
    
    private static int getMoveCost(Cell from, Cell to) {
        // تكلفة الحركة تعتمد على نوع التضاريس
        int baseCost = 10;
        
        switch (to.getType()) {
            case Constants.EMPTY:
                return baseCost;
            case Constants.WIRE:
                return baseCost / 2; // الأسلاك أرخص للحركة
            case Constants.WATER:
                return baseCost * 3;
            case Constants.MOUNTAIN:
                return baseCost * 2;
            default:
                return baseCost;
        }
    }
    
    private static int getTerrainCost(Cell cell) {
        switch (cell.getType()) {
            case Constants.EMPTY:
                return Constants.WIRE_COST;
            case Constants.WATER:
                return Constants.WIRE_COST * 3;
            case Constants.MOUNTAIN:
                return Constants.WIRE_COST * 2;
            default:
                return Constants.WIRE_COST;
        }
    }
    
    private static int heuristic(Cell a, Cell b) {
        // مسافة مانهاتن
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
    }
    
    // ============ الطبقات المساعدة الداخلية ============
    
    private static class Node {
        Cell cell;
        int cost;
        
        Node(Cell cell, int cost) {
            this.cell = cell;
            this.cost = cost;
        }
    }
    
    private static class AStarNode {
        Cell cell;
        int fCost;
        
        AStarNode(Cell cell, int fCost) {
            this.cell = cell;
            this.fCost = fCost;
        }
    }
}