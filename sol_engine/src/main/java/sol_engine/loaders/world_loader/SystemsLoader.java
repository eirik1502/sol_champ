package sol_engine.loaders.world_loader;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Sets;
import sol_engine.ecs.SystemBase;
import sol_engine.loaders.JsonUtils;
import sol_engine.loaders.LoadersLogger;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class SystemsLoader {

    public static List<Class<? extends SystemBase>> loadComponentSystems(JsonNode systemsNode, CompSystemScanner scanner) {
        List<String> systemSimpleNames = JsonUtils.getArrayChildNodesAsText(systemsNode);
        List<Class<? extends SystemBase>> systemsClasses = systemSimpleNames.stream()
                .map(scanner::getSystemClassBySimpleName)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // check what systems were not loaded
        Set<String> systemsNotFound = Sets.difference(
                new HashSet<>(systemSimpleNames),
                systemsClasses.stream().map(Class::getSimpleName).collect(Collectors.toSet())
        );
        if (!systemsNotFound.isEmpty()) LoadersLogger.logger.warning("System class not found. For systems:\n\t" +
                systemsNotFound);

        return systemsClasses;
    }

}
