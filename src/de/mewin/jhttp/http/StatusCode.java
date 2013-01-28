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
 *
 * @author mewin<mewin001@hotmail.de>
 */
public enum StatusCode
{
    OK(200, "OK"), 
    FOUND(302, "Found"), 
    UNAUTHORIZED(401, "Unauthorized"), 
    ACCESS_DENIED(403, "Access Denied"), 
    NOT_FOUND(404, "Not Found"), 
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");
    
    private int code;
    private String name;
    
    private StatusCode(int code, String name)
    {
        this.code = code;
        this.name = name;
    }
    
    public int getCode()
    {
        return code;
    }
    
    public String getName()
    {
        return name;
    }
}