package io.quagmire.randompermissions.permissions.entry;

import io.quagmire.randompermissions.RandomPermissionsPlugin;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PermissionEntry {
  private final RandomPermissionsPlugin plugin;

  @Getter private final String id;
  @Getter private final String permission;
  @Getter private final String displayName;
  @Getter private final List<String> commands;

  public PermissionEntry(RandomPermissionsPlugin plugin, String id, String permission, String displayName, List<String> commands) {
    this.plugin = plugin;
    this.id = id;
    this.permission = permission;
    this.displayName = displayName;
    this.commands = commands;
  }

  public static @Nullable PermissionEntry deserialize(RandomPermissionsPlugin plugin, @Nullable ConfigurationSection section) {
    if (section == null) return null;

    String id = section.getString("id");
    if (id == null) {
      plugin.getLogger().warning("Permission entry is missing an id!");
      return null;
    }

    String permission = section.getString("permission");
    String displayName = section.getString("display-name");
    List<String> commands = section.getStringList("commands");

    return new PermissionEntry(plugin, id.toLowerCase(), permission, displayName, commands);
  }
}
