package com.freda.common.util;

import java.util.Random;

public class RandomUtil {

    private static final Random R = new Random();

    public static int nextInt(int num) {
        return R.nextInt(num);
    }

    public static double nextDouble() {
        return R.nextDouble();
    }

    public static float nextFloat() {
        return R.nextFloat();
    }

    public static boolean nextBoolean() {
        return R.nextBoolean();
    }

}
