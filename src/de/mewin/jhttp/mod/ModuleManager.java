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

import java.util.ArrayList;

/**
 *
 * @author mewin<mewin001@hotmail.de>
 */
public class ModuleManager
{
    private ArrayList<Module> modules;
    
    public ModuleManager()
    {
        this.modules = new ArrayList<Module>();
    }
    
    public void registerModule(Module module)
    {
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
}
