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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.jasig.portal.IPortalInfoProvider;
import org.jasig.portal.concurrency.locking.IClusterLockService.TryLockFunctionResult;
import org.jasig.portal.test.BasePortalJpaDaoTest;
import org.jasig.portal.test.ThreadGroupRunner;
import org.jasig.portal.utils.threading.ThrowingRunnable;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.base.Function;

/**
 * This test class has its tests disabled with Ignore annotations because it is non-deterministic and is found to
 * (one hopes, falsely) fail intermittently.
 *
 * @author Eric Dalquist
 * @version $Revision$
 */
//@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:jpaClusterLockDaoTestContext.xml")
public class ClusterLockServiceImplDbBackedTest extends BasePortalJpaDaoTest {
    @Autowired
    @Qualifier("normal")
    private IClusterLockService clusterLockService;
    
    @Autowired
    @Qualifier("dbOnly")
    private IClusterLockService dbOnlyclusterLockService;
    
    @Autowired
    private IPortalInfoProvider portalInfoProvider;

    @Ignore
    public void testLocalTryLockFunction() throws InterruptedException  {
        testTryLockFunction(this.clusterLockService);
    }

    @Ignore
    public void testDbOnlyTryLockFunction() throws InterruptedException  {
        testTryLockFunction(this.dbOnlyclusterLockService);
    }

    private void testTryLockFunction(final IClusterLockService service) throws InterruptedException {
        reset(portalInfoProvider);
        when(portalInfoProvider.getUniqueServerName()).thenReturn("ServerA");
        
        final ThreadGroupRunner threadGroupRunner = new ThreadGroupRunner("ClusterLockServiceImplTest-", true);
        final String mutexName = "testLockFunction";
        
        final AtomicInteger executionCounter = new AtomicInteger(0);
        final AtomicBoolean concurrent = new AtomicBoolean(false);
        final AtomicInteger trueCounter = new AtomicInteger(0);
        final AtomicInteger falseCounter = new AtomicInteger(0);
        
        final int threads = 3;
        threadGroupRunner.addTask(threads, new ThrowingRunnable() {
            @Override
            public void runWithException() throws Throwable {
                execute(new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        threadGroupRunner.tick(1);
                        final TryLockFunctionResult<Object> result = service.doInTryLock(mutexName, new Function<ClusterMutex, Object>() {
                            @Override
                            public Object apply(ClusterMutex input) {
                                if (concurrent.getAndSet(true)) {
                                    fail("Only one thread should be in Function at a time");
                                }
                                
                                try {
                                    executionCounter.incrementAndGet();
                                    logger.debug("Starting 1500ms of work");
                                    Thread.sleep(1500);
                                    logger.debug("Completed 1500ms of work");
                                }
                                catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                                finally {
                                    concurrent.set(false);
                                }
                                return null;
                            }
                        });
                        
                        if (result.isExecuted()) {
                            trueCounter.incrementAndGet();
                        }
                        else {
                            falseCounter.incrementAndGet();
                        }
                        
                        return result;
                    }
                });
            }
        });
        
        threadGroupRunner.start();
        threadGroupRunner.join();

        assertEquals(1, executionCounter.get());
        assertEquals(1, trueCounter.get());
        assertEquals(threads - 1, falseCounter.get());
        assertFalse(concurrent.get());
    }
}
