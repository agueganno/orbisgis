/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.view.toc.actions.cui.components;

import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.data.schema.Metadata;
import org.gdms.driver.DriverException;
import org.orbisgis.sif.components.WideComboBox;

/**
 * Root class for combo boxes containing field names.
 *
 * @author Adam Gouge
 */
public abstract class AbsFieldsComboBox extends WideComboBox {

    private static final Logger LOGGER = Logger.getLogger(AbsFieldsComboBox.class);

    protected DataSource ds;

    /**
     * Constructor that feeds the ComboBox with the fields of ds that match the condition
     * implemented in canAddField.
     *
     * @param ds     DataSource
     * @param current The value that must be selected at the beginning.
     */
    public AbsFieldsComboBox(DataSource ds, final String current) {
        super();
        if (ds == null) {
            throw new IllegalStateException("A FieldsComboBox requires " +
                    "a non-null DataSource.");
        }
        this.ds = ds;
        addFields();
        if (current != null && !current.isEmpty()) {
            setSelectedItem(current);
        }
    }

    /**
     * Add the fields.
     */
    private void addFields() {
        try {
            Metadata md = ds.getMetadata();
            int fc = md.getFieldCount();
            for (int i = 0; i < fc; i++) {
                if (canAddField(i)) {
                    addItem(md.getFieldName(i));
                }
            }
        } catch (DriverException ex) {
            LOGGER.error(ex);
        }
    }

    /**
     * Determine which kind of fields to add.
     * @return true if the field at position index in the inner DataSource
     *         should be added to the ComboBox.
     */
    protected abstract boolean canAddField(int index);
}
