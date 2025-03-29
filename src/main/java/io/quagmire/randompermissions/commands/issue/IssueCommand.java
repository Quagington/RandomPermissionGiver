package io.quagmire.randompermissions.commands.issue;

import io.quagmire.randompermissions.RandomPermissionsPlugin;
import io.quagmire.randompermissions.commands.RandomPermissionsCommand;
import io.quagmire.randompermissions.messages.Message;
import io.quagmire.randompermissions.permissions.group.PermissionGroup;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class IssueCommand extends RandomPermissionsCommand {
  private Player target;

  public IssueCommand(RandomPermissionsPlugin plugin, Command command, String[] args, CommandSender sender) {
    super(plugin, command, args, sender);
    setDescription("Issues a random permission to a player.");
    setPermission(getPermissionPrefix() + ".issue");
    setSyntax("[player] [group]");
  }

  @Override
  public boolean validate() {
    if (!sender.hasPermission(permission)) {
      messageSender(Message.NO_PERMISSIONS);
      return false;
    }

    if (args.length != 2) {
      messageSender(getSyntax());
      return false;
    }

    target = plugin.getServer().getPlayer(args[0]);
    if (target == null) {
      messageSender(Message.PLAYER_NOT_FOUND);
      return false;
    }

    if (!plugin.getPermissionGroupManager().groupExists(args[1])) {
      messageSender(Message.GROUP_NOT_FOUND);
      return false;
    }

    return true;
  }

  @Override
  public void execute() {
    try {
      PermissionGroup group = plugin.getPermissionGroupManager().getGroup(args[1]);
      if (group == null) {
        messageSender(Message.GROUP_NOT_FOUND);
        return;
      }

      group.issue(target)
        .thenAccept((entry) -> {
          if (entry == null) {
            if (sender instanceof Player) messageSender(Message.PERMISSION_NOT_ISSUED);
            return;
          }

          Map<String, String> placeholders = Map.of(
            "player", target.getName(),
            "group", group.getId(),
            "permission", entry.getDisplayName()
          );

          if (sender instanceof Player) messageSender(Message.PERMISSION_ISSUED, placeholders);
        })
        .exceptionally((ex) -> {
          plugin.getLogger().severe("Failed to issue permission: " + ex.getMessage());
          ex.printStackTrace();
          messageSender(Message.ERROR_GENERIC);
          return null;
        });
    }
    catch (Exception e) {
      plugin.getLogger().severe("Failed to issue random permission: " + e.getMessage());
      e.printStackTrace();
      messageSender(Message.ERROR_GENERIC);
    }
  }

  @Override
  public List<String> tab() {
    return switch (args.length) {
      case 1 -> getTabPlayers(args[0]);
      case 2 -> plugin.getPermissionGroupManager().getMatchingGroups(args[1]);
      default -> List.of();
    };
  }

  @Override
  public String subcommand() {
    return "issue";
  }
}
