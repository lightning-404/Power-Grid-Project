package powergrid.model;

/**
 * كلاس يمثل مصدر طاقة (محطة توليد) في الشبكة
 * يمكن أن يكون محطة طاقة كبيرة أو مصدراً صغيراً
 */
public class PowerSource {
    private int x;               // الموقع الأفقي
    private int y;               // الموقع العمودي
    private int powerOutput;     // كمية الطاقة المنتجة (بالواط)
    private boolean active;      // هل المصدر نشط؟
    private String sourceType;   // نوع المصدر (شمسي، رياح، فحم، إلخ)
    private int maxCapacity;     // أقصى طاقة يمكن إنتاجها
    private int currentLoad;     // الحمل الحالي
    
    /**
     * مُنشئ مصدر الطاقة الأساسي
     * @param x الموقع الأفقي
     * @param y الموقع العمودي
     */
    public PowerSource(int x, int y) {
        this.x = x;
        this.y = y;
        this.powerOutput = 1000;     // 1000 واط افتراضياً
        this.active = true;          // نشط افتراضياً
        this.sourceType = "عام";      // نوع عام
        this.maxCapacity = 1000;     // نفس القدرة المنتجة
        this.currentLoad = 0;        // لا يوجد حمل ابتدائياً
    }
    
    /**
     * مُنشئ مصدر الطاقة مع تفاصيل إضافية
     * @param x الموقع الأفقي
     * @param y الموقع العمودي
     * @param powerOutput قدرة الإنتاج
     * @param sourceType نوع المصدر
     */
    public PowerSource(int x, int y, int powerOutput, String sourceType) {
        this(x, y);
        this.powerOutput = powerOutput;
        this.sourceType = sourceType;
        this.maxCapacity = powerOutput;
    }
    
    // ============ دوال الوصول (Getters) ============
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public int getPowerOutput() {
        return powerOutput;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public String getSourceType() {
        return sourceType;
    }
    
    public int getMaxCapacity() {
        return maxCapacity;
    }
    
    public int getCurrentLoad() {
        return currentLoad;
    }
    
    public int getAvailablePower() {
        return maxCapacity - currentLoad;
    }
    
    // ============ دوال التعديل (Setters) ============
    
    public void setPowerOutput(int powerOutput) {
        if (powerOutput >= 0) {
            this.powerOutput = powerOutput;
            if (powerOutput > maxCapacity) {
                maxCapacity = powerOutput;
            }
        }
    }
    
    public void setActive(boolean active) {
        this.active = active;
        if (!active) {
            currentLoad = 0; // إذا تم إيقاف المصدر، فالحمل يصبح 0
        }
    }
    
    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }
    
    // ============ دوال إضافية ============
    
    /**
     * تشغيل مصدر الطاقة
     */
    public void activate() {
        this.active = true;
        System.out.println("تم تشغيل مصدر الطاقة في (" + x + ", " + y + ")");
    }
    
    /**
     * إيقاف مصدر الطاقة
     */
    public void deactivate() {
        this.active = false;
        this.currentLoad = 0;
        System.out.println("تم إيقاف مصدر الطاقة في (" + x + ", " + y + ")");
    }
    
    /**
     * إضافة حمل إلى المصدر
     * @param load كمية الحمل المطلوب إضافتها
     * @return true إذا تمت الإضافة بنجاح، false إذا تجاوزت السعة
     */
    public boolean addLoad(int load) {
        if (active && (currentLoad + load) <= maxCapacity) {
            currentLoad += load;
            return true;
        }
        return false;
    }
    
    /**
     * إزالة حمل من المصدر
     * @param load كمية الحمل المطلوب إزالتها
     */
    public void removeLoad(int load) {
        currentLoad = Math.max(0, currentLoad - load);
    }
    
    /**
     * ترقية المصدر لزيادة سعته
     * @param additionalCapacity السعة الإضافية
     */
    public void upgrade(int additionalCapacity) {
        maxCapacity += additionalCapacity;
        powerOutput = maxCapacity; // بعد الترقية، الإنتاج يساوي السعة القصوى
        System.out.println("تم ترقية مصدر الطاقة إلى " + maxCapacity + " واط");
    }
    
    /**
     * صيانة المصدر (إعادة التشغيل وإعادة الضبط)
     */
    public void performMaintenance() {
        currentLoad = 0;
        active = true;
        System.out.println("تمت صيانة مصدر الطاقة في (" + x + ", " + y + ")");
    }
    
    /**
     * @return نسبة استخدام المصدر (الحمل الحالي ÷ السعة القصوى)
     */
    public double getUtilizationRate() {
        if (maxCapacity == 0) return 0;
        return (double) currentLoad / maxCapacity * 100;
    }
    
    /**
     * @return نص يصف حالة المصدر
     */
    @Override
    public String toString() {
        String status = active ? "نشط ✅" : "متوقف ⏸️";
        return "مصدر طاقة " + sourceType + " في (" + x + ", " + y + ") - " +
               status + " - الإنتاج: " + powerOutput + " واط - الحمل: " + 
               currentLoad + "/" + maxCapacity + " واط (" + 
               String.format("%.1f", getUtilizationRate()) + "%)";
    }
    
    /**
     * @return معلومات مختصرة عن المصدر
     */
    public String getInfo() {
        String icon = "";
        switch (sourceType) {
            case "شمسي": icon = "☀️"; break;
            case "رياح": icon = "💨"; break;
            case "مائي": icon = "💧"; break;
            case "فحم": icon = "⚫"; break;
            case "نووي": icon = "☢️"; break;
            default: icon = "⚡"; break;
        }
        
        return icon + " مصدر " + sourceType + " في (" + x + ", " + y + ") - " +
               (active ? "✅" : "⏸️") + " " + currentLoad + "/" + maxCapacity + "W";
    }
    
    /**
     * تحقق مما إذا كان المصدر يستطيع توفير طاقة إضافية
     * @param requiredPower الطاقة المطلوبة
     * @return true إذا كان بإمكانه توفيرها
     */
    public boolean canProvidePower(int requiredPower) {
        return active && (getAvailablePower() >= requiredPower);
    }
}