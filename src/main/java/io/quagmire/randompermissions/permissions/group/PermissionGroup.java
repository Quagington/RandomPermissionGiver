package io.quagmire.randompermissions.permissions.group;

import io.quagmire.core.utilities.random.RandomWrapper;
import io.quagmire.randompermissions.RandomPermissionsPlugin;
import io.quagmire.randompermissions.permissions.entry.PermissionEntry;
import io.quagmire.randompermissions.permissions.fallback.PermissionGroupFallback;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class PermissionGroup {
  private final RandomPermissionsPlugin plugin;

  @Getter private final String id;
  private final String message;
  private final Map<String, PermissionEntry> permissions;
  private final PermissionGroupFallback fallback;

  public PermissionGroup(RandomPermissionsPlugin plugin, String id, String message, Map<String, PermissionEntry> permissions, PermissionGroupFallback fallback) {
    this.plugin = plugin;
    this.id = id;
    this.message = message;
    this.permissions = permissions;
    this.fallback = fallback;
  }

  public CompletableFuture<PermissionEntry> issue(Player player) {
    CompletableFuture<PermissionEntry> future = new CompletableFuture<>();

    List<PermissionEntry> entries = new ArrayList<>();
    for (PermissionEntry entry : permissions.values()) {
      if (!player.hasPermission(entry.getPermission())) entries.add(entry);
    }

    /* Player has all permissions for this group. */
    if (entries.isEmpty()) {
      if (fallback == null) {
        plugin.getLogger().warning("Player " + player.getName() + " has all permissions for group " + id + " and there is no fallback!");
        future.complete(null);
        return future;
      }

      plugin.getLogger().warning("Player " + player.getName() + " has all permissions for group " + id + ", issuing fallback.");
      plugin.getChatToolkit().message(player, fallback.getMessage());

      plugin.getScheduler().runNextTick((task) -> {
        for (String command : fallback.getCommands()) {
          plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command.replaceAll("%player%", player.getName()));
        }
      });
      future.complete(null);
      return future;
    }

    SecureRandom random = RandomWrapper.getInstance().getRandom();
    PermissionEntry entry = entries.get(random.nextInt(entries.size()));

    Map<String, String> placeholders = Map.of(
      "name", entry.getDisplayName()
    );

    plugin.getChatToolkit().message(player, message, false, placeholders);

    plugin.getScheduler().runNextTick((task) -> {
      try {
        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), plugin.getPermissionGroupManager().getSetPermissionCommand()
            .replaceAll("%player%", player.getName())
            .replaceAll("%permission%", entry.getPermission())
        );

        for (String command : entry.getCommands()) {
          plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command.replaceAll("%player%", player.getName()));
        }

        plugin.getLogger().info("Issued permission " + entry.getPermission() + " to " + player.getName() + " for group " + id);
        future.complete(entry);
      }
      catch (Exception e) {
        plugin.getLogger().severe("Failed to issue permission " + entry.getPermission() + " to " + player.getName() + ": " + e.getMessage());
        future.completeExceptionally(e);
      }
    });

    return future;
  }

  public static @Nullable PermissionGroup deserialize(RandomPermissionsPlugin plugin, @Nullable ConfigurationSection section) {
    if (section == null) return null;

    String id = section.getString("id");
    if (id == null) {
      plugin.getLogger().warning("Permissions group does not have an id!");
      return null;
    }

    String message = section.getString("message", "");
    Map<String, PermissionEntry> permissions = new HashMap<>();

    ConfigurationSection permissionsSection = section.getConfigurationSection("permissions");
    if (permissionsSection == null) {
      plugin.getLogger().warning("Permissions group " + id + " does not have a permissions section!");
      return null;
    }

    for (String key : permissionsSection.getKeys(false)) {
      PermissionEntry permission = PermissionEntry.deserialize(plugin, permissionsSection.getConfigurationSection(key));
      if (permission != null) permissions.put(permission.getId(), permission);
    }

    PermissionGroupFallback fallback = PermissionGroupFallback.deserialize(plugin, section.getConfigurationSection("fallback"));

    return new PermissionGroup(plugin, id.toLowerCase(), message, permissions, fallback);
  }
}
