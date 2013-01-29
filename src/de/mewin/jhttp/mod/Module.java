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

package de.mewin.jhttp.mod;

import de.mewin.jhttp.HttpServer;

/**
 *
 * @author mewin<mewin001@hotmail.de>
 */
public abstract class Module
{
    private HttpServer server = null;
    private boolean enabled = false;
    
    protected void onEnable()
    {
        
    }
    
    protected void onDisable()
    {
        
    }
    
    public final void setEnabled(boolean enabled)
    {
        if (enabled != this.enabled)
        {
            this.enabled = enabled;
            if (enabled)
            {
                this.onEnable();
            }
            else
            {
                this.onDisable();
            }
        }
    }
    
    public final boolean isEnabled()
    {
        return this.enabled;
    }
    
    public final void setServer(HttpServer server)
    {
        if (server == null)
        {
            this.server = server;
        }
        else
        {
            throw new IllegalArgumentException("Server has allready been set.");
        }
    }
    
    /**
     * the priority for the module to be enabled
     * a module is enable before any other module that has a lower priority
     * the default priority is 0
     * @return the priority of this module
     */
    public int priority()
    {
        return 0;
    }
    
    protected HttpServer getServer()
    {
        return this.server;
    }
}