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
package org.jasig.portal.version.dao.jpa;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import org.jasig.portal.concurrency.CallableWithoutResult;
import org.jasig.portal.test.BasePortalJpaDaoTest;
import org.jasig.portal.version.dao.VersionDao;
import org.jasig.portal.version.om.Version;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:jpaPortalTestApplicationContext.xml")
public class JpaVersionDaoTest extends BasePortalJpaDaoTest {

    @Autowired
    private VersionDao versionDao;
    
    @Autowired
    private JdbcOperations jdbcOperations;

    @Test
    public void testVersionLifecycle() {
        final String productName = "TEST_VERSION";
        
        //Doesn't exist
        this.execute(new CallableWithoutResult() {
            @Override
            protected void callWithoutResult() {
                final Version version = versionDao.getVersion(productName);
                assertNull(version);
            }
        });

        //Create
        this.execute(new CallableWithoutResult() {
            @Override
            protected void callWithoutResult() {
                Version version = versionDao.getVersion(productName);
                assertNull(version);
                version = versionDao.setVersion(productName, 1, 2, 3, null);
                assertNotNull(version);
                assertEquals(1, version.getMajor());
                assertEquals(2, version.getMinor());
                assertEquals(3, version.getPatch());
                assertNull(version.getLocal());
            }
        });

        //Update
        this.execute(new CallableWithoutResult() {
            @Override
            protected void callWithoutResult() {
                Version version = versionDao.getVersion(productName);
                assertNotNull(version);
                assertEquals(1, version.getMajor());
                assertEquals(2, version.getMinor());
                assertEquals(3, version.getPatch());
                assertNull(version.getLocal());
                

                version = versionDao.setVersion(productName, 4, 5, 6, null);
                assertNotNull(version);
                assertEquals(4, version.getMajor());
                assertEquals(5, version.getMinor());
                assertEquals(6, version.getPatch());
                assertNull(version.getLocal());
            }
        });

        //verify
        this.execute(new CallableWithoutResult() {
            @Override
            protected void callWithoutResult() {
                Version version = versionDao.getVersion(productName);
                assertNotNull(version);
                assertEquals(4, version.getMajor());
                assertEquals(5, version.getMinor());
                assertEquals(6, version.getPatch());
                assertNull(version.getLocal());
            }
        });
    }
    


    @Test
    public void testVersionBadSql() {
        final String productName = "TEST_VERSION";
        
        //Create
        this.execute(new CallableWithoutResult() {
            @Override
            protected void callWithoutResult() {
                Version version = versionDao.getVersion(productName);
                assertNull(version);
                version = versionDao.setVersion(productName, 1, 2, 3, null);
                assertNotNull(version);
                assertEquals(1, version.getMajor());
                assertEquals(2, version.getMinor());
                assertEquals(3, version.getPatch());
                assertNull(version.getLocal());
            }
        });
        
        jdbcOperations.execute("ALTER TABLE UP_VERSION DROP LOCAL_VER");
        try {
            //Doesn't exist
            this.execute(new CallableWithoutResult() {
                @Override
                protected void callWithoutResult() {
                    final Version version = versionDao.getVersion(productName);
                    assertNotNull(version);
                    assertEquals(1, version.getMajor());
                    assertEquals(2, version.getMinor());
                    assertEquals(3, version.getPatch());
                    assertNull(version.getLocal());
                }
            });
        }
        finally {
            jdbcOperations.execute("ALTER TABLE UP_VERSION ADD COLUMN LOCAL_VER INTEGER");
        }
    }
}
