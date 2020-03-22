package sol_engine.graphics_module.gui.imgui;

import imgui.ImGui;
import org.joml.Vector2f;
import sol_engine.utils.collections.ArrayUtils;

import java.util.List;

import static sol_engine.graphics_module.gui.imgui.GuiCommandsUtils.convertVector2;

public class GuiCommandsChart {

    private ImGui imgui;

    public GuiCommandsChart(ImGui imgui) {
        this.imgui = imgui;
    }


    public void plotLines(String label, List<Float> values, float scaleMin, float scaleMax, Vector2f graphSize, String overlayText) {
        float[] arrayValues = ArrayUtils.listToFloatArray(values);
        imgui.plotLines(label, arrayValues, 0, overlayText, scaleMin, scaleMax, convertVector2(graphSize), 1);
    }

    public void plotHistogram(String label, List<Float> values, float scaleMin, float scaleMax, Vector2f graphSize, String overlayText) {
        float[] arrayValues = ArrayUtils.listToFloatArray(values);
        imgui.plotHistogram(label, arrayValues, 0, overlayText, scaleMin, scaleMax, convertVector2(graphSize), 1);
    }
}
