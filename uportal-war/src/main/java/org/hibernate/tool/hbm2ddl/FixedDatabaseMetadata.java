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
package org.hibernate.tool.hbm2ddl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.hibernate.exception.internal.StandardSQLExceptionConverter;
import org.hibernate.exception.spi.SQLExceptionConverter;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.mapping.Table;
import org.jboss.logging.Logger;

public class FixedDatabaseMetadata extends DatabaseMetadata {

    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, DatabaseMetaData.class.getName());

    private final Map tables = new HashMap();
    private final Set sequences = new HashSet();
    private final boolean extras;

    private DatabaseMetaData meta;
    private SQLExceptionConverter sqlExceptionConverter;

    public FixedDatabaseMetadata(Connection connection, Dialect dialect) throws SQLException {
        this(connection, dialect, true);
    }

    public FixedDatabaseMetadata(Connection connection, Dialect dialect, boolean extras) throws SQLException {
        super(connection, dialect, extras);

        final StandardSQLExceptionConverter ssec = new StandardSQLExceptionConverter();
        ssec.addDelegate(dialect.buildSQLExceptionConversionDelegate());
        sqlExceptionConverter = ssec;
        meta = connection.getMetaData();
        this.extras = extras;
        initSequences(connection, dialect);
    }

    private static final String[] TYPES = {"TABLE", "VIEW"};

    public TableMetadata getTableMetadata(String name, String schema, String catalog, boolean isQuoted) throws HibernateException {

        Object identifier = identifier(catalog, schema, name);
        TableMetadata table = (TableMetadata) tables.get(identifier);
        if (table!=null) {
            return table;
        }
        else {

            try {
                ResultSet rs = null;
                try {
                    if ( (isQuoted && meta.storesMixedCaseQuotedIdentifiers())) {
                        rs = meta.getTables(catalog, schema, name, TYPES);
                    } else if ( (isQuoted && meta.storesUpperCaseQuotedIdentifiers())
                        || (!isQuoted && meta.storesUpperCaseIdentifiers() )) {
                        rs = meta.getTables(
                                StringHelper.toUpperCase(catalog),
                                StringHelper.toUpperCase(schema),
                                StringHelper.toUpperCase(name),
                                TYPES
                            );
                    }
                    else if ( (isQuoted && meta.storesLowerCaseQuotedIdentifiers())
                            || (!isQuoted && meta.storesLowerCaseIdentifiers() )) {
                        rs = meta.getTables( 
                                StringHelper.toLowerCase( catalog ),
                                StringHelper.toLowerCase(schema), 
                                StringHelper.toLowerCase(name), 
                                TYPES 
                            );
                    }
                    else {
                        rs = meta.getTables(catalog, schema, name, TYPES);
                    }

                    while ( rs.next() ) {
                        String tableName = rs.getString("TABLE_NAME");
                        if ( name.equalsIgnoreCase(tableName) ) {
                            table = new TableMetadata(rs, meta, extras);
                            tables.put(identifier, table);
                            return table;
                        }
                    }

                    LOG.tableNotFound( name );
                    return null;

                }
                finally {
                    if (rs!=null) rs.close();
                }
            }
            catch (SQLException sqlException) {
                throw new SqlExceptionHelper( sqlExceptionConverter )
                        .convert( sqlException, "could not get table metadata: " + name );
            }
        }

    }

    private Object identifier(String catalog, String schema, String name) {
        return Table.qualify(catalog,schema,name);
    }

    private void initSequences(Connection connection, Dialect dialect) throws SQLException {
        if ( dialect.supportsSequences() ) {
            String sql = dialect.getQuerySequencesString();
            if (sql!=null) {

                Statement statement = null;
                ResultSet rs = null;
                try {
                    statement = connection.createStatement();
                    rs = statement.executeQuery(sql);

                    while ( rs.next() ) {
                        sequences.add( rs.getString(1).toLowerCase().trim() );
                    }
                }
                finally {
                    if (rs!=null) rs.close();
                    if (statement!=null) statement.close();
                }

            }
        }
    }

    public boolean isSequence(Object key) {
        if (key instanceof String){
            String[] strings = StringHelper.split(".", (String) key);
            return sequences.contains( strings[strings.length-1].toLowerCase());
        }
        return false;
    }

    public boolean isTable(Object key) throws HibernateException {
        if(key instanceof String) {
            Table tbl = new Table((String)key);
            if ( getTableMetadata( tbl.getName(), tbl.getSchema(), tbl.getCatalog(), tbl.isQuoted() ) != null ) {
                return true;
            } else {
                String[] strings = StringHelper.split(".", (String) key);
                if(strings.length==3) {
                    tbl = new Table(strings[2]);
                    tbl.setCatalog(strings[0]);
                    tbl.setSchema(strings[1]);
                    return getTableMetadata( tbl.getName(), tbl.getSchema(), tbl.getCatalog(), tbl.isQuoted() ) != null;
                } else if (strings.length==2) {
                    tbl = new Table(strings[1]);
                    tbl.setSchema(strings[0]);
                    return getTableMetadata( tbl.getName(), tbl.getSchema(), tbl.getCatalog(), tbl.isQuoted() ) != null;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "DatabaseMetadata" + tables.keySet().toString() + sequences.toString();
    }

}
