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
 * Copyright (C) 2009 Erwan BOCHER, Pierre-yves FADET
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
 *    Pierre-Yves.Fadet_at_ec-nantes.fr
 *    thomas.leduc _at_ cerma.archi.fr
 */

package org.orbisgis.core.ui.plugins.editors.tableEditor;

import java.util.Observable;

import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.ConstraintFactory;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.driver.DriverException;
import org.orbisgis.core.Services;
import org.orbisgis.core.errorManager.ErrorManager;
import org.orbisgis.core.images.IconNames;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.editors.table.TableEditableElement;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;
import org.orbisgis.core.ui.plugins.views.OutputManager;

public class ShowFieldInfoPlugIn extends AbstractPlugIn {

	private Integer selectedColumn;
	private boolean isVisible;

	public boolean execute(PlugInContext context) throws Exception {
		IEditor editor = context.getActiveEditor();
		final TableEditableElement element = (TableEditableElement) editor
				.getElement();
		try {
			Metadata metadata = element.getDataSource().getMetadata();
			OutputManager om = Services.getService(OutputManager.class);
			om.print("Field name:" + metadata.getFieldName(selectedColumn)
					+ "\n");
			Type type = metadata.getFieldType(selectedColumn);
			om.print("Field type:"
					+ TypeFactory.getTypeName(type.getTypeCode())
					+ "\nConstraints:\n");
			Constraint[] cons = type.getConstraints();
			for (Constraint constraint : cons) {
				om.print("  "
						+ ConstraintFactory.getConstraintName(constraint
								.getConstraintCode()) + ": "
						+ constraint.getConstraintHumanValue() + "\n");
			}

		} catch (DriverException e) {
			Services.getService(ErrorManager.class).error(
					"Cannot access field information", e);
		}
		return true;
	}

	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = (WorkbenchFrame) wbContext.getWorkbench()
				.getFrame().getTableEditor();
		context.getFeatureInstaller().addPopupMenuItem(frame, this,
				new String[] { Names.POPUP_TABLE_SHOWFIELD_PATH1 },
				Names.POPUP_TABLE_SHOWFIELD_GROUP, false,
				getIcon(IconNames.POPUP_TABLE_SHOWFIELD_ICON), wbContext);
	}

	public void update(Observable o, Object arg) {
		isVisible(arg);
	}

	public boolean isEnabled() {
		return true;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public boolean isVisible(Object arg) {
		if(getPlugInContext().getTableEditor() != null){
			try {
				selectedColumn = (Integer) arg;
			} catch (Exception e) {
				return isVisible = false;
			}
			return isVisible = true;
		}
		return isVisible = false;
	}

	@Override
	public boolean isSelected() {
		// TODO Auto-generated method stub
		return false;
	}
}
