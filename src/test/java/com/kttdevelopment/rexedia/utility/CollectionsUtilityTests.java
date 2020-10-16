package com.kttdevelopment.rexedia.utility;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

public class CollectionsUtilityTests {

    @Test
    public void testPartition(){
        final Integer[] arr = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0};
        final List<Integer> list = Arrays.asList(arr);
        final List<List<Integer>> expected = Arrays.asList(
            Arrays.asList(1, 2, 3),
            Arrays.asList(4, 5, 6),
            Arrays.asList(7, 8, 9),
            Collections.singletonList(0)
        );

        Assertions.assertEquals(expected, CollectionsUtility.partitionList(arr, 3));
        Assertions.assertEquals(expected, CollectionsUtility.partitionList(list,3));
    }

}
