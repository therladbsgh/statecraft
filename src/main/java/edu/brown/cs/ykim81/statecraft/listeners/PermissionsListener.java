package edu.brown.cs.ykim81.statecraft.listeners;

import com.google.common.collect.ImmutableMap;
import edu.brown.cs.ykim81.statecraft.cache.PermManager;
import edu.brown.cs.ykim81.statecraft.database.*;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

/**
 * Created by therl on 4/28/2017.
 */
public class PermissionsListener implements Listener {

  private Database db;

  public PermissionsListener(Database db) {
    this.db = db;
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();

    if (!db.searchPlayer(ImmutableMap.<String, Object>of("id", player.getUniqueId().toString()))) {
      PermManager.makeBandit(player);
    } else {
      PlayerProxy pp = db.readPlayer(ImmutableMap.<String, Object>of("id", player.getUniqueId().toString())).get(0);
      if (pp.getLeader() == 1) {
        PermManager.makeLeader(player);
      } else {
        PermManager.makeCitizen(player);

      }
    }
  }

  @EventHandler
  public void onPlayerBreakEvent(BlockBreakEvent event) {
    Player player = event.getPlayer();
    Block block = event.getBlock();
    if (PermManager.isBandit(player)) {
      event.setCancelled(true);
      player.sendMessage("You cannot break blocks as a bandit.");
    }

    if (PermManager.isLeader(player)) {
      Chunk chunk = player.getWorld().getChunkAt(block.getLocation());
      PlayerProxy pp = db.readPlayer(ImmutableMap.<String, Object>of("id", player.getUniqueId().toString())).get(0);
      List<ChunkProxy> chunks = db.getChunk(ImmutableMap.<String, Object>of("x", chunk.getX(), "z", chunk.getZ()));

      if (chunks.size() == 0) {
        event.setCancelled(true);
        player.sendMessage("You cannot break blocks in the wilderness.");
        return;
      }

      if (chunks.get(0).getStateId() != pp.getState()) {
        event.setCancelled(true);
        player.sendMessage("You cannot break blocks of another state.");
        return;
      }
    }

    if (PermManager.isCitizen(player)) {
      Chunk chunk = player.getWorld().getChunkAt(block.getLocation());
      PlayerProxy pp = db.readPlayer(ImmutableMap.<String, Object>of("id", player.getUniqueId().toString())).get(0);
      List<ChunkProxy> chunks = db.getChunk(ImmutableMap.<String, Object>of("x", chunk.getX(), "z", chunk.getZ()));

      if (chunks.size() == 0) {
        event.setCancelled(true);
        player.sendMessage("You cannot break blocks in the wilderness.");
        return;
      }

      if (chunks.get(0).getStateId() != pp.getState()) {
        event.setCancelled(true);
        player.sendMessage("You cannot break blocks of another state.");
        return;
      }

      List<ChunkBuildProxy> cbp = db.getChunkBuild(ImmutableMap.<String, Object>of("userId", player.getUniqueId().toString(),
              "chunkId", chunks.get(0).getChunkId()));
      if (cbp.size() == 0 && chunks.get(0).getDistrict() != District.PRIMARY) {
        event.setCancelled(true);
        player.sendMessage("You cannot break blocks in this district.");
      }
    }
  }

  @EventHandler
  public void onPlayerPlaceEvent(BlockPlaceEvent event) {
    Player player = event.getPlayer();
    if (PermManager.isBandit(player)) {
      event.setCancelled(true);
      player.sendMessage("You cannot place blocks as a bandit.");
    }
  }

}
