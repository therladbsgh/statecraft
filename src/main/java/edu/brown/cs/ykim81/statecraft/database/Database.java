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

  public int countState(Map<String, Object> params) {
    List<String> paramList = new ArrayList<>();
    List<String> keys = new ArrayList<>(params.keySet());
    for (String s : keys) {
      paramList.add(s + "=?");
    }
    String param = StringUtils.join(paramList, " AND ");

    try (Connection conn = getSqlConnection()) {
      try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM states WHERE " + param + ";")) {
        for (int i = 0; i < keys.size(); i++) {
          ps.setObject(i + 1, params.get(keys.get(i)));
        }
        try (ResultSet rs = ps.executeQuery()) {
          if (rs.next()) {
            return rs.getInt(1);
          } else {
            return 0;
          }
        }
      }
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), e);
      return 0;
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

  public int countPlayer(Map<String, Object> params) {
    List<String> paramList = new ArrayList<>();
    List<String> keys = new ArrayList<>(params.keySet());
    for (String s : keys) {
      paramList.add(s + "=?");
    }
    String param = StringUtils.join(paramList, " AND ");

    try (Connection conn = getSqlConnection()) {
      try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM players WHERE " + param + ";")) {
        for (int i = 0; i < keys.size(); i++) {
          ps.setObject(i + 1, params.get(keys.get(i)));
        }
        try (ResultSet rs = ps.executeQuery()) {
          if (rs.next()) {
            return rs.getInt(1);
          } else {
            return 0;
          }
        }
      }
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), e);
      return 0;
    }
  }

  public void createChunk(double x, double z, int state, int city, District district) {
    try (Connection conn = getSqlConnection()) {
      try (PreparedStatement ps = conn.prepareStatement("INSERT INTO chunks(x, z, state, city, district) VALUES(?,?,?,?,?)")) {
        ps.setDouble(1, x);
        ps.setDouble(2, z);
        ps.setInt(3, state);
        ps.setInt(4, city);
        ps.setString(5, district.toString());
        ps.executeUpdate();
      }
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), e);
    }
  }

  public boolean chunkExists(Map<String, Object> params) {
    List<String> paramList = new ArrayList<>();
    List<String> keys = new ArrayList<>(params.keySet());
    for (String s : keys) {
      paramList.add(s + "=?");
    }
    String param = StringUtils.join(paramList, " AND ");

    try (Connection conn = getSqlConnection()) {
      try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM chunks WHERE " + param + " LIMIT 1;")) {
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

  public List<ChunkProxy> getChunk(Map<String, Object> params) {
    if (!chunkExists(params)) {
      return new ArrayList<>();
    } else {
      List<String> paramList = new ArrayList<>();
      List<String> keys = new ArrayList<>(params.keySet());
      for (String s : keys) {
        paramList.add(s + "=?");
      }
      String param = StringUtils.join(paramList, " AND ");

      try (Connection conn = getSqlConnection()) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT id, x, z, state, city, district FROM chunks WHERE " + param + ";")) {
          for (int i = 0; i < keys.size(); i++) {
            ps.setObject(i + 1, params.get(keys.get(i)));
          }
          try (ResultSet rs = ps.executeQuery()) {
            List<ChunkProxy> chunks = new ArrayList<>();
            while (rs.next()) {
              int id = rs.getInt(1);
              double x = rs.getDouble(2);
              double z = rs.getDouble(3);
              int state = rs.getInt(4);
              int city = rs.getInt(5);
              String district = rs.getString(6);
              chunks.add(new ChunkProxy(id, x, z, state, city, District.fromString(district)));
            }
            return chunks;
          }
        }
      } catch (SQLException e) {
        plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), e);
        return new ArrayList<>();
      }
    }
  }

  public void updateChunk(double x, double z, Map<String, Object> params) {
    List<String> paramList = new ArrayList<>();
    List<String> keys = new ArrayList<>(params.keySet());
    for (String s : keys) {
      paramList.add(s + "=?");
    }
    String param = StringUtils.join(paramList, " AND ");

    try (Connection conn = getSqlConnection()) {
      try (PreparedStatement ps = conn.prepareStatement("UPDATE chunks SET " + param + " WHERE x=? AND z=?;")) {
        for (int i = 0; i < keys.size(); i++) {
          ps.setObject(i + 1, params.get(keys.get(i)));
        }
        ps.setDouble(keys.size() + 1, x);
        ps.setDouble(keys.size() + 2, z);
        ps.executeUpdate();
        return;
      }
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), e);
    }
  }

  public void deleteChunk(double x, double z) {
    try (Connection conn = getSqlConnection()) {
      try (PreparedStatement ps = conn.prepareStatement("DELETE FROM chunks WHERE x=? AND z=?;")) {
        ps.setDouble(1, x);
        ps.setDouble(2, z);
        ps.executeUpdate();
        return;
      }
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), e);
    }
  }

  /**
   * Note: This actually gets a rxr square, not an actual circle.
   *
   * @param x
   * @param z
   * @param r
   */
  public List<ChunkProxy> getChunkRadius(double x, double z, int r) {
    try (Connection conn = getSqlConnection()) {
      try (PreparedStatement ps = conn.prepareStatement("SELECT id, x, z, state, city, district FROM chunks WHERE (x BETWEEN ? AND ?) AND (z BETWEEN ? AND ?)")) {
        ps.setDouble(1, x - r);
        ps.setDouble(2, x + r);
        ps.setDouble(3, z - r);
        ps.setDouble(4, z + r);
        try (ResultSet rs = ps.executeQuery()) {
          List<ChunkProxy> chunks = new ArrayList<>();
          while (rs.next()) {
            int id = rs.getInt(1);
            double xR = rs.getDouble(2);
            double zR = rs.getDouble(3);
            int state = rs.getInt(4);
            int city = rs.getInt(5);
            String district = rs.getString(6);
            chunks.add(new ChunkProxy(id, xR, zR, state, city, District.fromString(district)));
          }
          return chunks;
        }
      }
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), e);
      return new ArrayList<>();
    }
  }

  public void createChunkBuild(String userId, int chunkId) {
    try (Connection conn = getSqlConnection()) {
      try (PreparedStatement ps = conn.prepareStatement("INSERT INTO chunkbuilds(userId, chunkId) VALUES(?,?)")) {
        ps.setString(1, userId);
        ps.setInt(2, chunkId);
        ps.executeUpdate();
      }
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), e);
    }
  }

  public boolean chunkBuildExists(Map<String, Object> params) {
    List<String> paramList = new ArrayList<>();
    List<String> keys = new ArrayList<>(params.keySet());
    for (String s : keys) {
      paramList.add(s + "=?");
    }
    String param = StringUtils.join(paramList, " AND ");

    try (Connection conn = getSqlConnection()) {
      try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM chunkbuilds WHERE " + param + " LIMIT 1;")) {
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

  public List<ChunkBuildProxy> getChunkBuild(Map<String, Object> params) {
    if (!chunkBuildExists(params)) {
      return new ArrayList<>();
    } else {
      List<String> paramList = new ArrayList<>();
      List<String> keys = new ArrayList<>(params.keySet());
      for (String s : keys) {
        paramList.add(s + "=?");
      }
      String param = StringUtils.join(paramList, " AND ");

      try (Connection conn = getSqlConnection()) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT userId, chunkId FROM chunkbuilds WHERE " + param + ";")) {
          for (int i = 0; i < keys.size(); i++) {
            ps.setObject(i + 1, params.get(keys.get(i)));
          }
          try (ResultSet rs = ps.executeQuery()) {
            List<ChunkBuildProxy> chunks = new ArrayList<>();
            while (rs.next()) {
              String userId = rs.getString(1);
              int chunkId = rs.getInt(2);
              chunks.add(new ChunkBuildProxy(userId, chunkId));
            }
            return chunks;
          }
        }
      } catch (SQLException e) {
        plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), e);
        return new ArrayList<>();
      }
    }
  }

  public void deleteChunkBuild(String userId, int chunkId) {
    try (Connection conn = getSqlConnection()) {
      try (PreparedStatement ps = conn.prepareStatement("DELETE FROM chunkbuilds WHERE userId=? AND chunkId=?;")) {
        ps.setString(1, userId);
        ps.setInt(2, chunkId);
        ps.executeUpdate();
        return;
      }
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), e);
    }
  }

  public CityProxy createCity(String name, int stateId) {
    try (Connection conn = getSqlConnection()) {
      try (PreparedStatement ps = conn.prepareStatement("INSERT INTO cities(name, state) VALUES(?,?)")) {
        ps.setString(1, name);
        ps.setInt(2, stateId);
        ps.executeUpdate();
      }
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), e);
      return null;
    }
    return getCity(ImmutableMap.<String, Object>of("name", name, "state", stateId)).get(0);
  }

  public boolean cityExists(Map<String, Object> params) {
    List<String> paramList = new ArrayList<>();
    List<String> keys = new ArrayList<>(params.keySet());
    for (String s : keys) {
      paramList.add(s + "=?");
    }
    String param = StringUtils.join(paramList, " AND ");

    try (Connection conn = getSqlConnection()) {
      try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM cities WHERE " + param + " LIMIT 1;")) {
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

  public List<CityProxy> getCity(Map<String, Object> params) {
    List<String> paramList = new ArrayList<>();
    List<String> keys = new ArrayList<>(params.keySet());
    for (String s : keys) {
      paramList.add(s + "=?");
    }
    String param = StringUtils.join(paramList, " AND ");

    try (Connection conn = getSqlConnection()) {
      try (PreparedStatement ps = conn.prepareStatement("SELECT id, name, state FROM cities WHERE " + param + ";")) {
        for (int i = 0; i < keys.size(); i++) {
          ps.setObject(i + 1, params.get(keys.get(i)));
        }
        try (ResultSet rs = ps.executeQuery()) {
          List<CityProxy> cities = new ArrayList<>();
          while (rs.next()) {
            int id = rs.getInt(1);
            String name = rs.getString(2);
            int state = rs.getInt(3);
            cities.add(new CityProxy(id, name, state));
          }
          return cities;
        }
      }
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), e);
      return new ArrayList<>();
    }
  }

  public void updateCity(int id, Map<String, Object> params) {
    List<String> paramList = new ArrayList<>();
    List<String> keys = new ArrayList<>(params.keySet());
    for (String s : keys) {
      paramList.add(s + "=?");
    }
    String param = StringUtils.join(paramList, " AND ");

    try (Connection conn = getSqlConnection()) {
      try (PreparedStatement ps = conn.prepareStatement("UPDATE cities SET " + param + " WHERE id = ?;")) {
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

  public void deleteCity(int id) {
    try (Connection conn = getSqlConnection()) {
      try (PreparedStatement ps = conn.prepareStatement("DELETE FROM cities WHERE id = ?;")) {
        ps.setInt(1, id);
        ps.executeUpdate();
        return;
      }
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), e);
    }
  }



}
