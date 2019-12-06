package sol_engine.creator;

import com.google.common.collect.EvictingQueue;
import org.joml.Vector2f;
import sol_engine.ecs.Component;
import sol_engine.ecs.SystemBase;
import sol_engine.ecs.World;
import sol_engine.ecs.WorldUpdateListener;
import sol_engine.graphics_module.GraphicsModule;
import sol_engine.graphics_module.imgui.GuiCommands;
import sol_engine.graphics_module.imgui.GuiWindowFlags;
import sol_engine.utils.mutable_primitives.MBoolean;
import sol_engine.utils.stream.WithIndex;
import sol_engine.utils.tickers.DeltaTimer;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class WorldProfiler implements CreatorFrame, WorldUpdateListener {

    private int recordLength = 60 * 3;

    private EvictingQueue<Float> internalWorkTime = EvictingQueue.create(recordLength);
    private EvictingQueue<Float> totalWorkTime = EvictingQueue.create(recordLength);
    private Map<SystemBase, EvictingQueue<Float>> systemsWorkTime = new HashMap<>();

    private DeltaTimer totalWorkTimer = new DeltaTimer();
    private DeltaTimer partialWorkTimer = new DeltaTimer();

    private MBoolean watchingStats = new MBoolean(true);
    private MBoolean watchingTotalTime = new MBoolean(true);
    private MBoolean watchingInternalTime = new MBoolean(false);

    private Set<SystemBase> watchingSystems = new LinkedHashSet<>();


    @Override
    public void onStart(World world) {
        world.listeners.addWorldUpdateListener(this);
    }

    @Override
    public void onEnd(World world) {
        world.listeners.removeWorldUpdateListener(this);
    }

    @Override
    public boolean draw(GuiCommands cmds, World world, GraphicsModule graphicsModule) {
        if (cmds.begin("Profiler", true,
                GuiWindowFlags.AlwaysAutoResize, GuiWindowFlags.MenuBar)) {

            //menu
            if (cmds.menu.beginMenuBar()) {
                if (cmds.menu.beginMenu("General", true)) {
                    cmds.menu.menuItem("Total update time", "", watchingTotalTime, true);
                    cmds.menu.menuItem("Internal world time", "", watchingInternalTime, true);
                    cmds.menu.endMenu();
                }

                //systems
                if (cmds.menu.beginMenu("Systems", true)) {
                    systemsWorkTime.keySet().forEach(system -> {
                        boolean selected = watchingSystems.contains(system);
                        if (cmds.menu.menuItem(system.getClass().getSimpleName(), "", selected, true)) {
                            watchingSystems.add(system);
                        } else {
                            watchingSystems.remove(system);
                        }
                    });
                    cmds.menu.endMenu();
                }
                cmds.menu.endMenuBar();
            }
            if (cmds.container.collapsingHeader("Stats")) {
                drawStats(cmds, world);
            }

            if (watchingTotalTime.value) {

                drawRawFrameTimings(cmds, "Total update time", totalWorkTime);
            }
            if (watchingInternalTime.value) {
                drawRawFrameTimings(cmds, "Internal update time", internalWorkTime);
            }

            watchingSystems.forEach(system ->
                    drawRawFrameTimings(cmds, system.getClass().getSimpleName(), systemsWorkTime.get(system))
            );
            cmds.end();
        }

        return true;
    }

    private void drawRawFrameTimings(GuiCommands cmds, String label, Queue<Float> timings) {
        List<Float> times = timings.stream()
                .map(t -> t * 1000)
                .collect(Collectors.toList());
        float lastTime = times.isEmpty() ? 0 : times.get(times.size() - 1);
        cmds.chart.plotLines(
                String.format(" (%3.2f) " + label, lastTime),
                times, 1f, 16.6f,
                new Vector2f(500, 30), "");
    }

    private void drawStats(GuiCommands cmds, World world) {
        int entitiesCount = world.insight.getEntities().size();
        int systemsCount = world.insight.getSystems().size();
        Map<Class<? extends Component>, Integer> compTypesCount = world.insight.getEntities().stream()
                .flatMap(e -> e.getComponentTypeGroup().stream())
                .collect(Collectors.groupingBy(Function.identity()))
                .entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().size()));
        int compsCount = compTypesCount.values().stream().mapToInt(i -> i).sum();

        cmds.core.text("Entities count: %d", entitiesCount);
        cmds.core.text("Systems count: %d", systemsCount);
        cmds.core.text("Components count: %d", compsCount);

        if (cmds.container.collapsingHeader("Components details")) {
            int maxCompTypeCount = compTypesCount.values().stream().mapToInt(i -> i).max().orElse(0);
            List<Class<? extends Component>> mostOccurringCompTypes = compTypesCount.entrySet().stream()
                    .sorted((entry1, entry2) -> entry2.getValue() - entry1.getValue())
                    .limit(3).map(Map.Entry::getKey).collect(Collectors.toList());


            List<Float> compTypeCountValues = compTypesCount.values().stream().map(v -> (float) v).collect(Collectors.toList());
            List<String> barLabels = compTypesCount.keySet().stream()
                    .map(WithIndex.map())
                    .map(cti ->
                            String.format("%d - (%.0f) %s", cti.i, compTypeCountValues.get(cti.i), cti.value.getSimpleName()))
                    .collect(Collectors.toList());
            String barLabelsCombined = String.join("\n", barLabels);
            float compTypeHistogramHeight = cmds.getNative().getTextLineHeight() * barLabels.size();

            cmds.core.text("Most occurring: %s", mostOccurringCompTypes.stream()
                    .map(compType -> String.format("(%d) %s", compTypesCount.get(compType), compType.getSimpleName()))
                    .collect(Collectors.joining(", "))
            );
            cmds.chart.plotHistogram(barLabelsCombined + "##Component types count",
                    compTypeCountValues,
                    1, maxCompTypeCount,
                    new Vector2f(500, compTypeHistogramHeight),
                    "Component types count");
        }

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
