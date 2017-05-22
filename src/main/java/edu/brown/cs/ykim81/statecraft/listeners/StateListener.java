package edu.brown.cs.ykim81.statecraft.listeners;

import com.google.common.collect.ImmutableMap;
import edu.brown.cs.ykim81.statecraft.database.*;
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
  private Economy economy;
  private ScoreboardManager scoreboardManager;
  private Map<UUID, ChunkProxy> playerToChunk;
  private Map<UUID, Integer> playerToState;
  private Map<UUID, Integer> playerToCity;
  private Map<UUID, District> playerToDistrict;

  public StateListener(Database db, Economy economy) {
    this.db = db;
    this.economy = economy;
    this.playerToChunk = new HashMap<>();
    this.playerToState = new HashMap<>();
    this.playerToCity = new HashMap<>();
    this.playerToDistrict = new HashMap<>();
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    System.out.println("Joined");
    ChunkProxy chunk = getCurrentChunk(event.getPlayer());
    playerToChunk.put(event.getPlayer().getUniqueId(), chunk);
    playerToState.put(event.getPlayer().getUniqueId(), chunk.getStateId());
    playerToCity.put(event.getPlayer().getUniqueId(), chunk.getCityId());
    playerToDistrict.put(event.getPlayer().getUniqueId(), chunk.getDistrict());
    ScoreboardManager.updateMiniMapMode(event.getPlayer().getUniqueId(), false);
    ScoreboardManager.setMainScoreboard(event.getPlayer(), db, economy);
  }

  private ChunkProxy getCurrentChunk(Player p) {
    Chunk chunk = p.getWorld().getChunkAt(p.getLocation());
    if (db.chunkExists(ImmutableMap.<String, Object>of("x", chunk.getX(), "z", chunk.getZ()))) {
      return db.getChunk(ImmutableMap.<String, Object>of("x", chunk.getX(), "z", chunk.getZ())).get(0);
    } else {
      return new ChunkProxy(-1, chunk.getX(), chunk.getZ(), -1, -1, District.NULL);
    }
  }

  @EventHandler
  public void onPlayerMove(PlayerMoveEvent event) {
    ChunkProxy chunk = getCurrentChunk(event.getPlayer());
    if (ScoreboardManager.isMiniMapMode(event.getPlayer().getUniqueId())) {
      ChunkProxy prevChunk = playerToChunk.get(event.getPlayer().getUniqueId());

      if (!chunk.equals(prevChunk)) {
        playerToChunk.put(event.getPlayer().getUniqueId(), chunk);
        ScoreboardManager.updateMiniMapScoreboard(event.getPlayer(), db);
      }
    }

      int currentState = chunk.getStateId();
      int currentCity = chunk.getCityId();
      District currentDistrict = chunk.getDistrict();
      int prevState = playerToState.get(event.getPlayer().getUniqueId()).intValue();
      int prevCity = playerToCity.get(event.getPlayer().getUniqueId()).intValue();
      District prevDistrict = playerToDistrict.get(event.getPlayer().getUniqueId());

      if (currentState != prevState
              || currentDistrict != prevDistrict
              || currentCity != prevCity) {
        playerToState.put(event.getPlayer().getUniqueId(), currentState);
        playerToCity.put(event.getPlayer().getUniqueId(), currentCity);
        playerToDistrict.put(event.getPlayer().getUniqueId(), currentDistrict);
        if (currentState == -1) {
          if (currentState != prevState) {
            event.getPlayer().sendMessage("~~~Wilderness~~~");
          }
          if (!ScoreboardManager.isMiniMapMode(event.getPlayer().getUniqueId())) {
            ScoreboardManager.updateMainScoreboard(event.getPlayer(), "Wilderness", "--", "--");
          }
        } else {
            StateProxy stateProxy = db.readState(ImmutableMap.<String, Object>of("id", currentState)).get(0);
          if (!ScoreboardManager.isMiniMapMode(event.getPlayer().getUniqueId())) {
            CityProxy cityProxy = db.getCity(ImmutableMap.<String, Object>of("id", chunk.getCityId())).get(0);
            ScoreboardManager.updateMainScoreboard(event.getPlayer(), stateProxy.getName(), cityProxy.getName(), chunk.getDistrict().toString());
          }
          if (currentState != prevState) {
            event.getPlayer().sendMessage("[" + stateProxy.getName() + "]");
          }
        }

    }
  }

}
