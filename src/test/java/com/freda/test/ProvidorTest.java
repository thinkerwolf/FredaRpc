package com.freda.test;

import com.freda.bootstrap.FredaProvidor;

public class ProvidorTest {

    public static void main(String args[]) {

        FredaProvidor providor = new FredaProvidor();
        providor.setConfFilePath("classpath:freda-providor.xml");
        providor.start();
    }

}
