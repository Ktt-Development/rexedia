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

package com.kttdevelopment.rexedia.preset;

import com.kttdevelopment.rexedia.utility.ToStringBuilder;

import java.util.Objects;
import java.util.regex.Pattern;

public final class MetadataPreset {

    private final String metadata_key, format;
    private final Pattern regex;

    public MetadataPreset(final String key, final String regex, final String format){
        this.metadata_key = key;
        this.regex        = Pattern.compile(regex);
        this.format       = format;
    }

    public final String getKey(){
        return metadata_key;
    }

    public final String getFormat(){
        return format;
    }

    public final Pattern getRegex(){
        return regex;
    }

    public final String format(final String string){
        return regex.matcher(string).replaceAll(format);
    }

    @Override
    public boolean equals(final Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        final MetadataPreset that = (MetadataPreset) o;
        return Objects.equals(metadata_key, that.metadata_key) &&
               Objects.equals(format, that.format) &&
               Objects.equals(regex.pattern(), that.regex.pattern());
    }

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("key",metadata_key)
            .addObject("format",format)
            .addObject("regex",regex.pattern())
            .toString();
    }

}
