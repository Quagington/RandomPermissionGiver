package io.quagmire.randompermissions.commands;

import io.quagmire.core.commands.CoreCommand;
import io.quagmire.randompermissions.RandomPermissionsPlugin;
import io.quagmire.randompermissions.messages.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;

public abstract class RandomPermissionsCommand extends CoreCommand<RandomPermissionsPlugin> {
  public RandomPermissionsCommand(RandomPermissionsPlugin plugin, Command command, String[] args, CommandSender sender) {
    super(plugin, command, args, sender);
  }

  public List<String> getTabPermissionGroups(String query) {
    return plugin.getPermissionGroupManager().getMatchingGroups(query);
  }

  protected void messageSender(Message message) {
    super.messageSender(message.name(), true);
  }

  protected void messageSender(Message message, Map<String, String> renders) {
    super.messageSender(message.name(), true, renders);
  }
}
