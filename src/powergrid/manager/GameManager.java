package powergrid.manager;

import powergrid.model.*;
import powergrid.effects.*;
import powergrid.ui.*;
import powergrid.utils.Constants;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameManager {
    // المكونات الأساسية
    private Grid grid;
    private EffectManager effectManager;
    private List<GameStateListener> stateListeners;
    
    // حالة اللعبة
    private int score;
    private int money;
    private int day;
    private boolean isGameRunning;
    private boolean isGamePaused;
    private int powerDemand;
    private int powerSupply;
    private int satisfiedHouses;
    private int totalHouses;
    private int repairCrews;
    private double disasterProbability;
    
    // إحصائيات
    private int earthquakesTriggered;
    private int totalDamageCost;
    private int repairsCompleted;
    private int housesPowered;
    private int factoriesPowered;
    
    // مؤقتات
    private ScheduledExecutorService gameTimer;
    private Random random;
    
    // مستويات الصعوبة
    public enum Difficulty {
        EASY(10000, 0.01, 5),
        MEDIUM(5000, 0.03, 3),
        HARD(2000, 0.05, 2);
        
        private int startingMoney;
        private double disasterChance;
        private int repairCrews;
        
        Difficulty(int money, double chance, int crews) {
            this.startingMoney = money;
            this.disasterChance = chance;
            this.repairCrews = crews;
        }
    }
    
    // حدث تغيير حالة اللعبة
    public interface GameStateListener {
        void onScoreChanged(int newScore);
        void onMoneyChanged(int newMoney);
        void onDayChanged(int newDay);
        void onPowerUpdate(int demand, int supply);
        void onGameOver(boolean win, String message);
        void onDisasterWarning(String disasterType, int severity);
        void onNewObjective(String objective);
    }
    
    public GameManager(Grid grid, EffectManager effectManager) {
        this.grid = grid;
        this.effectManager = effectManager;
        this.stateListeners = new ArrayList<>();
        this.random = new Random();
        
        // الإعدادات الافتراضية
        initializeGame(Difficulty.MEDIUM);
    }
    
    private void initializeGame(Difficulty difficulty) {
        this.score = 0;
        this.money = difficulty.startingMoney;
        this.day = 1;
        this.isGameRunning = true;
        this.isGamePaused = false;
        this.powerDemand = 0;
        this.powerSupply = 0;
        this.repairCrews = difficulty.repairCrews;
        this.disasterProbability = difficulty.disasterChance;
        this.earthquakesTriggered = 0;
        this.totalDamageCost = 0;
        this.repairsCompleted = 0;
        this.housesPowered = 0;
        this.factoriesPowered = 0;
        
        // حساب المنازل والمصانع
        countBuildings();
        
        // بدء مؤقت اللعبة
        startGameTimer();
        
        // إعداد أهداف أولية
        setupInitialObjectives();
        
        notifyStateListeners();
    }
    
    private void countBuildings() {
        totalHouses = 0;
        satisfiedHouses = 0;
        
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                Cell cell = grid.getCell(x, y);
                if (cell.getType() == Constants.HOUSE) {
                    totalHouses++;
                    if (cell.isPowered()) {
                        satisfiedHouses++;
                    }
                }
            }
        }
    }
    
    private void startGameTimer() {
        gameTimer = Executors.newScheduledThreadPool(1);
        
        // تحديث كل ثانية (يوم في اللعبة)
        gameTimer.scheduleAtFixedRate(() -> {
            if (!isGamePaused && isGameRunning) {
                updateGame();
            }
        }, 0, 5, TimeUnit.SECONDS); // 5 ثواني حقيقية = يوم في اللعبة
    }
    
    private void setupInitialObjectives() {
        addObjective("⚡ وصل الطاقة إلى 5 منازل على الأقل");
        addObjective("💰 وفر 2000 دولار للإصلاحات الطارئة");
        addObjective("🔧 احتفظ بـ3 أطقم إصلاح على الأقل");
        
        // إضافة مستمع لتأثيرات الكوارث
        effectManager.addListener(new EffectListener() {
            @Override
            public void onEarthquakeStarted(int magnitude, int affectedCells) {
                earthquakesTriggered++;
                notifyDisasterWarning("زلزال", magnitude);
                
                // خصم نقاط حسب شدة الزلزال
                if (magnitude > 7) {
                    addScore(-50);
                }
                
                // تحديث الإحصائيات
                updateStatistics();
            }
            
            @Override
            public void onEarthquakeEnded() {
                // تحقق إذا كانت هناك حاجة للإصلاحات
                checkRepairNeeds();
            }
            
            @Override
            public void onDamageReported(int x, int y, String damageType, int severity) {
                totalDamageCost += severity * 100;
                
                // تقليل المال إذا كان الضرر كبيراً
                if (severity > 7) {
                    deductMoney(severity * 50);
                }
            }
            
            @Override
            public void onRepairNeeded(int x, int y, int repairCost) {
                // يمكن إضافة المزيد من المنطق هنا
            }
        });
    }
    
    private void updateGame() {
        // زيادة اليوم
        day++;
        
        // تحديث الطلب على الطاقة
        updatePowerDemand();
        
        // تحديث العرض (التوليد)
        updatePowerSupply();
        
        // تحديث الرضا (المنازل المتصلة)
        updateSatisfaction();
        
        // حساب النقاط
        calculateScore();
        
        // أحداث عشوائية (كوارث)
        checkRandomEvents();
        
        // تحديث الإحصائيات
        updateStatistics();
        
        // تحقق من شروط الفوز/الخسارة
        checkGameConditions();
        
        // إشعار المستمعين بالتحديثات
        notifyStateListeners();
    }
    
    private void updatePowerDemand() {
        powerDemand = 0;
        
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                Cell cell = grid.getCell(x, y);
                if (cell.isPowered()) {
                    switch(cell.getType()) {
                        case Constants.HOUSE:
                            powerDemand += 10;
                            break;
                        case Constants.FACTORY:
                            powerDemand += 50;
                            break;
                        case Constants.TRANSFORMER:
                            powerDemand += 5;
                            break;
                    }
                }
            }
        }
    }
    
    private void updatePowerSupply() {
        powerSupply = 0;
        
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                Cell cell = grid.getCell(x, y);
                if (cell.getType() == Constants.POWER_SOURCE && !cell.isDamaged()) {
                    powerSupply += cell.getOutputPower();
                }
            }
        }
    }
    
    private void updateSatisfaction() {
        satisfiedHouses = 0;
        housesPowered = 0;
        factoriesPowered = 0;
        
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                Cell cell = grid.getCell(x, y);
                if (cell.isPowered()) {
                    if (cell.getType() == Constants.HOUSE) {
                        housesPowered++;
                        if (!cell.isDamaged()) {
                            satisfiedHouses++;
                        }
                    } else if (cell.getType() == Constants.FACTORY) {
                        factoriesPowered++;
                    }
                }
            }
        }
    }
    
    private void calculateScore() {
        int newScore = 0;
        
        // نقاط المنازل الراضية
        newScore += satisfiedHouses * 10;
        
        // نقاط المصانع العاملة
        newScore += factoriesPowered * 25;
        
        // نقاط للتمويل
        newScore += money / 100;
        
        // نقاط للإصلاحات المكتملة
        newScore += repairsCompleted * 50;
        
        // نقاط سلبية للتلف
        newScore -= totalDamageCost / 10;
        
        // نقاط للكفاءة
        if (powerSupply >= powerDemand && powerDemand > 0) {
            newScore += 100; // مكافأة للكفاءة
        }
        
        // نقاط للبقاء
        newScore += day * 5;
        
        score = Math.max(0, newScore);
    }
    
    private void checkRandomEvents() {
        // احتمالية حدوث كارثة
        if (random.nextDouble() < disasterProbability) {
            triggerRandomDisaster();
        }
        
        // احتمالية حدث إيجابي
        if (random.nextDouble() < 0.05) { // 5% فرصة لحدث إيجابي
            triggerPositiveEvent();
        }
    }
    
    private void triggerRandomDisaster() {
        int eventType = random.nextInt(4);
        
        switch(eventType) {
            case 0: // زلزال
                int magnitude = random.nextInt(5) + 3; // 3-7
                int epicenterX = random.nextInt(grid.getWidth());
                int epicenterY = random.nextInt(grid.getHeight());
                effectManager.triggerEarthquake(epicenterX, epicenterY, magnitude);
                break;
                
            case 1: // عاصفة
                notifyDisasterWarning("عاصفة رعدية", 4);
                // يمكن إضافة تأثيرات العاصفة هنا
                break;
                
            case 2: // فيضان
                notifyDisasterWarning("فيضان", 5);
                // يمكن إضافة تأثيرات الفيضان هنا
                break;
                
            case 3: // حريق
                notifyDisasterWarning("حريق", 6);
                // يمكن إضافة تأثيرات الحريق هنا
                break;
        }
    }
    
    private void triggerPositiveEvent() {
        int eventType = random.nextInt(3);
        
        switch(eventType) {
            case 0: // تمويل إضافي
                int bonus = random.nextInt(500) + 500;
                addMoney(bonus);
                notifyStateChange("💰 تبرع بقيمة $" + bonus);
                break;
                
            case 1: // فريق إصلاح تطوعي
                repairCrews++;
                notifyStateChange("👷 فريق إصلاح تطوعي انضم!");
                break;
                
            case 2: // هدية معدات
                notifyStateChange("🎁 وصلت معدات إصلاح مجانية");
                // يمكن إضافة معدات إضافية هنا
                break;
        }
    }
    
    private void checkRepairNeeds() {
        List<Cell> damagedCells = grid.getDamagedCells();
        if (!damagedCells.isEmpty() && repairCrews > 0) {
            int repairsPossible = Math.min(repairCrews, damagedCells.size());
            
            for (int i = 0; i < repairsPossible; i++) {
                Cell cell = damagedCells.get(i);
                int repairCost = cell.getDamageLevel() * 100;
                
                if (money >= repairCost) {
                    // إصلاح الخلية
                    cell.repair();
                    deductMoney(repairCost);
                    repairsCompleted++;
                    grid.removeDamagedCell(cell);
                    
                    notifyStateChange("🔧 تم إصلاح موقع (" + cell.getX() + "," + cell.getY() + ")");
                }
            }
        }
    }
    
    private void checkGameConditions() {
        // شروط الفوز
        if (day >= 30 && satisfiedHouses >= totalHouses * 0.8) {
            endGame(true, "🎉 فوز! نجحت في توفير الطاقة لـ 80% من المنازل لمدة 30 يومًا");
            return;
        }
        
        if (score >= 10000) {
            endGame(true, "🏆 فوز! وصلت إلى " + score + " نقطة");
            return;
        }
        
        // شروط الخسارة
        if (money <= 0 && grid.getDamagedCells().size() > 10) {
            endGame(false, "💀 إفلاس! لا تملك المال للإصلاحات");
            return;
        }
        
        if (satisfiedHouses < totalHouses * 0.2 && day > 10) {
            endGame(false, "😞 خسارة! أقل من 20% من المنازل تحصل على الطاقة");
            return;
        }
        
        if (powerSupply < powerDemand * 0.3 && day > 15) {
            endGame(false, "⚡ خسارة! انقطاع التيار الكهربائي على نطاق واسع");
            return;
        }
    }
    
    // ===== واجهة المستخدم =====
    
    public void addStateListener(GameStateListener listener) {
        stateListeners.add(listener);
    }
    
    public void removeStateListener(GameStateListener listener) {
        stateListeners.remove(listener);
    }
    
    private void notifyStateListeners() {
        for (GameStateListener listener : stateListeners) {
            listener.onScoreChanged(score);
            listener.onMoneyChanged(money);
            listener.onDayChanged(day);
            listener.onPowerUpdate(powerDemand, powerSupply);
        }
    }
    
    private void notifyDisasterWarning(String disasterType, int severity) {
        for (GameStateListener listener : stateListeners) {
            listener.onDisasterWarning(disasterType, severity);
        }
    }
    
    private void notifyStateChange(String message) {
        // يمكن إضافة نظام إشعارات هنا
        System.out.println("📢 " + message);
    }
    
    private void addObjective(String objective) {
        for (GameStateListener listener : stateListeners) {
            listener.onNewObjective(objective);
        }
    }
    
    // ===== التحكم في اللعبة =====
    
    public void startNewGame(Difficulty difficulty) {
        if (gameTimer != null && !gameTimer.isShutdown()) {
            gameTimer.shutdown();
        }
        
        initializeGame(difficulty);
        isGameRunning = true;
        isGamePaused = false;
        
        notifyStateChange("🎮 بدأت لعبة جديدة - مستوى " + difficulty);
    }
    
    public void pauseGame() {
        isGamePaused = true;
        notifyStateChange("⏸️ اللعبة متوقفة");
    }
    
    public void resumeGame() {
        isGamePaused = false;
        notifyStateChange("▶️ اللعبة مستأنفة");
    }
    
    public void endGame(boolean win, String message) {
        isGameRunning = false;
        if (gameTimer != null) {
            gameTimer.shutdown();
        }
        
        for (GameStateListener listener : stateListeners) {
            listener.onGameOver(win, message);
        }
        
        notifyStateChange(message);
    }
    
    public void addMoney(int amount) {
        money += amount;
        notifyStateListeners();
    }
    
    public void deductMoney(int amount) {
        money = Math.max(0, money - amount);
        notifyStateListeners();
    }
    
    public void addScore(int points) {
        score = Math.max(0, score + points);
        notifyStateListeners();
    }
    
    public void purchaseRepairCrew(int cost) {
        if (money >= cost) {
            repairCrews++;
            deductMoney(cost);
            notifyStateChange("👷 اشتريت فريق إصلاح جديد");
        }
    }
    
    public void manualRepair(Cell cell) {
        if (cell.isDamaged() && money >= 500) {
            cell.repair();
            deductMoney(500);
            repairsCompleted++;
            grid.removeDamagedCell(cell);
            notifyStateChange("🔧 إصلاح يدوي في (" + cell.getX() + "," + cell.getY() + ")");
        }
    }
    
    // ===== Getters =====
    
    public int getScore() { return score; }
    public int getMoney() { return money; }
    public int getDay() { return day; }
    public boolean isGameRunning() { return isGameRunning; }
    public boolean isGamePaused() { return isGamePaused; }
    public int getPowerDemand() { return powerDemand; }
    public int getPowerSupply() { return powerSupply; }
    public int getSatisfiedHouses() { return satisfiedHouses; }
    public int getTotalHouses() { return totalHouses; }
    public int getRepairCrews() { return repairCrews; }
    public int getHousesPowered() { return housesPowered; }
    public int getFactoriesPowered() { return factoriesPowered; }
    public int getEarthquakesTriggered() { return earthquakesTriggered; }
    public int getTotalDamageCost() { return totalDamageCost; }
    public int getRepairsCompleted() { return repairsCompleted; }
    public double getPowerEfficiency() { 
        return powerDemand > 0 ? (double) powerSupply / powerDemand : 0; 
    }
    
    public Grid getGrid() { return grid; }
    public EffectManager getEffectManager() { return effectManager; }
    
    private void updateStatistics() {
        // تحديث أي إحصائيات إضافية هنا
    }
    
    // ===== تنظيف الموارد =====
    
    public void cleanup() {
        if (gameTimer != null && !gameTimer.isShutdown()) {
            gameTimer.shutdown();
            try {
                if (!gameTimer.awaitTermination(5, TimeUnit.SECONDS)) {
                    gameTimer.shutdownNow();
                }
            } catch (InterruptedException e) {
                gameTimer.shutdownNow();
            }
        }
    }
}