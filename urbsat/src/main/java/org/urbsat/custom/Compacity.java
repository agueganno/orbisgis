package org.urbsat.custom;

import java.util.Iterator;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.FreeingResourcesException;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.edition.PhysicalDirection;
import org.gdms.data.indexes.IndexQuery;
import org.gdms.data.indexes.SpatialIndexQuery;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.spatial.GeometryValue;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.gdms.sql.customQuery.CustomQuery;

import com.hardcode.driverManager.DriverLoadException;
import com.vividsolutions.jts.geom.Geometry;
/**
 * return the average compacity of the geometry witch intersect the grid, for each cell.
 * more the result is near 1, more the geometry is compact
 * @author thebaud
 *On calcule le rapport Rb entre la surface du batiment et son p�rimetre, 
 *puis le perimetre du cercle dont la surface est �gale a la surface de 
 *ce batiment, on r�alise �galement le rapport Rc entre la surface et le 
 *p�rimetre du cercle et enfin la compacit� est donn�e par le rapport entre Rc et Rb. 
 *Plus le rapport est proche de 1, plus le batiment a une forme compacte
 */
public class Compacity implements CustomQuery {

	public DataSource evaluate(DataSourceFactory dsf, DataSource[] tables, Value[] values)
			throws ExecutionException {

		if (tables.length != 2)
			throw new ExecutionException(
					"Compacity only operates on two tables");
		if (values.length != 2)
			throw new ExecutionException(
					"Compacity only operates with two values");

		DataSource resultDs = null;
		try {
			final ObjectMemoryDriver driver = new ObjectMemoryDriver(
					new String[] { "index", "Compacity" }, new Type[] {
							TypeFactory.createType(Type.INT),
							TypeFactory.createType(Type.DOUBLE) });

			resultDs = dsf.getDataSource(driver);
			resultDs.open();
			SpatialDataSourceDecorator parcels = new SpatialDataSourceDecorator(
					tables[0]);
			SpatialDataSourceDecorator grid = new SpatialDataSourceDecorator(tables[1]);
			String parcelFieldName = values[0].toString();
			String gridFieldName = values[1].toString();
			grid.open();
			parcels.open();
			grid.setDefaultGeometry(gridFieldName);

			for (int i = 0; i < grid.getRowCount(); i++) {
				Geometry cell = grid.getGeometry(i);
				Value k = grid.getFieldValue(i, 1);
				IndexQuery query = new SpatialIndexQuery(cell
						.getEnvelopeInternal(), parcelFieldName);
				Iterator<PhysicalDirection> iterator = parcels
						.queryIndex(query);
				
				double number = 0;
				double totcomp=0;
				while (iterator.hasNext()) {
					
					PhysicalDirection dir = (PhysicalDirection) iterator.next();
					Value geom = dir.getFieldValue(parcels
							.getFieldIndexByName(parcelFieldName));
					Geometry g = ((GeometryValue) geom).getGeom();
					if (g.intersects(cell)) {
					double Rb = 0;
					double Rc = 0;
					Rb=g.getArea()/g.getLength();
					double ray= Math.sqrt((g.getLength())/Math.PI);
					double per = ray*Math.PI*2;
					Rc=g.getArea()/per;
					double comp=Rc/Rb;
					totcomp+=comp;
					number++;
					}
				}
				resultDs.insertFilledRow(new Value[]{k,
						ValueFactory.createValue(totcomp/number)});
			}

			resultDs.commit();
			grid.cancel();
			parcels.cancel();
		} catch (DriverException e) {
			e.printStackTrace();
		} catch (InvalidTypeException e) {
			e.printStackTrace();
		} catch (DriverLoadException e) {
			e.printStackTrace();
		} catch (DataSourceCreationException e) {
			e.printStackTrace();
		} catch (FreeingResourcesException e) {
			e.printStackTrace();
		} catch (NonEditableDataSourceException e) {
			e.printStackTrace();
		}
		return resultDs;
		// call COMPACITY from landcover2000, gdms1182439943162 values ('the_geom', 'the_geom');

	}

	public String getName() {
		return "COMPACITY";
	}
}