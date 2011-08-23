/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.driver.geotif;

import java.io.File;
import java.io.IOException;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeDefinition;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.FileReadWriteDriver;
import org.gdms.source.SourceManager;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.RasterMetadata;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.utils.FileUtils;

import com.vividsolutions.jts.geom.Envelope;
import fr.cts.crs.CoordinateReferenceSystem;
import org.apache.log4j.Logger;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.DefaultSchema;
import org.gdms.data.schema.MetadataUtilities;
import org.gdms.data.schema.Schema;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.ConstraintFactory;
import org.gdms.driver.AbstractDataSet;
import org.gdms.driver.DataSet;
import org.orbisgis.wkt.parser.PRJUtils;
import org.orbisgis.wkt.parser.ParseException;

public abstract class AbstractRasterDriver extends AbstractDataSet implements FileReadWriteDriver {

        protected GeoRaster geoRaster;
        protected RasterMetadata metadata;
        protected Schema schema;
        private DefaultMetadata gdmsMetadata;
        protected Envelope envelope;
        private static final Logger LOG = Logger.getLogger(AbstractRasterDriver.class);
        private int srid = -1;
        private File file;

        @Override
        public void open() throws DriverException {
                LOG.trace("Opening file");
                try {
                        geoRaster = GeoRasterFactory.createGeoRaster(file.getAbsolutePath());
                        geoRaster.open();
                        metadata = geoRaster.getMetadata();
                        envelope = metadata.getEnvelope();

                        // Check prjFile File prjFile =
                        File prj = FileUtils.getFileWithExtension(file, "prj");

                        if (prj != null && prj.exists()) {
                                try {
                                        // we have a prj!!
                                        CoordinateReferenceSystem c = PRJUtils.getCRSFromPRJ(prj);
                                        if (c.getAuthority() != null) {
                                                // let's set the SRID of this source
                                                srid = c.getAuthority().getCode();
                                        }
                                } catch (ParseException ex) {
                                }

                        }

                        gdmsMetadata.clear();
                        if (srid == -1) {
                                gdmsMetadata.addField("raster", TypeFactory.createType(Type.RASTER,
                                        ConstraintFactory.createConstraint(Constraint.RASTER_TYPE, geoRaster.getType())));
                        } else {
                                gdmsMetadata.addField("raster", TypeFactory.createType(Type.RASTER,
                                        ConstraintFactory.createConstraint(Constraint.RASTER_TYPE, geoRaster.getType()),
                                        ConstraintFactory.createConstraint(Constraint.SRID, srid)));
                        }

                } catch (IOException e) {
                        throw new DriverException("Cannot access the source: " + file, e);
                }
        }

        @Override
        public void setDataSourceFactory(DataSourceFactory dsf) {
        }

        @Override
        public void close() throws DriverException {
        }

        @Override
        public void createSource(String path, Metadata metadata,
                DataSourceFactory dataSourceFactory) throws DriverException {
                throw new UnsupportedOperationException("Cannot create an empty raster");
        }

        @Override
        public void copy(File in, File out) throws IOException {
                FileUtils.copy(in, out);
        }

        @Override
        public void writeFile(File file, DataSet dataSource, ProgressMonitor pm)
                throws DriverException {
                LOG.trace("Writing file");
                checkMetadata(dataSource.getMetadata());
                if (dataSource.getRowCount() == 0) {
                        throw new DriverException("Cannot store an empty raster");
                } else if (dataSource.getRowCount() > 1) {
                        throw new DriverException("Cannot store more than one raster");
                } else {
                        Value raster = dataSource.getFieldValue(0,
                                MetadataUtilities.getSpatialFieldIndex(dataSource.getMetadata()));
                        if (!raster.isNull()) {
                                try {
                                        raster.getAsRaster().save(file.getAbsolutePath());
                                } catch (IOException e) {
                                        throw new DriverException("Cannot write raster", e);
                                }
                        }
                }
        }

        @Override
        public void setFile(File file) {
                this.file = file;
                schema = new DefaultSchema(getTypeName() + file.getAbsolutePath().hashCode());
                gdmsMetadata = new DefaultMetadata();
                schema.addTable("main", gdmsMetadata);
        }

        @Override
        public TypeDefinition[] getTypesDefinitions() {
                return null;
        }

        protected void checkMetadata(Metadata metadata) throws DriverException {
                if (metadata.getFieldCount() != 1) {
                        throw new DriverException("This source only "
                                + "accepts an unique raster field");
                } else {
                        Type fieldType = metadata.getFieldType(0);
                        if (fieldType.getTypeCode() != Type.RASTER) {
                                throw new DriverException("Raster field expected");
                        }
                }
        }

        @Override
        public String validateMetadata(Metadata metadata) throws DriverException {
                if (metadata.getFieldCount() != 1) {
                        return "Cannot store more than one raster field";
                } else {
                        int typeCode = metadata.getFieldType(0).getTypeCode();
                        if (typeCode != Type.RASTER) {
                                return "Cannot store " + TypeFactory.getTypeName(typeCode);
                        }
                }
                return null;
        }

        @Override
        public int getType() {
                return SourceManager.RASTER | SourceManager.FILE;
        }

        @Override
        public boolean isCommitable() {
                return false;
        }

        @Override
        public Schema getSchema() throws DriverException {
                return schema;
        }

        @Override
        public DataSet getTable(String name) {
                if (!name.equals("main")) {
                        return null;
                }
                return this;
        }
        
        @Override
        public boolean isOpen() {
                // once .open() is called, the content is always accessible
                // thus the driver is always open.
                return true;
        }

        @Override
        public Value getFieldValue(long rowIndex, int fieldId) throws DriverException {
                if (fieldId == 0) {
                        return ValueFactory.createValue(geoRaster);
                } else {
                        throw new DriverException("No such field:" + fieldId);

                }
        }

        @Override
        public long getRowCount() throws DriverException {
                return 1;
        }

        @Override
        public Number[] getScope(int dimension) throws DriverException {
                switch (dimension) {
                        case DataSet.X:
                                return new Number[]{envelope.getMinX(), envelope.getMaxX()};
                        case DataSet.Y:
                                return new Number[]{envelope.getMinY(), envelope.getMaxY()};
                        default:
                                return null;
                }
        }

        @Override
        public Metadata getMetadata() throws DriverException {
                return schema.getTableByName("main");
        }
}
