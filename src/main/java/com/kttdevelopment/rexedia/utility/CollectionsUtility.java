/*
 * Copyright (C) 2021 Ktt Development
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

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
