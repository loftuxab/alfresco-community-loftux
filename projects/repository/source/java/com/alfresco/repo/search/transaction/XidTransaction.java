/*
 * Created on Mar 30, 2005
 * 
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search.transaction;

import javax.transaction.Transaction;
import javax.transaction.xa.Xid;

public interface XidTransaction extends Xid, Transaction
{

}
