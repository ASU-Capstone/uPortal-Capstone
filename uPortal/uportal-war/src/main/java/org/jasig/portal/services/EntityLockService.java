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
package org.jasig.portal.services;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portal.EntityIdentifier;
import org.jasig.portal.concurrency.IEntityLock;
import org.jasig.portal.concurrency.IEntityLockService;
import org.jasig.portal.concurrency.IEntityLockServiceFactory;
import org.jasig.portal.concurrency.LockingException;
import org.jasig.portal.properties.PropertiesManager;

/**
  * This is a bootstrap class and facade for the IEntityLockService implementation.
  * It presents a simple api for acquiring lock objects, <code>IEntityLocks</code>,
  * that can be used to control concurrent access to portal entities in a
  * multi-server environment.  (See org.jasig.portal.concurrency.IEntityLockService
  * for a fuller description.)
  * <p>
  * Currently supported lock types are IEntityLockService.READ_LOCK and
  * IEntityLockService.WRITE_LOCK.
  * <p>
  * If I want to lock an entity for update, I ask the service for a write lock:
  * <p>
  * <code>
  *       Class type = anEntity.getClass(); // maybe hard-coded(?)<br>
  *       String key = anEntity.getKey();<br>
  *       EntityIdentifier ei = new EntityIdentifier(key, type);<br>
  *       String owner = getThePortalUserId();<br>
  *       IEntityLock lock = EntityLockService.instance().newWriteLock(ei, owner);<br>
  * </code>
  * <p>
  * Or maybe:
  * <p>
  * <code>
  *       IEntityLock lock = EntityLockService.instance().newWriteLock(ei, owner, duration);<br>
  * </code>
  * <p>
  * If there are no conflicting locks on the entity, the service returns the
  * requested lock.  If I acquire the lock, I know that no other client will be
  * able to get a conflicting lock, and from then on, I communicate with the
  * service via the lock:
  * <p>
  * <code>
  *   lock.convert(int newType); // See IEntityLockService for types.<br>
  *   lock.isValid();<br>
  *   lock.release();<br>
  *   lock.renew();<br>
  * </code>
  * <p>
  * A READ lock guarantees shared access; other clients can get READ locks
  * but not WRITE locks.  A WRITE lock guarantees exclusive access; no other
  * clients can get either READ or WRITE locks on the entity.
  *
  * @author  Dan Ellentuck
  * @version $Revision$
  */

public class EntityLockService
{
    
    private static final Log log = LogFactory.getLog(EntityLockService.class);
    
    // Singleton instance of the bootstrap class:
    private static EntityLockService instance = null;
    // The lock service:
    private IEntityLockService lockService = null;
    /** Creates new EntityLockService */
    private EntityLockService() throws LockingException
    {
        super();
        initialize();
    }
/**
 * @exception LockingException
 */
private void initialize() throws LockingException
{
    String eMsg = null;
    String factoryName =
        PropertiesManager.getProperty("org.jasig.portal.concurrency.IEntityLockServiceFactory", null);

    if ( factoryName == null ) {
        log.warn("Property org.jasig.portal.concurrency.IEntityLockServiceFactory not configured in PropertiesManager.  Defaulting to  org.jasig.portal.concurrency.locking.ReferenceEntityLockServiceFactory");
        factoryName =  "org.jasig.portal.concurrency.locking.ReferenceEntityLockServiceFactory";
    }

    try
    {
        IEntityLockServiceFactory lockServiceFactory =
            (IEntityLockServiceFactory)Class.forName(factoryName).newInstance();
        lockService = lockServiceFactory.newLockService();
    }
    catch (Exception e)
    {
        eMsg = "EntityLockService.initialize(): Problem creating entity lock service...";
        log.error( eMsg, e);
        throw new LockingException(eMsg, e);
    }
}
    public static synchronized EntityLockService instance() throws LockingException {
        if ( instance==null ) {
            instance = new EntityLockService();
        }
        return instance;
    }
/**
 * Returns a read lock for the entity type, entity key and owner.
 * @return org.jasig.portal.concurrency.locking.IEntityLock
 * @param entityType Class
 * @param entityKey String
 * @param owner String
 * @exception LockingException
 */
public IEntityLock newReadLock(Class entityType, String entityKey, String owner)
throws LockingException
{
    return lockService.newLock(entityType, entityKey, IEntityLockService.READ_LOCK, owner);
}
/**
 * Returns a read lock for the entity type, entity key and owner.
 * @return org.jasig.portal.concurrency.locking.IEntityLock
 * @param entityType Class
 * @param entityKey String
 * @param owner String
 * @param duration int (in seconds)
 * @exception LockingException
 */
public IEntityLock newReadLock(Class entityType, String entityKey, String owner, int duration)
throws LockingException
{
    return lockService.newLock(entityType, entityKey, IEntityLockService.READ_LOCK, owner, duration);
}
/**
 * Returns a read lock for the <code>IBasicEntity</code> and owner.
 * @return org.jasig.portal.concurrency.locking.IEntityLock
 * @param entityID EntityIdentifier
 * @param owner String
 * @exception LockingException
 */
public IEntityLock newReadLock(EntityIdentifier entityID, String owner)
throws LockingException
{
    return lockService.newLock(entityID.getType(), entityID.getKey(), IEntityLockService.READ_LOCK, owner);
}
/**
 * Returns a read lock for the <code>IBasicEntity</code>, owner and duration.
 * @return org.jasig.portal.concurrency.locking.IEntityLock
 * @param entityID EntityIdentifier
 * @param owner String
 * @param durationSecs int
 * @exception LockingException
 */
public IEntityLock newReadLock(EntityIdentifier entityID, String owner, int durationSecs)
throws LockingException
{
    return lockService.newLock(entityID.getType(), entityID.getKey(), IEntityLockService.READ_LOCK, owner, durationSecs);
}
/**
 * Returns a write lock for the entity type, entity key and owner.
 * @return org.jasig.portal.concurrency.locking.IEntityLock
 * @param entityType Class
 * @param entityKey String
 * @param owner String
 * @exception LockingException
 */
public IEntityLock newWriteLock(Class entityType, String entityKey, String owner)
throws LockingException
{
    return lockService.newLock(entityType, entityKey, IEntityLockService.WRITE_LOCK, owner);
}
/**
 * Returns a write lock for the entity type, entity key and owner.
 * @return org.jasig.portal.concurrency.locking.IEntityLock
 * @param entityType Class
 * @param entityKey String
 * @param owner String
 * @param durationSecs int
 * @exception LockingException
 */
public IEntityLock newWriteLock(Class entityType, String entityKey, String owner, int durationSecs)
throws LockingException
{
    return lockService.newLock(entityType, entityKey, IEntityLockService.WRITE_LOCK, owner, durationSecs);
}
/**
 * Returns a write lock for the <code>IBasicEntity</code> and owner.
 * @return org.jasig.portal.concurrency.locking.IEntityLock
 * @param entityID EntityIdentifier
 * @param owner String
 * @exception LockingException
 */
public IEntityLock newWriteLock(EntityIdentifier entityID, String owner)
throws LockingException
{
    return lockService.newLock(entityID.getType(), entityID.getKey(), IEntityLockService.WRITE_LOCK, owner);
}
/**
 * Returns a write lock for the <code>IBasicEntity</code>, owner and duration.
 * @return org.jasig.portal.concurrency.locking.IEntityLock
 * @param entityID EntityIdentifier
 * @param owner String
 * @param durationSecs int
 * @exception LockingException
 */
public IEntityLock newWriteLock(EntityIdentifier entityID, String owner, int durationSecs)
throws LockingException
{
    return lockService.newLock(entityID.getType(), entityID.getKey(), IEntityLockService.WRITE_LOCK, owner, durationSecs);
}
}
