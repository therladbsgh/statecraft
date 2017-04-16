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
  // The name of the table we created back in SQLite class.
  public String table = "state";
  public int tokens = 0;
  public Database(Main instance){
    plugin = instance;
  }

  public abstract Connection getSqlConnection();

  public abstract void load();

  public void initialize() {
    connection = getSqlConnection();
    try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + table  + " LIMIT 1;")) {
      try (ResultSet rs = ps.executeQuery()) {

      }
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection", e);
    }
  }

  // These are the methods you can use to get things out of your database. You of course can make new ones to return different things in the database.
  // This returns the number of people the player killed.
  public Integer getTokens(String string) {
    try (Connection conn = getSqlConnection()) {
      try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE player = '"+string+"';")) {
        try (ResultSet rs = ps.executeQuery()) {
          while(rs.next()){
            if(rs.getString("player").equalsIgnoreCase(string.toLowerCase())){
              return rs.getInt("kills");
            }
          }
        }
      }
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), e);
    }
    return 0;
  }

  public Integer getTotal(String string) {
    try (Connection conn = getSqlConnection()) {
      try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE player = '"+string+"';")) {
        try (ResultSet rs = ps.executeQuery()) {
          while(rs.next()){
            if(rs.getString("player").equalsIgnoreCase(string.toLowerCase())){
              return rs.getInt("total");
            }
          }
        }
      }
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), e);
    }
    return 0;
  }

  public void setTokens(Player player, Integer tokens, Integer total) {
    try (Connection conn = getSqlConnection()) {
      try (PreparedStatement ps = conn.prepareStatement("REPLACE INTO " + table + " (player,kills,total) VALUES(?,?,?)")) {
        ps.setString(1, player.getName().toLowerCase());
        ps.setInt(2, tokens);
        ps.setInt(3, total);
        ps.executeUpdate();
        return;
      }
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), e);
    }
  }

}
