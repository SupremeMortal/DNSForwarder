package com.github.suprememortal.dnsforwarder;

import org.itxtech.nemisys.Client;
import org.itxtech.nemisys.event.EventHandler;
import org.itxtech.nemisys.event.EventPriority;
import org.itxtech.nemisys.event.Listener;
import org.itxtech.nemisys.event.player.PlayerLoginEvent;
import org.itxtech.nemisys.plugin.PluginBase;
import org.itxtech.nemisys.utils.Config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DNSForwarder extends PluginBase implements Listener {
    private final Map<String, String> mappings = new HashMap<String, String>();

    @Override
    @SuppressWarnings("unchecked")
    public void onEnable() {
        saveDefaultConfig();
        Config config = getConfig();

        if (!config.exists("forward")) {
            this.saveResource("config.yml", false);
            reloadConfig();
            config = getConfig();
        }

        List<Map<String, Object>> forward = config.getList("forward", null);

        for (Map<String, Object> map : forward) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String clientId = (String) entry.getValue();

                getLogger().info("Mapped: " + entry.getKey() + " ---> " + clientId);

                this.mappings.put(entry.getKey().toLowerCase(), clientId);
            }
        }

        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onJoin(PlayerLoginEvent event) {
        String address = event.getPlayer().getLoginChainData().getServerAddress().split(":")[0];
        String clientId = mappings.get(address);
        Client client = clientId == null ? null : getServer().getClientByDesc(clientId);
        if (client != null) {
            event.setClientHash(client.getHash());
        }
    }
}
