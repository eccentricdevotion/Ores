package me.eccentric_nz.ores;

import com.google.common.collect.ImmutableList;
import me.eccentric_nz.ores.ore.OreType;
import me.eccentric_nz.ores.pipe.PipeShape;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class OresTabCompleter implements TabCompleter {

    private List<String> ORE_SUBS = new ArrayList<>();
    private List<String> PIPE_SUBS = new ArrayList<>();

    public OresTabCompleter() {
        for (OreType o : OreType.values()) {
            ORE_SUBS.add(o.toString());
        }
        for (PipeShape p : PipeShape.values()) {
            PIPE_SUBS.add(p.toString());
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        String lastArg = args[args.length - 1];
        if (args.length == 1) {
            if (cmd.getName().equalsIgnoreCase("ore")) {
                return partial(lastArg, ORE_SUBS);
            }
            if (cmd.getName().equalsIgnoreCase("pipe")) {
                return partial(lastArg, PIPE_SUBS);
            }
        }
        return ImmutableList.of();
    }

    protected List<String> partial(String token, Collection<String> from) {
        return StringUtil.copyPartialMatches(token, from, new ArrayList<>(from.size()));
    }
}
