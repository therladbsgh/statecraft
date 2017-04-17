package edu.brown.cs.ykim81.statecraft.database;

import com.google.common.collect.ImmutableMap;
import edu.brown.cs.ykim81.statecraft.Main;
import org.apache.commons.lang.StringUtils;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;

/**
 * Created by therl on 4/15/2017.
 */
public abstract class Database {

  Main plugin;
  Connection connection;

  Map<String, StateProxy> stateCache;
  Map<String, PlayerProxy> playerCache;

  public Database(Main instance){
    plugin = instance;
    this.stateCache = new HashMap<>();
    this.playerCache = new HashMap<>();
  }

  public abstract Connection getSqlConnection();

  public abstract void load();

  public abstract void initialize();

  public StateProxy createState(String name) {
    return createState(name, 0, 0);
  }

  public StateProxy createState(String name, double money, int tax) {
    try (Connection conn = getSqlConnection()) {
      try (PreparedStatement ps = conn.prepareStatement("INSERT INTO states(name, money, tax) VALUES(?,?,?)")) {
        ps.setString(1, name);
        ps.setDouble(2, money);
        ps.setInt(3, tax);
        ps.executeUpdate();
      }
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), e);
      return null;
    }
    return readState(ImmutableMap.<String, Object>of("name", name, "money", money, "tax", tax)).get(0);
  }

  public boolean searchState(Map<String, Object> params) {
    List<String> paramList = new ArrayList<>();
    List<String> keys = new ArrayList<>(params.keySet());
    for (String s : keys) {
      paramList.add(s + "=?");
    }
    String param = StringUtils.join(paramList, " AND ");

    try (Connection conn = getSqlConnection()) {
      try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM states WHERE " + param + " LIMIT 1;")) {
        for (int i = 0; i < keys.size(); i++) {
          ps.setObject(i + 1, params.get(keys.get(i)));
        }
        try (ResultSet rs = ps.executeQuery()) {
          if (rs.next()) {
            return true;
          } else {
            return false;
          }
        }
      }
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), e);
      return false;
    }
  }

  public List<StateProxy> readState(Map<String, Object> params) {
    List<String> paramList = new ArrayList<>();
    List<String> keys = new ArrayList<>(params.keySet());
    for (String s : keys) {
      paramList.add(s + "=?");
    }
    String param = StringUtils.join(paramList, " AND ");

    try (Connection conn = getSqlConnection()) {
      try (PreparedStatement ps = conn.prepareStatement("SELECT id, name, money, tax FROM states WHERE " + param + ";")) {
        for (int i = 0; i < keys.size(); i++) {
          ps.setObject(i + 1, params.get(keys.get(i)));
        }
        try (ResultSet rs = ps.executeQuery()) {
          List<StateProxy> states = new ArrayList<>();
          while (rs.next()) {
            int id = rs.getInt(1);
            String name = rs.getString(2);
            double money = rs.getDouble(3);
            int tax = rs.getInt(4);
            states.add(new StateProxy(id, name, money, tax));
          }
          return states;
        }
      }
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), e);
      return new ArrayList<>();
    }
  }

  public void updateState(int id, Map<String, Object> params) {
    List<String> paramList = new ArrayList<>();
    List<String> keys = new ArrayList<>(params.keySet());
    for (String s : keys) {
      paramList.add(s + "=?");
    }
    String param = StringUtils.join(paramList, " AND ");

    try (Connection conn = getSqlConnection()) {
      try (PreparedStatement ps = conn.prepareStatement("UPDATE states SET " + param + " WHERE id = ?;")) {
        for (int i = 0; i < keys.size(); i++) {
          ps.setObject(i + 1, params.get(keys.get(i)));
        }
        ps.setInt(keys.size() + 1, id);
        ps.executeUpdate();
        return;
      }
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), e);
    }
  }

  public void deleteState(int id) {
    try (Connection conn = getSqlConnection()) {
      try (PreparedStatement ps = conn.prepareStatement("DELETE FROM states WHERE id = ?;")) {
        ps.setInt(1, id);
        ps.executeUpdate();
        return;
      }
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), e);
    }
  }

  public PlayerProxy createPlayer(String id, int state, int leader) {
    try (Connection conn = getSqlConnection()) {
      try (PreparedStatement ps = conn.prepareStatement("INSERT INTO players(id, state, leader) VALUES(?,?,?)")) {
        ps.setString(1, id);
        ps.setInt(2, state);
        ps.setInt(3, leader);
        ps.executeUpdate();
      }
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), e);
      return null;
    }
    return readPlayer(ImmutableMap.<String, Object>of("id", id, "state", state, "leader", leader)).get(0);
  }

  public boolean searchPlayer(Map<String, Object> params) {
    List<String> paramList = new ArrayList<>();
    List<String> keys = new ArrayList<>(params.keySet());
    for (String s : keys) {
      paramList.add(s + "=?");
    }
    String param = StringUtils.join(paramList, " AND ");

    try (Connection conn = getSqlConnection()) {
      try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM players WHERE " + param + " LIMIT 1;")) {
        for (int i = 0; i < keys.size(); i++) {
          ps.setObject(i + 1, params.get(keys.get(i)));
        }
        try (ResultSet rs = ps.executeQuery()) {
          if (rs.next()) {
            return true;
          } else {
            return false;
          }
        }
      }
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), e);
      return false;
    }
  }

  public List<PlayerProxy> readPlayer(Map<String, Object> params) {
    List<String> paramList = new ArrayList<>();
    List<String> keys = new ArrayList<>(params.keySet());
    for (String s : keys) {
      paramList.add(s + "=?");
    }
    String param = StringUtils.join(paramList, " AND ");

    try (Connection conn = getSqlConnection()) {
      try (PreparedStatement ps = conn.prepareStatement("SELECT id, state, leader FROM players WHERE " + param + ";")) {
        for (int i = 0; i < keys.size(); i++) {
          ps.setObject(i + 1, params.get(keys.get(i)));
        }
        try (ResultSet rs = ps.executeQuery()) {
          List<PlayerProxy> players = new ArrayList<>();
          while (rs.next()) {
            String id = rs.getString(1);
            int state = rs.getInt(2);
            int leader = rs.getInt(3);
            players.add(new PlayerProxy(id, state, leader));
          }
          return players;
        }
      }
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), e);
      return new ArrayList<>();
    }
  }

  public void updatePlayer(String id, Map<String, Object> params) {
    List<String> paramList = new ArrayList<>();
    List<String> keys = new ArrayList<>(params.keySet());
    for (String s : keys) {
      paramList.add(s + "=?");
    }
    String param = StringUtils.join(paramList, " AND ");

    try (Connection conn = getSqlConnection()) {
      try (PreparedStatement ps = conn.prepareStatement("UPDATE players SET " + param + " WHERE id = ?;")) {
        for (int i = 0; i < keys.size(); i++) {
          ps.setObject(i + 1, params.get(keys.get(i)));
        }
        ps.setString(keys.size() + 1, id);
        ps.executeUpdate();
        return;
      }
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), e);
    }
  }

  public void deletePlayer(String id) {
    try (Connection conn = getSqlConnection()) {
      try (PreparedStatement ps = conn.prepareStatement("DELETE FROM players WHERE id = ?;")) {
        ps.setString(1, id);
        ps.executeUpdate();
        return;
      }
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), e);
    }
  }

}
