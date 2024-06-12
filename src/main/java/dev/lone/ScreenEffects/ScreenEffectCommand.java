package dev.lone.ScreenEffects;

import dev.lone.ScreenEffects.NMS.GamemodeNMS;
import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ScreenEffectCommand implements CommandExecutor, TabCompleter
{
    private static final List<String> colors = new ArrayList<>();
    public WeakHashMap<Player, Title> sentTitles = new WeakHashMap<>();

    public void register()
    {
        colors.add("#000000");
        colors.add("#FFFFFF");
        for(ChatColor chatColor : ChatColor.values())
        {
            colors.add(chatColor.getName().toUpperCase());
        }

        Bukkit.getPluginCommand("screeneffect").setExecutor(this);
        Bukkit.getPluginCommand("screeneffect").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args)
    {
        List<Player> toShow = new ArrayList<>();
        int playerIndex = 6;

        if(args.length >= 1 && args[0].equals("stop"))
            playerIndex = 1;

        if(args.length >= playerIndex+1 && (args[playerIndex].equals("all") || Bukkit.getPlayer(args[playerIndex]) != null))
        {
            if (commandSender.hasPermission("screeneffect.show.others"))
            {
                if(args[playerIndex].equals("all"))
                {
                    for(Player p : Bukkit.getOnlinePlayers())
                        toShow.add(p);
                }
                else
                {
                    toShow.add(Bukkit.getPlayerExact(args[playerIndex]));
                }
            }
            else
            {
                commandSender.sendMessage(ChatColor.RED + "You don't have permission screeneffect.show.others");
                return true;
            }
        }
        else
        {
            if (commandSender instanceof Player)
            {
                toShow.add((Player) commandSender);
            }
            else
            {
                commandSender.sendMessage(ChatColor.YELLOW + "You're not a player! Please specify a player when executing this command as console.");
                return true;
            }
        }

        if (toShow.isEmpty())
        {
            commandSender.sendMessage(ChatColor.YELLOW + "Player not found!");
            return true;
        }

        //if stop
        if(playerIndex == 1)
        {
            for (Player player : toShow)
            {
                GamemodeNMS.showHUD(player);

                Main.inst().frozen.remove(player, true);

                if(sentTitles.containsKey(player))
                {
                    Title tmp = sentTitles.get(player);
                    sendTitle(player, tmp.message, tmp.image, 0, 5, tmp.fadeout);
                    sentTitles.remove(player);
                }
                else
                {
                    player.sendTitle(" ", " ", 0, 0, 20);
                }
            }
        }
        else
        {
            ChatColor color = null;
            try
            {
                color = ChatColor.valueOf(args[1]);
            } catch (Exception e)
            {
                //hex support
                try
                {
                    color = ChatColor.of(args[1]);
                } catch (Exception e2)
                {
                    commandSender.sendMessage(ChatColor.YELLOW + "Invalid color! Examples: RED, #770000 (max 6 characters)");
                    return true;
                }
            }
            String effect = "effects:" + args[0];

            int fadein = Integer.parseInt(args[2]);
            int stay = Integer.parseInt(args[3]);
            int fadeout = Integer.parseInt(args[4]);
            boolean freeze = args[5].equals("freeze");

            String image = new FontImageWrapper(effect).getString();
            image = ChatColor.stripColor(image);
            image = color + image;

            //https://hub.spigotmc.org/jira/browse/SPIGOT-6608?jql=text%20~%20%22sendtitle%22
            String message = "";
            if (args.length >= 8)
            {
                for (int i = 7; i < args.length; i++)
                {
                    message += args[i] + " ";
                }
                message = message.substring(0, message.length() - 1);
                message = ChatColor.translateAlternateColorCodes('&', message);
            }
            else
            {
                message = " ";
            }

            for (Player player : toShow)
            {
                showEffect(player, message, image, fadein, stay, fadeout, freeze);
            }
        }
        return true;
    }

    private void showEffect(Player player, String message, String image, int fadein, int stay, int fadeout, boolean freeze)
    {
        if(freeze)
            Main.inst().frozen.put(player, true);

        GamemodeNMS.hideHUD(player);

        sendTitle(player, image, message, fadein, stay, fadeout);

        sentTitles.put(player, new Title(message, image, fadein, stay, fadeout));

        if(Main.inst().getConfig().getBoolean("execute_commands_on_start.enabled"))
        {
            for (String cmd : Main.inst().getConfig().getStringList("execute_commands_on_start.commands"))
            {
                executeCommandForPlayer(player, cmd);
            }
        }

        Bukkit.getScheduler().runTaskLater(Main.inst(), () -> {
            GamemodeNMS.showHUD(player);
            if(freeze)
                Main.inst().frozen.remove(player);
        }, fadein + stay + fadeout / 2);

        if(Main.inst().getConfig().getBoolean("execute_commands_on_finish.enabled"))
        {
            Bukkit.getScheduler().runTaskLater(Main.inst(), () -> {

                for (String cmd : Main.inst().getConfig().getStringList("execute_commands_on_finish.commands"))
                {
                    executeCommandForPlayer(player, cmd);
                }

            }, fadein + stay + fadeout);
        }
    }

    private void executeCommandForPlayer(Player player, String cmd)
    {
        if(Main.hasPlaceholderAPI)
        {
            //noinspection UnnecessaryFullyQualifiedName
            cmd = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, cmd);
        }

        cmd = cmd.replace("%player%", player.getName());
        cmd = cmd.replace("%player_name%", player.getName());
        cmd = cmd.replace("{player}", player.getName());
        cmd = cmd.replace("{player_name}", player.getName());

        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd);
    }

    /**
     * Tested on vanilla game
     * 1.15.2, 1.16.5, 1.19.3
     * title = message
     * subtitle = image
     *
     * 1.17.1, 1.18.2
     * title = image
     * subtitle = message
     */
    private void sendTitle(Player player, String image, String message, int fadein, int stay, int fadeout)
    {
        if (Main.hasTitleBug(player))
            player.sendTitle(image, message, fadein, stay, fadeout);
        else
            player.sendTitle(message, image, fadein, stay, fadeout);
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, Command command, @NotNull String alias, @NotNull String[] args)
    {
        if(args.length > 1 && args[0].equals("stop"))
        {
            if(args.length == 2)
            {
                List<String> players = new ArrayList<>();
                players.add("all");
                for(Player p : Bukkit.getOnlinePlayers())
                    players.add(p.getName());
                return players;
            }
            return Collections.singletonList("");
        }

        if(args.length == 1)
        {
            return Arrays.asList("stop", "fullscreen", "fullscreen_transparent");
        }
        else if(args.length == 2)
        {
            return colors;
        }
        else if(args.length == 3 || args.length == 4 || args.length == 5)
        {
            return Arrays.asList("5", "10", "20", "40", "60", "80", "100");
        }
        else if(args.length == 6)
        {
            return Arrays.asList("freeze", "nofreeze");
        }
        else if(args.length == 7)
        {
            List<String> players = new ArrayList<>();
            players.add("all");
            for(Player p : Bukkit.getOnlinePlayers())
                players.add(p.getName());
            return players;
        }
        else if(args.length == 8)
        {
            return Collections.singletonList("Welcome to my server!");
        }

        return Collections.singletonList("");
    }
}
