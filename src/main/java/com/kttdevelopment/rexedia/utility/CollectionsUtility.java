package com.kttdevelopment.rexedia.utility;

import java.util.Arrays;
import java.util.List;

// todo
public abstract class CollectionsUtility {

    public static <T> List<List<T>> partitionList(final T[] list, final int partitions){
        return partitionList(Arrays.asList(list),partitions);
    }

    public static <T> List<List<T>> partitionList(final List<T> list, final int partitions){
        return null;
    }

}
