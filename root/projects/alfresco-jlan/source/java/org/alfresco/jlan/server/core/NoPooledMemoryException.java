/*
 * Copyright (C) 2005-2008 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.jlan.server.core;

import java.io.IOException;

/**
 * No Pooled Memory Exception Class
 * 
 * <p>Indicates that no buffers are available in the global memory pool or per protocol pool.
 * 
 * @author gkspencer
 */
public class NoPooledMemoryException extends IOException {

	private static final long serialVersionUID = 6852939454477894406L;

	/**
	   * Default constructor
	   */
	  public NoPooledMemoryException() {
	    super();
	  }

	  /**
	   * Class constructor
	   *
	   * @param s String
	   */
	  public NoPooledMemoryException(String s) {
	    super(s);
	  }
}
