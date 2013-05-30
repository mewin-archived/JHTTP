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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * represents a HTTP header send by the client or the server using the HTTP protocol
 * @version 25-01-2013
 * @author mewin<mewin001@hotmail.de>
 */
public class HttpHeader
{
    public static final byte HTTP_VERSION = 0x11;
    private RequestMethod method;
    private byte httpVersion;
    private HashMap<String, String> headers;
    private String url;
    private boolean received;
    private StatusCode status;
    
    /**
     * creates a new header to be sent to a client
     * you can add more information using the addHeader method
     * @param status the status code of the header
     */
    public HttpHeader(StatusCode status)
    {
        this.method = null;
        this.received = false;
        this.httpVersion = HTTP_VERSION;
        this.headers = new HashMap<String, String>();
        this.status = status;
    }
    
    /**
     * creates a new header to be received by the server
     * normally the constructor using an InputStream is used for this
     * @param method the request method of the client querying the file
     * @param url the url of the file to be sent to the client
     */
    public HttpHeader(RequestMethod method, String url)
    {
        this.method = method;
        this.headers = new HashMap<String, String>();
        this.httpVersion = HTTP_VERSION;
        this.url = url;
        this.received = true;
        this.status = null;
    }
    
    /**
     * reads a http header from an input stream
     * after the reading process the stream will be forwarded to the end of the header
     * 
     * @param in the input stream to read the http header from
     * @throws IOException if an IOException occures during the reading process
     * @throws ProtocolException if the header is not http valid
     */
    public HttpHeader(InputStream in) throws IOException, ProtocolException
    {
        this.received = true;
        this.headers = new HashMap<String, String>();
        this.status = null;
        int r;
        String tmp = "";
        while ((r = in.read()) != ' ')
        {
            if (r == -1)
            {
                throw new ProtocolException("Unexpected end of stream.");
            }
            
            if (r == '\n' || r == '\r')
            {
                throw new ProtocolException("Unexpected end of line.");
            }
            
            tmp += (char) r;
        }
        
        try
        {
            this.method = RequestMethod.valueOf(tmp);
        }
        catch(IllegalArgumentException ex)
        {
            throw new ProtocolException("Unknown method type: ", ex);
        }
        
        tmp = "";
        while ((r = in.read()) != ' ')
        {
            if (r == -1)
            {
                throw new ProtocolException("Unexpected end of stream.");
            }
            
            if (r == '\n' || r == '\r')
            {
                throw new ProtocolException("Unexpected end of line.");
            }
            
            tmp += (char) r;
        }
        
        this.url = tmp;
        if (in.read() != 'H'
                || in.read() != 'T'
                || in.read() != 'T'
                || in.read() != 'P'
                || in.read() != '/')
        {
            throw new ProtocolException("Not a http request.");
        }
        int fNum = in.read();
        if (in.read() != '.')
        {
            throw new ProtocolException("Invalid http version.");
        }
        int sNum = in.read();
        if (fNum == -1 || sNum == -1)
        {
            throw new ProtocolException("Unexpected end of stream.");
        }
        this.httpVersion = (byte) (((byte) fNum - '0') * 16 | ((byte) sNum - '0')); //TODO: parse HTTP versions with multiple diggits
        int t = in.read();
        if (t == '\r')
        {
            t = in.read();
        }
        
        if (t != '\n')
        {
            throw new ProtocolException("Expected end of line.");
        }
        
        String name;
        boolean finished = false;
        while (!finished)
        {
            name = null;
            tmp = "";
            while((r = in.read()) != '\n' && !finished)
            {
                if (r == '\r')
                {
                    continue;
                }
                else if (r == ':')
                {
                    name = tmp;
                    tmp = "";
                }
                else if (r == -1)
                {
                    finished = true;
                }
                else
                {
                    tmp += (char) r;
                }
            }
            if (name == null && tmp.trim().equals(""))
            {
                finished = true;
            }
            else
            {
                headers.put(name, tmp);
            }
        }
    }
    
    /**
     * returns the request method of this header
     * this is either the method set in the constructor or the method given by an input stream
     * @see RequestMethod
     * @return a value from RequestMethod
     */
    public RequestMethod getMethod()
    {
        return this.method;
    }
    
    /**
     * returns the status code of this header
     * this is either the status code set in the constructor or null if this header was received from the client
     * @see StatusCode
     * @return a value from StatusCode or null
     */
    public StatusCode getStatus()
    {
        return this.status;
    }
    
    /**
     * returns the HTTP version used in this header
     * this is returned as a byte where the first four bits represent the major and the second four bits the minor version
     * for example HTTP/1.1 is 0x11
     * @return a byte representing the HTTP version
     */
    public byte getVersion()
    {
        return this.httpVersion;
    }
    
    /**
     * returns the URL of the header
     * this is either the URL given by a client or null if this header is send to a client
     * @return the URL of the request or null
     */
    public String getUrl()
    {
        return this.url;
    }
    
    /**
     * returns the header values of this header
     * these are information like
     * Host: www.example.com
     * or
     * Location: /index.php?file=index
     * where the key contains the name of the line and the value of the map entry the value of the information
     * @return a HashMap containing the header information of this header
     */
    public HashMap<String, String> getHeaderValues()
    {
        return new HashMap<String, String>(this.headers);
    }
    
    /**
     * 
     * @param name
     * @param value 
     */
    public void addHeaderValue(String name, String value)
    {
        this.headers.put(name, value);
    }
    
    private String versionString()
    {
        return (this.httpVersion >> 4) + "." + (this.httpVersion & 15);
    }
    
    /**
     * returns the http equivalent of this header
     * @return a string that can be send via http
     */
    public String toHTTP()
    {
        String http = "HTTP/" + versionString() + " " + status.getCode() + " " + status.getName() + "\n";
        
        for (Entry<String, String> ent : headers.entrySet())
        {
            http += ent.getKey() + ":" + ent.getValue() + "\n";
        }
        
        return http + "\n";
    }
}