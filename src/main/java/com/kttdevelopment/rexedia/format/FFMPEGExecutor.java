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

package com.kttdevelopment.rexedia.format;

import com.kttdevelopment.rexedia.utility.ToStringBuilder;

import java.io.IOException;

final class FFMPEGExecutor {

    private final String pathToFFMPEG, pathToFFPROBE;

    public FFMPEGExecutor(final String pathToFFMPEG, final String pathToFFPROBE){
        this.pathToFFMPEG  = pathToFFMPEG;
        this.pathToFFPROBE = pathToFFPROBE;
    }

    public final String executeFFMPEG(final String[] args) throws IOException{
        return execute(pathToFFMPEG, args);
    }

    public final String executeFFPROBE(final String[] args) throws IOException{
        return execute(pathToFFPROBE, args);
    }

    //

    private String execute(final String path, final String[] args) throws IOException{
        final CommandExecutor executor = new CommandExecutor(path);
        return executor.execute(args);
    }

    //

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("pathToFFMPEG",pathToFFMPEG)
            .addObject("pathToFFPROBE",pathToFFPROBE)
            .toString();
    }

}
