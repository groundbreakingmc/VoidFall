package noslowdwn.voidfall.constructors;

import lombok.Builder;
import noslowdwn.voidfall.actions.AbstractAction;

import java.util.List;

@Builder
public class WorldsConstructor {

    public final boolean roofModeEnabled;
    public final int roofExecuteHeight;
    public final int roofRepeatFix;
    public final boolean roofRandom;
    public final List<AbstractAction> roofActions;

    public final boolean floorModeEnabled;
    public final int floorExecuteHeight;
    public final int floorRepeatFix;
    public final boolean floorRandom;
    public final List<AbstractAction> floorActions;
}
