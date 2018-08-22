package com.freda.test;

import com.freda.bootstrap.Bootstrap;

public class Providor {

    public static void main(String args[]) {
    	
    	try {
    		Bootstrap b = new Bootstrap();
			b.setConfFilePath("classpath:freda-providor.xml");
			b.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	
    }

}
