package com.freda.registry;

public class ServerNameBuilder {

    private static final ServerNameBuilder INSTANCE = new ServerNameBuilder();

    public static ServerNameBuilder getInstance() {
        return INSTANCE;
    }

    public String generateServerName(String prefix, String ip, int port) {
        return new StringBuilder(prefix).append("_").append(ip).append("_").append(port).toString();
    }

}
