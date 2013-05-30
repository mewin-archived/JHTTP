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

package de.mewin.jhttp.util;

import de.mewin.jhttp.http.HttpHeader;
import de.mewin.jhttp.http.StatusCode;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 *
 * @author mewin<mewin001@hotmail.de>
 */
public final class HttpUtil
{
    private static SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
    
    public static HttpHeader generateFileHeader(StatusCode status, String content, String mimeType, File file)
    {
        HttpHeader header = generateDefaultHeader(status, content, mimeType);
        
        header.addHeaderValue("Last-Modified", sdf.format(file.lastModified()));
        
        return header;
    }
    
    public static HttpHeader generateDefaultHeader(StatusCode status, String content, String mimeType)
    {
        HttpHeader header = new HttpHeader(status);
        
        header.addHeaderValue("Content-Type", mimeType);
        header.addHeaderValue("Content-Length", String.valueOf(content.length()));
        header.addHeaderValue("Date", sdf.format(new Date()));
        
        return header;
    }
    
    public static HashMap<String, String> getGetData(String url)
    {
        String[] split = url.split("\\?", 2);
        if (split.length < 2)
        {
            return new HashMap<>();
        }
        else
        {
            return parsePostData(split[1]);
        }
    }
    
    public static HashMap<String, String> parsePostData(String body)
    {
        String[] split = body.split("&");
        HashMap<String, String> map = new HashMap<>();
        for (String data : split)
        {
            String[] split2 = data.split("=", 2);
            if (split2.length < 2)
            {
                map.put(split2[0], "");
            }
            else
            {
                map.put(split2[0], split2[1]);
            }
        }
        return map;
    }
}