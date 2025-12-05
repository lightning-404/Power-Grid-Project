package powergrid.model;

import powergrid.utils.Constants;

public class Cell {
    private int x, y;
    private int type;
    private boolean powered;
    private int resistance;
    private boolean damaged;
    private int damageLevel; // 0-10 (0 = سليم، 10 = تالف كلياً)
    private String specialEffect;
    private int effectDuration;
    private int population; // للمنازل
    private int production; // للمصانع
    private int outputPower; // لمصادر الطاقة
    
    public Cell(int x, int y, int type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.powered = false;
        this.resistance = calculateResistance(type);
        this.damaged = false;
        this.damageLevel = 0;
        this.specialEffect = "";
        this.effectDuration = 0;
        this.population = (type == Constants.HOUSE) ? 100 : 0;
        this.production = (type == Constants.FACTORY) ? 50 : 0;
        this.outputPower = (type == Constants.POWER_SOURCE) ? 1000 : 0;
    }
    
    private int calculateResistance(int type) {
        switch(type) {
            case Constants.WATER: return 2;
            case Constants.MOUNTAIN: return 3;
            case Constants.OBSTACLE: return 100;
            case Constants.RUBBLE: return 50;
            case Constants.ROCK_FALL: return 40;
            case Constants.FLOODED: return 5;
            case Constants.CRACK: return 10;
            default: return 1;
        }
    }
    
    // === الجوهر الجديد ===
    public void applyDamage(int damageAmount) {
        if (damageAmount > 0) {
            this.damaged = true;
            this.damageLevel = Math.min(10, this.damageLevel + damageAmount);
            this.resistance += damageAmount * 2;
            
            // إذا كان التلف شديداً، يتغير النوع
            if (damageLevel >= 8) {
                if (type == Constants.WIRE || type == Constants.TRANSFORMER) {
                    type = Constants.BROKEN_WIRE;
                } else if (type == Constants.HOUSE) {
                    type = Constants.RUBBLE;
                    population = Math.max(0, population - 70);
                }
            } else if (damageLevel >= 5) {
                // تلف متوسط
                powered = false;
            }
        }
    }
    
    public void repair() {
        if (damaged) {
            damaged = false;
            damageLevel = Math.max(0, damageLevel - 1);
            resistance = calculateResistance(type);
            
            if (damageLevel <= 2) {
                powered = true; // إعادة الطاقة بعد الإصلاح
            }
        }
    }
    
    public String getDamageDescription() {
        if (!damaged) return "سليم";
        
        switch(damageLevel) {
            case 1: case 2: case 3: return "تلف طفيف";
            case 4: case 5: case 6: return "تلف متوسط";
            case 7: case 8: case 9: return "تلف شديد";
            case 10: return "مدمر تماماً";
            default: return "غير محدد";
        }
    }
    
    // === Getters & Setters ===
    public int getX() { return x; }
    public int getY() { return y; }
    public int getType() { return type; }
    public void setType(int type) { 
        this.type = type; 
        this.resistance = calculateResistance(type);
    }
    public boolean isPowered() { return powered && !damaged; }
    public void setPowered(boolean powered) { 
        if (!damaged || damageLevel < 5) {
            this.powered = powered; 
        }
    }
    public int getResistance() { 
        return damaged ? resistance * 2 : resistance; 
    }
    public boolean isDamaged() { return damaged; }
    public void setDamaged(boolean damaged) { this.damaged = damaged; }
    public int getDamageLevel() { return damageLevel; }
    public void setDamageLevel(int level) { 
        this.damageLevel = Math.min(10, Math.max(0, level)); 
        this.damaged = (level > 0);
    }
    public String getSpecialEffect() { return specialEffect; }
    public void setSpecialEffect(String effect) { this.specialEffect = effect; }
    public int getEffectDuration() { return effectDuration; }
    public void setEffectDuration(int duration) { this.effectDuration = duration; }
    public int getPopulation() { return population; }
    public void setPopulation(int population) { this.population = population; }
    public int getProduction() { return production; }
    public void setProduction(int production) { this.production = production; }
    public int getOutputPower() { return outputPower; }
    public void setOutputPower(int power) { this.outputPower = power; }
    
    public boolean canPowerPass() {
        if (damaged && damageLevel > 5) return false;
        
        return type == Constants.WIRE ||
               type == Constants.TRANSFORMER ||
               type == Constants.HOUSE ||
               type == Constants.EMPTY ||
               type == Constants.POWER_SOURCE ||
               type == Constants.FACTORY;
    }
    
    public boolean isPassable() {
        if (damaged && damageLevel > 7) return false;
        
        return type != Constants.OBSTACLE && 
               type != Constants.MOUNTAIN && 
               type != Constants.RUBBLE && 
               type != Constants.ROCK_FALL;
    }
    
    public void updateEffects() {
        if (effectDuration > 0) {
            effectDuration--;
            if (effectDuration == 0) {
                specialEffect = "";
            }
        }
    }
}