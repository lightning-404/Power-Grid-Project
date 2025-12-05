package powergrid.utils;

public class Constants {
    // أنواع الخلايا الأساسية
    public static final int EMPTY = 0;
    public static final int WIRE = 1;
    public static final int TRANSFORMER = 2;
    public static final int HOUSE = 3;
    public static final int POWER_SOURCE = 4;
    public static final int FACTORY = 5;
    public static final int WATER = 6;
    public static final int MOUNTAIN = 7;
    public static final int OBSTACLE = 8;
    
    // أنواع التلف الناتج عن الزلزال
    public static final int RUBBLE = 9;
    public static final int ROCK_FALL = 10;
    public static final int FLOODED = 11;
    public static final int CRACK = 12;
    public static final int BROKEN_WIRE = 13;
    
    // ألوان للتأثيرات
    public static final Color EARTHQUAKE_COLOR = new Color(255, 100, 100, 150);
    public static final Color DAMAGE_COLOR = new Color(255, 0, 0, 100);
    public static final Color REPAIR_COLOR = new Color(0, 255, 0, 100);
    
    // إعدادات الزلزال
    public static final int MIN_EARTHQUAKE_MAGNITUDE = 1;
    public static final int MAX_EARTHQUAKE_MAGNITUDE = 10;
    public static final int EARTHQUAKE_DURATION_BASE = 3000; // 3 ثواني
}