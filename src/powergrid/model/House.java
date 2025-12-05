package powergrid.model;

/**
 * كلاس يمثل منزلاً في الشبكة الكهربائية
 * يحتوي على معلومات عن موقع المنزل وحالة الكهرباء فيه
 */
public class House {
    private int x;            // الموقع الأفقي في الشبكة
    private int y;            // الموقع العمودي في الشبكة
    private boolean powered;  // هل المنزل موصول بالكهرباء؟
    private int powerLevel;   // مستوى الكهرباء (من 0 إلى 100)
    private String ownerName; // اسم صاحب المنزل (اختياري)
    
    /**
     * مُنشئ المنزل - يُنشئ منزلاً في موقع محدد
     * @param x الموقع الأفقي
     * @param y الموقع العمودي
     */
    public House(int x, int y) {
        this.x = x;
        this.y = y;
        this.powered = false;  // ابتدائياً غير موصول
        this.powerLevel = 0;   // ابتدائياً بدون كهرباء
        this.ownerName = "ساكن"; // اسم افتراضي
    }
    
    /**
     * مُنشئ المنزل مع اسم المالك
     * @param x الموقع الأفقي
     * @param y الموقع العمودي
     * @param ownerName اسم صاحب المنزل
     */
    public House(int x, int y, String ownerName) {
        this(x, y);
        this.ownerName = ownerName;
    }
    
    // ============ دوال الوصول (Getters) ============
    
    /**
     * @return الموقع الأفقي
     */
    public int getX() {
        return x;
    }
    
    /**
     * @return الموقع العمودي
     */
    public int getY() {
        return y;
    }
    
    /**
     * @return هل المنزل موصول بالكهرباء؟
     */
    public boolean isPowered() {
        return powered;
    }
    
    /**
     * @return مستوى الكهرباء الحالي
     */
    public int getPowerLevel() {
        return powerLevel;
    }
    
    /**
     * @return اسم صاحب المنزل
     */
    public String getOwnerName() {
        return ownerName;
    }
    
    // ============ دوال التعديل (Setters) ============
    
    /**
     * تغيير حالة توصيل الكهرباء
     * @param powered true إذا كان موصولاً، false إذا كان غير موصول
     */
    public void setPowered(boolean powered) {
        this.powered = powered;
        if (powered && powerLevel == 0) {
            this.powerLevel = 100; // إذا تم التوصيل، اجعل المستوى 100%
        } else if (!powered) {
            this.powerLevel = 0;   // إذا تم فصل الكهرباء، اجعل المستوى 0%
        }
    }
    
    /**
     * ضبط مستوى الكهرباء
     * @param powerLevel مستوى الكهرباء من 0 إلى 100
     */
    public void setPowerLevel(int powerLevel) {
        if (powerLevel >= 0 && powerLevel <= 100) {
            this.powerLevel = powerLevel;
            this.powered = (powerLevel > 0); // إذا كان المستوى > 0 فهو موصول
        }
    }
    
    /**
     * تغيير اسم المالك
     * @param ownerName الاسم الجديد
     */
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
    
    // ============ دوال إضافية ============
    
    /**
     * توصيل الكهرباء بالكامل للمنزل
     */
    public void connectPower() {
        setPowered(true);
        setPowerLevel(100);
        System.out.println("تم توصيل الكهرباء للمنزل في (" + x + ", " + y + ")");
    }
    
    /**
     * فصل الكهرباء عن المنزل
     */
    public void disconnectPower() {
        setPowered(false);
        setPowerLevel(0);
        System.out.println("تم فصل الكهرباء عن المنزل في (" + x + ", " + y + ")");
    }
    
    /**
     * تقليل مستوى الكهرباء (مثلاً بسبب عطل)
     * @param amount الكمية المطلوب تقليلها
     */
    public void reducePower(int amount) {
        if (amount > 0) {
            powerLevel = Math.max(0, powerLevel - amount);
            if (powerLevel == 0) {
                powered = false;
            }
        }
    }
    
    /**
     * زيادة مستوى الكهرباء
     * @param amount الكمية المطلوب زيادتها
     */
    public void increasePower(int amount) {
        if (amount > 0) {
            powerLevel = Math.min(100, powerLevel + amount);
            if (powerLevel > 0) {
                powered = true;
            }
        }
    }
    
    /**
     * @return نص يصف حالة المنزل
     */
    @Override
    public String toString() {
        String status = powered ? "موصول ⚡" : "غير موصول 🔌";
        return "منزل " + ownerName + " في (" + x + ", " + y + ") - " + 
               status + " - المستوى: " + powerLevel + "%";
    }
    
    /**
     * @return معلومات مختصرة عن المنزل
     */
    public String getInfo() {
        return "🏠 منزل في (" + x + ", " + y + ") - " + 
               (powered ? "⚡" : "🔌") + " " + powerLevel + "%";
    }
}