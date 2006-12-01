/*-----------------------------------------------------------------------------
*  Copyright 2006 Alfresco Inc.
*  
*  Licensed under the Mozilla Public License version 1.1
*  with a permitted attribution clause. You may obtain a
*  copy of the License at:
*  
*      http://www.alfresco.org/legal/license.txt
*  
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
*  either express or implied. See the License for the specific
*  language governing permissions and limitations under the
*  License.
*  
*  
*  Author  Jon Cox  <jcox@alfresco.com>
*  File    VirtWebappRegistry.java
*----------------------------------------------------------------------------*/


package org.alfresco.mbeans;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import java.util.ArrayList;

public class VirtWebappRegistry implements VirtWebappRegistryMBean 
{
    private int moo_ = 1;
    private String [] virtWebapps_ = { "totally", "bogus", "example", "of", "list" };

    public VirtWebappRegistry()
    {
        System.out.println("------VirtWebappRegistry ctor: " + this);
    }

    public void setMoo(int moo)
    {
        moo_ = moo;
    }
    public int  getMoo()
    {
        return moo_;
    }
    public void setVirtWebapp(String virtWebapp)
    {
        System.out.println("TODO: reset virtWebapp: " + virtWebapp);
    }
    public String[] getVirtWebapps()
    {
        return virtWebapps_;
    }
}
