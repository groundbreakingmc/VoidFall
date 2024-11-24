package com.github.groundbreakingmc.voidfall.constructors;

import com.github.groundbreakingmc.voidfall.actions.AbstractAction;
import lombok.Builder;

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
