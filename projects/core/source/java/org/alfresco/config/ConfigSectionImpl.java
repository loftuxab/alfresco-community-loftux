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
package org.alfresco.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of a config section
 * 
 * @author gavinc
 */
public class ConfigSectionImpl implements ConfigSection
{
    private String evaluator;
    private String condition;
    private List<ConfigElement> configElements;

    public ConfigSectionImpl(String evaluator, String condition)
    {
        this.evaluator = evaluator;
        this.condition = condition;
        this.configElements = new ArrayList<ConfigElement>();
    }

    /**
     * @see org.alfresco.config.ConfigSection#getEvaluator()
     */
    public String getEvaluator()
    {
        return this.evaluator;
    }

    /**
     * @see org.alfresco.config.ConfigSection#getCondition()
     */
    public String getCondition()
    {
        return this.condition;
    }

    /**
     * @see org.alfresco.config.ConfigSection#getConfigElements()
     */
    public List<ConfigElement> getConfigElements()
    {
        return this.configElements;
    }

    /**
     * Adds a config element to the results for the lookup
     * 
     * @param configElement
     */
    public void addConfigElement(ConfigElement configElement)
    {
        this.configElements.add(configElement);
    }

    /**
     * @see org.alfresco.config.ConfigSection#isGlobal()
     */
    public boolean isGlobal()
    {
        boolean global = false;

        if (this.evaluator == null
                || this.evaluator.length() == 0
                || this.condition == null
                || this.condition.length() == 0)
        {
            global = true;
        }

        return global;
    }

    public String toString()
    {
        StringBuilder buffer = new StringBuilder(super.toString());
        buffer.append(" (evaluator=").append(this.evaluator);
        buffer.append(" condition=").append(this.condition).append(")");
        return buffer.toString();
    }
}
