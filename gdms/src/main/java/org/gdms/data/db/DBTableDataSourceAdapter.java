/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 * 
 *  Team leader Erwan BOCHER, scientific researcher,
 * 
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.gdms.data.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.apache.log4j.Logger;

import org.gdms.data.DataSource;
import org.gdms.data.DriverDataSource;
import org.gdms.data.edition.Commiter;
import org.gdms.data.edition.DeleteEditionInfo;
import org.gdms.data.edition.EditionInfo;
import org.gdms.data.edition.PhysicalRowAddress;
import org.gdms.data.schema.MetadataUtilities;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DBDriver;
import org.gdms.driver.DBReadWriteDriver;
import org.gdms.driver.DriverException;
import org.gdms.source.CommitListener;
import org.gdms.source.DefaultSourceManager;
import org.gdms.source.Source;
import org.orbisgis.utils.I18N;

/**
 * Adapter to the DataSource interface for database drivers
 * 
 */
public class DBTableDataSourceAdapter extends DriverDataSource implements
        Commiter, CommitListener {

        private static final String GDMS_DRIVER_ERROR_CONNECTION_OPEN = "gdms.driver.error.connection.open";
        private DBDriver driver;
        private DBSource def;
        protected Connection con;
        private int[] cachedPKIndices;
        private static final Logger LOG = Logger.getLogger(DBTableDataSourceAdapter.class);

        /**
         * Creates a new DBTableDataSourceAdapter
         *
         *
         * @param src
         * @param def
         * @param driver
         */
        public DBTableDataSourceAdapter(Source src, DBSource def, DBDriver driver) {
                super(src);
                this.def = def;
                this.driver = driver;
                LOG.trace("Constructor");
        }

        @Override
        public void close() throws DriverException {
                LOG.trace("Closing");
                driver.close(con);
                fireCancel(this);
                try {
                        con.close();
                        con = null;
                } catch (SQLException e) {
                        throw new DriverException(I18N.getString("gdms.datasource.error.datasource.close"), e);
                }

                DefaultSourceManager sm = (DefaultSourceManager) getDataSourceFactory().getSourceManager();
                sm.removeCommitListener(this);
        }

        @Override
        public DBDriver getDriver() {
                return driver;
        }

        /**
         * Gets a connection to the driver
         *
         * @return Connection
         *
         * @throws SQLException
         *             if the connection cannot be established
         */
        private Connection getConnection() throws SQLException {
                LOG.trace("Getting connection");
                if (con == null) {
                        con = driver.getConnection(def.getHost(), def.getPort(), def.isSsl(), def.getDbName(), def.getUser(), def.getPassword());
                }
                return con;
        }

        @Override
        public void open() throws DriverException {
                LOG.trace("Opening");
                try {
                        con = getConnection();
                        driver.open(con, def.getTableName(), def.getSchemaName());
                        fireOpen(this);
                } catch (SQLException e) {
                        throw new DriverException(I18N.getString(GDMS_DRIVER_ERROR_CONNECTION_OPEN), e);
                }

                DefaultSourceManager sm = (DefaultSourceManager) getDataSourceFactory().getSourceManager();
                sm.addCommitListener(this);
        }

        /**
         * @throws DriverException
         * @see org.gdms.data.DataSource#getPKNames()
         */
        private String[] getPKNames() throws DriverException {
                final String[] ret = new String[getPKCardinality()];

                for (int i = 0; i < ret.length; i++) {
                        ret[i] = getPKName(i);
                }

                return ret;
        }

        /**
         * @param dataSource
         * @see org.gdms.data.DataSource#saveData(org.gdms.data.DataSource)
         */
        @Override
        public void saveData(DataSource dataSource) throws DriverException {
                LOG.trace("Saving data");
                dataSource.open();

                DBReadWriteDriver readWriteDriver = ((DBReadWriteDriver) driver);
                if (driver instanceof DBReadWriteDriver) {
                        Connection localCon;
                        try {
                                localCon = getConnection();
                                readWriteDriver.beginTrans(localCon);
                        } catch (SQLException e) {
                                throw new DriverException(I18N.getString(GDMS_DRIVER_ERROR_CONNECTION_OPEN), e);
                        }
                }

                for (int i = 0; i < dataSource.getRowCount(); i++) {
                        Value[] row = new Value[dataSource.getFieldNames().length];
                        for (int j = 0; j < row.length; j++) {
                                row[j] = dataSource.getFieldValue(i, j);
                        }

                        try {
                                Type[] fieldTypes = MetadataUtilities.getFieldTypes(dataSource.getMetadata());
                                String sql = readWriteDriver.getInsertSQL(dataSource.getFieldNames(), fieldTypes, row);

                                readWriteDriver.execute(con, sql);
                        } catch (SQLException e) {
                                if (driver instanceof DBReadWriteDriver) {
                                        try {
                                                Connection localCon = getConnection();
                                                readWriteDriver.rollBackTrans(localCon);
                                        } catch (SQLException e1) {
                                                LOG.error("Error saving data.", e);
                                                throw new DriverException(I18N.getString(GDMS_DRIVER_ERROR_CONNECTION_OPEN),
                                                        e1);
                                        }
                                }

                                throw new DriverException(I18N.getString(GDMS_DRIVER_ERROR_CONNECTION_OPEN), e);
                        }
                }

                if (driver instanceof DBReadWriteDriver) {
                        try {
                                Connection localCon = getConnection();
                                readWriteDriver.commitTrans(localCon);
                        } catch (SQLException e) {
                                throw new DriverException(I18N.getString(GDMS_DRIVER_ERROR_CONNECTION_OPEN), e);
                        }
                }

                dataSource.close();
        }

        public long[] getWhereFilter() throws IOException {
                return null;
        }

        @Override
        public boolean commit(List<PhysicalRowAddress> rowsDirections,
                String[] fieldNames, List<EditionInfo> schemaActions,
                List<EditionInfo> editionActions,
                List<DeleteEditionInfo> deletedPKs, DataSource modifiedSource)
                throws DriverException {
                LOG.trace("Commiting");
                try {
                        ((DBReadWriteDriver) driver).beginTrans(getConnection());
                } catch (SQLException e) {
                        throw new DriverException(I18N.getString(GDMS_DRIVER_ERROR_CONNECTION_OPEN), e);
                }

                String sql = null;
                try {
                        for (EditionInfo info : schemaActions) {
                                sql = info.getSQL(getPKNames(), fieldNames,
                                        (DBReadWriteDriver) driver);
                                ((DBReadWriteDriver) driver).execute(con, sql);
                        }
                        for (DeleteEditionInfo info : deletedPKs) {
                                sql = info.getSQL(getPKNames(), fieldNames,
                                        (DBReadWriteDriver) driver);
                                ((DBReadWriteDriver) driver).execute(con, sql);
                        }
                        for (EditionInfo info : editionActions) {
                                sql = info.getSQL(getPKNames(), fieldNames,
                                        (DBReadWriteDriver) driver);
                                if (sql != null) {
                                        sql = info.getSQL(getPKNames(), fieldNames,
                                                (DBReadWriteDriver) driver);
                                        ((DBReadWriteDriver) driver).execute(con, sql);
                                }
                        }
                } catch (SQLException e) {
                        try {
                                ((DBReadWriteDriver) driver).rollBackTrans(getConnection());
                        } catch (SQLException e1) {
                                LOG.error("Error commiting", e);
                                throw new DriverException(I18N.getString(GDMS_DRIVER_ERROR_CONNECTION_OPEN), e1);
                        }
                        throw new DriverException(e.getMessage() + ":" + sql, e);
                }

                try {
                        ((DBReadWriteDriver) driver).commitTrans(getConnection());
                } catch (SQLException e) {
                        throw new DriverException(I18N.getString(GDMS_DRIVER_ERROR_CONNECTION_OPEN), e);
                }

                fireCommit(this);

                return true;
        }

        private int[] getPrimaryKeys() throws DriverException {
                if (cachedPKIndices == null) {
                        cachedPKIndices = MetadataUtilities.getPKIndices(getMetadata());
                }
                return cachedPKIndices;
        }

        private String getPKName(int fieldId) throws DriverException {
                int[] fieldsId = getPrimaryKeys();
                return getMetadata().getFieldName(fieldsId[fieldId]);
        }

        private int getPKCardinality() throws DriverException {
                return getPrimaryKeys().length;
        }

        @Override
        public void commitDone(String name) throws DriverException {
                sync();
        }

        @Override
        public void syncWithSource() throws DriverException {
                sync();
        }

        private void sync() throws DriverException {
                try {
                        driver.close(con);
                        con.close();
                        con = null;
                        con = getConnection();
                        driver.open(con, def.getTableName(), def.getSchemaName());
                } catch (SQLException e) {
                        throw new DriverException(I18N.getString("gdms.driver.error.connection.close"), e);
                }
        }

        @Override
        public void isCommiting(String name, Object source) {
        }
}
