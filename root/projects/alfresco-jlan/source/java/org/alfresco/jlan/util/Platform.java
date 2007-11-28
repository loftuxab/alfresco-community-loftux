package org.alfresco.jlan.util;

/**
 * Platform Class
 * 
 * <p>Determine the platform type that we are runnng on.
 * 
 * @author gkspencer
 */
public class Platform {

  // Platform types

  public enum Type {
    Unchecked, Unknown, WINDOWS, LINUX, SOLARIS, MACOSX
  };

  // Platform type we are running on

  private static Type _platformType = Type.Unchecked;

  /**
   * Determine the platform type
   * 
   * @return Type
   */
  public static final Type isPlatformType() {

    // Check if the type has been set

    if (_platformType == Type.Unchecked) {
      
      // Get the operating system type

      String osName = System.getProperty("os.name");

      if (osName.startsWith("Windows"))
        _platformType = Type.WINDOWS;
      else if (osName.equalsIgnoreCase("Linux"))
        _platformType = Type.LINUX;
      else if (osName.startsWith("Mac OS X"))
        _platformType = Type.MACOSX;
      else if (osName.startsWith("Solaris") || osName.startsWith("SunOS"))
        _platformType = Type.SOLARIS;
    }

    // Return the current platform type

    return _platformType;
  }
}
