package net.sunken.bungeecord.chat;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.sunken.bungeecord.Constants;
import net.sunken.bungeecord.chat.config.ChatConfiguration;
import net.sunken.bungeecord.player.BungeePlayer;
import net.sunken.common.config.InjectConfig;
import net.sunken.common.inject.Facet;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.common.player.module.PlayerManager;

import java.util.Optional;

public class ChatHandler implements Facet, Listener {

    @Inject @InjectConfig
    private ChatConfiguration chatConfiguration;
    @Inject
    private PlayerManager playerManager;

    @EventHandler
    public void onChat(ChatEvent event) {
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        String message = event.getMessage();

        //--- Remove duplicating characters
        StringBuilder strippedCharacters = new StringBuilder();

        char lastCharacter = message.charAt(0);
        strippedCharacters.append(lastCharacter);

        for (int i = 1; i < message.length(); i++) {
            char curCharacter = message.charAt(i);

            if (lastCharacter != curCharacter)
                strippedCharacters.append(curCharacter);

            lastCharacter = curCharacter;
        }

        String messageWithNoRecur = strippedCharacters.toString();

        //--- Check for same message as last time
        Optional<AbstractPlayer> abstractPlayerOptional = playerManager.get(player.getUniqueId());
        if (abstractPlayerOptional.isPresent() && !event.isProxyCommand() && !event.isCommand()) {
            BungeePlayer bungeePlayer = (BungeePlayer) abstractPlayerOptional.get();
            event.setCancelled(!bungeePlayer.canSendMessage(messageWithNoRecur, chatConfiguration.getDelayBetweenMessages()));

            if (event.isCancelled()) {
                player.sendMessage(TextComponent.fromLegacyText(Constants.CHAT_CANNOT_SEND_AGAIN));
                return;
            }
        }

        //--- Check blacklisted words config
        String[] wordsWithNoRecur = messageWithNoRecur.toLowerCase().split(" ");
        String[] words = message.split(" ");

        StringBuilder finalMessage = new StringBuilder();
        for (int i = 0; i < wordsWithNoRecur.length; i++) {
            String word = wordsWithNoRecur[i];
            String originalWord = words[i];

            boolean isBlockedWord = false;
            for (String blockedWord : chatConfiguration.getBlacklistedWords()) {
                if (word.indexOf(blockedWord) >= 0)
                    isBlockedWord = true;
            }

            for (String blockedWord : chatConfiguration.getExactBlacklistedWords()) {
                if (word.equalsIgnoreCase(blockedWord))
                    isBlockedWord = true;
            }

            finalMessage.append(finalMessage.length() <= 0 ? "" : " ");
            if (!isBlockedWord) {
                finalMessage.append(originalWord);
            } else {
                finalMessage.append(Strings.repeat("*", originalWord.length()));
            }
        }

        event.setMessage(finalMessage.toString());
    }

}
