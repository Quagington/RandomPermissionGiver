package io.quagmire.randompermissions.permissions.group;

import io.quagmire.core.utilities.files.FileCollection;
import io.quagmire.randompermissions.RandomPermissionsPlugin;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PermissionGroupManager {
  private final RandomPermissionsPlugin plugin;
  private final Map<String, PermissionGroup> groups;

  @Getter private String setPermissionCommand;

  public PermissionGroupManager(RandomPermissionsPlugin plugin) {
    this.plugin = plugin;
    this.groups = new HashMap<>();
  }

  public List<String> getMatchingGroups(String query) {
    List<String> matches = new ArrayList<>();
    for (PermissionGroup group : groups.values()) {
      if (group.getId().contains(query.toLowerCase())) matches.add(group.getId());
    }
    return matches;
  }

  public boolean groupExists(String id) {
    return groups.containsKey(id);
  }

  public @Nullable PermissionGroup getGroup(String id) {
    return groups.get(id);
  }

  public void reload() {
    reloadParent();

    /* Determine if the groups directory exists, if not create it. */
    File directory = new File(plugin.getDataFolder(), "groups");

    if (!directory.exists() || !directory.isDirectory()) {
      directory.mkdirs();
      plugin.getLogger().info("Created groups directory because it did not exist.");
      return;
    }

    /*
     * Load all files in the groups directory that end with .yml and deserialize them into PermissionGroup objects.
     * If a group is successfully deserialized, add it to the groups map.
     */
    FileCollection collection = new FileCollection();
    List<File> files = collection.findAllFilesInFolder(directory)
      .stream()
      .filter(file -> file.getName().endsWith(".yml"))
      .toList();

    for (File file : files) {
      YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
      PermissionGroup group = PermissionGroup.deserialize(plugin, config);
      if (group != null) groups.put(group.getId(), group);
    }

    plugin.getLogger().info("Loaded " + groups.size() + " permission groups.");
  }

  private void reloadParent() {
    plugin.getConfigurationManager().reload("groups");

    YamlConfiguration config = plugin.getConfigurationManager().getConfiguration("groups");

    config.addDefault("commands.set-permission", "lp user %player% permission set %permission% true");
    plugin.getConfigurationManager().save("groups", config);

    setPermissionCommand = config.getString("commands.set-permission", "");

    groups.clear();
  }
}
