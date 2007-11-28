package org.alfresco.jlan.server.filesys.pseudo;

/*
 * LocalPseudoFile.java
 *
 * Copyright (c) 2007 Starlasoft. All rights reserved.
 */

import java.io.File;

import org.alfresco.jlan.server.filesys.FileInfo;
import org.alfresco.jlan.server.filesys.NetworkFile;


/**
 * Local Pseudo File Class
 * 
 * <p>
 * Pseudo file class that uses a file on the local filesystem.
 * 
 * @author gkspencer
 */
public class LocalPseudoFile extends PseudoFile {
  
  // Path to the file on the local filesystem

  private String m_path;

  /**
   * Class constructor
   * 
   * @param name String
   * @param path String
   */
  public LocalPseudoFile(String name, String path) {
    super(name);

    m_path = path;
  }

  /**
   * Return the path to the file on the local filesystem
   * 
   * @return String
   */
  public final String getFilePath() {
    return m_path;
  }

  /**
   * Return the file information for the pseudo file
   * 
   * @return FileInfo
   */
  public FileInfo getFileInfo() {

    // Check if the file information is valid

    if (getInfo() == null) {

      // Get the file details

      File localFile = new File(getFilePath());
      if (localFile.exists()) {
        // Create the file information

        FileInfo fInfo = new FileInfo(getFileName(), localFile.length(), getAttributes());

        // Set the file creation/modification times

        fInfo.setModifyDateTime(localFile.lastModified());
        fInfo.setCreationDateTime(_creationDateTime);
        fInfo.setChangeDateTime(_creationDateTime);

        // Set the allocation size, round up the actual length

        fInfo.setAllocationSize((localFile.length() + 512L) & 0xFFFFFFFFFFFFFE00L);

        setFileInfo(fInfo);
      }
    }

    // Return the file information

    return getInfo();
  }

  /**
   * Return a network file for reading/writing the pseudo file
   * 
   * @param netPath String
   * @return NetworkFile
   */
  public NetworkFile getFile(String netPath) {

    // Create a pseudo file mapped to a file in the local filesystem

    return new PseudoNetworkFile(getFileName(), getFilePath(), netPath);
  }
}
