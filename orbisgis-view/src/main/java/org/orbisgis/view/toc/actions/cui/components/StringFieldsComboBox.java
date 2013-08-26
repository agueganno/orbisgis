package org.orbisgis.view.toc.actions.cui.components;

import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;

/**
 * ComboBox that gathers fields from a DataSource that are of type String.
 * @author Alexis Guéganno
 */
public class StringFieldsComboBox extends AbsFieldsComboBox {

    private static final Logger LOGGER = Logger.getLogger(StringFieldsComboBox.class);

    /**
     * Constructor
     *
     * @param ds     DataSource
     * @param expected The value we want to be set after the creation of the ComboBox.
     */
    public StringFieldsComboBox(DataSource ds,
                                 String expected) {
        super(ds, expected);
    }

    @Override
    protected boolean canAddField(int index) {
        try {
            int t = ds.getMetadata().getFieldType(index).getTypeCode();
            return t == Type.STRING;
        } catch (DriverException ex) {
            LOGGER.error("Cannot add field at position " + index
                    + " to the NonSpatialFieldsComboBox because the metadata " +
                    "could not be recovered.");
            return false;
        }
    }
}
