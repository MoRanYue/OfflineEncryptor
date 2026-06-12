package io.github.lumine1909.offlineencryptor.paper;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

/**
 * Compatibility utility for detecting LimitedOfflineMode and checking
 * whether a given username is whitelisted for offline-mode access.
 *
 * <p>All interaction is done via reflection so that OfflineEncryptor has
 * <b>no hard dependency</b> on LimitedOfflineMode at compile or runtime.</p>
 *
 * <p>Usage:
 * <pre>{@code
 *   if (LimitedOfflineModeCompat.isPresent() && !LimitedOfflineModeCompat.isUserAllowed(username)) {
 *       // Not whitelisted – let normal online-mode auth handle this player.
 *   }
 * }</pre></p>
 */
public final class LimitedOfflineModeCompat {

    private static Boolean present = null;
    private static Method isUserAllowedMethod = null;

    private LimitedOfflineModeCompat() {
        // Utility class – no instantiation.
    }

    /**
     * Returns {@code true} if the LimitedOfflineMode plugin is loaded
     * and the {@code isUserAllowed} method is accessible.
     */
    public static boolean isPresent() {
        if (present == null) {
            initialize();
        }
        return present;
    }

    /**
     * Checks whether the given username is allowed by LimitedOfflineMode.
     *
     * @param username the player's username (case-insensitive on LOM's side)
     * @return {@code true} if the player is whitelisted, {@code false} otherwise
     */
    public static boolean isUserAllowed(String username) {
        if (!isPresent() || username == null || username.isEmpty()) {
            return false;
        }
        try {
            Plugin plugin = Bukkit.getPluginManager().getPlugin("LimitedOfflineMode");
            if (plugin == null) {
                return false;
            }
            return (boolean) isUserAllowedMethod.invoke(plugin, username);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Performs one-time reflection-based detection of LimitedOfflineMode.
     */
    private static void initialize() {
        try {
            // Verify the main plugin class exists on the classpath.
            Class.forName("de.moritxius.limitedofflinemode.LimitedOfflineModePaper");

            Plugin plugin = Bukkit.getPluginManager().getPlugin("LimitedOfflineMode");
            if (plugin != null) {
                isUserAllowedMethod = plugin.getClass().getMethod("isUserAllowed", String.class);
                present = true;
                return;
            }
        } catch (Exception ignored) {
            // LimitedOfflineMode is not installed or not loaded.
        }
        present = false;
    }
}
