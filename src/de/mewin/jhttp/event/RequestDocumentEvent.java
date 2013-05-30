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

package de.mewin.jhttp.event;

import de.mewin.jhttp.http.HttpHeader;
import java.io.File;
import java.io.InputStream;

/**
 *
 * @author mewin<mewin001@hotmail.de>
 */
public class RequestDocumentEvent extends Event
{
    private String requestURL;
    private File requestFile;
    private HTTPAnswer newAnswer = null;
    private HttpHeader requestHeader;
    private String requestBody;
    
    public RequestDocumentEvent(String url, File file, HttpHeader header, String body)
    {
        this.requestURL = url;
        this.requestFile = file;
        this.requestHeader = header;
        this.requestBody = body;
    }
    
    public String getURL()
    {
        return this.requestURL;
    }
    
    public File getFile()
    {
        return this.requestFile;
    }
    
    public void setFile(File file)
    {
        this.requestFile = file;
    }
    
    public void setNewAnswer(HTTPAnswer newAnswer)
    {
        this.newAnswer = newAnswer;
    }
    
    public HTTPAnswer getNewAnswer()
    {
        return this.newAnswer;
    }
    
    public HttpHeader getRequestHeader()
    {
        return this.requestHeader;
    }
    
    public String getRequestBody()
    {
        return this.requestBody;
    }
    
    public static class HTTPAnswer
    {
        public HttpHeader header;
        public String content;
        public InputStream in = null;
    }
}