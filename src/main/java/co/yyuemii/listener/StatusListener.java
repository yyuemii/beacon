package co.yyuemii.listener;

import co.yyuemii.Beacon;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerPing;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

// Handles all events related to the 'STATUS' state
public class StatusListener {
    // intercepts any pings made to the proxy
    @Subscribe(order = PostOrder.EARLY)
    public EventTask onProxyPingEvent(final ProxyPingEvent event) {
        return EventTask.async(() -> this.onProxyPingEventAsync(event));
    }

    // finds the correct server to get ping information from
    private void onProxyPingEventAsync(final ProxyPingEvent event) {
        // https://github.com/PaperMC/Velocity/blob/dev/3.0.0/proxy/src/main/java/com/velocitypowered/proxy/connection/util/ServerListPingHandler.java#L144
        String host = event.getConnection().getVirtualHost().map(InetSocketAddress::getHostString)
                .map(str -> str.toLowerCase(Locale.ROOT))
                .orElse("");

        // gets a list of servers to ping (in order from forced hosts to other connection orders)
        List<String> servers = Beacon.getProxy().getConfiguration().getForcedHosts()
                .getOrDefault(host, Beacon.getProxy().getConfiguration().getAttemptConnectionOrder());

        // iterates over every server to attempt to find a ping to passthrough
        for (String s : servers) {
            Optional<RegisteredServer> server = Beacon.getProxy().getServer(s);

            if (server.isEmpty()) {
                continue;
            }

            try {
                ServerPing ping = server.get().ping().get();
                event.setPing(ping);
            } catch (Exception e) {
                return;
            }
        }
    }
}
