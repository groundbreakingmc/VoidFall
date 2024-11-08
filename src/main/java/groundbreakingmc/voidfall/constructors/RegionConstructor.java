package groundbreakingmc.voidfall.constructors;

import lombok.Builder;
import groundbreakingmc.voidfall.actions.AbstractAction;

import java.util.List;
import java.util.Set;

@Builder
public final class RegionConstructor {

    public final Set<String> worlds;

    public final List<AbstractAction> enterActions;
    public final boolean enterRandom;

    public final List<AbstractAction> leaveActions;
    public final boolean leaveRandom;
}
