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
package org.jasig.portal.jpa;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.jasig.portal.utils.cache.CacheKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;


/**
 * Aspect opens an entity manager around a method invocation
 * 
 * @author Eric Dalquist
 * @see OpenEntityManager
 * @see org.springframework.orm.jpa.JpaInterceptor
 */
@Aspect
@Component("openEntityManagerAspect")
public class OpenEntityManagerAspect implements ApplicationContextAware {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    private final Map<CacheKey, EntityManagerFactory> entityManagerFactories = new ConcurrentHashMap<CacheKey, EntityManagerFactory>();

    private ApplicationContext applicationContext;
    
	
    @Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
    	this.applicationContext = applicationContext;
	}
    
	@Pointcut(value="execution(public * *(..))")
    public void anyPublicMethod() { }
    
    @Around("anyPublicMethod() && @annotation(openEntityManager)")
    public Object openEntityManager(ProceedingJoinPoint pjp, OpenEntityManager openEntityManager) throws Throwable {
        final EntityManagerFactory emf = getEntityManagerFactory(openEntityManager);
        
        EntityManager em = getTransactionalEntityManager(emf);
        boolean isNewEm = false;
        if (em == null) {
            logger.debug("Opening JPA EntityManager in OpenEntityManagerAspect");
            em = createEntityManager(emf);
            isNewEm = true;
            TransactionSynchronizationManager.bindResource(emf, new EntityManagerHolder(em));
        }
        else {
            logger.debug("Using Existing JPA EntityManager in OpenEntityManagerAspect");
        }
		try {
			return pjp.proceed();
		}
		finally {
            if (isNewEm) {
                logger.debug("Closing JPA EntityManager in OpenEntityManagerAspect");
                TransactionSynchronizationManager.unbindResource(emf);
                EntityManagerFactoryUtils.closeEntityManager(em);
            }
		}
    }

    /**
     * Obtain the transactional EntityManager for this accessor's EntityManagerFactory, if any.
     * @return the transactional EntityManager, or <code>null</code> if none
     * @throws IllegalStateException if this accessor is not configured with an EntityManagerFactory
     * @see EntityManagerFactoryUtils#getTransactionalEntityManager(javax.persistence.EntityManagerFactory)
     * @see EntityManagerFactoryUtils#getTransactionalEntityManager(javax.persistence.EntityManagerFactory, java.util.Map)
     */
    protected EntityManager getTransactionalEntityManager(EntityManagerFactory emf) throws IllegalStateException{
        Assert.state(emf != null, "No EntityManagerFactory specified");
        return EntityManagerFactoryUtils.getTransactionalEntityManager(emf);
    }

	/**
	 * Get the EntityManagerFactory that this filter should use.
	 * @return the EntityManagerFactory to use
	 * @see #lookupEntityManagerFactory(OpenEntityManager)
	 */
    protected EntityManagerFactory getEntityManagerFactory(OpenEntityManager openEntityManager) {
    	final CacheKey key = this.createEntityManagerFactoryKey(openEntityManager);
    	EntityManagerFactory emf = this.entityManagerFactories.get(key);
    	if (emf == null) {
    		emf = this.lookupEntityManagerFactory(openEntityManager);
    		this.entityManagerFactories.put(key, emf);
    	}
    	return emf;
    }

	/**
	 * Look up the EntityManagerFactory that this filter should use.
	 * <p>The default implementation looks for a bean with the specified name
	 * in Spring's root application context.
	 * @return the EntityManagerFactory to use
	 * @see #getEntityManagerFactoryBeanName
	 */
	protected EntityManagerFactory lookupEntityManagerFactory(OpenEntityManager openEntityManager) {
		String emfBeanName = openEntityManager.name();
		String puName = openEntityManager.unitName();
		if (StringUtils.hasLength(emfBeanName)) {
			return this.applicationContext.getBean(emfBeanName, EntityManagerFactory.class);
		}
		else if (!StringUtils.hasLength(puName) && this.applicationContext.containsBean(OpenEntityManagerInViewFilter.DEFAULT_ENTITY_MANAGER_FACTORY_BEAN_NAME)) {
			return this.applicationContext.getBean(OpenEntityManagerInViewFilter.DEFAULT_ENTITY_MANAGER_FACTORY_BEAN_NAME, EntityManagerFactory.class);
		}
		else {
			// Includes fallback search for single EntityManagerFactory bean by type.
			return EntityManagerFactoryUtils.findEntityManagerFactory(this.applicationContext, puName);
		}
	}

	/**
	 * Create a JPA EntityManager to be bound to a request.
	 * <p>Can be overridden in subclasses.
	 * @param emf the EntityManagerFactory to use
	 * @see javax.persistence.EntityManagerFactory#createEntityManager()
	 */
	protected EntityManager createEntityManager(EntityManagerFactory emf) {
		return emf.createEntityManager();
	}
	
	/**
	 * @param openEntityManager The annotation to create a key for
	 * @return The key used to lookup the entity manager for an annotation
	 */
	protected final CacheKey createEntityManagerFactoryKey(OpenEntityManager openEntityManager) {
		return CacheKey.build("", openEntityManager.name(), openEntityManager.unitName());
	}
}
