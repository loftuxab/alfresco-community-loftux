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
package org.alfresco.repo.search.transaction;

import org.springframework.transaction.TransactionException;

/**
 * @author Andy Hind
 */
public class LuceneTransactionException extends TransactionException
{
    private static final long serialVersionUID = 3978985453464335925L;

    public LuceneTransactionException(String arg0)
    {
        super(arg0);
    }

    public LuceneTransactionException(String arg0, Throwable arg1)
    {
        super(arg0, arg1);
    }
}
