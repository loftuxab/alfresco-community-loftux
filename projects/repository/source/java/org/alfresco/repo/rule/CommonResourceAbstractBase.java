/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.repo.rule;

import java.io.IOException;
import java.util.Properties;

import org.alfresco.service.cmr.rule.RuleServiceException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.core.io.Resource;

/**
 * Common resouce abstract base class.
 * 
 * @author Roy Wetherall
 */
public abstract class CommonResourceAbstractBase implements BeanNameAware
{
    /**
     * The bean name
     */
    protected String name;

    /**
     * The properties object
     */
    protected Properties properties;

    /**
     * Set the bean name
     * 
     * @param name
     *            the bean name
     */
    public void setBeanName(String name)
    {
        this.name = name;
    }

    /**
     * Set the resource
     * 
     * @param resource
     *            the resource object
     */
    public void setResource(Resource resource)
    {
        try
        {
            this.properties = new Properties();
            this.properties.load(resource.getInputStream());
        } 
        catch (IOException exception)
        {
            throw new RuleServiceException("Unable to load resource file: " + resource.getFilename(), exception);
        }
    }

}
