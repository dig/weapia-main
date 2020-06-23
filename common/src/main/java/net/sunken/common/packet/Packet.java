package net.sunken.common.packet;

import lombok.extern.java.Log;

import javax.annotation.Nullable;
import java.io.*;
import java.util.logging.Level;

@Log
public abstract class Packet implements Serializable {

    @Nullable
    public byte[] toBytes() {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutput out = new ObjectOutputStream(bos)) {

            out.writeObject(this);
            return bos.toByteArray();
        } catch (IOException e) {
            log.log(Level.SEVERE, "Unable to serialize packet", e);
        }

        return null;
    }

    @Nullable
    public static Packet fromBytes(byte[] bytes) {
        try {
            try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                 ObjectInput in = new ObjectInputStream(bis)) {

                Object object = in.readObject();
                if (object instanceof Packet) {
                    return (Packet) object;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            log.log(Level.SEVERE, "Unable to parse packet", e);
        }

        return null;
    }
}