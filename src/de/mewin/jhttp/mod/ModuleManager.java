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
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;

/**
 *
 * @author mewin<mewin001@hotmail.de>
 */
public class ModuleManager
{
    private ArrayList<Module> modules;
    private HttpServer server;
    
    public ModuleManager(HttpServer server)
    {
        this.modules = new ArrayList<Module>();
        this.server = server;
    }
    
    public void registerModule(Module module)
    {
        module.setServer(server);
        insertModule(this.modules, module);
    }
    
    public void enableModules()
    {
        for (Module mod : this.modules)
        {
            mod.setEnabled(true);
        }
    }
    
    private void insertModule(ArrayList<Module> list, Module module) // sortieren durch Einfügen o.0
    {
        for (int i = 0; i < list.size(); i++)
        {
            if (list.get(i).priority() > module.priority())
            {
                list.add(i, module);
                return;
            }
        }
        list.add(module);
    }
    
    public void loadModules(File folder)
    {
        if (folder == null || !folder.exists() || !folder.isDirectory())
        {
            throw new IllegalArgumentException("Parameter must be a folder.");
        }
        
        File[] jars = folder.listFiles(new FilenameFilter()
        {
            @Override
            public boolean accept(File file, String name)
            {
                return name.toLowerCase().endsWith(".jar");
            }
        });
        
        for (File jar : jars)
        {
            loadModule(jar);
        }
    }
    
    public void loadModule(File jar)
    {
        if (jar != null && jar.exists() && jar.isFile())
        {
            try
            {
                JarFile jarFile = new JarFile(jar);
                ZipEntry infoFile = jarFile.getEntry("module.conf");
                
                if (infoFile != null && !infoFile.isDirectory())
                {
                    InputStream in = jarFile.getInputStream(infoFile);
                    try
                    {
                        ModuleConfiguration conf = new ModuleConfiguration(in);
                        Class cls = URLClassLoader.newInstance(new URL[] {jar.toURI().toURL()}).loadClass(conf.mainClass);
                        if (!Module.class.isAssignableFrom(cls))
                        {
                            throw new Exception("Main class is not a module.");
                        }
                        else
                        {
                            registerModule((Module) cls.newInstance());
                        }
                    }
                    catch(IOException | ModuleException ex)
                    {
                        throw new Exception("Failed to load module configuration:", ex);
                    }
                }
            }
            catch(Exception ex)
            {
                getLogger().log(Level.WARNING, "Could not open jar file: ", ex);
                return;
            }
        }
    }
    
    private Logger getLogger()
    {
        return Logger.getLogger("HttpServer");
    }
}
