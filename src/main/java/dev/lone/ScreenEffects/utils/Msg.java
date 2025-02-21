package dev.lone.ScreenEffects.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;

public class Msg
{
    public static String PREFIX = "[ScreenEffects] ";
    public static String PREFIX_CONSOLE = "[ScreenEffects] ";
    private static final ColorLog LOG = new ColorLog(PREFIX);

    public static void setPrefix(String prefix)
    {
        PREFIX = prefix;
        LOG.setPrefix(prefix);
    }

    public static void setPrefixConsole(String prefix)
    {
        PREFIX_CONSOLE = prefix;
    }

    public static void toConsoleAndSender(CommandSender commandSender, String message)
    {
        toConsoleAndSender(commandSender, message, null);
    }

    public static void toConsoleAndSender(CommandSender commandSender, String message, @Nullable Throwable e)
    {
        log(message);
        if (commandSender != null && commandSender != Bukkit.getConsoleSender())
            commandSender.sendMessage(PREFIX + message);

        if (e != null)
            e.printStackTrace();
    }

    public static void log(String message)
    {
        LOG.log(Level.INFO, message);
    }

    public static void log(String error, Level level)
    {
        LOG.log(level, error);
    }

    public static void error(String error)
    {
        if (ChatColor.stripColor(error).equals(error))
            LOG.log(Level.SEVERE, ChatColor.RED + error);
        else
            LOG.log(Level.SEVERE, error);
    }

    public static void error(String error, @Nullable Throwable e)
    {
        if (ChatColor.stripColor(error).equals(error))
            LOG.log(Level.SEVERE, ChatColor.RED + error);
        else
            LOG.log(Level.SEVERE, error);

        if (e != null)
            e.printStackTrace();
    }

    public static void error(CommandSender commandSender, String error)
    {
        error(commandSender, error, null);
    }

    public static void error(CommandSender commandSender, String error, @Nullable Throwable e)
    {
        if (commandSender instanceof Player)
        {
            message(commandSender, error);
        }
        else
        {
            if (ChatColor.stripColor(error).equals(error))
                LOG.log(Level.SEVERE, ChatColor.RED + error);
            else
                LOG.log(Level.SEVERE, error);
        }

        if (e != null)
            e.printStackTrace();
    }

    public static void warn(String error)
    {
        if (ChatColor.stripColor(error).equals(error))
            LOG.log(Level.WARNING, ChatColor.YELLOW + error);
        else
            LOG.log(Level.WARNING, error);
    }

    public static void warn(String error, @Nullable Throwable e)
    {
        warn(error);

        if (e != null)
            e.printStackTrace();
    }

    public static void messageNoPrefix(CommandSender commandSender, String message)
    {
        commandSender.sendMessage(message);
    }

    public static void message(Player player, String message)
    {
        player.sendMessage(PREFIX + message);
    }
    public static void message(CommandSender sender, String message)
    {
        sender.sendMessage(PREFIX + message);
    }
}
