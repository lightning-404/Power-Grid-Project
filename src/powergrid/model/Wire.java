package powergrid.model;

/**
 * كلاس يمثل سلكاً كهربائياً
 */
public class Wire {
    private int x, y;
    private boolean powered;
    private int resistance;
    private boolean connectedToSource;
    
    public Wire(int x, int y) {
        this.x = x;
        this.y = y;
        this.powered = false;
        this.resistance = 1; // مقاومة منخفضة
        this.connectedToSource = false;
    }
    
    // Getters and Setters
    public int getX() { return x; }
    public int getY() { return y; }
    public boolean isPowered() { return powered; }
    public void setPowered(boolean powered) { this.powered = powered; }
    public int getResistance() { return resistance; }
    public boolean isConnectedToSource() { return connectedToSource; }
    public void setConnectedToSource(boolean connected) { this.connectedToSource = connected; }
    
    /**
     * توصيل السلك بالكهرباء
     */
    public void connectPower() {
        this.powered = true;
        this.connectedToSource = true;
    }
    
    /**
     * فصل الكهرباء عن السلك
     */
    public void disconnectPower() {
        this.powered = false;
    }
    
    @Override
    public String toString() {
        return "سلك في (" + x + ", " + y + ") - " + (powered ? "موصول ⚡" : "غير موصول");
    }
}