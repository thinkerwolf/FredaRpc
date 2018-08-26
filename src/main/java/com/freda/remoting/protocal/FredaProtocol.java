package com.freda.remoting.protocal;

public class FredaProtocol implements Protocol {

    public static final String NAME = "freda";

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void send(Object obj) {

    }

    @Override
    public <T> T refer(Class<T> clazz) {

        return null;
    }

}
