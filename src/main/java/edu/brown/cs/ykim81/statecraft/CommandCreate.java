package edu.brown.cs.ykim81.statecraft;

import com.google.common.collect.ImmutableMap;
import edu.brown.cs.ykim81.statecraft.database.*;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Created by therl on 4/15/2017.
 */
public class CommandCreate implements CommandExecutor {

  private Database db;
  private Map<String, Integer> invitedPlayers;

  public CommandCreate(Database db) {
    this.db = db;
    this.invitedPlayers = new HashMap<>();
  }

  public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
    if (strings.length == 2 && strings[0].equals("create")) {
      return createState(sender, strings[1]);
    } else if (strings.length == 2 && strings[0].equals("tax")) {
      return setTax(sender, strings[1]);
    } else if (strings.length == 1 && strings[0].equals("leave")) {
      return leaveState(sender);
    } else if (strings.length == 1 && strings[0].equals("info")) {
      return getStateInfo(sender);
    } else if (strings.length == 1 && strings[0].equals("help")){
      return getHelp(sender);
    } else if (strings.length == 2 && strings[0].equals("help")) {
      return getHelp(sender, strings[1]);
    } else if (strings.length == 2 && strings[0].equals("invite")) {
      return invitePlayer(sender, strings[1]);
    } else if (strings.length == 1 && strings[0].equals("accept")) {
      return acceptInvite(sender);
    } else if (strings.length == 1 && strings[0].equals("reject")) {
      return rejectInvite(sender);
    } else if (strings.length == 1 && strings[0].equals("claim")) {
      return claimChunk(sender);
    } else if (strings.length == 1 && strings[0].equals("unclaim")) {
      return unclaimChunk(sender);
    } else if (strings.length == 2 && strings[0].equals("district")) {
      return setDistrictOfChunk(sender, strings[1]);
    } else if (strings.length == 1 && strings[0].equals("debug")) {
      return debugMessage(sender);
    }
    return false;
  }

  private boolean debugMessage(CommandSender sender) {
    if (!(sender instanceof Player)) {
      sender.sendMessage("You must be a player!");
      return false;
    }
    Player player = (Player) sender;

    player.sendMessage("---DEBUG---");
    if (player.hasPermission("sc.player.bandit")) {
      player.sendMessage("You are a bandit.");
    }

    if (player.hasPermission("sc.player.citizen")) {
      player.sendMessage("You are a citizen.");
    }

    if (player.hasPermission("sc.player.leader")) {
      player.sendMessage("You are a leader.");
    }
    return true;
  }

  private boolean getHelp(CommandSender sender) {
    sender.sendMessage("----SC Help----");
    sender.sendMessage("/sc help general: General commands");
    sender.sendMessage("/sc help leader: Leader commands");
    sender.sendMessage("/sc help citizen: Citizen commands");
    return true;
  }

  private boolean getHelp(CommandSender sender, String arg) {
    if (arg.equals("leader")) {
      sender.sendMessage("----SC Leader Commands----");
      sender.sendMessage("/sc tax [number]: Sets the tax rate of your state");
      sender.sendMessage("/sc claim: Claim the chunk you are standing on");
      sender.sendMessage("/sc unclaim: Unclaim the chunk that you are standing on");
      sender.sendMessage("/sc district [type]: Sets the district of the chunk");
      return true;
    } else if (arg.equals("general")) {
      sender.sendMessage("----SC General Commands----");
      sender.sendMessage("/sc create [name]: Creates a state");
      sender.sendMessage("/sc info: Gets the info of your state");
      sender.sendMessage("/sc leave: Abandon your citizenship");
      sender.sendMessage("/sc invite [name]: Invites a player to the state");
      sender.sendMessage("/sc accept: Accepts an invite from a state");
      sender.sendMessage("/sc reject: Rejects an invite from a state");
      return true;
    } else if (arg.equals("citizen")) {
      sender.sendMessage("----SC Citizen Commands----");
      sender.sendMessage("None yet!");
      return true;
    } else {
      return getHelp(sender);
    }
  }

  private boolean createState(CommandSender sender, String name) {
    if (!(sender instanceof Player)) {
      sender.sendMessage("You must be a player!");
      return false;
    }
    Player player = (Player) sender;

    if (name.length() == 0) {
      player.sendMessage("ERROR: State name must not be empty.");
      return true;
    }

    if (playerIsLeader(player.getUniqueId().toString())) {
      sender.sendMessage("ERROR: You are already a leader of a state!");
      return true;
    }

    if (db.searchState(ImmutableMap.<String, Object>of("name", name))) {
      sender.sendMessage("ERROR: State name already exists.");
      return true;
    }

    Chunk chunk = player.getWorld().getChunkAt(player.getLocation());
    List<ChunkProxy> chunkList = db.getChunk(ImmutableMap.<String, Object>of("x", chunk.getX(), "z", chunk.getZ()));
    if (chunkList.size() > 0) {
      sender.sendMessage("ERROR: You cannot start a state on claimed land.");
      return true;
    }

    StateProxy state = db.createState(name);
    db.createPlayer(player.getUniqueId().toString(), state.getId(), 1);
    claimChunk(sender);
    PermissionsListener.makeLeader(player);
    sender.sendMessage("State " + name + " created.");
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
    Player player = (Player) sender;

    if (db.readPlayer(ImmutableMap.<String, Object>of("id", player.getUniqueId().toString())).size() == 0) {
      sender.sendMessage("ERROR: You are not in a state.");
      return true;
    }

    int stateId = db.readPlayer(ImmutableMap.<String, Object>of("id", player.getUniqueId().toString())).get(0).getState();
    int playersInState = db.countPlayer(ImmutableMap.<String, Object>of("state", stateId));

    if (playerIsLeader(player.getUniqueId().toString()) && playersInState > 1) {
      sender.sendMessage("ERROR: You cannot leave if you are a leader and other people are in the state.");
      return true;
    }

    db.deletePlayer(player.getUniqueId().toString());
    PermissionsListener.makeBandit(player);
    sender.sendMessage("You have abandoned your citizenship.");
    if (playersInState == 1) {
      db.deleteState(stateId);
      List<ChunkProxy> chunks = db.getChunk(ImmutableMap.<String, Object>of("state", stateId));
      for (ChunkProxy c : chunks) {
        db.deleteChunk(c.getX(), c.getZ());
      }
      sender.sendMessage("As you were the last person, the state is now removed from existence.");
    }
    return true;
  }

  private boolean getStateInfo(CommandSender sender) {
    if (!(sender instanceof Player)) {
      sender.sendMessage("You must be a player!");
      return false;
    }
    Player player = (Player) sender;

    List<PlayerProxy> playerList = db.readPlayer(ImmutableMap.<String, Object>of("id", player.getUniqueId().toString()));
    if (playerList.size() == 0) {
      sender.sendMessage("ERROR: You are not a citizen of a state!");
      return true;
    }

    int stateId = playerList.get(0).getState();
    StateProxy state = db.readState(ImmutableMap.<String, Object>of("id", stateId)).get(0);

    List<PlayerProxy> players = db.readPlayer(ImmutableMap.<String, Object>of("state", stateId));
    List<String> leaders = new ArrayList<>();
    List<String> nonLeaders = new ArrayList<>();

    for (PlayerProxy p : players) {
      if (playerIsLeader(p.getId())) {
        leaders.add(Bukkit.getOfflinePlayer(UUID.fromString(p.getId())).getName());
      } else {
        nonLeaders.add(Bukkit.getOfflinePlayer(UUID.fromString(p.getId())).getName());
      }
    }

    sender.sendMessage("----" + state.getName() + "----");
    sender.sendMessage("Leaders: " + String.join(", ", leaders));
    sender.sendMessage("Citizens: " + String.join(", ", nonLeaders));
    sender.sendMessage("Funds: $" + state.getMoney());
    sender.sendMessage("Tax rate: " + state.getTax());
    return true;
  }

  /**
   * BUG: Does not work if there are multiple players with the same name.
   * BUG: Assumes a relation in the players database = player is in a state.
   *
   * @param sender
   * @param name
   * @return
   */
  private boolean invitePlayer(CommandSender sender, String name) {
    if (!(sender instanceof Player)) {
      sender.sendMessage("You must be a player!");
      return false;
    }
    Player player = (Player) sender;

    List<PlayerProxy> playerList = db.readPlayer(ImmutableMap.<String, Object>of("id", player.getUniqueId().toString()));
    if (playerList.size() == 0) {
      sender.sendMessage("ERROR: You are not a citizen of a state!");
      return true;
    }

    List<Player> playerList1 = getPlayer(name);
    if (playerList1.size() == 0) {
      sender.sendMessage("ERROR: There is no online player with that name.");
      return true;
    }

    Player playerToInvite = playerList1.get(0);
    if (db.searchPlayer(ImmutableMap.<String, Object>of("id", playerToInvite.getUniqueId().toString()))) {
      sender.sendMessage("ERROR: The player is already in a state!");
      return true;
    }

    if (invitedPlayers.containsKey(playerToInvite.getUniqueId().toString())) {
      sender.sendMessage("ERROR: That player already has a pending invite.");
      return true;
    }

    int stateId = playerList.get(0).getState();
    String stateName = db.readState(ImmutableMap.<String, Object>of("id", stateId)).get(0).getName();
    invitedPlayers.put(playerToInvite.getUniqueId().toString(), stateId);
    player.sendMessage("Sent an invite to " + name + ".");
    playerToInvite.sendMessage("You have been invited to become a citizen of " + stateName + "!");
    playerToInvite.sendMessage("Type '/sc accept' to become a citizen, or /sc reject to reject the offer.");
    return true;
  }

  /**
   * BUG: Assumes a relation in the players database = player is in a state.
   *
   * @param sender
   * @return
   */
  private boolean acceptInvite(CommandSender sender) {
    if (!(sender instanceof Player)) {
      sender.sendMessage("You must be a player!");
      return false;
    }
    Player player = (Player) sender;

    if (!invitedPlayers.containsKey(player.getUniqueId().toString())) {
      sender.sendMessage("ERROR: You are not invited to any state.");
      return true;
    }

    List<PlayerProxy> playerList = db.readPlayer(ImmutableMap.<String, Object>of("id", player.getUniqueId().toString()));
    if (playerList.size() > 0) {
      sender.sendMessage("ERROR: You are already a citizenship of another state!");
      return true;
    }

    int stateId = invitedPlayers.get(player.getUniqueId().toString());
    db.createPlayer(player.getUniqueId().toString(), stateId, 0);
    PermissionsListener.makeCitizen(player);
    invitedPlayers.remove(player.getUniqueId().toString());
    sender.sendMessage("You have now joined the state!");
    return true;
  }

  /**
   * BUG: Assumes a relation in the player database = player is in a state.
   *
   * @param sender
   * @return
   */
  private boolean rejectInvite(CommandSender sender) {
    if (!(sender instanceof Player)) {
      sender.sendMessage("You must be a player!");
      return false;
    }
    Player player = (Player) sender;

    if (!invitedPlayers.containsKey(player.getUniqueId().toString())) {
      sender.sendMessage("ERROR: You are not invited to any state.");
      return true;
    }

    invitedPlayers.remove(player.getUniqueId().toString());
    sender.sendMessage("You have rejected the offer.");
    return true;
  }

  /**
   * BUG: Assumes a relation in the player database = player is in a state.
   * Assumes Player UUID is unique (it should be).
   *
   * @param sender
   * @return
   */
  public boolean claimChunk(CommandSender sender) {
    if (!(sender instanceof Player)) {
      sender.sendMessage("You must be a player!");
      return false;
    }
    Player player = (Player) sender;

    if (!playerIsLeader(player.getUniqueId().toString())) {
      sender.sendMessage("ERROR: You must be a leader to do this.");
      return true;
    }

    List<PlayerProxy> playerList = db.readPlayer(ImmutableMap.<String, Object>of("id", player.getUniqueId().toString()));
    if (playerList.size() == 0) {
      sender.sendMessage("ERROR: You are not in a state!");
      return true;
    }

    Chunk chunk = player.getWorld().getChunkAt(player.getLocation());
    List<ChunkProxy> chunkList = db.getChunk(ImmutableMap.<String, Object>of("x", chunk.getX(), "z", chunk.getZ()));
    if (chunkList.size() > 0) {
      sender.sendMessage("ERROR: This land is already claimed.");
      return true;
    }

    db.createChunk(chunk.getX(), chunk.getZ(), playerList.get(0).getState(), District.STATE);
    sender.sendMessage("You have claimed the land.");
    return true;
  }

  public boolean unclaimChunk(CommandSender sender) {
    if (!(sender instanceof Player)) {
      sender.sendMessage("You must be a player!");
      return false;
    }
    Player player = (Player) sender;

    if (!playerIsLeader(player.getUniqueId().toString())) {
      sender.sendMessage("ERROR: You must be a leader to do this.");
      return true;
    }

    List<PlayerProxy> playerList = db.readPlayer(ImmutableMap.<String, Object>of("id", player.getUniqueId().toString()));
    if (playerList.size() == 0) {
      sender.sendMessage("ERROR: You are not in a state!");
      return true;
    }

    Chunk chunk = player.getWorld().getChunkAt(player.getLocation());
    List<ChunkProxy> chunkList = db.getChunk(ImmutableMap.<String, Object>of("x", chunk.getX(), "z", chunk.getZ()));
    if (chunkList.size() == 0) {
      sender.sendMessage("ERROR: This land is not claimed.");
      return true;
    }

    if (chunkList.get(0).getId() != playerList.get(0).getState()) {
      sender.sendMessage("ERROR: This is not your state land!");
      return true;
    }

    db.deleteChunk(chunk.getX(), chunk.getZ());
    sender.sendMessage("You have unclaimed the land.");
    return true;
  }

  public boolean setDistrictOfChunk(CommandSender sender, String district) {
    if (!(sender instanceof Player)) {
      sender.sendMessage("You must be a player!");
      return false;
    }
    Player player = (Player) sender;

    if (!playerIsLeader(player.getUniqueId().toString())) {
      sender.sendMessage("ERROR: You must be a leader to do this.");
      return true;
    }

    List<PlayerProxy> playerList = db.readPlayer(ImmutableMap.<String, Object>of("id", player.getUniqueId().toString()));
    if (playerList.size() == 0) {
      sender.sendMessage("ERROR: You are not in a state!");
      return true;
    }

    Chunk chunk = player.getWorld().getChunkAt(player.getLocation());
    List<ChunkProxy> chunkList = db.getChunk(ImmutableMap.<String, Object>of("x", chunk.getX(), "z", chunk.getZ()));
    if (chunkList.size() == 0) {
      sender.sendMessage("ERROR: This land is not claimed.");
      return true;
    }

    if (chunkList.get(0).getId() != playerList.get(0).getState()) {
      sender.sendMessage("ERROR: This is not your state land!");
      return true;
    }

    switch (district.toLowerCase()) {
      case "state":
        db.updateChunk(chunk.getX(), chunk.getZ(), ImmutableMap.<String, Object>of("district", District.STATE.toString()));
        sender.sendMessage("Updated district to State.");
        return true;
      case "primary":
        db.updateChunk(chunk.getX(), chunk.getZ(), ImmutableMap.<String, Object>of("district", District.PRIMARY.toString()));
        sender.sendMessage("Updated district to Primary.");
        return true;
      case "industrial":
        db.updateChunk(chunk.getX(), chunk.getZ(), ImmutableMap.<String, Object>of("district", District.INDUSTRIAL.toString()));
        sender.sendMessage("Updated district to Industrial.");
        return true;
      case "military":
        db.updateChunk(chunk.getX(), chunk.getZ(), ImmutableMap.<String, Object>of("district", District.MILITARY.toString()));
        sender.sendMessage("Updated district to Military.");
        return true;
      case "university":
        db.updateChunk(chunk.getX(), chunk.getZ(), ImmutableMap.<String, Object>of("district", District.UNIVERSITY.toString()));
        sender.sendMessage("Updated district to University.");
        return true;
      case "residential":
        db.updateChunk(chunk.getX(), chunk.getZ(), ImmutableMap.<String, Object>of("district", District.RESIDENTIAL.toString()));
        sender.sendMessage("Updated district to Residential.");
        return true;
      default:
        sender.sendMessage("ERROR: Could not recognize state. ");
        sender.sendMessage("Possible types: STATE, PRIMARY, INDUSTRIAL, MILITARY, UNIVERSITY, RESIDENTIAL");
        return true;
    }
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

  private List<Player> getPlayer(String name) {
    List<Player> players = new ArrayList<>();
    for (Player p : Bukkit.getServer().getOnlinePlayers()) {
      if (p.getName().equals(name)) {
        players.add(p);
      }
    }
    return players;
  }
}
