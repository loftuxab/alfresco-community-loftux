/*
 * Copyright (C) 2006-2008 Alfresco Software Limited.
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

package org.alfresco.jlan.smb.nt;

/**
 * Well Known Security IDs Class
 *
 * @author gkspencer
 */
public class WellKnownSID {

	//	Well known security IDs
	
	public static final SID SIDEveryone 				= new SID("Everyone", 1,1,0);
	public static final SID SIDBuiltinDomain 		= new SID("Builtin", 1,5,32);
	public static final SID SIDLocalSystem			= new SID("LocalSystem", 1,5,18);
	
	/**
	 * Return a well known SID name, return null if the SID is not a well known SID
	 * 
	 * @param sid SID
	 * @return String
	 */
	public static final String getSIDName(SID sid) {
	  
	  //	Check the revision and identifier authority
	  
	  if ( sid.getRevision() != 1 ||
	       sid.getSubauthorityCount() != 1)
	    return null;
	  
	  //	Get the sub-authority value to determine the name
	  
	  String name = null;
	  int identAuth = sid.getIdentifierAuthority()[5];
	  int subAuth   = sid.getSubauthority(0);
	  
	  if ( identAuth == SID.IdentAuthWorld) {
	    
	    if ( subAuth == SID.SubAuthWorld)
	      name = "Everyone";
	  }
	  else if ( identAuth == SID.IdentAuthNT) {

	    //	Determine the well known SID name
	    
		  switch ( sid.getSubauthority(0)) {
		  	case SID.SubAuthNTDialup:
		  	  name = "Dialup";
		  		break;
		  	case SID.SubAuthNTNetwork:
		  	  name = "Network";
		  		break;
		  	case SID.SubAuthNTBatch:
		  	  name = "Batch";
		  		break;
		  	case SID.SubAuthNTInteractive:
		  	  name = "Interactive";
		  		break;
		  	case SID.SubAuthNTService:
		  	  name = "Service";
		  		break;
		  	case SID.SubAuthNTAnonymous:
		  	  name = "Anonymous";
		  		break;
		  	case SID.SubAuthNTProxy:
		  	  name = "Proxy";
		  		break;
		  	case SID.SubAuthNTEnterpriseCtrl:
		  	  name = "EnterpriseController";
		  		break;
		  	case SID.SubAuthNTPrincipalSelf:
		  	  name = "PrincipalSelf";
		  		break;
		  	case SID.SubAuthNTAuthenticated:
		  	  name = "AuthenticatedUser";
		  		break;
		  	case SID.SubAuthNTRestrictedCode:
		  	  name = "RestrictedCode";
		  		break;
		  	case SID.SubAuthNTTerminalServer:
		  	  name = "TerminalServer";
		  		break;
		  	case SID.SubAuthNTLocalSystem:
		  	  name = "LocalSystem";
		  		break;
		  	case SID.SubAuthNTNonUnique:
		  	  name = "NTNonUnique";
		  		break;
		  	case SID.SubAuthNTBuiltinDomain:
		  	  name = "BuilinDomain";
		  		break;
		  }
	  }
	  
	  //	Set the SID name
	  
	  if ( name != null)
	    sid.setName(name);
	  
	  //	Return the name
	  
	  return name;
	}
}
