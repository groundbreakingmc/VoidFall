package groundbreakingmc.voidfall.actions;

import groundbreakingmc.voidfall.VoidFall;
import lombok.AllArgsConstructor;
import groundbreakingmc.voidfall.utils.StringUtil;
import org.bukkit.entity.Player;

@AllArgsConstructor
public abstract class AbstractAction {

    protected final VoidFall plugin;
    private final String string;

    public void run(final Player player, final String[] searchList, final String[] replacementList) {
        final String formattedString = StringUtil.replaceEach(string, searchList, replacementList);
        this.process(player, formattedString);
    }

    abstract void process(Player player, String string);

}
