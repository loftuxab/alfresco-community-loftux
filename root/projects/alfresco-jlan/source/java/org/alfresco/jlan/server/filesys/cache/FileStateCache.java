package org.alfresco.jlan.server.filesys.cache;

/*
 * FileStateCache.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Hashtable;

import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.server.filesys.FileName;
import org.alfresco.jlan.server.filesys.FileStatus;

/**
 * File State Cache Class
 * 
 * <p>Contains a cache of file/directory information for recently accessed files/directories
 * to reduce the calls made by the core server code to the shared device driver.
 */
public class FileStateCache implements Runnable {

	//	Initial allocation size for the state cache
	
	private static final int INITIAL_SIZE					= 500;
	
	//	Default expire check thread interval
	
	private static final long DEFAULT_EXPIRECHECK	= 3000;	// 60000;	//	1 minute
	
  //  File state cache, keyed by file path

  private Hashtable<String, FileState> m_stateCache;

	//	Wakeup interval for the expire file state checker thread
	
	private long m_expireInterval = DEFAULT_EXPIRECHECK;
	
	//	File state expiry time in seconds
	
	private long m_cacheTimer = 5 * 60000L;	//	5 minutes default
	
	//	File state listener
	
	private FileStateListener m_stateListener;
	
  //  File state expire daemon thread and shutdown flag
  
  private Thread m_expireThread;
  private boolean m_shutdown = false;
  
	//	Debug enable and output stream
	
	private boolean m_debug = false;
	
  /**
   * Class constructor
   */
  public FileStateCache() {
    m_stateCache = new Hashtable<String, FileState>(INITIAL_SIZE);
    
    //	Start the expired file state checker thread
    
    m_expireThread = new Thread(this);
    m_expireThread.setDaemon(true);
    m_expireThread.setName("FileStateExpire");
    m_expireThread.start();
  }
  
  /**
   * Return the expired file state checker interval, in milliseconds
   * 
   * @return long
   */
  public final long getCheckInterval() {
  	return m_expireInterval;
  }

	/**
	 * Get the file state cache timer, in milliseconds  
	 * 
	 * @return long
	 */
	public final long getCacheTimer() {
		return m_cacheTimer;
	}
	
	/**
	 * Return the number of states in the cache
	 * 
	 * @return int
	 */
	public final int numberOfStates() {
		return m_stateCache.size();
	}
	
  /**
   * Set the default file state cache timer, in milliseconds
   * 
   * @param tmo long
   */
  public final void setCacheTimer(long tmo) {
  	m_cacheTimer = tmo;
  }
  
  /**
   * Set the expired file state checker interval, in milliseconds
   * 
   * @param chkIntval long
   */
  public final void setCheckInterval(long chkIntval) {
  	m_expireInterval = chkIntval;
  }
  
  /**
   * Determine if debug output is enabled
   * 
   * @return boolean
   */
  public final boolean hasDebug() {
  	return m_debug;
  }
  
  /**
   * Enable/disable debug output
   * 
   * @param dbg boolean
   */
  public final void setDebug(boolean dbg) {
  	m_debug = dbg;
  }

  /**
   * Shutdown the file state expire thread
   */
  public final void shutdownRequest() {
    
    //  Check if the expire thread is valid
    
    if ( m_expireThread != null) {  
      
      //  Set the shutdown flag
      
      m_shutdown = true;
      
      //  Wakeup the expire thread

      m_expireThread.interrupt();
    }
  }
  
  /**
   * Add a new file state to the cache
   * 
   * @param fstate FileState
   */
  public final synchronized void addFileState(FileState fstate) {
  	
  	//	Check if the file state already exists in the cache
  	
  	if ( Debug.EnableInfo && hasDebug() && m_stateCache.get(fstate.getPath()) != null)
  		Debug.println("***** addFileState() state=" + fstate.toString() + " - ALREADY IN CACHE *****");

		//	DEBUG
		
		if ( Debug.EnableError && fstate == null) {
			Debug.println("addFileState() NULL FileState");
			return;
		}
		  		
  	//	Set the file state timeout and add to the cache
  	
  	fstate.setExpiryTime(System.currentTimeMillis() + getCacheTimer());
    m_stateCache.put( fstate.getPath(), fstate);
  }
  
  /**
   * Find the file state for the specified path
   * 
   * @param path String
   * @return FileState
   */
  public final synchronized FileState findFileState(String path) {
    return m_stateCache.get(FileState.normalizePath(path));
  }
  
  /**
   * Find the file state for the specified path, and optionally create a new file state if not found
   * 
   * @param path String
   * @param create boolean
   * @return FileState
   */
  public final synchronized FileState findFileState(String path, boolean create) {
  	
  	//	Find the required file state, if it exists
  	
  	FileState state = m_stateCache.get(FileState.normalizePath(path));
  	
  	//	Check if we should create a new file state
  	
  	if ( state == null && create == true) {
  		
  		//	Create a new file state
  		
  		state = new FileState(path);
  		
	  	//	Set the file state timeout and add to the cache
	  	
	  	state.setExpiryTime(System.currentTimeMillis() + getCacheTimer());
	    m_stateCache.put(state.getPath(), state);
  	}
  	
  	//	Return the file state
  	
  	return state;
  }
  
  /**
   * Update the name that a file state is cached under, and the associated file state
   * 
   * @param oldName String
   * @param newName String
   * @return FileState
   */
  public final synchronized FileState updateFileState(String oldName, String newName) {
  	
  	//	Find the current file state
  	
  	FileState state = m_stateCache.remove(FileState.normalizePath(oldName));

		//	Rename the file state and add it back into the cache using the new name
		
		if ( state != null) {
			state.setPath(newName);
			addFileState(state);
		}
		
		//	Return the updated file state
		
		return state;
  }
  
  /**
   * Enumerate the file state cache
   * 
   * @return Enumeration<String>
   */
  public final Enumeration<String> enumerate() {
    return m_stateCache.keys();
  }
  
  /**
   * Remove the file state for the specified path
   * 
   * @param path String
   * @return FileState
   */
  public final synchronized FileState removeFileState(String path) {

		//	Remove the file state from the cache
		
    FileState state = m_stateCache.remove(FileState.normalizePath(path));

		//	Check if there is a state listener
		
		if ( m_stateListener != null && state != null)
			m_stateListener.fileStateClosed(state);

		//	Return the removed file state
		
		return state;
  }
  
  /**
   * Rename a file state, remove the existing entry, update the path and add the state back into the
   * cache using the new path.
   *
   * @param newPath String 
   * @param state FileState
   * @param isDir boolean
   */
  public final void renameFileState(String newPath, FileState state, boolean isDir) {

    // Synchronize the cache update

    String oldPath = state.getPath();
    
    synchronized ( m_stateCache) {
      
      //  Remove the existing file state from the cache, using the original name
      
      m_stateCache.remove( state.getPath());
      
      //	Update the file state path and add it back to the cache using the new name

      state.setPath( newPath);
      state.setFileStatus( isDir ? FileStatus.DirectoryExists : FileStatus.FileExists);
        
      m_stateCache.put(state.getPath(), state);
    }
    
    // If the path is to a folder we must change the file status of all file states that are using the old
    // path
    
    if ( isDir == true) {
      
      // Get the old path and normalize

      if ( oldPath.endsWith( FileName.DOS_SEPERATOR_STR) == false)
        oldPath = oldPath + FileName.DOS_SEPERATOR_STR;
      oldPath = oldPath.toUpperCase();
      
      // Enumerate the file states
      
      Enumeration enm = enumerate();
      
      while ( enm.hasMoreElements()) {
        
        //  Get the current path from the state cache
        
        String statePath = (String) enm.nextElement();
        
        // Check if the path is below the renamed path
        
        if ( statePath.length() > oldPath.length() && statePath.startsWith( oldPath)) {

          // Get the associated file state, update and put back into the cache

          FileState renState = (FileState) m_stateCache.remove( statePath);

          renState.setFileStatus( FileStatus.NotExist);
          renState.setFileId( FileState.UnknownFileId);

          m_stateCache.put(renState.getPath(), renState);
          
          //  DEBUG
          
          if ( Debug.EnableInfo && hasDebug())
            Debug.println("++ Rename update " + statePath);
        }
      }
    }
  }
  
  /**
   * Remove all file states from the cache
   */
  public final synchronized void removeAllFileStates() {
  	
		//	Check if there are any items in the cache
		
		if ( m_stateCache == null || m_stateCache.size() == 0)
			return;
			    
    //	Enumerate the file state cache and remove expired file state objects
    
   	Enumeration<String> enm = m_stateCache.keys();
  	
  	while ( enm.hasMoreElements()) {

   	  //	Get the file state
   	  
   	  FileState state = m_stateCache.get(enm.nextElement());

			//	Check if there is a state listener
			
			if ( m_stateListener != null)
				m_stateListener.fileStateClosed(state);

	    //	DEBUG
	    
	    if ( Debug.EnableInfo && hasDebug())
	    	Debug.println("++ Closed: " + state.getPath());
   	}
   	
   	//	Remove all the file states
   	
   	m_stateCache.clear();
  }
  
  /**
   * Remove expired file states from the cache
   * 
   * @return int
   */
  public final int removeExpiredFileStates() {

		//	Check if there are any items in the cache
		
		if ( m_stateCache == null || m_stateCache.size() == 0)
			return 0;
			    
    //	Enumerate the file state cache and remove expired file state objects
    
   	Enumeration<String> enm = m_stateCache.keys();
		long curTime = System.currentTimeMillis();
		
		int expiredCnt = 0;
    int openCnt    = 0;
   	
   	while(enm.hasMoreElements()) {
   	  
   	  //	Get the file state
   	  
   	  FileState state = m_stateCache.get(enm.nextElement());
   	  
   	  if ( state != null && state.hasNoTimeout() == false) {
   	  	
	   	  synchronized ( state) {
	
					//	Check if the file state has expired and there are no open references to the file
					
		   	  if ( state.hasExpired(curTime) && state.getOpenCount() == 0) {
		
						//	Check if there is a state listener
						
						if ( m_stateListener == null || m_stateListener.fileStateExpired(state) == true) {
		
			   	    //	Remove the expired file state
			   	    
			   	    m_stateCache.remove(state.getPath());
			   	    
			   	    //	DEBUG
			   	    
//			   	    if ( m_debug)
//			   	    	Debug.println("++ Expired file state: " + state);
			   	    
			   	    //	Update the expired count
			   	    
			   	    expiredCnt++;
						}
		   	  }
          else if ( state.getOpenCount() > 0)
            openCnt++;
	   	  }
   	  }
   	}

    //  DEBUG
    
    if ( m_debug && openCnt > 0) {
      Debug.println("++ Open files " + openCnt);
      Dump( System.out, false);
    }
    
		//	Return the count of expired file states that were removed
		
		return expiredCnt;
  }
  
  /**
   * Add a file state listener
   * 
   * @param l FileStateListener
   */
  public final void addStateListener(FileStateListener l) {
  	m_stateListener = l;
  }
  
  /**
   * Remove a file state listener
   * 
   * @param l FileStateListener
   */
  public final void removeStateListener(FileStateListener l) {
		if ( m_stateListener == l)
  		m_stateListener = null;
  }
  
  /**
   * Expired file state checker thread
   */
  public void run() {
  	
  	//	Loop forever
  	
  	while ( m_shutdown == false) {
  		
  		//	Sleep for the required interval
  		
  		try {
  			Thread.sleep(getCheckInterval());
  		}
  		catch (InterruptedException ex) {
        if ( m_shutdown == true)
          continue;
  		}
  		
  		try {
  			
				//	Check for expired file states
				
				int cnt = removeExpiredFileStates();
				
				//	Debug
				
				if ( Debug.EnableInfo && hasDebug() && cnt > 0)
					Debug.println("++ Expired " + cnt + " file states, cache=" + m_stateCache.size());
  		}
  		catch (Exception ex) {
  			Debug.println(ex);
  		}
  	}
  }
  
  /**
   * Dump the state cache entries to the specified stream
   * 
   * @param out PrintStream
   * @param dumpAttribs boolean
   */
  public final void Dump(PrintStream out, boolean dumpAttribs) {
  	
  	//	Dump the file state cache entries to the specified stream
  	
  	if ( m_stateCache.size() > 0)
  		out.println("++ FileStateCache Entries:");
  	
  	Enumeration<String> enm = m_stateCache.keys();
  	long curTime = System.currentTimeMillis();
  	
  	while ( enm.hasMoreElements()) {
  		String fname = enm.nextElement();
  		FileState state = m_stateCache.get(fname);
  		
  		out.println("++  " + fname + "(" + state.getSecondsToExpire(curTime) + ") : " + state.toString());
  		
  		//	Check if the state attributes should be output
  		
  		if ( dumpAttribs == true)
  		  state.DumpAttributes(out);
  	}
  }
}