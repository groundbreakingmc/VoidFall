package groundbreakingmc.voidfall.actions;

import groundbreakingmc.voidfall.VoidFall;
import groundbreakingmc.voidfall.utils.PapiUtil;
import groundbreakingmc.voidfall.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

@AllArgsConstructor
public abstract class AbstractAction {

    protected final VoidFall plugin;
    private final String string;

    public void run(final Player player, final String[] searchList, final String[] replacementList) {
        final String formattedString = this.plugin.getColorizer().colorize(
                PapiUtil.parse(
                    player,
                    StringUtil.replaceEach(this.string, searchList, replacementList)
            )
        );
        this.process(player, formattedString);
    }

    abstract void process(Player player, String string);

}
