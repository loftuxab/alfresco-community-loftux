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
package org.alfresco.config;

import org.alfresco.config.evaluator.Evaluator;

/**
 * Interface definition for a config lookup algorithm, this may be last value
 * wins, a merging strategy or based on inheritance.
 * 
 * @author gavinc
 */
public interface ConfigLookupAlgorithm
{
   /**
    * Determines whether the given section applies to the given object, if so
    * the section is added to the results
    * 
    * @param section The config section to test 
    * @param evaluator The evaluator for the section being processed
    * @param object The object which is the subject of the config lookup
    * @param results The Config object holding all the matched sections
    */
   public void process(ConfigSection section, Evaluator evaluator, Object object, Config results);
}
