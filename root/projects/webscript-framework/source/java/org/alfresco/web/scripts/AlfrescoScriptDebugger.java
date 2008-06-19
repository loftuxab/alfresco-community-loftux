/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.scripts;

import java.awt.event.ActionEvent;

import javax.swing.WindowConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.tools.debugger.Dim;
import org.mozilla.javascript.tools.debugger.ScopeProvider;
import org.mozilla.javascript.tools.debugger.SwingGui;
import org.mozilla.javascript.tools.shell.Global;


/**
 * Alfresco implementation of Rhino JavaScript debugger
 * 
 * @author davidc
 */
public class AlfrescoScriptDebugger implements Runnable
{
    private static final Log logger = LogFactory.getLog(AlfrescoScriptDebugger.class);
    
    private ContextFactory factory = null;
    private SwingGui gui = null;
    protected Dim dim = null;
    
    
    protected void initDebugger()
    {
        dim = new Dim();
    }
    
    /**
     * Start the Debugger
     */
    public void start()
    {
        if (logger.isDebugEnabled())
        {
            activate();
            show();
        }
    }

    /**
     * Activate the Debugger
     */
    public synchronized void activate()
    {
        factory = ContextFactory.getGlobal();
        Global global = new Global();
        global.init(factory);
        global.setIn(System.in);
        global.setOut(System.out);
        global.setErr(System.err);        
        initDebugger();
        ScopeProvider sp = new AlfrescoScopeProvider((Scriptable)global);
        dim.setScopeProvider(sp);
        gui = new AlfrescoGui(dim, getTitle(), this);
        gui.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        gui.setExitAction(this);
    }
    
    protected String getTitle()
    {
        return "Alfresco web-tier JavaScript Debugger";
    }

    
    /**
     * Show the debugger
     */
    public synchronized void show()
    {
        if (!isActive())
        {
            activate();
        }
        
        dim.setBreakOnExceptions(true);
        dim.setBreak();
        dim.attachTo(factory);
        gui.pack();
        gui.setSize(600, 460);
        gui.setVisible(true);
    }
    

    /**
     * Hide the Debugger
     */
    public synchronized void hide()
    {
        if (isVisible())
        {
            dim.detach();
            gui.dispose();
        }
    }
    
    /**
     * Is Debugger visible?
     * 
     * @return
     */
    public boolean isVisible()
    {
        return isActive() && gui.isVisible();
    }
    
    /**
     * Is Debugger active?
     * 
     * @return
     */
    public boolean isActive()
    {
        return gui != null;
    }
    
    /**
     * Exit action.
     */
    public void run()
    {
        dim.detach();
        gui.dispose();
    }
    
    
    private static class AlfrescoGui extends SwingGui
    {
        private static final long serialVersionUID = 5053205080777378416L;
        private AlfrescoScriptDebugger debugger;
        
        public AlfrescoGui(Dim dim, String title, AlfrescoScriptDebugger debugger)
        {
            super(dim, title);
            this.debugger = debugger;
        }

        public void actionPerformed(ActionEvent e)
        {
            String cmd = e.getActionCommand();
            if (cmd.equals("Exit"))
            {
                debugger.hide();
            }
            else
            {
                super.actionPerformed(e);
            }
        }
    }
    
    
    public static class AlfrescoScopeProvider implements ScopeProvider
    {
        AlfrescoScopeProvider(Scriptable scope)
        {
            this.scope = scope;
        }
        
        /**
         * The scope object to expose
         */
        private Scriptable scope;
        
        /**
         * Returns the scope for script evaluations.
         */
        public Scriptable getScope()
        {
            return scope;
        }
    }
}
