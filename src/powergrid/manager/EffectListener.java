package powergrid.manager;

public interface EffectListener {
    void onEarthquakeStarted(int magnitude, int affectedCells);
    void onEarthquakeEnded();
    void onDamageReported(int x, int y, String damageType, int severity);
    void onRepairNeeded(int x, int y, int repairCost);
}