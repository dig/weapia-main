package net.sunken.core.engine.state;

import net.sunken.core.engine.state.impl.BasePlayerState;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class PlayerSpectatorState extends BasePlayerState {

    public PlayerSpectatorState(Player player) {
        super(player);
    }

    @Override
    public void start(BasePlayerState previous) {
        player.setGameMode(GameMode.SPECTATOR);
        for (PotionEffect effect : player.getActivePotionEffects())
            player.removePotionEffect(effect.getType());
    }

    @Override
    public void stop(BasePlayerState next) {
    }

}
