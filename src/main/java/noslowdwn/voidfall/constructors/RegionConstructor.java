package noslowdwn.voidfall.constructors;

import lombok.Builder;
import noslowdwn.voidfall.actions.AbstractAction;

import java.util.List;
import java.util.Set;

@Builder
public class RegionConstructor {

    public final Set<String> worlds;

    public final List<AbstractAction> enterActions;
    public final boolean enterRandom;

    public final List<AbstractAction> leaveActions;
    public final boolean leaveRandom;
}
