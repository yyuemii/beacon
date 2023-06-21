package co.yyuemii;

import co.yyuemii.listener.StatusListener;
import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;

@Plugin(
        id = "beacon",
        name = "beacon",
        authors = "yyuemii",
        description = "Plugin for Velocity that allows for forced hosts to pass through their pings when using TCPShield.",
        version = BuildConstants.VERSION
)
public class Beacon {
    @Getter
    private static ProxyServer proxy;
    @Getter
    private static Logger logger;

    @Inject
    public Beacon(ProxyServer server, Logger logger) {
        Beacon.proxy = server;
        Beacon.logger = logger;
    }

    @Subscribe
    public void onProxyInitialization(final @NonNull ProxyInitializeEvent event)  {
        proxy.getEventManager().register(this, new StatusListener());
    }
}
