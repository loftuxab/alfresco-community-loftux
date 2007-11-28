package org.alfresco.jlan.server;

/*
 * PacketHandlerList.java
 *
 * Copyright (c) Starlasoft 2004. All rights reserved.
 */

import java.util.Vector;

/**
 * Packet Handler List Class
 */
public class PacketHandlerList {

  //	List of session handlers
  
  private Vector<PacketHandlerInterface> m_handlers;
  
  /**
   * Default constructor
   */
  public PacketHandlerList() {
    m_handlers = new Vector<PacketHandlerInterface>();
  }
  
  /**
   * Add a handler to the list
   * 
   * @param handler PacketHandlerInterface
   */
  public final void addHandler(PacketHandlerInterface handler) {
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
   * @return PacketHandlerInterface
   */
  public final PacketHandlerInterface getHandlerAt(int idx) {
    
    //	Range check the index
    
    if ( idx < 0 || idx >= m_handlers.size())
      return null;
    return m_handlers.get(idx);
  }
  
  /**
   * Remove a handler from the list
   * 
   * @param idx int
   * @return PacketHandlerInterface
   */
  public final PacketHandlerInterface remoteHandler(int idx) {
    
    //	Range check the index
    
    if ( idx < 0 || idx >= m_handlers.size())
      return null;
    
    //	Remove the handler, and return it
    
    return m_handlers.remove(idx); 
  }
  
  /**
   * Remove all handlers from the list
   */
  public final void removeAllHandlers() {
    m_handlers.removeAllElements();
  }
}
