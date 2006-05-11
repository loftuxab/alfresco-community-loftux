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
package org.alfresco.benchmark.alfresco.jcr;

import javax.jcr.Repository;

import org.alfresco.benchmark.alfresco.AlfrescoUtils;
import org.alfresco.benchmark.framework.jcr.JCRUtils;

/**
 * @author Roy Wetherall
 */
public class AlfrescoJCRUtils extends JCRUtils
{
    public static synchronized Repository getRepository()
    {
        if (repository == null)
        {
            try
            {
                repository = (Repository)AlfrescoUtils.getApplicationContext().getBean("JCR.Repository");
           }
            catch (Exception exception)
            {
                throw new RuntimeException("Unable to create repository", exception);
            }
        }
        return repository;
    }
}
