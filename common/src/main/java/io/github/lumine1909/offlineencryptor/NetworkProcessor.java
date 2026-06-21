package io.github.lumine1909.offlineencryptor;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class NetworkProcessor<C2SHello> {

    /**
     * Cache of pending login hello packets.
     * Uses ConcurrentHashMap for thread safety under Folia's multi-threaded environment.
     */
    protected final Map<String, C2SHello> C2S_HELLO_CACHE = new ConcurrentHashMap<>();

    public abstract void inject(Channel channel);

    public abstract void uninject(Channel channel);

    public Map<String, C2SHello> getCache() {
        return C2S_HELLO_CACHE;
    }
}
