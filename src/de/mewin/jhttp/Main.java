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

import java.util.HashMap;

/**
 *
 * @author mewin<mewin001@hotmail.de>
 */
public class Main
{
    public static void main(String[] args)
    {
        int port = 80;
        HashMap<String, String> options = options(args);
        if (options.containsKey("port"))
        {
            try
            {
                port = Integer.valueOf(options.get("port"));
            }
            catch(NumberFormatException ex)
            {
                System.err.println("Invalid port: " + options.get("port"));
                System.exit(1);
            }
            if (port < 1 || port > 65535)
            {
                System.err.println("Port must be between 1 and 65535");
                System.exit(2);
            }
        }
        HttpServer server = new HttpServer(80);
        
        server.start();
    }
    
    private static HashMap<String, String> options(String[] args)
    {
        HashMap<String, String> map = new HashMap<>();
        for (String arg : args)
        {
            String[] split = arg.split("=", 2);
            String name = split[0].toLowerCase();
            String value = "";
            if (split.length > 1)
            {
                value = split[1];
            }
            if (name.startsWith("-") || name.startsWith("/"))
            {
                name = name.substring(1);
            }
            map.put(name, value);
        }
        return map;
    }
}