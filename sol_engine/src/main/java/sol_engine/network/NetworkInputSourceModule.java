package sol_engine.network;

import org.joml.Vector2f;
import sol_engine.input_module.InputSourceModule;

public class NetworkInputSourceModule extends InputSourceModule {
    @Override
    public void onSetup() {
        usingModules(NetworkModule.class);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onEnd() {

    }

    @Override
    public void onUpdate() {

    }

    @Override
    public boolean checkAction(String label) {
        return false;
    }

    @Override
    public float floatInput(String label) {
        return 0;
    }

    @Override
    public Vector2f vectorInput(String label) {
        return null;
    }
}
