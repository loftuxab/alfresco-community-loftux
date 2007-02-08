/*
 * Copyright (C) 2005 Alfresco, Inc.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.alfresco.config.evaluator;

/**
 * Definition of an evaluator, an object that decides whether the config section applies to
 * the current object being looked up. 
 * 
 * @author gavinc
 */
public interface Evaluator
{
   /**
    * Determines whether the given condition evaluates to true for the given object
    * 
    * @param obj The object to use as the basis for the test
    * @param condition The condition to test
    * @return true if this evaluator applies to the given object, false otherwise
    */
   public boolean applies(Object obj, String condition);
}
