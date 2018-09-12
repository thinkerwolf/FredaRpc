package com.freda.registry;

public class ServerNameBuilder {

    private static final ServerNameBuilder INSTANCE = new ServerNameBuilder();
    private static final String SERVER = "server";
    public static ServerNameBuilder getInstance() {
        return INSTANCE;
    }

    public String generateServerName(String prefix, String ip, int port) {
        return new StringBuilder(SERVER).append("_").append(ip).append("_").append(port).toString();
    }

}
