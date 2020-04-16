package sol_engine.graphics_module.render;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sol_engine.graphics_module.FrameBuffer;
import sol_engine.graphics_module.RenderConfig;
import sol_engine.graphics_module.RenderingContext;
import sol_engine.graphics_module.graphical_objects.Renderable;
import sol_engine.graphics_module.gui.imgui.GuiRenderer;
import sol_engine.graphics_module.materials.Material;
import sol_engine.graphics_module.shaders.ColorShader;
import sol_engine.graphics_module.shaders.MVPShader;
import sol_engine.graphics_module.shaders.ShaderManager;
import sol_engine.utils.collections.EqualTypedPair;
import sol_engine.utils.collections.SetUtils;

import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {
    private final Logger logger = LoggerFactory.getLogger(Renderer.class);

    private static class RenderData {
        public Renderable renderable;
        public Vector3f position;
        float rotationZ;

        public RenderData(Renderable renderable, Vector3f position, float rotationZ) {
            this.renderable = renderable;
            this.position = position;
            this.rotationZ = rotationZ;
        }
    }

    public static final String NULL_MESH = MeshManager.NULL_MESH;
    public static final String UNIT_CORNERED_RECTANGLE_MESH = "UNIT_CORNERED_RECTANGLE_MESH";
    public static final String UNIT_CENTERED_CIRCLE_MESH = "UNIT_CENTERED_CIRCLE_MESH";
    public static final String UNIT_CENTERED_CIRCLE_POINTING_MESH = "UNIT_CENTERED_CIRCLE_POINTING_MESH";


    private RenderConfig config;

    private RenderingContext context;
    private FrameBuffer frameBuffer;
    private GuiRenderer guiRenderer;
    private ShaderManager shaderManager;
    private MeshManager meshManager;


    private Set<RenderData> toBeRendered = new HashSet<>();

    private float time = 0;

    public Renderer(RenderConfig config, RenderingContext context) {
        this.config = config;
        this.context = context;

        if (config.disableGui) {
            guiRenderer = null;
        } else {
            guiRenderer = new GuiRenderer(context.getWindow());
            guiRenderer.startFrame();
        }

        shaderManager = new ShaderManager();
        meshManager = new MeshManager();
        meshManager.addMesh(UNIT_CORNERED_RECTANGLE_MESH, MeshUtils.createUnitCorneredRectangleMesh());
        meshManager.addMesh(UNIT_CENTERED_CIRCLE_MESH, MeshUtils.createUnitCenteredCircleMesh(16));
        meshManager.addMesh(UNIT_CENTERED_CIRCLE_POINTING_MESH, MeshUtils.createUnitCenteredCirclePointingMesh(16));
    }

    public void terminate() {
        if (guiRenderer != null) {
            guiRenderer.terminate();

        }
    }

    public RenderingContext getContext() {
        return context;
    }

    public void renderObject(Renderable renderable, Vector3f position) {
        renderObject(renderable, position, 0f);
    }

    public void renderObject(Renderable renderable, Vector3f position, float rotationZ) {
        toBeRendered.add(new RenderData(renderable, position, rotationZ));
    }

    public GuiRenderer getGuiRenderer() {
        if (guiRenderer != null) {
            return guiRenderer;
        } else {
            logger.error("Trying to access gui renderer after disabling gui");
            return null;
        }
    }

    public void render() {
        float cameraDist = 4;
//        Matrix4f viewTrans = new Matrix4f().lookAt(
//                new Vector3f(viewWidth/2, viewHeight/2, cameraDist),
//                new Vector3f(viewWidth/2, viewHeight/2, 0),
//                new Vector3f(0, -1, 0));
//        Matrix4f projTrans = new Matrix4f().perspective((float)Math.atan(viewHeight/(2*cameraDist))*2, viewWidth/viewHeight, cameraDist-1, -10);

        Matrix4f viewTrans = new Matrix4f().identity();//.translate(-viewWidth/2, -viewHeight/2, -1);
        Matrix4f projTrans = new Matrix4f().ortho(0, config.viewWidth, config.viewHeight, 0, 10, -10);
//        GL11.glViewport(0, 0, 640, 480);

        context.clear();

        EqualTypedPair<Set<RenderData>> toBeRenderedSolidTransparent =
                SetUtils.splitSet(toBeRendered, data -> data.renderable.material.transparancy == 1);

        toBeRenderedSolidTransparent.forEach(toBeRenderedSection -> {
            toBeRenderedSection.forEach(renderData -> {
                Renderable renderable = renderData.renderable;
                Matrix4f modTrans = new Matrix4f()
                        .translate(renderData.position)
                        .scale(renderable.width, renderable.height, 1)
                        .rotate(renderData.rotationZ, 0, 0, 1);

                Material material = renderable.material;
                Class<? extends MVPShader> shaderType = material.getShaderType();
                MVPShader shader = shaderManager.get(shaderType);
                Mesh mesh = meshManager.getMesh(renderable.meshName);

                shader.bind();
                material.applyShaderProps(shader);

                shader.setViewTransform(viewTrans);
                shader.setProjectionTransform(projTrans);
                shader.setModelTransform(modTrans);
                ((ColorShader) shader).setTime(time);

                mesh.bind();
                glDrawElements(GL_TRIANGLES, mesh.getIndicesCount(), GL_UNSIGNED_BYTE, 0);
            });
        });
        if (guiRenderer != null) {
            guiRenderer.render();
        }
        context.swapBuffers();

        toBeRendered.clear();
        time++;

        if (guiRenderer != null) {
            guiRenderer.startFrame();
        }
    }
}
