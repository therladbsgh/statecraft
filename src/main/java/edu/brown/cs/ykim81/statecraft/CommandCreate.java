package edu.brown.cs.ykim81.statecraft;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by therl on 4/15/2017.
 */
public class CommandCreate implements CommandExecutor {

  public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
    if (strings.length == 1 && strings[0].equals("create")) {
      return createState(sender);
    }
    return false;
  }

  private boolean createState(CommandSender sender) {
    if (sender instanceof Player) {
      Player player = (Player) sender;
      player.sendMessage("Hello");
    } else {
      sender.sendMessage("You must be a player!");
      return false;
    }
    return true;
  }
}
