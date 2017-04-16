package edu.brown.cs.ykim81.statecraft;

import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

/**
 * Created by therl on 4/15/2017.
 */
public abstract class Database {

  Main plugin;
  Connection connection;

  public Database(Main instance){
    plugin = instance;
  }

  public abstract Connection getSqlConnection();

  public abstract void load();

  public abstract void initialize();

  public boolean nameOfStateExists(String string) {
    try (Connection conn = getSqlConnection()) {
      try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM state WHERE name=? LIMIT 1;")) {
        ps.setString(1, string);
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

  public void addNewState(String name) {
    try (Connection conn = getSqlConnection()) {
      try (PreparedStatement ps = conn.prepareStatement("INSERT INTO state(name, money, tax) VALUES(?,0,0)")) {
        ps.setString(1, name);
        ps.executeUpdate();
        return;
      }
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), e);
    }
  }

  public int getStateIdFromName(String name) {
    try (Connection conn = getSqlConnection()) {
      try (PreparedStatement ps = conn.prepareStatement("SELECT id FROM state WHERE name=?;")) {
        ps.setString(1, name);
        try (ResultSet rs = ps.executeQuery()) {
          rs.next();
          return rs.getInt("id");
        }
      }
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), e);
      return -1;
    }
  }

  public void addPlayerToState(String id, String state, boolean leader) {
    int stateId = getStateIdFromName(state);
    int leaderInt = 0;
    if (leader) {
      leaderInt = 1;
    }

    try (Connection conn = getSqlConnection()) {
      try (PreparedStatement ps = conn.prepareStatement("INSERT OR REPLACE INTO player(id, state, leader) VALUES(?,?,?)")) {
        ps.setString(1, id);
        ps.setInt(2, stateId);
        ps.setInt(3, leaderInt);
        ps.executeUpdate();
        return;
      }
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), e);
    }
  }

  public boolean playerIsLeader(String id) {
    try (Connection conn = getSqlConnection()) {
      try (PreparedStatement ps = conn.prepareStatement("SELECT leader FROM player WHERE id=? LIMIT 1;")) {
        ps.setString(1, id);
        try (ResultSet rs = ps.executeQuery()) {
          if (rs.next()) {
            if (rs.getInt("leader") == 1) {
              return true;
            } else {
              return false;
            }
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

  public String getStateFromPlayer(String name) {
    try (Connection conn = getSqlConnection()) {
      try (PreparedStatement ps = conn.prepareStatement("SELECT state FROM player WHERE id=?;")) {
        ps.setString(1, name);
        try (ResultSet rs = ps.executeQuery()) {
          rs.next();
          return rs.getString("state");
        }
      }
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), e);
      return "";
    } catch (Exception e) {
      return "";
    }
  }

  public void updateTaxOfState(String stateId, int tax) {
    try (Connection conn = getSqlConnection()) {
      try (PreparedStatement ps = conn.prepareStatement("UPDATE state SET tax = ? WHERE id = ?;")) {
        ps.setInt(1, tax);
        ps.setString(2, stateId);
        ps.executeUpdate();
        return;
      }
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), e);
    }
  }

  public int getTaxOfState(String stateId) {
    try (Connection conn = getSqlConnection()) {
      try (PreparedStatement ps = conn.prepareStatement("SELECT tax FROM state WHERE id=?;")) {
        ps.setString(1, stateId);
        try (ResultSet rs = ps.executeQuery()) {
          rs.next();
          return rs.getInt("tax");
        }
      }
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), e);
      return -1;
    }
  }

  public void updateMoneyOfState(double money, String stateId) {
    try (Connection conn = getSqlConnection()) {
      try (PreparedStatement ps = conn.prepareStatement("UPDATE state SET money = money + ? WHERE id = ?;")) {
        ps.setDouble(1, money);
        ps.setString(2, stateId);
        ps.executeUpdate();
        return;
      }
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), e);
    }
  }

}
