package io.quagmire.randompermissions.placeholders;

import io.quagmire.core.placeholders.CorePlaceholderExpansion;
import io.quagmire.randompermissions.RandomPermissionsPlugin;

public class PlaceholderExpansionWrapper {
  private final RandomPermissionsPlugin plugin;
  private CorePlaceholderExpansion<RandomPermissionsPlugin> placeholderExpansion;

  public PlaceholderExpansionWrapper(RandomPermissionsPlugin plugin) {
    this.plugin = plugin;
  }

  public void register() {
    placeholderExpansion = new CorePlaceholderExpansion<>(plugin, plugin.getPlaceholderManager());
    if (placeholderExpansion.canRegister()) {
      placeholderExpansion.register();
      plugin.getLogger().info("PlaceholderAPI found! Registering placeholders...");
    }
  }

  public void unregister() {
    if (placeholderExpansion != null) placeholderExpansion.unregister();
  }
}