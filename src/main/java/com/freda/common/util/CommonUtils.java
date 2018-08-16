package com.freda.common.util;

import java.util.regex.Pattern;

public class CommonUtils {
	private static final Pattern PORT_PATTERN = Pattern.compile("\\d+");

	public static boolean checkPort(String portStr) {
		return PORT_PATTERN.matcher(portStr).matches();
	}
	
	public static byte[] int2bytes(int num) {
		byte[] result = new byte[4];
		result[0] = (byte) ((num >>> 24) & 0xff);
		result[1] = (byte) ((num >>> 16) & 0xff);
		result[2] = (byte) ((num >>> 8) & 0xff);
		result[3] = (byte) ((num >>> 0) & 0xff);
		return result;
	}

}
