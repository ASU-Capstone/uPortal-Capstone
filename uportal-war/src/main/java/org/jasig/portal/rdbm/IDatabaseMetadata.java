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
package org.jasig.portal.rdbm;

import java.util.Date;

/**
 * @author susan.bramhall@yale.edu
 * @version $Revision$
 * Apr 8, 2005
 *
 */
public interface IDatabaseMetadata {
    /**
     * Gets the appropriate {@link IJoinQueryString} implemenation for
     * the database. If {@link #supportsOuterJoins()} returns <code>false</code>
     * this will return <code>null</code>.
     *
     * @return The appropriate {@link IJoinQueryString} implemenation.
     */
    public IJoinQueryString getJoinQuery();

    /**
     * Returns <code>true</code> if the database server supports outer
     * joins. The query to use if this returns <code>true</code> can
     * be retrieved from the {@link #getJoinQuery()} method.
     *
     * @return <code>true</code> if the server supports outer joins.
     */
    public boolean supportsOuterJoins();

    /**
     * Returns <code>true</code> if the database server supports transactions.
     *
     * @return <code>true</code> if the server supports transactions.
     */
    public boolean supportsTransactions();

    /**
     * Returns <code>true</code> if the database server supports prepared statements.
     *
     * @return <code>true</code> if the server supports prepared statements.
     */
    public boolean supportsPreparedStatements();

    /**
     * SQL TimeStamp format of current time.
     *
     * @return SQL TimeStamp of the current time.
     */
    public String sqlTimeStamp();

    /**
     * SQL TimeStamp format a long.
     *
     * @param date The time in milliseconds to format.
     * @return SQL TimeStamp of the specified time.
     */
    public String sqlTimeStamp(long date);

    /**
     * SQL TimeStamp format a Date.
     *
     * @param date The date to format.
     * @return SQL TimeStamp or "NULL" if date is null.
     */
    public String sqlTimeStamp(Date date);

    /**
     * @return Database Name
     */
    public String getDatabaseProductName();

    /**
     * @return Database Version
     */
    public String getDatabaseProductVersion();
    
    /**
     * @return The JDBC Driver being used
     */
    public String getJdbcDriver();

    /**
     * @return The JDBC Driver version
     */
    public String getJdbcDriverVersion();
    
    /**
     * @return The JDBC URL being used
     */
    public String getJdbcUrl();
    
    /**
     * @return The JDBC user name
     */
    public String getJdbcUser();
}