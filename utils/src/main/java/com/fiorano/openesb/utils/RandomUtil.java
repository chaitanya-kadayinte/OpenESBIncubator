/**
 * Copyright (c) 1999-2007, Fiorano Software Technologies Pvt. Ltd. and affiliates.
 * Copyright (c) 2008-2015, Fiorano Software Pte. Ltd. and affiliates.
 *
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Fiorano Software ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * enclosed with this product or entered into with Fiorano.
 */





package com.fiorano.openesb.utils;

/**
 * @author Santhosh Kumar T
 */
public class RandomUtil {
    public static int random(int min, int max){
        return (int) Math.ceil(min + Math.random() * ((long) max - (long) min));
    }

    public static long random(long min, long max){
        return (long) Math.ceil(min + Math.random() * (max - min));
    }

    public static short random(short min, short max){
        return (short) Math.ceil(min + Math.random() * ((long) max - (long) min));
    }

    public static byte random(byte min, byte max){
        return (byte) Math.ceil(min + Math.random() * ((long) max - (long) min));
    }

    public static boolean randomBoolean(){
        return Math.random()<0.5d;
    }

    public static boolean randomBoolean(Boolean bool){
        if(Boolean.TRUE.equals(bool))
            return true;
        else if(Boolean.FALSE.equals(bool))
            return false;
        else // random either 0 or 1
            return randomBoolean();
    }
}
