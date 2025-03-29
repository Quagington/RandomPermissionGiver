package io.quagmire.randompermissions.commands.reload;

import io.quagmire.randompermissions.RandomPermissionsPlugin;
import io.quagmire.randompermissions.commands.RandomPermissionsCommand;
import io.quagmire.randompermissions.messages.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class ReloadCommand extends RandomPermissionsCommand {
  public ReloadCommand(RandomPermissionsPlugin plugin, Command command, String[] args, CommandSender sender) {
    super(plugin, command, args, sender);
    setDescription("Reloads the configuration.");
    setPermission(getPermissionPrefix() + ".reload");
    setSyntax("");
  }

  @Override
  public boolean validate() {
    if (!sender.hasPermission(permission)) {
      messageSender(Message.NO_PERMISSIONS);
      return false;
    }
    return true;
  }

  @Override
  public void execute() {
    try {
      plugin.reloadConfig();
      plugin.getConfigurationManager().reloadAll();

      plugin.getMessagesManager().reload();
      plugin.getPermissionGroupManager().reload();

      messageSender(Message.RELOAD_SUCCESS);
    } catch (Exception ex) {
      ex.printStackTrace();
      messageSender(Message.RELOAD_FAILURE);
    }
  }

  @Override
  public List<String> tab() {
    return Collections.emptyList();
  }

  @Override
  public String subcommand() {
    return "reload";
  }
}