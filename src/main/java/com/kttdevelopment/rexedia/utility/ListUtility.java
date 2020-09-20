package com.kttdevelopment.rexedia.utility;

import java.util.ArrayList;
import java.util.List;

public class ListUtility {

    public static <T> List<List<T>> partition(final T[] array, final int length){
        final List<List<T>> arrs = new ArrayList<>();

        final int len = array.length / length; // we want int div so next part doesn't fail
        for(int i = 0; i < len; i += length){
            final List<T> arr = new ArrayList<>();
            for(int i2 = 0; i < length; i++)
                arr.add(array[i+i2]);
            arrs.add(arr);
        }
        return arrs;
    }

}
