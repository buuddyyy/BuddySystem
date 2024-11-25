package de.buuddyyy.buddysystem.events;

import com.google.common.collect.Maps;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import java.util.Map;

public class FoodLevelChangeListener implements Listener {

    private static final int LOSE_HUNGER = 5;
    private final Map<Player, Integer> playerHungerMap;

    public FoodLevelChangeListener() {
        this.playerHungerMap = Maps.newHashMap();
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player p))
            return;
        if (event.getItem() == null) {
            int counter = this.playerHungerMap.getOrDefault(p, 0);
            if (counter <= LOSE_HUNGER) {
                event.setCancelled(true);
                counter++;
                this.playerHungerMap.put(p, counter);
                return;
            }
        }
        this.playerHungerMap.put(p, 0);
     }

}
