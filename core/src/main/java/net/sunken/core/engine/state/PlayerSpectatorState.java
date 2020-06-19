package net.sunken.core.engine.state;

import net.sunken.common.player.AbstractPlayer;
import net.sunken.core.engine.state.impl.BasePlayerState;
import net.sunken.core.player.CorePlayer;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.Optional;

public class PlayerSpectatorState extends BasePlayerState {

    @Override
    public void start(AbstractPlayer abstractPlayer, BasePlayerState previous) {
        CorePlayer corePlayer = (CorePlayer) abstractPlayer;
        Optional<? extends Player> playerOptional = corePlayer.toPlayer();

        if (playerOptional.isPresent()) {
            Player player = playerOptional.get();

            player.setGameMode(GameMode.SPECTATOR);
            for (PotionEffect effect : player.getActivePotionEffects())
                player.removePotionEffect(effect.getType());
        }
    }

    @Override
    public void stop(AbstractPlayer abstractPlayer, BasePlayerState next) {
    }

}
