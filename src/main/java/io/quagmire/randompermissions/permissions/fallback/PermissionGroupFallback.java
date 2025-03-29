package io.quagmire.randompermissions.permissions.fallback;

import io.quagmire.randompermissions.RandomPermissionsPlugin;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PermissionGroupFallback {
  private final RandomPermissionsPlugin plugin;

  @Getter private final String message;
  @Getter private final List<String> commands;

  public PermissionGroupFallback(RandomPermissionsPlugin plugin, String message, List<String> commands) {
    this.plugin = plugin;
    this.message = message;
    this.commands = commands;
  }

  public static @Nullable PermissionGroupFallback deserialize(RandomPermissionsPlugin plugin, @Nullable ConfigurationSection section) {
    if (section == null) return null;

    String message = section.getString("message");
    List<String> commands = section.getStringList("commands");

    return new PermissionGroupFallback(plugin, message, commands);
  }
}
