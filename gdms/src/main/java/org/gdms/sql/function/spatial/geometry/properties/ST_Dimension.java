/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 *
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV FR CNRS 2488
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.sql.function.spatial.geometry.properties;

import com.vividsolutions.jts.geom.Geometry;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.FunctionException;

/**
 * Return the geometry dimension (0 for [Multi]Point, 1 for
 * [Multi]LineString or LinearRing and 2 for [Multi]Polygon)
 */
public final class ST_Dimension extends AbstractSpatialPropertyFunction {

        @Override
        public Value evaluateResult(DataSourceFactory dsf, Value[] args) throws FunctionException {
                final Geometry g = args[0].getAsGeometry();
                return ValueFactory.createValue(g.getDimension());
        }

        @Override
        public String getName() {
                return "ST_Dimension";
        }

        @Override
        public Type getType(Type[] types) {
                return TypeFactory.createType(Type.INT);
        }

        @Override
        public String getDescription() {
                return "Return the geometry dimension (0 for [Multi]Point, 1 for "
                        + " [Multi]LineString or LinearRing and 2 for [Multi]Polygon)";
        }

        @Override
        public String getSqlOrder() {
                return "select ST_Dimension(the_geom) from myTable;";
        }
}
