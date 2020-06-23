package net.sunken.master.queue.impl;

import com.google.inject.Inject;
import lombok.extern.java.Log;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.server.module.ServerManager;
import net.sunken.master.instance.InstanceManager;
import net.sunken.master.party.PartyManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

@Log
public class BalancerFactory {

    @Inject
    private PartyManager partyManager;
    @Inject
    private ServerManager serverManager;
    @Inject
    private InstanceManager instanceManager;
    @Inject
    private PacketUtil packetUtil;

    public AbstractBalancer create(Class<? extends AbstractBalancer> clazz) {
        try {
            Constructor<?> constructor = clazz.getConstructor(PartyManager.class, ServerManager.class, InstanceManager.class, PacketUtil.class);
            return (AbstractBalancer) constructor.newInstance(partyManager, serverManager, instanceManager, packetUtil);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            log.log(Level.SEVERE, "Unable to create balancer", e);
        }
        return null;
    }
}
