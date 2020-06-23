package net.sunken.core.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.java.Log;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.logging.Level;

@Log
@UtilityClass
public class InventoryUtil {

    private static Method writeNbt;
    private static Method readNbt;

    static {
        try {
            writeNbt = NBTCompressedStreamTools.class.getDeclaredMethod("a", NBTBase.class, DataOutput.class);
            writeNbt.setAccessible(true);

            readNbt = NBTCompressedStreamTools.class.getDeclaredMethod("a", DataInput.class, Integer.TYPE, NBTReadLimiter.class);
            readNbt.setAccessible(true);
        } catch (NoSuchMethodException e) {
            log.log(Level.SEVERE, "Unable to find writeNbt or readNbt method", e);
        }
    }

    public static String encode(@NonNull ItemStack[] items) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        NBTTagList nbtTagList = new NBTTagList();

        for (int i = 0; i < items.length; ++i) {
            CraftItemStack craftVersion = getCraftVersion(items[i]);
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            if (craftVersion != null) {
                CraftItemStack.asNMSCopy(craftVersion).save(nbtTagCompound);
            }
            nbtTagList.add(nbtTagCompound);
        }

        try {
            writeNbt.invoke(null, nbtTagList, dataOutputStream);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.log(Level.SEVERE, "Unable to invoke writeNbt", e);
            return null;
        }

        return new String(Base64.getEncoder().encode(byteArrayOutputStream.toByteArray()));
    }

    public static ItemStack[] decode(@NonNull String value) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Base64.getDecoder().decode(value));
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);

        NBTTagList nbtTagList;
        try {
            nbtTagList = (NBTTagList) readNbt.invoke(null, dataInputStream, 0, new NBTReadLimiter(Long.MAX_VALUE));
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.log(Level.SEVERE, "Unable to invoke readNbt", e);
            return null;
        }

        ItemStack[] items = new ItemStack[nbtTagList.size()];
        for (int i = 0; i < nbtTagList.size(); ++i) {
            NBTTagCompound nbtTagCompound = (NBTTagCompound) nbtTagList.get(i);
            if (!nbtTagCompound.isEmpty()) {
                items[i] = CraftItemStack.asCraftMirror(net.minecraft.server.v1_15_R1.ItemStack.a(nbtTagCompound));
            }
        }

        return items;
    }

    private static CraftItemStack getCraftVersion(ItemStack itemStack) {
        if (itemStack instanceof CraftItemStack) return (CraftItemStack) itemStack;
        if (itemStack != null) return CraftItemStack.asCraftCopy(itemStack);
        return null;
    }
}
