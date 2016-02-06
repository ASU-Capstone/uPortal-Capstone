/**
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.portal.concurrency.locking;

import java.util.Date;

import org.jasig.portal.concurrency.IEntityLock;
import org.jasig.portal.concurrency.LockingException;

/**
 * Interface for finding and maintaining <code>IEntityLocks</code>.
 * @author Dan Ellentuck
 * @version $Revision$
 */
public interface IEntityLockStore
{
/**
 * Adds this IEntityLock to the store.
 * @param lock org.jasig.portal.concurrency.locking.IEntityLock
 */
public void add(IEntityLock lock) throws LockingException;

/**
 * Deletes this IEntityLock from the store.
 * @param lock org.jasig.portal.concurrency.locking.IEntityLock
 */
public void delete(IEntityLock lock) throws LockingException;

/**
 * Delete all IEntityLocks from the store.
 */
public void deleteAll() throws LockingException;

/**
 * Deletes the expired IEntityLocks from the underlying store.
 * @param expiration java.util.Date
 */
public void deleteExpired(Date expiration) throws LockingException;

/**
 * Returns an IEntityLock[] based on the params, any or all of which may be null.
 * A null param means any value, so <code>find(myType,myKey,null,null,null)</code>
 * will return all <code>IEntityLocks</code> for myType and myKey.
 *
 * @return org.jasig.portal.groups.IEntityLock[]
 * @param entityType Class
 * @param entityKey String
 * @param lockType Integer - so we can accept a null value.
 * @param expiration Date
 * @param lockOwner String
 * @exception LockingException - wraps an Exception specific to the store.
 */
public IEntityLock[] find(Class entityType, String entityKey, Integer lockType,
  Date expiration, String lockOwner)
throws LockingException;

/**
 * Returns an IEntityLock[] containing unexpired locks, based on the params,
 * any or all of which may be null EXCEPT FOR <code>expiration</code>.  A null
 * param means any value, so <code> find(expir,myType,myKey,null,null)</code>
 * will return all <code>IEntityLocks</code> for myType and myKey unexpired
 * as of <code>expiration</code>.
 *
 * @param expiration Date
 * @param entityType Class
 * @param entityKey String
 * @param lockType Integer - so we can accept a null value.
 * @param lockOwner String
 * @exception LockingException - wraps an Exception specific to the store.
 */
public IEntityLock[] findUnexpired(
    Date expiration,
    Class entityType,
    String entityKey,
    Integer lockType,
    String lockOwner)
throws LockingException;

/**
 * Updates the lock's <code>expiration</code> in the underlying store.
 * @param lock org.jasig.portal.concurrency.locking.IEntityLock
 * @param newExpiration java.util.Date
 */
public void update(IEntityLock lock, Date newExpiration)
throws LockingException;

/**
 * Updates the lock's <code>expiration</code> and <code>lockType</code> in the
 * underlying store.  Param <code>lockType</code> may be null.
 * @param lock org.jasig.portal.concurrency.locking.IEntityLock
 * @param newExpiration java.util.Date
 * @param newLockType Integer
 */
public void update(IEntityLock lock, Date newExpiration, Integer newLockType)
throws LockingException;

}
