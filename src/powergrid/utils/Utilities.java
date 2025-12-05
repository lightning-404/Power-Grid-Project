package powergrid.utils;

import java.awt.Font;

/**
 * أدوات مساعدة للإحصائيات والرسومات
 */
public class Utilities {
    
    /**
     * تنسيق المبالغ المالية
     */
    public static String formatCurrency(int amount) {
        return String.format("$%,d", amount);
    }
    
    /**
     * تنسيق الوقت (ثواني إلى دقائق:ثواني)
     */
    public static String formatTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d", minutes, secs);
    }
    
    /**
     * حساب النسبة المئوية
     */
    public static int calculatePercentage(int part, int total) {
        if (total == 0) return 0;
        return (part * 100) / total;
    }
    
    /**
     * إنشاء خط عربي
     */
    public static Font createArabicFont(int size, int style) {
        String[] arabicFonts = {
            "Tahoma", "Arial", "Segoe UI", "Microsoft Sans Serif"
        };
        
        for (String fontName : arabicFonts) {
            Font font = new Font(fontName, style, size);
            if (font.canDisplay('أ')) { // اختبار إذا يدعم العربية
                return font;
            }
        }
        
        return new Font("Dialog", style, size);
    }
    
    /**
     * تحويل لون HTML إلى كائن Color
     */
    public static java.awt.Color hexToColor(String hex) {
        try {
            return java.awt.Color.decode(hex);
        } catch (Exception e) {
            return java.awt.Color.BLACK;
        }
    }
    
    /**
     * حساب كفاءة الشبكة
     */
    public static double calculateNetworkEfficiency(int connectedHouses, int totalHouses, 
                                                   int totalWires, int budgetUsed) {
        if (totalHouses == 0) return 0.0;
        
        double connectionRate = (double) connectedHouses / totalHouses;
        double wireEfficiency = totalWires > 0 ? 100.0 / totalWires : 1.0;
        double budgetEfficiency = budgetUsed > 0 ? 1000.0 / budgetUsed : 1.0;
        
        return (connectionRate * 0.6 + wireEfficiency * 0.2 + budgetEfficiency * 0.2) * 100;
    }
}