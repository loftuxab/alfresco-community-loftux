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
package org.alfresco.repo.integrity;

/**
 * Integrity service interface
 * 
 * @author Derek Hulley
 */
public interface IntegrityService
{
    /**
     * Enables or disables integrity checking.  If false (disabled), this service will incur zero
     * performance overhead.  If enabled, it will incur a minor performance penalty if tracing is
     * off, but a high performance overhead if tracing is enabled.
     *  
     * @param enabled true to enable integrity
     */
    public void setEnabled(boolean enabled);
    
    /**
     * Enables or disables event tracing.  Enabling event tracing incurs a significant performance
     * penalty and should only be used when attempting to pinpoint the cause of a particular
     * integrity violation.
     * 
     * @param traceOn true if the event tracing must be enabled.
     */
    public void setTraceOn(boolean traceOn);
    
    /**
     * Set whether the {@link #checkIntegrity(String)} method should fail when a violation occurs
     * or whether it should just log the errors and return successfully.  In either case, the
     * violation is logged.
     * 
     * @param failOnViolation true to have a runtime exception thrown when an integrity
     *      violation is found, false to just log the violation.
     */
    public void setFailOnViolation(boolean failOnViolation);

    /**
     * Performs an integrity check for all integrity events that occured during the transaction
     * with the given ID.
     * <p>
     * This process will also perform any cleanup.  It can therefore only be run
     * <b>once per transaction</b>.
     * 
     * @param txnId the ID of the transaction to check
     * @throws IntegrityException with a list of integrity violations
     * 
     * @see org.alfresco.repo.transaction.AlfrescoTransactionManager#getTransactionId()
     */
    public void checkIntegrity(String txnId) throws IntegrityException;
}
