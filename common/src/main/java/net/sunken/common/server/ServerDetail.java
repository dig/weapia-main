package net.sunken.common.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.net.InetSocketAddress;

@ToString
@AllArgsConstructor
public class ServerDetail implements Serializable {

    @Getter
    private final String id;
    @Getter
    private final String host;
    @Getter
    private final int port;

    public InetSocketAddress toInetSocketAddress() {
        return new InetSocketAddress(this.host, this.port);
    }
}
