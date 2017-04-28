package edu.brown.cs.ykim81.statecraft;

import edu.brown.cs.ykim81.statecraft.database.Database;
import edu.brown.cs.ykim81.statecraft.database.SqliteDb;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by therl on 4/15/2017.
 */
public class Main extends JavaPlugin {

  private Database db;
  private static Economy econ = null;

  @Override
  public void onEnable() {
    //Fired when the server enables the plugin
    try {
      if (!getDataFolder().exists()) {
        getDataFolder().mkdirs();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (!setupEconomy() ) {
      Logger.getLogger("Minecraft").severe("Disabled due to no Vault dependency found!");
      getServer().getPluginManager().disablePlugin(this);
      return;
    }

    this.db = new SqliteDb(this);
    this.db.load();

    getServer().getPluginManager().registerEvents(new TaxListener(db), this);
    getServer().getPluginManager().registerEvents(new StateListener(db, econ), this);
    getServer().getPluginManager().registerEvents(new PermissionsListener(db), this);

    this.getCommand("sc").setExecutor(new CommandCreate(db));
    getLogger().info("StateCraft Enabled.");
  }

  @Override
  public void onDisable() {
    //Fired when the server disables the plugin
    getLogger().info("StateCraft disabled.");
  }

  public Database getPluginDatabase() {
    return db;
  }

  private boolean setupEconomy() {
    if (getServer().getPluginManager().getPlugin("Vault") == null) {
      return false;
    }
    RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
    if (rsp == null) {
      return false;
    }
    econ = rsp.getProvider();
    return econ != null;
  }

}
