package edu.brown.cs.ykim81.statecraft.listeners;

import com.google.common.collect.ImmutableMap;
import edu.brown.cs.ykim81.statecraft.database.ChunkProxy;
import edu.brown.cs.ykim81.statecraft.database.Database;
import edu.brown.cs.ykim81.statecraft.database.StateProxy;
import net.ess3.api.events.UserBalanceUpdateEvent;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by therl on 4/16/2017.
 */
public class TaxListener implements Listener {

  private Database db;

  public TaxListener(Database db) {
    this.db = db;
  }

  @EventHandler
  public void onPlayerEarnMoney(UserBalanceUpdateEvent event) {
    double profit = event.getNewBalance().subtract(event.getOldBalance()).doubleValue();
    if (profit > 0) {
      int stateId = db.readPlayer(ImmutableMap.<String, Object>of("id", event.getPlayer().getUniqueId().toString())).get(0).getState();
      StateProxy state = db.readState(ImmutableMap.<String, Object>of("id", stateId)).get(0);
      if (state.getName().length() > 0) {
        int tax = state.getTax();
        if (tax > 0) {
          double moneyToTake = (profit * tax) /100.0;
          event.setNewBalance(event.getNewBalance().subtract(BigDecimal.valueOf(moneyToTake)));
          double stateBalance = state.getMoney() + moneyToTake;
          db.updateState(stateId, ImmutableMap.<String, Object>of("money", stateBalance));
          event.getPlayer().sendMessage("$" + moneyToTake + " has been taken as tax.");
        }
      }
    }
  }

}
