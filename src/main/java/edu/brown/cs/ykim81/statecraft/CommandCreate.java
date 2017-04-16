package edu.brown.cs.ykim81.statecraft;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by therl on 4/15/2017.
 */
public class CommandCreate implements CommandExecutor {

  private Database stateDatabase;
  private Database playerDatabase;

  public CommandCreate(Database stateDatabase, Database playerDatabase) {
    this.stateDatabase = stateDatabase;
    this.playerDatabase = playerDatabase;
  }

  public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
    if (strings.length == 2 && strings[0].equals("create")) {
      return createState(sender, strings[1]);
    }
    return false;
  }

  private boolean createState(CommandSender sender, String name) {
    if (sender instanceof Player) {
      Player player = (Player) sender;

      if (name.length() == 0) {
        player.sendMessage("ERROR: State name must not be empty.");
        return true;
      }

      if (playerDatabase.playerIsLeader(player.getUniqueId().toString())) {
        sender.sendMessage("ERROR: You are already a leader of a state!");
        return true;
      }

      if (!stateDatabase.nameOfStateExists(name)) {
        stateDatabase.addNewState(name);
        playerDatabase.addPlayerToState(player.getUniqueId().toString(), name, true);
        sender.sendMessage("State " + name + " created.");
      } else {
        sender.sendMessage("ERROR: State name already exists.");
        return true;
      }
    } else {
      sender.sendMessage("You must be a player!");
      return false;
    }
    return true;
  }
}
