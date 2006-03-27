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
package org.alfresco.benchmark.util;

import java.util.Random;

/**
 * @author Roy Wetherall
 */
public class RandUtils
{
    public static final Random rand = new Random();
    
    /**
     * Gets a random number from a normal distribution where the numbers center arouns the 'mean' interger
     * and the 90% of the generated numbers will fall within the range [mean-(2*variation)<=x<=mean+(2*variation)] and 70%
     * will fall in within the range [mean-variation<=x<=mean+variation].
     * 
     * Note: all negative numbers will returned as 0, please ensure that you take this into account when specifying the 
     *       variation value
     *  
     * @param mean          the mean
     * @param variation     the variation
     * @return              the generated number
     */
    public static int nextGaussianInteger(int mean, int variation)
    {
        double number = RandUtils.rand.nextGaussian();        
        int value = (int)Math.round((number*variation)+mean);
        if (value < 0)
        {
            value = 0;
        }
        return value;
    }
}
