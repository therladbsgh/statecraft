package edu.brown.cs.ykim81.statecraft;

import com.google.common.collect.ImmutableMap;
import edu.brown.cs.ykim81.statecraft.database.Database;
import edu.brown.cs.ykim81.statecraft.database.PlayerProxy;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by therl on 4/28/2017.
 */
public class PermissionsListener implements Listener {

  private Database db;
  private static Map<UUID, PermissionAttachment> perms;

  public PermissionsListener(Database db) {
    this.db = db;
    this.perms = new HashMap<>();
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    PermissionAttachment attachment = player.addAttachment(JavaPlugin.getPlugin(Main.class));
    perms.put(player.getUniqueId(), attachment);

    if (!db.searchPlayer(ImmutableMap.<String, Object>of("id", player.getUniqueId().toString()))) {
      attachment.setPermission("sc.player.bandit", true);
      attachment.unsetPermission("sc.player.citizen");
      attachment.unsetPermission("sc.player.leader");
    } else {
      PlayerProxy pp = db.readPlayer(ImmutableMap.<String, Object>of("id", player.getUniqueId().toString())).get(0);
      if (pp.getLeader() == 1) {
        attachment.unsetPermission("sc.player.bandit");
        attachment.unsetPermission("sc.player.citizen");
        attachment.setPermission("sc.player.leader", true);
      } else {
        attachment.unsetPermission("sc.player.bandit");
        attachment.setPermission("sc.player.citizen", true);
        attachment.unsetPermission("sc.player.leader");

      }
    }
  }

  public static void makeBandit(Player p) {
    PermissionAttachment pa = perms.get(p.getUniqueId());
    pa.setPermission("sc.player.bandit", true);
    pa.unsetPermission("sc.player.citizen");
    pa.unsetPermission("sc.player.leader");
  }

  public static void makeCitizen(Player p) {
    PermissionAttachment pa = perms.get(p.getUniqueId());
    pa.unsetPermission("sc.player.bandit");
    pa.setPermission("sc.player.citizen", true);
    pa.unsetPermission("sc.player.leader");
  }

  public static void makeLeader(Player p) {
    PermissionAttachment pa = perms.get(p.getUniqueId());
    pa.unsetPermission("sc.player.bandit");
    pa.unsetPermission("sc.player.citizen");
    pa.setPermission("sc.player.leader", true);
  }

  public static PermissionAttachment getPlayerPermission(Player p) {
    return perms.get(p.getUniqueId());
  }

}
