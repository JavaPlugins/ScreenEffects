package dev.lone.ScreenEffects.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.fusesource.jansi.Ansi;

import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extends the normal Bukkit Logger to write Colors
 * Based on Timeout's implementation: https://www.spigotmc.org/threads/87576/
 *
 * @author LoneDev
 */
public class ColorLog
{

    private static final Map<ChatColor, String> BUKKIT_COLORS_TO_ANSI = new EnumMap<>(ChatColor.class);
    static
    {
        BUKKIT_COLORS_TO_ANSI.put(ChatColor.BLACK, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLACK).boldOff().toString());
        BUKKIT_COLORS_TO_ANSI.put(ChatColor.DARK_BLUE, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLUE).boldOff().toString());
        BUKKIT_COLORS_TO_ANSI.put(ChatColor.DARK_GREEN, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.GREEN).boldOff().toString());
        BUKKIT_COLORS_TO_ANSI.put(ChatColor.DARK_AQUA, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.CYAN).boldOff().toString());
        BUKKIT_COLORS_TO_ANSI.put(ChatColor.DARK_RED, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.RED).boldOff().toString());
        BUKKIT_COLORS_TO_ANSI.put(ChatColor.DARK_PURPLE, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.MAGENTA).boldOff().toString());
        BUKKIT_COLORS_TO_ANSI.put(ChatColor.GOLD, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.YELLOW).boldOff().toString());
        BUKKIT_COLORS_TO_ANSI.put(ChatColor.GRAY, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.WHITE).boldOff().toString());
        BUKKIT_COLORS_TO_ANSI.put(ChatColor.DARK_GRAY, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLACK).bold().toString());
        BUKKIT_COLORS_TO_ANSI.put(ChatColor.BLUE, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLUE).bold().toString());
        BUKKIT_COLORS_TO_ANSI.put(ChatColor.GREEN, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.GREEN).bold().toString());
        BUKKIT_COLORS_TO_ANSI.put(ChatColor.AQUA, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.CYAN).bold().toString());
        BUKKIT_COLORS_TO_ANSI.put(ChatColor.RED, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.RED).bold().toString());
        BUKKIT_COLORS_TO_ANSI.put(ChatColor.LIGHT_PURPLE, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.MAGENTA).bold().toString());
        BUKKIT_COLORS_TO_ANSI.put(ChatColor.YELLOW, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.YELLOW).bold().toString());
        BUKKIT_COLORS_TO_ANSI.put(ChatColor.WHITE, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.WHITE).bold().toString());
        BUKKIT_COLORS_TO_ANSI.put(ChatColor.MAGIC, Ansi.ansi().a(Ansi.Attribute.BLINK_SLOW).toString());
        BUKKIT_COLORS_TO_ANSI.put(ChatColor.BOLD, Ansi.ansi().a(Ansi.Attribute.UNDERLINE_DOUBLE).toString());
        BUKKIT_COLORS_TO_ANSI.put(ChatColor.STRIKETHROUGH, Ansi.ansi().a(Ansi.Attribute.STRIKETHROUGH_ON).toString());
        BUKKIT_COLORS_TO_ANSI.put(ChatColor.UNDERLINE, Ansi.ansi().a(Ansi.Attribute.UNDERLINE).toString());
        BUKKIT_COLORS_TO_ANSI.put(ChatColor.ITALIC, Ansi.ansi().a(Ansi.Attribute.ITALIC).toString());
        BUKKIT_COLORS_TO_ANSI.put(ChatColor.RESET, Ansi.ansi().a(Ansi.Attribute.RESET).toString());
    }
    private static final ChatColor[] BUKKIT_CHAT_COLORS = ChatColor.values();
    private static final String PATTERN_HEX_STRING = '\u001b' + "[38;2;%d;%d;%dm";
    private static final Pattern PATTERN_HEX_TRANSLATE = Pattern.compile("§x(§[A-F0-9]){6}", Pattern.CASE_INSENSITIVE);

    private Logger logger;
    private String prefix;

    /**
     * Creates a logger with no prefix.
     */
    public ColorLog()
    {
        this("");
    }

    /**
     * Creates a new ColorLog with prefix.
     *
     * @param prefix the prefix of the plugin
     */
    @SuppressWarnings("ConstantConditions")
    public ColorLog(String prefix)
    {
        this.prefix = applyColors(prefix);
        if(Bukkit.getServer() == null)
            logger = Logger.getGlobal();
        else
            logger = Bukkit.getLogger();
    }

    /**
     * Creates a new ColorLog with prefix and a specific logger hooked.
     *
     * @param prefix the prefix of the plugin
     * @param logger the logger
     */
    public ColorLog(String prefix, Logger logger)
    {
        this.prefix = applyColors(prefix);
        this.logger = logger;
    }

    /**
     * Sets a new prefix for this ColorLog.
     *
     * @param prefix the new prefix of the plugin
     */
    public void setPrefix(String prefix)
    {
        this.prefix = applyColors(prefix);
    }

    /**
     * Changes the current logger.
     *
     * @param logger the logger
     */
    public void setLogger(Logger logger)
    {
        this.logger = logger;
    }

    /**
     * Logs a message in the console. See {@link Logger#log(Level, String)}.
     *
     * @param level   The level of the log
     * @param message the message you want to show
     */
    public void log(Level level, String message)
    {
        logger.log(level, prefix + applyColors(message));
    }

    /**
     * Logs a message in the console. See {@link Logger#log(Level, String, Throwable)}.
     *
     * @param level   the Level of the log
     * @param message the message
     * @param e       the exception
     */
    public void log(Level level, String message, Throwable e)
    {
        logger.log(level, prefix + applyColors(message), e);
    }

    /**
     * Converts a String with Minecraft-ColorCodes into Ansi-Colors.
     * Returns null if the string is null.
     *
     * @param string the string.
     * @return the converted string or null if the string is null
     */
    private static String applyColors(String string)
    {
        if (!string.isEmpty())
        {
            string = ChatColor.translateAlternateColorCodes('&', string);
            String result = convertHexColors(string);
            for (ChatColor color : BUKKIT_CHAT_COLORS)
            {
                result = result.replaceAll("(?i)" + color.toString(), BUKKIT_COLORS_TO_ANSI.getOrDefault(color, ""));
            }

            return result + Ansi.ansi().reset().toString();
        }
        
        return string;
    }

    private static String convertHexColors(String input)
    {
        Matcher matcher = PATTERN_HEX_TRANSLATE.matcher(input);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find())
        {
            String s = matcher.group().replace("§", "").replace('x', '#');
            java.awt.Color color = java.awt.Color.decode(s);
            int red = color.getRed();
            int blue = color.getBlue();
            int green = color.getGreen();
            String replacement = String.format(PATTERN_HEX_STRING, red, green, blue);
            matcher.appendReplacement(buffer, replacement);
        }

        matcher.appendTail(buffer);
        return buffer.toString();
    }
}