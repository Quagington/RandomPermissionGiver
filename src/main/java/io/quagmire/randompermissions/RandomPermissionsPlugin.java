package io.quagmire.randompermissions;

import io.quagmire.core.CorePlugin;
import io.quagmire.core.chat.ChatToolkit;
import io.quagmire.core.commands.CoreCommandExecutor;
import io.quagmire.core.commands.CoreCommandRegistry;
import io.quagmire.core.commands.CoreCommandTabCompleter;
import io.quagmire.core.configuration.ConfigurationManager;
import io.quagmire.core.folia.impl.PlatformScheduler;
import io.quagmire.core.messages.MessagesManager;
import io.quagmire.core.placeholders.PlaceholderManager;
import io.quagmire.randompermissions.commands.core.HelpCommand;
import io.quagmire.randompermissions.commands.core.UnknownCommand;
import io.quagmire.randompermissions.commands.issue.IssueCommand;
import io.quagmire.randompermissions.commands.reload.ReloadCommand;
import io.quagmire.randompermissions.messages.Message;
import io.quagmire.randompermissions.permissions.group.PermissionGroupManager;
import io.quagmire.randompermissions.placeholders.PlaceholderExpansionWrapper;
import lombok.Getter;
import org.bukkit.event.HandlerList;

import java.util.Objects;

public class RandomPermissionsPlugin extends CorePlugin {
  @Getter private final ChatToolkit chatToolkit;
  @Getter private final MessagesManager messagesManager;
  @Getter private final ConfigurationManager configurationManager;


  @Getter private CoreCommandRegistry<RandomPermissionsPlugin> commandRegistry;
  @Getter private PlatformScheduler scheduler;

  @Getter private final PermissionGroupManager permissionGroupManager;

  @Getter private PlaceholderManager<RandomPermissionsPlugin> placeholderManager;
  private PlaceholderExpansionWrapper placeholderExpansionWrapper;

  public RandomPermissionsPlugin() {
    chatToolkit = new ChatToolkit(this);
    commandRegistry = new CoreCommandRegistry<>(this, RandomPermissionsPlugin.class);
    configurationManager = new ConfigurationManager(this);

    messagesManager = new MessagesManager(this);
    permissionGroupManager = new PermissionGroupManager(this);
  }

  @Override
  public void onEnable() {
    saveDefaultConfig();
    getConfig().options().copyDefaults(true);

    scheduler = folia.getScheduler();
    messagesManager.initialize(Message.getInitializers());

    setupConfigurations();

    permissionGroupManager.reload();

    setupCommands();
    setupPlaceholders();
  }

  private void setupConfigurations() {
    configurationManager.register("groups");
    configurationManager.reloadAll();
  }

  private void setupPlaceholders() {
    if (getServer().getPluginManager().getPlugin("PlaceholderAPI") == null) {
      getLogger().warning("PlaceholderAPI not found, registering placeholders skipped!");
      return;
    }

    placeholderManager = new PlaceholderManager<>(this, RandomPermissionsPlugin.class);
    placeholderManager.setVerbose(getConfig().getBoolean("placeholders.verbose", false));

    placeholderExpansionWrapper = new PlaceholderExpansionWrapper(this);
    placeholderExpansionWrapper.register();
  }

  private void setupCommands() {
    commandRegistry = new CoreCommandRegistry<>(this, RandomPermissionsPlugin.class);
    commandRegistry.setAlias("randompermissions");
    commandRegistry.setPermissionPrefix("randompermissions");
    commandRegistry.setDisplayName("Random Permissions");

    commandRegistry.register(HelpCommand.class);
    commandRegistry.register(UnknownCommand.class);
    commandRegistry.register(ReloadCommand.class);
    commandRegistry.register(IssueCommand.class);

    commandRegistry.setDefaultCommand("help");
    commandRegistry.setFallbackCommand("unknown");

    Objects.requireNonNull(getCommand("randompermissions")).setExecutor(new CoreCommandExecutor<>(this));
    Objects.requireNonNull(getCommand("randompermissions")).setTabCompleter(new CoreCommandTabCompleter<>(this));
  }

  @Override
  public void onDisable() {
    placeholderExpansionWrapper.unregister();
    HandlerList.unregisterAll(this);
    scheduler.cancelAllTasks();
  }
}
