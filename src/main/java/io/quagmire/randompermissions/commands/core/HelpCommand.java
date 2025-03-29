package io.quagmire.randompermissions.commands.core;

import io.quagmire.randompermissions.RandomPermissionsPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class HelpCommand extends io.quagmire.core.commands.help.HelpCommand<RandomPermissionsPlugin> {
  public HelpCommand(RandomPermissionsPlugin plugin, Command command, String[] args, CommandSender sender) {
    super(plugin, command, args, sender, plugin.getCommandRegistry());
  }
}
