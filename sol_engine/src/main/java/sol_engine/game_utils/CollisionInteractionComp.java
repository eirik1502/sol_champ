package sol_engine.game_utils;

import sol_engine.ecs.Component;

import java.util.*;

public class CollisionInteractionComp extends Component {

    public Set<String> tags = new HashSet<>();
    public Map<String, CollisionInteraction> interactionByTag = new HashMap<>();


    public CollisionInteractionComp() {
    }

    public CollisionInteractionComp(String... tags) {
        this();
        Arrays.stream(tags).forEach(this::addTag);
    }

    public CollisionInteractionComp(String tag, CollisionInteraction interaction) {
        this();
        addInteraction(tag, interaction);
    }

    public CollisionInteractionComp addInteraction(String tag, CollisionInteraction interaction) {
        this.interactionByTag.put(tag, interaction);
        return this;
    }

    public CollisionInteractionComp addTag(String tag) {
        this.tags.add(tag);
        return this;
    }


    @Override
    public void copy(Component other) {
        CollisionInteractionComp otherComp = (CollisionInteractionComp) other;
        tags = new HashSet<>(otherComp.tags);
        interactionByTag = new HashMap<>(otherComp.interactionByTag);
    }
}
