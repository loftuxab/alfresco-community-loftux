/*
 * Copyright (C) 2005 Alfresco, Inc.
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
package org.alfresco.service.cmr.security;

/**
 * The types of authority that are available.
 * <p>
 * <p>
 * Available types are:
 * <ol>
 * <li>USER - an authority that identifies a user
 * <li>GROUP - an authority that identifies a group
 * <li>OWNER - the special authority that applies to the owner of a node
 * <li>EVERYONE - the special authority that is interpreted as everyone
 * <li>GUEST - the special authority that applies to a GUEST (An unknown,  unauthenticated user) 
 * </ol>
 * 
 * @author Andy Hind
 */
public enum AuthorityType
{
    USER, GROUP, OWNER, EVERYONE, GUEST
}
