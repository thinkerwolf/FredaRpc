package com.freda.test;

import com.freda.common.conf.Configuration;

public class ProvidorTest {

    public static void main(String args[]) {
    	
    	try {
			Configuration conf = Configuration.newConfiguration("classpath:freda-providor.xml");
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	
    	
    }

}
