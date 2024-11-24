package com.github.groundbreakingmc.voidfall.constructors;

import com.github.groundbreakingmc.voidfall.VoidFall;
import com.github.groundbreakingmc.voidfall.actions.AbstractAction;
import lombok.Builder;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Builder
public final class WorldsConstructor {

    public final boolean roofModeEnabled;
    public final int roofExecuteHeight;
    public final int roofRepeatFix;
    public final boolean roofRandom;
    public final List<AbstractAction> roofActions;
    public final Set<String> executingRoof = new HashSet<>();


    public final boolean floorModeEnabled;
    public final int floorExecuteHeight;
    public final boolean floorRandom;
    public final List<AbstractAction> floorActions;

    private final int floorRepeatFix;
    private final Set<String> executingFloor = new HashSet<>();

    public boolean executingRoofContains(final VoidFall plugin, final String playerName) {
        if (!this.executingRoof.contains(playerName)) {
            this.executingRoof.add(playerName);
            Bukkit.getScheduler().runTaskLater(plugin, () -> this.executingRoof.remove(playerName), this.roofRepeatFix * 20L);

            return false;
        }

        return true;
    }

    public boolean executingFloorContains(final VoidFall plugin, final String playerName) {
        if (!this.executingFloor.contains(playerName)) {
            this.executingFloor.add(playerName);
            Bukkit.getScheduler().runTaskLater(plugin, () -> this.executingFloor.remove(playerName), this.floorRepeatFix * 20L);

            return false;
        }

        return true;
    }
}
