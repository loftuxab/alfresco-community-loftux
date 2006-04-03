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
package com.alfresco.benchmark.report;

public class BenchmarkTypeData
{
    String type;
    int count = 0;
    double totalDuration = 0;
    
    public BenchmarkTypeData(String type)
    {
        this.type = type;
    }
    
    public String getType()
    {
        return type;
    }
    
    public int getCount()
    {
        return count;
    }
    
    public double getTotalDuration()
    {
        return totalDuration;
    }
    
    public void incrementCount()
    {
        this.count++;
    }
    
    public void incrementTotalDuration(double duration)
    {
        this.totalDuration += duration;
    }
}