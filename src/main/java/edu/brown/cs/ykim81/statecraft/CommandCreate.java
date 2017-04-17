package edu.brown.cs.ykim81.statecraft;

import com.google.common.collect.ImmutableMap;
import edu.brown.cs.ykim81.statecraft.database.Database;
import edu.brown.cs.ykim81.statecraft.database.PlayerProxy;
import edu.brown.cs.ykim81.statecraft.database.StateProxy;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

/**
 * Created by therl on 4/15/2017.
 */
public class CommandCreate implements CommandExecutor {

  private Database db;

  public CommandCreate(Database db) {
    this.db = db;
  }

  public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
    if (strings.length == 2 && strings[0].equals("create")) {
      return createState(sender, strings[1]);
    } else if (strings.length == 2 && strings[0].equals("tax")) {
      return setTax(sender, strings[1]);
    } else if (strings.length == 1 && strings[0].equals("leave")) {
      return leaveState(sender);
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

      if (playerIsLeader(player.getUniqueId().toString())) {
        sender.sendMessage("ERROR: You are already a leader of a state!");
        return true;
      }

      if (!db.searchState(ImmutableMap.<String, Object>of("name", name))) {
        StateProxy state = db.createState(name);
        db.createPlayer(player.getUniqueId().toString(), state.getId(), 1);
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

  private boolean setTax(CommandSender sender, String number) {
    if (sender instanceof Player) {
      Player player = (Player) sender;
      if (!playerIsLeader(player.getUniqueId().toString())) {
        sender.sendMessage("ERROR: You are not a leader.");
        return true;
      }

      int taxRate = 0;
      try {
        taxRate = Integer.parseInt(number);
      } catch (NumberFormatException e) {
        sender.sendMessage("ERROR: Tax rate must be an integer.");
        return true;
      }

      if (taxRate < 0 || taxRate > 100) {
        sender.sendMessage("ERROR: Tax rate must be between 0 to 100");
        return true;
      }

      int stateId = db.readPlayer(ImmutableMap.<String, Object>of("id", player.getUniqueId().toString())).get(0).getState();
      db.updateState(stateId, ImmutableMap.<String, Object>of("tax", taxRate));
      sender.sendMessage("Tax rate updated to " + taxRate + ".");
      return true;
    } else {
      sender.sendMessage("You must be a player!");
      return false;
    }
  }

  private boolean leaveState(CommandSender sender) {
    if (!(sender instanceof Player)) {
      sender.sendMessage("You must be a player!");
      return false;
    }

    sender.sendMessage("This is a test message");
    return true;
  }

  private boolean playerIsLeader(String id) {
    List<PlayerProxy> li = db.readPlayer(ImmutableMap.<String, Object>of("id", id));
    if (li.size() == 0) {
      return false;
    }
    PlayerProxy player = li.get(0);
    if (player.getLeader() == 1) {
      return true;
    } else {
      return false;
    }
  }
}
