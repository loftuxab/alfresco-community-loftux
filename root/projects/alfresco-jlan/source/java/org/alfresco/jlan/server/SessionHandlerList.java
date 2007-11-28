package org.alfresco.jlan.server;

/*
 * SessionHandlerList.java
 *
 * Copyright (c) Starlasoft 2004. All rights reserved.
 */

import java.util.Vector;

/**
 * Session Handler List Class
 */
public class SessionHandlerList {

  //	List of session handlers
  
  private Vector<SessionHandlerInterface> m_handlers;
  
  /**
   * Default constructor
   */
  public SessionHandlerList() {
    m_handlers = new Vector<SessionHandlerInterface>();
  }
  
  /**
   * Add a handler to the list
   * 
   * @param handler SessionHandlerInterface
   */
  public final void addHandler(SessionHandlerInterface handler) {
    m_handlers.add(handler);
  }
  
  /**
   * Return the number of handlers in the list
   * 
   * @return int
   */
  public final int numberOfHandlers() {
    return m_handlers.size();
  }
  
  /**
   * Return the specified handler
   * 
   * @param idx int
   * @return SessionHandlerInterface
   */
  public final SessionHandlerInterface getHandlerAt(int idx) {
    
    //	Range check the index
    
    if ( idx < 0 || idx >= m_handlers.size())
      return null;
    return m_handlers.get(idx);
  }
  
  /**
   * Find the required handler by name
   * 
   * @param name String
   * @return SessionHandlerInterface
   */
  public final SessionHandlerInterface findHandler(String name) {
    
    //	Search for the required handler
    
    for ( int i = 0; i < m_handlers.size(); i++) {
      
      //	Get the current handler
      
      SessionHandlerInterface handler = m_handlers.get(i);
      
      if ( handler.getHandlerName().equals(name))
        return handler;
    }
    
    //	Handler not found
    
    return null;
  }
  
  /**
   * Remove a handler from the list
   * 
   * @param idx int
   * @return SessionHandlerInterface
   */
  public final SessionHandlerInterface remoteHandler(int idx) {
    
    //	Range check the index
    
    if ( idx < 0 || idx >= m_handlers.size())
      return null;
    
    //	Remove the handler, and return it
    
    return m_handlers.remove(idx); 
  }
  
  /**
   * Remove a handler from the list
   * 
   * @param name String
   * @return SessionHandlerInterface
   */
  public final SessionHandlerInterface remoteHandler(String name) {
    
    //	Search for the required handler
    
    for ( int i = 0; i < m_handlers.size(); i++) {
      
      //	Get the current handler
      
      SessionHandlerInterface handler = m_handlers.get(i);
      
      if ( handler.getHandlerName().equals(name)) {
        
        //	Remove the handler from the list
        
        m_handlers.removeElementAt(i);
        return handler;
      }
    }
    
    //	Handler not found
    
    return null;
  }
  
  /**
   * Remove all handlers from the list
   */
  public final void removeAllHandlers() {
    m_handlers.removeAllElements();
  }
}
