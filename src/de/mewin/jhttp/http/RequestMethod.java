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

package de.mewin.jhttp.http;

/**
 * represents all HTTP request methods (also the ones not supported by the server)
 * use [method].isSupported to check whether the method is supported
 * @version 01-25-2013
 * @author mewin<mewin001@hotmail.de>
 */
public enum RequestMethod
{
    /**
     * the HTTP GET method
     * GET is used to retrive files from the server
     * you can also send data to the server by attaching them to the URL
     */
    GET, 
    /**
     * the HTTP POST method
     * POST is used to retrive a file and transmit more data to the server
     */
    POST, 
    /**
     * the HTTP HEAD method
     * HEAD is used to only retrieve the HTTP header for a specific file on the server
     */
    HEAD, 
    /**
     * the HTTP PUT method
     * PUT is used to upload a resource to the server
     * this method is not supported by the server (yet)
     */
    PUT(false), 
    /**
     * the HTTP DELETE method
     * DELETE is used to delete a file from the server
     * this method is not supported by the server (yet)
     */
    DELETE(false), 
    /**
     * the HTTP TRACE method
     * TRACE sends the query of the client back as it was send
     * it is used for debug perposes
     */
    TRACE, 
    /**
     * the HTTP OPTIONS method
     * OPTIONS is used to retrive the request methods supported by the server
     */
    OPTIONS, 
    /**
     * the HTTP CONNECT method
     * CONNECT is used by HTTP proxies and not supported by this server
     */
    CONNECT(false);
    
    private boolean supported = true;
    
    private RequestMethod()
    {
        this(true);
    }
    
    private RequestMethod(boolean supported)
    {
        this.supported = supported;
    }
    
    /**
     * returns a boolean representing whether this request method is suppoerted by the server
     * @return true if the method is support, else false
     */
    public boolean isSupported()
    {
        return supported;
    }
}