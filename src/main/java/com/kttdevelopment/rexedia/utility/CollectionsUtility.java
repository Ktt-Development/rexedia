package com.kttdevelopment.rexedia.utility;

import java.util.*;

public abstract class CollectionsUtility {

    public static <T> List<List<T>> partitionList(final T[] list, final int partitions){
        return partitionList(Arrays.asList(list),partitions);
    }

    public static <T> List<List<T>> partitionList(final List<T> list, final int partitions){
        final List<List<T>> lists = new ArrayList<>();

        List<T> buffer = new ArrayList<>();
        final int length = list.size();
        for(int i = 0; i < length; i++){
            if(i != 0 && i % partitions == 0){ // if index is partition size
                lists.add(buffer);
                buffer = new ArrayList<>();
            }
            buffer.add(list.get(i));
        }
        if(!buffer.isEmpty())
            lists.add(buffer);
        return lists;
    }

}
