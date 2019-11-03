package sol_engine.creator;

import com.google.common.collect.EvictingQueue;
import glm_.vec2.Vec2;
import imgui.ImGui;
import imgui.MutableProperty0;
import imgui.WindowFlag;
import sol_engine.core.ModuleSystemBase;
import sol_engine.ecs.Component;
import sol_engine.ecs.SystemBase;
import sol_engine.ecs.World;
import sol_engine.ecs.WorldUpdateListener;
import sol_engine.graphics_module.GraphicsModule;
import sol_engine.utils.stream.CollectorsUtils;
import sol_engine.utils.stream.WithIndex;
import sol_engine.utils.tickers.DeltaTimer;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class WorldProfilerSystem extends ModuleSystemBase implements WorldUpdateListener {

    private int recordLength = 60 * 3;

    private EvictingQueue<Float> internalWorkTime = EvictingQueue.create(recordLength);
    private EvictingQueue<Float> totalWorkTime = EvictingQueue.create(recordLength);
    private Map<SystemBase, EvictingQueue<Float>> systemsWorkTime = new HashMap<>();

    private DeltaTimer totalWorkTimer = new DeltaTimer();
    private DeltaTimer partialWorkTimer = new DeltaTimer();

    private MutableProperty0<Boolean> watchingStats = new MutableProperty0<>(true);
    private MutableProperty0<Boolean> watchingTotalTime = new MutableProperty0<>(true);
    private MutableProperty0<Boolean> watchingInternalTime = new MutableProperty0<>(false);
    private Set<SystemBase> watchingSystems = new LinkedHashSet<>();

    @Override
    protected void onSetup() {
        usingModules(GraphicsModule.class);
        usingComponents();
    }

    @Override
    protected void onStart() {
        world.addWorldUpdateListener(this);
    }

    @Override
    protected void onEnd() {
        world.removeWorldUpdateListener(this);
    }

    @Override
    protected void onUpdate() {
        GraphicsModule graphicsModule = getModule(GraphicsModule.class);

        boolean[] pOpen = {true};
        graphicsModule.getRenderer().getImgui().draw(imgui -> {
            if (imgui.begin("Profiler", pOpen, WindowFlag.AlwaysAutoResize.i | WindowFlag.MenuBar.i)) {

                //menu
                if (imgui.beginMenuBar()) {
//                    if (imgui.beginMenu("Stats", true)) {
//                        imgui.menuItem("World stats", "", watchingStats, true);
//                        imgui.endMenu();
//                    }
                    if (imgui.beginMenu("General", true)) {
                        imgui.menuItem("Total update time", "", watchingTotalTime, true);
                        imgui.menuItem("Internal world time", "", watchingInternalTime, true);
                        imgui.endMenu();
                    }

                    //systems
                    if (imgui.beginMenu("Systems", true)) {
                        systemsWorkTime.keySet().forEach(system -> {
                            boolean[] selected = {watchingSystems.contains(system)};
                            imgui.menuItem(system.getClass().getSimpleName(), "", selected, true);
                            if (selected[0]) {
                                watchingSystems.add(system);
                            } else {
                                watchingSystems.remove(system);
                            }
                        });
                        imgui.endMenu();
                    }
                    imgui.endMenuBar();
                }
                if (imgui.collapsingHeader("Stats", 0)) {
                    int entitiesCount = world.getEntities().size();
                    int systemsCount = world.getSystems().size();
                    Map<Class<? extends Component>, Integer> compTypesCount = world.getEntities().stream()
                            .flatMap(e -> e.getComponentTypeGroup().stream())
                            .collect(Collectors.groupingBy(Function.identity()))
                            .entrySet().stream()
                            .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().size()));
                    int compsCount = compTypesCount.values().stream().mapToInt(i -> i).sum();

                    imgui.text("Entities count: %d", entitiesCount);
                    imgui.text("Systems count: %d", systemsCount);
                    imgui.text("Components count: %d", compsCount);

                    if (imgui.collapsingHeader("Components details", 0)) {
                        int maxCompTypeCount = compTypesCount.values().stream().mapToInt(i -> i).max().orElse(0);
                        List<Class<? extends Component>> mostOccurringCompTypes = compTypesCount.entrySet().stream()
                                .sorted((entry1, entry2) -> entry2.getValue() - entry1.getValue())
                                .limit(3).map(Map.Entry::getKey).collect(Collectors.toList());


                        float[] compTypeCountValues = compTypesCount.values().stream().map(v -> (float) v).collect(CollectorsUtils.toFloatArray());
                        List<String> barLabels = compTypesCount.keySet().stream()
                                .map(WithIndex.map())
                                .map(cti ->
                                        String.format("%d - (%.0f) %s", cti.i, compTypeCountValues[cti.i], cti.value.getSimpleName()))
                                .collect(Collectors.toList());
                        String barLabelsCombined = String.join("\n", barLabels);
                        float compTypeHistogramHeight = imgui.getTextLineHeight() * barLabels.size();

                        imgui.text("Most occurring: %s", mostOccurringCompTypes.stream()
                                .map(compType -> String.format("(%d) %s", compTypesCount.get(compType), compType.getSimpleName()))
                                .collect(Collectors.joining(", "))
                        );
                        imgui.plotHistogram(barLabelsCombined + "##Component types count",
                                compTypeCountValues, 0,
                                "Component types count",
                                1, maxCompTypeCount,
                                new Vec2(500, compTypeHistogramHeight), 1);
                    }

                }

                if (watchingTotalTime.get()) {
                    drawRawFrameTimings(imgui, "Total update time", totalWorkTime);
                }
                if (watchingInternalTime.get()) {
                    drawRawFrameTimings(imgui, "Internal update time", internalWorkTime);
                }

                watchingSystems.forEach(system ->
                        drawRawFrameTimings(imgui, system.getClass().getSimpleName(), systemsWorkTime.get(system))
                );
                imgui.end();
            }
        });
    }

    private void drawRawFrameTimings(ImGui imgui, String label, Queue<Float> timings) {
        float[] times = timings.stream()
                .map(t -> t * 1000)
                .collect(CollectorsUtils.toFloatArray());
        float lastTime = times.length != 0 ? times[times.length - 1] : 0;
        imgui.plotLines(
                String.format(" (%3.2f) " + label, lastTime),
                times, 0, "", 1f, 16.6f,
                new Vec2(500, 30), 1);
    }


    @Override
    public void onUpdateStart(World world) {
        totalWorkTimer.setTime();
    }

    @Override
    public void onUpdateEnd(World world) {
        totalWorkTime.add(totalWorkTimer.deltaTime());
    }

    @Override
    public void onInternalWorkStart(World world) {
        partialWorkTimer.setTime();
    }

    @Override
    public void onInternalWorkEnd(World world) {
        internalWorkTime.add(partialWorkTimer.deltaTime());
    }

    @Override
    public void onSystemUpdateStart(World world, SystemBase system) {
        partialWorkTimer.setTime();
    }

    @Override
    public void onSystemUpdateEnd(World world, SystemBase system) {
        systemsWorkTime.computeIfAbsent(system, nsystem -> EvictingQueue.create(recordLength))
                .add(partialWorkTimer.deltaTime());
    }

}
