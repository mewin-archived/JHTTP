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

import de.mewin.jhttp.http.HttpHeader;
import de.mewin.jhttp.http.StatusCode;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mewin<mewin001@hotmail.de>
 */
public class HttpServer
{
    private ServerSocket ssocket;
    private int port;
    private Thread serverThread;
    private List<HttpConnection> activeConnections;
    private ServerSettingsFile settings, mime;
    private SimpleDateFormat sdf;
    
    private static final boolean DEBUG = true;
    
    public HttpServer(int port)
    {
        this.port = port;
        this.activeConnections = new ArrayList<>();
        if (DEBUG)
        {
            getLogger().setLevel(Level.ALL);
        }
        else
        {
            getLogger().setLevel(Level.INFO);
        }
        this.settings = new ServerSettingsFile(new File("conf\\settings.conf"));
        this.mime = new ServerSettingsFile(new File("conf\\mime.conf"));
        try
        {
            this.settings.load();
            this.mime.load();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        
        if (settings.getSetting("http-root", "").equals(""))
        {
            getLogger().log(Level.SEVERE, "HTTP root not defined.");
            System.exit(1);
        }
        this.sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
    }
    
    public void start()
    {
        getLogger().log(Level.INFO, "Starting HTTP server on port {0}", port);
        try
        {
            getLogger().log(Level.FINEST, "Registering socket...");
            ssocket = new ServerSocket(port);
        }
        catch(Exception ex)
        {
            handleThrowable(ex);
            return;
        }
        final HttpServer srv = this;
        getLogger().log(Level.FINER, "Starting server thread...");
        serverThread = new Thread()
        {
            @Override
            public void run()
            {
                while (!ssocket.isClosed())
                {
                    try
                    {
                        Socket soc = ssocket.accept();
                        
                        activeConnections.add(new HttpConnection(soc, srv));
                    }
                    catch(Exception ex)
                    {
                        handleThrowable(ex);
                    }
                }
            }
        };
        serverThread.start();
    }
    
    public boolean isRunning()
    {
        return serverThread != null && serverThread.isAlive();
    }
    
    public void stop()
    {
        try
        {
            ssocket.close();
        }
        catch(Exception ex)
        {
            handleThrowable(ex);
        }
    }
    
    public void handleThrowable(Throwable t)
    {
        if (t instanceof Error || t instanceof Exception)
        {
            getLogger().log(Level.SEVERE, "An error occured during the execution: ", t);
            stop();
        }
        else
        {
            getLogger().log(Level.WARNING, "Something unexpected happened: ", t);
        }
    }
    
    public void handleQuery(HttpConnection con, HttpHeader header, String body) throws IOException
    {
        HttpHeader answerHeader = new HttpHeader(StatusCode.OK);
        String content = "";
        File file = getFile(header.getUrl());
        try
        {
            if (!file.exists())
            {
                answerHeader = new HttpHeader(StatusCode.NOT_FOUND);
            }
        }
        catch(Exception ex)
        {
            answerHeader = new HttpHeader(StatusCode.INTERNAL_SERVER_ERROR);
        }
        
        if (answerHeader.getStatus() == StatusCode.OK)
        {
            answerHeader.addHeaderValue("Content-Type", getMimeType(header.getUrl()));
        }
        else
        {
            answerHeader.addHeaderValue("Content-Type", "text/html");
        }
        if (!file.exists())
        {
            answerHeader.addHeaderValue("Content-Length", String.valueOf(content.length()));
        }
        else
        {
            answerHeader.addHeaderValue("Content-Length", String.valueOf(file.length()));
        }
        answerHeader.addHeaderValue("Date", sdf.format(new Date()));
        answerHeader.addHeaderValue("Last-Modified", sdf.format(file.lastModified()));
        
        if (!file.exists())
        {
            con.sendQuery(answerHeader, content);
        }
        else
        {
            con.sendQuery(answerHeader, new FileInputStream(file));
        }
    }
    
    private String getMimeType(String url)
    {
        if (url.indexOf("?") > -1)
        {
            url = url.substring(0, url.indexOf("?"));
        }
        
        if (url.indexOf(".") > -1)
        {
            return mime.getSetting(url.substring(url.indexOf(".") + 1).toLowerCase(), "application/octet-stream");
        }
        else
        {
            return "application/octet-stream";
        }
    }
    
    private File getFile(String url)
    {
        if (url.charAt(0) == '/')
        {
            url = url.substring(1);
        }
        
        if (url.indexOf("?") > -1)
        {
            url = url.substring(0, url.indexOf("?"));
        }
        
        File file = new File(settings.getSetting("http-root", "") + url);
        return file;
    }
    
    public static Logger getLogger()
    {
        return Logger.getLogger("HttpServer");
    }
}