package sol_engine.utils.tickers;

public class DeltaTimer {

    private long lastTime;

    /**
     * Sets the time reference point to now.
     */
    public void setTimeReference() {
        lastTime = System.nanoTime();
    }

    /**
     * Get the time since the last delta time reference point without updating the reference point.
     *
     * @return time since last reference point
     */
    public float getTime() {
        long newTime = System.nanoTime();
        long deltaTime = newTime - lastTime;

        return nanoToSeconds(deltaTime);
    }

    /**
     * Retrieves the time passed since last reference time point.
     * This sets the new delta reference to now.
     *
     * @return time since last reference point
     */
    public float deltaTime() {
        long newTime = System.nanoTime();
        long deltaTime = newTime - lastTime;

        lastTime = newTime;

        return nanoToSeconds(deltaTime);
    }

    private float nanoToSeconds(long nanoSeconds) {
        double seconds = ((double) nanoSeconds) * 0.000000001;


        return (float) seconds;
    }

}
