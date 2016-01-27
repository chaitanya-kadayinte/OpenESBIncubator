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
 *  The class consists of static methods that can be used for comparison
 *  purposes in implementing the equals method for the dmi objects
 *
 * @author Manoj
 * @created January 16, 2002
 * @version 2.0
 */

public class DmiEqualsUtil
{
    /**
     *  Tests if two specified strings are equal or not.
     *
     * @param str1 The first string.
     * @param str2 The second string which is to be compared.
     * @return True if strings are equal, false otherwise.
     */
    public static boolean checkStringEquality(String str1, String str2)
    {
        if (str1 == null && str2 == null)
            return true;

        if ((str1 != null && str2 == null) || (str1 == null && str2 != null))
            return false;

        if (str1.equalsIgnoreCase(str2))
            return true;
        else
            return false;
    }

    /**
     * @param bool1
     * @param bool2
     * @return
     */
    public static boolean checkBooleanEquality(boolean bool1, boolean bool2)
    {
        if (bool1 && bool2)
            return true;

        if (!bool1 & !bool2)
            return true;

        return false;
    }

    /**
     * @param obj1
     * @param obj2
     * @return
     */
    public static boolean checkObjectEquality(Object obj1, Object obj2)
    {
        if (obj1 == null && obj2 == null)
            return true;

        if ((obj1 != null && obj2 == null) || (obj1 == null && obj2 != null))
            return false;

        if (obj1.equals(obj2))
            return true;

        return false;
    }
}
