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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;

/**
 *
 * @author mewin<mewin001@hotmail.de>
 */
public class ServerSettingsFile
{
    private File file;
    private HashMap<String, Object> settings;
    
    public ServerSettingsFile(File file)
    {
        this.file = file;
        this.settings = new HashMap<String, Object>();
    }
    
    public void load() throws IOException
    {
        if (file.exists())
        {
            FileReader reader = new FileReader(file);
            parseConfig(reader);
        }
    }
    
    private void parseConfig(Reader in) throws IOException
    {
        String confName = "";
        String conf = null;
        
        int r;
        while ((r = in.read()) > -1)
        {
            if (r == '#')
            {
                while (r > -1 && r != '\n')
                {
                    r = in.read(); // skip comment
                }
                
                if (conf != null)
                {
                    parseConf(confName, conf);
                }
                conf = null;
                confName = "";
            }
            else if (r == '\r')
            {
                //skip
            }
            else if (r == '\n')
            {
                if (conf != null)
                {
                    parseConf(confName, conf);
                }
                conf = null;
                confName = "";
            }
            else if (r == '=')
            {
                conf = "";
            }
            else if (r == '"')
            {
                if (conf == null)
                {
                    confName += readReststring('"', in);
                }
                else
                {
                    conf += readReststring('"', in);
                }
            }
            else if (r == '\'')
            {
                if (conf == null)
                {
                    confName += readReststring('\'', in);
                }
                else
                {
                    conf += readReststring('\'', in);
                }
            }
            else if (conf == null)
            {
                confName += (char) r;
            }
            else
            {
                conf += (char) r;
            }
        }
        
        if (conf != null)
        {
            parseConf(confName, conf);
        }
    }
    
    private String readReststring(char end, Reader in) throws IOException
    {
        String str = "";
        int r;
        boolean bs = false; //backslash
        while ((r = in.read()) != -1)
        {
            if (r == '\\')
            {
                if (!(bs = !bs)) //Horst würde mich töten
                {
                    str += "\\";
                }
            }
            else if (r == end && !bs)
            {
                return str;
            }
            else
            {
                bs = false;
                str += (char) r;
            }
        }
        throw new IOException("Unexpected end of stream.");
    }
    
    private void parseConf(String name, String v)
    {
        Object val = v;
        String value = v.trim().toLowerCase();
        try
        {
            val = Integer.parseInt(value);
        }
        catch(NumberFormatException ex)
        {
            try
            {
                val = Double.parseDouble(value);
            }
            catch(NumberFormatException ex2)
            {
                if (value.equals("true"))
                {
                    val = true;
                }
                else if(value.equals("false"))
                {
                    val = false;
                }
            }
        }
        
        settings.put(name, val);
    }
    
    public <T> T getSetting(String name, T def)
    {
        if (settings.containsKey(name))
        {
            return (T) settings.get(name);
        }
        else
        {
            return def;
        }
    }
}