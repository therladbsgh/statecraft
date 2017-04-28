package edu.brown.cs.ykim81.statecraft;

import com.google.common.collect.ImmutableMap;
import edu.brown.cs.ykim81.statecraft.database.ChunkProxy;
import edu.brown.cs.ykim81.statecraft.database.Database;
import edu.brown.cs.ykim81.statecraft.database.District;
import edu.brown.cs.ykim81.statecraft.database.StateProxy;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scoreboard.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by therl on 4/26/2017.
 */
public class StateListener implements Listener {

  private Database db;
  private Map<UUID, Integer> playerToState;
  private Map<UUID, District> playerToDistrict;
  private Economy economy;

  public StateListener(Database db, Economy economy) {
    this.db = db;
    this.playerToState = new HashMap<>();
    this.playerToDistrict = new HashMap<>();
    this.economy = economy;
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    System.out.println("Joined");
    ChunkProxy chunk = getCurrentChunk(event.getPlayer());
    playerToState.put(event.getPlayer().getUniqueId(), chunk.getId());
    playerToDistrict.put(event.getPlayer().getUniqueId(), chunk.getDistrict());
    setScoreboard(event.getPlayer());
  }

  private ChunkProxy getCurrentChunk(Player p) {
    Chunk chunk = p.getWorld().getChunkAt(p.getLocation());
    if (db.chunkExists(ImmutableMap.<String, Object>of("x", chunk.getX(), "z", chunk.getZ()))) {
      return db.getChunk(ImmutableMap.<String, Object>of("x", chunk.getX(), "z", chunk.getZ())).get(0);
    } else {
      return new ChunkProxy(chunk.getX(), chunk.getZ(), -1, District.NULL);
    }
  }

  @EventHandler
  public void onPlayerMove(PlayerMoveEvent event) {
    ChunkProxy chunk = getCurrentChunk(event.getPlayer());
    int currentState = chunk.getId();
    District currentDistrict = chunk.getDistrict();
    int prevState = playerToState.get(event.getPlayer().getUniqueId()).intValue();
    District prevDistrict = playerToDistrict.get(event.getPlayer().getUniqueId());

    if (currentState != prevState
            || currentDistrict != prevDistrict) {
      playerToState.put(event.getPlayer().getUniqueId(), currentState);
      playerToDistrict.put(event.getPlayer().getUniqueId(), currentDistrict);
      if (currentState == -1) {
        if (currentState != prevState) {
          event.getPlayer().sendMessage("~~~Wilderness~~~");
        }
        updateScoreboardState(event.getPlayer(), "Wilderness", "None");
      } else {
        StateProxy stateProxy = db.readState(ImmutableMap.<String, Object>of("id", currentState)).get(0);
        updateScoreboardState(event.getPlayer(), stateProxy.getName(), chunk.getDistrict().toString());
        if (currentState != prevState) {
          event.getPlayer().sendMessage("[" + stateProxy.getName() + "]");
        }
      }
    }
  }

  private void setScoreboard(Player player) {
    Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
    Objective obj = board.registerNewObjective("StateCraft", "dummy");
    obj.setDisplaySlot(DisplaySlot.SIDEBAR);
    obj.setDisplayName("Statecraft");
    Score onlineName = obj.getScore(ChatColor.GRAY + "» Online");
    onlineName.setScore(15);

    Team onlineCounter = board.registerNewTeam("onlineCounter");
    onlineCounter.addEntry(ChatColor.BLACK + "" + ChatColor.WHITE + "");
    if(Bukkit.getOnlinePlayers().size() == 0){
      onlineCounter.setPrefix(ChatColor.DARK_RED + "" + "0" + ChatColor.RED + "/" + "" + ChatColor.DARK_RED + Bukkit.getServer().getMaxPlayers());
    }else{
      onlineCounter.setPrefix(String.valueOf(ChatColor.DARK_RED + "" + Bukkit.getOnlinePlayers().size() + "" + ChatColor.RED + "/" + "" + ChatColor.DARK_RED + "" + Bukkit.getServer().getMaxPlayers()));
    }
    obj.getScore(ChatColor.BLACK + "" + ChatColor.WHITE + "").setScore(14);

    Score money = obj.getScore(ChatColor.GRAY + "» Money");
    money.setScore(13);

    Team moneyCounter = board.registerNewTeam("moneyCounter");
    moneyCounter.addEntry(ChatColor.RED + "" + ChatColor.WHITE + "");
    moneyCounter.setPrefix(ChatColor.GREEN + "$" + economy.getBalance(player));
    obj.getScore(ChatColor.RED + "" + ChatColor.WHITE + "").setScore(12);

    Score state = obj.getScore(ChatColor.GRAY + "» State");
    state.setScore(11);

    ChunkProxy chunk = getCurrentChunk(player);
    String stateName;
    String districtName;
    if (chunk.getId() == -1) {
      stateName = "Wilderness";
      districtName = "None";
    } else {
      stateName = db.readState(ImmutableMap.<String, Object>of("id", chunk.getId())).get(0).getName();
      districtName = chunk.getDistrict().toString();
    }

    Team stateCounter = board.registerNewTeam("stateCounter");
    stateCounter.addEntry(ChatColor.YELLOW + "" + ChatColor.WHITE + "");
    stateCounter.setPrefix(ChatColor.GREEN + stateName);
    obj.getScore(ChatColor.YELLOW + "" + ChatColor.WHITE + "").setScore(10);

    Score district = obj.getScore(ChatColor.GRAY + "» District");
    district.setScore(9);

    Team districtCounter = board.registerNewTeam("districtCounter");
    districtCounter.addEntry(ChatColor.BLUE + "" + ChatColor.WHITE + "");
    districtCounter.setPrefix(ChatColor.GREEN + districtName);
    obj.getScore(ChatColor.BLUE + "" + ChatColor.WHITE + "").setScore(8);
    player.setScoreboard(board);
  }

  private void updateScoreboardState(Player player, String stateName, String districtName) {
    Scoreboard board = player.getScoreboard();
    board.getTeam("stateCounter").setPrefix(ChatColor.GREEN + stateName);
    board.getTeam("districtCounter").setPrefix(ChatColor.GREEN + districtName);
  }

}
