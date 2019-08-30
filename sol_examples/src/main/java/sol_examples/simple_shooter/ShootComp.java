package sol_examples.simple_shooter;

import sol_engine.ecs.Component;

public class ShootComp extends Component {
    public static final int NO_BUTTON = -1;

    // user set
    public int shootMouseButton = NO_BUTTON;
    public String bulletEntityClass;
    public float initialBulletSpeed = 120;
    public int reloadFrames = 60;

    // system set
    public int framesSinceLastShot = 0;

    public ShootComp() {
    }

    public ShootComp(int shootMouseButton, String bulletEntityClass, float initialBulletSpeed, int reloadFrames) {
        this.shootMouseButton = shootMouseButton;
        this.bulletEntityClass = bulletEntityClass;
        this.initialBulletSpeed = initialBulletSpeed;
        this.reloadFrames = reloadFrames;
    }
}
