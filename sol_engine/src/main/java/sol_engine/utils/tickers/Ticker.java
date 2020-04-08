package sol_engine.utils.tickers;

public interface Ticker {

    /**
     * Sets the listener to be called on tick
     *
     * @param listener the listener to be called
     */
    void setListener(Tickable listener);

    /**
     * Starts the ticker. This call will block until stop is called
     */
    void start();

    /**
     * Stops the ticker on next tick.
     */
    void stop();
}
