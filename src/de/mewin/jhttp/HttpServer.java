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

import de.mewin.jhttp.event.EventManager;
import de.mewin.jhttp.event.RequestDocumentEvent;
import de.mewin.jhttp.http.HttpHeader;
import de.mewin.jhttp.http.ProtocolException;
import de.mewin.jhttp.http.RequestMethod;
import de.mewin.jhttp.http.StatusCode;
import de.mewin.jhttp.mod.ModuleManager;
import de.mewin.jhttp.util.HttpUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mewin<mewin001@hotmail.de>
 */
public class HttpServer
{
    private static HttpServer instance;
    private ServerSocket ssocket;
    private int port;
    private Thread serverThread;
    private List<HttpConnection> activeConnections;
    private ServerSettingsFile settings, mime;
    private ModuleManager mm;
    private EventManager em;
    
    private static final boolean DEBUG = true;
    
    public HttpServer(int port)
    {
        instance = this;
        this.port = port;
        this.activeConnections = new ArrayList<HttpConnection>();
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
        this.mm = new ModuleManager(this);
        File moduleFolder = new File("modules");
        if (!moduleFolder.exists() && !moduleFolder.mkdir())
        {
            getLogger().log(Level.WARNING, "Could not create module folder.");
        }
        else
        {
            mm.loadModules(moduleFolder);
        }
        em = new EventManager();
        mm.enableModules();
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
    
    public EventManager getEventManager()
    {
        return em;
    }
    
    public void handleQuery(HttpConnection con, HttpHeader header, String body) throws IOException
    {
        HttpHeader answerHeader = null;
        String content = "";
        RequestDocumentEvent ev = new RequestDocumentEvent(header.getUrl(), getFile(header.getUrl()), header, body);
        em.handleEvent(ev);
        File file = ev.getFile();
        InputStream answerStream = null;
        if (ev.getNewAnswer() == null)
        {
            try
            {
                if (!file.exists())
                {
                    answerHeader = HttpUtil.generateDefaultHeader(StatusCode.NOT_FOUND, "not found", "text/html");
                }
            }
            catch(Exception ex)
            {
                answerHeader = HttpUtil.generateDefaultHeader(StatusCode.INTERNAL_SERVER_ERROR, "internal server error", "text/html");
            }
        
            if (answerHeader == null)
            {
                if (file.exists())
                {
                    answerHeader = HttpUtil.generateFileHeader(StatusCode.OK, "", getMimeType(file.getName()), file);
                    answerHeader.addHeaderValue("Content-Length", String.valueOf(file.length()));
                }
                else
                {
                    answerHeader = HttpUtil.generateDefaultHeader(StatusCode.NOT_FOUND, "not found", "text/html");
                }
            }
        }
        else
        {
            answerHeader = ev.getNewAnswer().header;
            content = ev.getNewAnswer().content;
            answerStream = ev.getNewAnswer().in;
        }
        
        if (header.getMethod() == RequestMethod.GET
                || header.getMethod() == RequestMethod.POST)
        {
            if (file.exists() && ev.getNewAnswer() == null)
            {
                con.sendQuery(answerHeader, new FileInputStream(file));
            }
            else if (answerStream == null)
            {
                con.sendQuery(answerHeader, content);
            }
            else
            {
                con.sendQuery(answerHeader, answerStream);
            }
        }
        else if (header.getMethod() == RequestMethod.HEAD)
        {
            con.sendQuery(answerHeader, "");
        }
        else
        {
            con.sendQuery(HttpUtil.generateDefaultHeader(StatusCode.NOT_IMPLEMENTED, "", "text/html"), "");
        }
        if (!header.getHeaderValues().containsKey("connection") || !header.getHeaderValues().get("connection").equalsIgnoreCase("keep-alive"))
        {
            con.close();
        }
    }
    
    public void handleClientException(HttpConnection con, Exception ex)
    {
        try
        {
            if (ex instanceof ProtocolException || ex instanceof NumberFormatException)
            {
                con.sendQuery(HttpUtil.generateDefaultHeader(StatusCode.BAD_REQUEST, "", "text/html"), "");
            }
            else
            {
                con.sendQuery(HttpUtil.generateDefaultHeader(StatusCode.INTERNAL_SERVER_ERROR, "", "text/html"), "");
            }
        }
        catch(IOException ex2)
        {
            //ex2.printStackTrace(System.err);
            con.close();
        }
    }
    
    public String getMimeType(String url)
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