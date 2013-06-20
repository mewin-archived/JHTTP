/*
 * Copyright (C) 2013 mewin<mewin001@hotmail.de>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.mewin.jhttp;

import java.lang.reflect.Field;

/**
 *
 * @author mewin<mewin001@hotmail.de>
 */
public class JHTTP
{
    public static HttpServer getServer()
    {
        try
        {
            Field f = HttpServer.class.getDeclaredField("instance");
            f.setAccessible(true);
            return (HttpServer) f.get(null);
        }
        catch(Exception ex)
        {
            ex.printStackTrace(System.err);
            return null;
        }
    }
}