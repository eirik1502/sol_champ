package sol_engine.graphics_module;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import sol_engine.graphics_module.graphical_objects.Renderable;
import sol_engine.graphics_module.graphical_objects.Square;
import sol_engine.graphics_module.render_api.Mesh;
import sol_engine.graphics_module.shaders.ColorShader;
import sol_engine.graphics_module.shaders.MVPShader;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {


    private RenderConfig config;

    private RenderingContext context;
    private FrameBuffer frameBuffer;

    private List<Renderable> renderables = new ArrayList<>();

    private float time = 0;

    public Renderer(RenderConfig config, RenderingContext context) {
        this.config = config;
        this.context = context;
    }

    public RenderingContext getContext() { return context; }

    public void render() {
        float cameraDist = 4;
//        Matrix4f viewTrans = new Matrix4f().lookAt(
//                new Vector3f(viewWidth/2, viewHeight/2, cameraDist),
//                new Vector3f(viewWidth/2, viewHeight/2, 0),
//                new Vector3f(0, -1, 0));
//        Matrix4f projTrans = new Matrix4f().perspective((float)Math.atan(viewHeight/(2*cameraDist))*2, viewWidth/viewHeight, cameraDist-1, -10);

        Matrix4f viewTrans = new Matrix4f().identity();//.translate(-viewWidth/2, -viewHeight/2, -1);
        Matrix4f projTrans = new Matrix4f().ortho(0, config.width, config.height, 0, 10, -10);

        context.clear();


//        shader.setModelTransform(new Matrix4f().identity().translate(100, 30, 0).scale(50, 50, 1));
//        shader.setColor(Color.RED.getRGBVec());
//
//        mesh.bind();
//        glDrawElements(GL_TRIANGLES, mesh.getIndicesCount(), GL_UNSIGNED_BYTE, 0);

        renderables.forEach(s -> {

            Matrix4f modTrans = new Matrix4f()
                    .translate(s.getX(), s.getY(), 0)
                    .scale(s.getWidth(), s.getHeight(), 1)
                    ;//.rotate(time/10, 0, 1, 0 );

            s.getMaterial().bind();
            MVPShader shader = s.getMaterial().getShader();

            shader.setViewTransform(viewTrans);
            shader.setProjectionTransform(projTrans);
            shader.setModelTransform(modTrans);
            ((ColorShader)shader).setTime(time);

            s.getMesh().bind();
            glDrawElements(GL_TRIANGLES, s.getMesh().getIndicesCount(), GL_UNSIGNED_BYTE, 0);

        });

        context.swapBuffers();

        time++;
    }

    public void addRenderable(Renderable renderable) {
        if (!renderables.contains(renderable)) {
            renderables.add(renderable);
        }

    }

}
