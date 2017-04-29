package edu.brown.cs.ykim81.statecraft.listeners;

import com.google.common.collect.ImmutableMap;
import edu.brown.cs.ykim81.statecraft.cache.PermManager;
import edu.brown.cs.ykim81.statecraft.database.Database;
import edu.brown.cs.ykim81.statecraft.database.PlayerProxy;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;

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
    if (PermManager.isBandit(player)) {
      event.setCancelled(true);
      player.sendMessage("You cannot break blocks as a bandit.");
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
