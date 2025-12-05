package powergrid.manager;

import powergrid.effects.*;
import powergrid.model.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class EffectManager {
    private List<EarthquakeEffect> activeEarthquakes;
    private Grid grid;
    private List<EffectListener> listeners;
    private Random random;
    
    public EffectManager(Grid grid) {
        this.grid = grid;
        this.activeEarthquakes = new CopyOnWriteArrayList<>();
        this.listeners = new ArrayList<>();
        this.random = new Random();
    }
    
    public void triggerEarthquake(int epicenterX, int epicenterY, int magnitude) {
        EarthquakeEffect earthquake = new EarthquakeEffect(epicenterX, epicenterY, magnitude);
        activeEarthquakes.add(earthquake);
        
        // تطبيق التأثير
        earthquake.applyEffect(grid);
        
        // إعلام المستمعين
        notifyEarthquakeTriggered(earthquake);
        
        // تأثيرات صوتية ومرئية
        playEarthquakeSound(magnitude);
        startScreenShake(magnitude);
    }
    
    public void triggerRandomEarthquake() {
        int x = random.nextInt(grid.getWidth());
        int y = random.nextInt(grid.getHeight());
        int magnitude = random.nextInt(5) + 3; // 3-7
        
        triggerEarthquake(x, y, magnitude);
    }
    
    public void update(float deltaTime) {
        // تحديث الزلازل النشطة
        for (EarthquakeEffect earthquake : activeEarthquakes) {
            earthquake.update();
            
            if (!earthquake.isActive()) {
                activeEarthquakes.remove(earthquake);
                notifyEarthquakeEnded(earthquake);
            }
        }
        
        // تحديث تأثيرات الخلايا
        updateCellEffects();
    }
    
    private void updateCellEffects() {
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                Cell cell = grid.getCell(x, y);
                cell.updateEffects();
            }
        }
    }
    
    public void addListener(EffectListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(EffectListener listener) {
        listeners.remove(listener);
    }
    
    private void notifyEarthquakeTriggered(EarthquakeEffect earthquake) {
        for (EffectListener listener : listeners) {
            listener.onEarthquakeStarted(
                earthquake.getMagnitude(),
                earthquake.getAffectedCells().size()
            );
        }
    }
    
    private void notifyEarthquakeEnded(EarthquakeEffect earthquake) {
        for (EffectListener listener : listeners) {
            listener.onEarthquakeEnded();
        }
    }
    
    private void playEarthquakeSound(int magnitude) {
        // محاكاة تأثير صوتي
        String soundFile = magnitude > 7 ? "earthquake_strong.wav" : 
                          magnitude > 5 ? "earthquake_medium.wav" : 
                          "earthquake_weak.wav";
        
        System.out.println("🔊 تشغيل صوت: " + soundFile);
    }
    
    private void startScreenShake(int magnitude) {
        // محاكاة اهتزاز الشاشة
        int shakeIntensity = magnitude * 3;
        int shakeDuration = magnitude * 1000; // ميلي ثانية
        
        System.out.println("📱 اهتزاز الشاشة: شدة " + shakeIntensity + "، مدة " + shakeDuration + "ms");
    }
    
    public List<EarthquakeEffect> getActiveEarthquakes() {
        return Collections.unmodifiableList(activeEarthquakes);
    }
    
    public int getActiveEarthquakeCount() {
        return activeEarthquakes.size();
    }
}