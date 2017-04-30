package edu.brown.cs.ykim81.statecraft.cache;

import edu.brown.cs.ykim81.statecraft.commands.Role;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by therl on 4/28/2017.
 */
public class PermManager {

  private static Map<UUID, Role> roles = new HashMap<>();

  public static Role get(UUID id) {
    return roles.get(id);
  }

  public static void put(UUID id, Role pa) {
    roles.put(id, pa);
  }

  public static void makeBandit(Player p) {
    roles.put(p.getUniqueId(), Role.BANDIT);
  }

  public static void makeCitizen(Player p) {
    roles.put(p.getUniqueId(), Role.CITIZEN);
  }

  public static void makeLeader(Player p) {
    roles.put(p.getUniqueId(), Role.LEADER);
  }

  public static boolean isBandit(Player p) {
    return roles.get(p.getUniqueId()).equals(Role.BANDIT);
  }

  public static boolean isCitizen(Player p) {
    return roles.get(p.getUniqueId()).equals(Role.CITIZEN);
  }

  public static boolean isLeader(Player p) {
    return roles.get(p.getUniqueId()).equals(Role.LEADER);
  }

}
