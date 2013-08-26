package org.orbisgis.view.toc.actions.cui.components;

import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.data.types.TypeFactory;
import org.gdms.driver.DriverException;

/**
 * ComboBox built by gathering spatial fields from the input DataSource.
 * @author Alexis Guéganno
 */
public class SpatialFieldsComboBox extends AbsFieldsComboBox {

    private static final Logger LOGGER = Logger.getLogger(SpatialFieldsComboBox.class);

    /**
     * Constructor
     *
     * @param ds     DataSource
     * @param expected The value we want to be set after the creation of the ComboBox.
     */
    public SpatialFieldsComboBox(DataSource ds,
                                    String expected) {
        super(ds, expected);
    }

    @Override
    protected boolean canAddField(int index) {
        try {
            return TypeFactory.isSpatial(
                    ds.getMetadata().getFieldType(index).getTypeCode());
        } catch (DriverException ex) {
            LOGGER.error("Cannot add field at position " + index
                    + " to the NonSpatialFieldsComboBox because the metadata " +
                    "could not be recovered.");
            return false;
        }
    }
}
