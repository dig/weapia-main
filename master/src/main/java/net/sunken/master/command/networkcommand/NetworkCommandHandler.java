package net.sunken.master.command.networkcommand;

import lombok.extern.java.*;
import net.sunken.common.command.*;
import net.sunken.common.command.impl.*;
import net.sunken.common.inject.*;
import net.sunken.common.networkcommand.*;
import net.sunken.common.packet.*;
import net.sunken.common.player.*;
import net.sunken.common.player.packet.*;
import net.sunken.common.util.cooldown.*;
import net.sunken.master.command.*;

import javax.inject.*;
import java.lang.reflect.*;
import java.util.*;

@Log
public class NetworkCommandHandler extends PacketHandler<NetworkCommandPacket> implements Facet, Enableable {

    @Inject
    private BaseCommandRegistry baseCommandRegistry;
    @Inject
    private Cooldowns cooldowns;
    @Inject
    private PacketUtil packetUtil;
    @Inject
    private PacketHandlerRegistry packetHandlerRegistry;

    @Override
    public void onReceive(NetworkCommandPacket packet) {
        String name = packet.getCommandName();
        String[] args = packet.getArgs();
        Optional<BaseCommand> baseCommandOptional = baseCommandRegistry.findCommandByName(name);

        boolean success = false;
        if (baseCommandOptional.isPresent()) {
            BaseCommand baseCommand = baseCommandOptional.get();

            Class<? extends BaseCommand> clazz = baseCommand.getClass();
            Command commandAnnotation = clazz.getAnnotation(Command.class);

            UUID uuid = packet.getSender().getUuid();
            
            String cooldownKey = "cmd:" + name;
            if (!cooldowns.canProceed(cooldownKey, uuid)) {
                packetUtil.send(new PlayerProxyMessagePacket(uuid, "&c" + commandAnnotation.errorCooldown()));
                return;
            }

            Rank rank = packet.getSender().getRank();
            CommandResponse commandResponse = baseCommandRegistry.matchCommandRequirements(commandAnnotation, rank, args);
            Optional<Method> subCommandMethodOptional = (args.length > 0 ? baseCommandRegistry.findSubCommandInCommand(clazz, rank, args) : Optional.empty());

            if (subCommandMethodOptional.isPresent()) {
                Method subCommandMethod = subCommandMethodOptional.get();

                try {
                    if (baseCommand instanceof BasicCommand) {
                        BasicCommand basicCommand = (BasicCommand) baseCommand;
                        Object[] params = {Arrays.copyOfRange(args, 1, args.length)};

                        success = (Boolean) subCommandMethod.invoke(basicCommand, params);
                    } else if (baseCommand instanceof MasterCommand) {
                        MasterCommand masterCommand = (MasterCommand) baseCommand;

                        success = (Boolean) subCommandMethod.invoke(masterCommand, packet.getSender(), Arrays.copyOfRange(args, 1, args.length));
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    log.severe(String.format("Command parameters wrong for %s %s.", name, subCommandMethod.getName()));
                    e.printStackTrace();
                }
            } else {
                switch (commandResponse) {
                    case INVALID_RANK:
                        packetUtil.send(new PlayerProxyMessagePacket(uuid, "&c" + commandAnnotation.errorPermission()));
                        break;
                    case INVALID_ARGS:
                        packetUtil.send(new PlayerProxyMessagePacket(uuid, "&cUsage: " + commandAnnotation.usage()));
                        break;
                    case SUCCESS:
                        if (baseCommand instanceof BasicCommand) {
                            success = ((BasicCommand) baseCommand).onCommand(args);
                        } else if (baseCommand instanceof MasterCommand) {
                            success = ((MasterCommand) baseCommand).onCommand(packet.getSender(), args);
                        } else {
                            log.severe(String.format("Command not handled for %s.", name));
                        }

                        break;
                }
            }

            if (success) {
                cooldowns.create("cmd:" + name, uuid, System.currentTimeMillis() + commandAnnotation.cooldown());
            }
        }
    }

    @Override
    public void enable() {
        packetHandlerRegistry.registerHandler(NetworkCommandPacket.class, this);
    }
}
