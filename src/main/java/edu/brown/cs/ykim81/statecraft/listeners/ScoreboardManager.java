package edu.brown.cs.ykim81.statecraft.listeners;

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
import org.bukkit.scoreboard.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by therl on 5/21/2017.
 */
public class ScoreboardManager {

  private static Map<UUID, Boolean> miniMapMode = new HashMap<>();

  public static void updateMiniMapMode(UUID uuid, boolean bool) {
    miniMapMode.put(uuid, bool);
  }

  public static boolean isMiniMapMode(UUID uuid) {
    return miniMapMode.get(uuid);
  }

  public static void setMainScoreboard(Player player, Database db, Economy economy) {
    Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
    Objective obj = board.registerNewObjective("StateCraft", "dummy");
    obj.setDisplaySlot(DisplaySlot.SIDEBAR);
    obj.setDisplayName("Statecraft");

    Score money = obj.getScore(ChatColor.GRAY + "» Money");
    money.setScore(15);

    Team moneyCounter = board.registerNewTeam("moneyCounter");
    moneyCounter.addEntry(ChatColor.RED + "" + ChatColor.WHITE + "");
    moneyCounter.setPrefix(ChatColor.GREEN + "$" + economy.getBalance(player));
    obj.getScore(ChatColor.RED + "" + ChatColor.WHITE + "").setScore(14);

    Score state = obj.getScore(ChatColor.GRAY + "» State");
    state.setScore(13);

    ChunkProxy chunk = getCurrentChunk(player, db);
    String stateName;
    String cityName;
    String districtName;
    if (chunk.getStateId() == -1) {
      stateName = "Wilderness";
      cityName = "--";
      districtName = "--";
    } else {
      stateName = db.readState(ImmutableMap.<String, Object>of("id", chunk.getStateId())).get(0).getName();
      cityName = db.getCity(ImmutableMap.<String, Object>of("id", chunk.getCityId())).get(0).getName();
      districtName = chunk.getDistrict().toString();
    }

    Team stateCounter = board.registerNewTeam("stateCounter");
    stateCounter.addEntry(ChatColor.YELLOW + "" + ChatColor.WHITE + "");
    stateCounter.setPrefix(ChatColor.GREEN + stateName);
    obj.getScore(ChatColor.YELLOW + "" + ChatColor.WHITE + "").setScore(12);

    Score city = obj.getScore(ChatColor.GRAY + "» City");
    city.setScore(11);

    Team cityCounter = board.registerNewTeam("cityCounter");
    cityCounter.addEntry(ChatColor.BLACK + "" + ChatColor.WHITE  + "");
    cityCounter.setPrefix(ChatColor.GREEN + cityName);
    obj.getScore(ChatColor.BLACK + "" + ChatColor.WHITE + "").setScore(10);

    Score district = obj.getScore(ChatColor.GRAY + "» District");
    district.setScore(9);

    Team districtCounter = board.registerNewTeam("districtCounter");
    districtCounter.addEntry(ChatColor.BLUE + "" + ChatColor.WHITE + "");
    districtCounter.setPrefix(ChatColor.GREEN + districtName);
    obj.getScore(ChatColor.BLUE + "" + ChatColor.WHITE + "").setScore(8);
    player.setScoreboard(board);
  }

  public static void updateMainScoreboard(Player player, String stateName, String cityName, String districtName) {
    Scoreboard board = player.getScoreboard();
    board.getTeam("stateCounter").setPrefix(ChatColor.GREEN + stateName);
    board.getTeam("cityCounter").setPrefix(ChatColor.GREEN + cityName);
    board.getTeam("districtCounter").setPrefix(ChatColor.GREEN + districtName);
  }

  private static ChunkProxy getCurrentChunk(Player p, Database db) {
    Chunk chunk = p.getWorld().getChunkAt(p.getLocation());
    if (db.chunkExists(ImmutableMap.<String, Object>of("x", chunk.getX(), "z", chunk.getZ()))) {
      return db.getChunk(ImmutableMap.<String, Object>of("x", chunk.getX(), "z", chunk.getZ())).get(0);
    } else {
      return new ChunkProxy(-1, chunk.getX(), chunk.getZ(), -1, -1, District.NULL);
    }
  }

  public static void setMiniMapScoreboard(Player player, Database db) {
    Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
    Objective obj = board.registerNewObjective("StateCraft", "dummy");
    obj.setDisplaySlot(DisplaySlot.SIDEBAR);
    obj.setDisplayName("Statecraft");

    ChunkProxy chunk = getCurrentChunk(player, db);
    List<ChunkProxy> chunks = db.getChunkRadius(chunk.getX(), chunk.getZ(), 5);
    String[][] chunkStr = new String[11][11];
    for (int i = 0; i < 11; i++) {
      for (int j = 0; j < 11; j++) {
        chunkStr[i][j] = "-";
      }
    }
    for (ChunkProxy cp : chunks) {
      int x = (int) (cp.getX() - chunk.getX() + 5);
      int z = (int) (cp.getZ() - chunk.getZ() + 5);
      chunkStr[z][x] = db.readState(ImmutableMap.<String, Object>of("id", cp.getStateId())).get(0).getName().substring(0, 1);
    }
    chunkStr[5][5] = "@";

    for (int i = 0; i < 11; i++) {
      String row = "";
      for (int j = 0; j < 11; j++) {
        row = row + chunkStr[i][j];
      }
      Team counter = board.registerNewTeam("" + i);
      counter.addEntry(getChatColor(i) + "" + ChatColor.WHITE + "");
      counter.setPrefix(row);
      obj.getScore(getChatColor(i) + "" + ChatColor.WHITE + "").setScore(15 - i);
    }
    player.setScoreboard(board);
  }

  public static void updateMiniMapScoreboard(Player player, Database db) {
    Scoreboard board = player.getScoreboard();
    ChunkProxy chunk = getCurrentChunk(player, db);
    List<ChunkProxy> chunks = db.getChunkRadius(chunk.getX(), chunk.getZ(), 5);
    String[][] chunkStr = new String[11][11];
    for (int i = 0; i < 11; i++) {
      for (int j = 0; j < 11; j++) {
        chunkStr[i][j] = "-";
      }
    }
    for (ChunkProxy cp : chunks) {
      int x = (int) (cp.getX() - chunk.getX() + 5);
      int z = (int) (cp.getZ() - chunk.getZ() + 5);
      chunkStr[z][x] = db.readState(ImmutableMap.<String, Object>of("id", cp.getStateId())).get(0).getName().substring(0, 1);
    }
    chunkStr[5][5] = "@";

    for (int i = 0; i < 11; i++) {
      String row = "";
      for (int j = 0; j < 11; j++) {
        row = row + chunkStr[i][j];
      }
      board.getTeam("" + i).setPrefix(row);
    }
  }

  private static ChatColor getChatColor(int i) {
    switch (i) {
      case 0:
        return ChatColor.BLACK;
      case 1:
        return ChatColor.DARK_BLUE;
      case 2:
        return ChatColor.DARK_GREEN;
      case 3:
        return ChatColor.DARK_AQUA;
      case 4:
        return ChatColor.DARK_RED;
      case 5:
        return ChatColor.DARK_PURPLE;
      case 6:
        return ChatColor.GOLD;
      case 7:
        return ChatColor.GRAY;
      case 8:
        return ChatColor.DARK_GRAY;
      case 9:
        return ChatColor.BLUE;
      case 10:
        return ChatColor.GREEN;
      case 11:
        return ChatColor.AQUA;
      default:
        return ChatColor.WHITE;
    }
  }

}
