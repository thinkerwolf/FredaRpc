package com.freda.config;

import java.util.Collection;

public class ConfigUtil {

    public static RegistryConfig findRegistryConfig(Collection<RegistryConfig> coll, String id) {
        for (RegistryConfig c : coll) {
            if (id.equals(c.getId())) {
                return c;
            }
        }
        return null;
    }

    public static ClientConfig findClientConfig(Collection<ClientConfig> coll, String id) {
        for (ClientConfig c : coll) {
            if (id.equals(c.getId())) {
                return c;
            }
        }
        return null;
    }

    public static ServerConfig findServerConfig(Collection<ServerConfig> coll, String id) {
        for (ServerConfig c : coll) {
            if (id.equals(c.getId())) {
                return c;
            }
        }
        return null;
    }

}
