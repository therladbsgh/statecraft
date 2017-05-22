package edu.brown.cs.ykim81.statecraft;

import edu.brown.cs.ykim81.statecraft.commands.CommandCreate;
import edu.brown.cs.ykim81.statecraft.database.Database;
import edu.brown.cs.ykim81.statecraft.database.SqliteDb;
import edu.brown.cs.ykim81.statecraft.listeners.PermissionsListener;
import edu.brown.cs.ykim81.statecraft.listeners.StateListener;
import edu.brown.cs.ykim81.statecraft.listeners.TaxListener;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
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

    File file = new File(getDataFolder(), "CityCenter.schematic");
    if (!file.exists()) {
      saveResource("CityCenter.schematic", false);
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

    this.getCommand("sc").setExecutor(new CommandCreate(db, econ));
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
