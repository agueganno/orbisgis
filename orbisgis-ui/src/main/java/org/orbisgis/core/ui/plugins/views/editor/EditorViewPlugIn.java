/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.core.ui.plugins.views.editor;

import java.awt.Component;

import javax.swing.JMenuItem;
import javax.swing.JPanel;

import org.orbisgis.core.PersistenceException;
import org.orbisgis.core.Services;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.ViewPlugIn;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;
import org.orbisgis.utils.I18N;

public class EditorViewPlugIn extends ViewPlugIn {

	private EditorPanel panel;
	private JMenuItem menuItem;

	public EditorViewPlugIn() {

	}

	public Component getComponent() {
		return panel;
	}

	public void initialize(PlugInContext context) throws Exception {
		panel = new EditorPanel();
		Services.registerService(EditorManager.class,
				I18N.getString("org.orbisgis.org.ui.editorViewPlugIn.accessToActiveEditor"), //$NON-NLS-1$
				new DefaultEditorManager(panel));
		menuItem = context.getFeatureInstaller().addMainMenuItem(this,
				new String[] { Names.VIEW }, Names.EDITORS, true,
				OrbisGISIcon.EDITORS_ICON, null, new JPanel(), context);
	}

	public boolean execute(PlugInContext context) throws Exception {
		getPlugInContext().loadView(getId());
		return true;
	}

	public void saveStatus() {
		panel.saveAllDocuments();
	}

	@Override
	public void loadStatus() throws PersistenceException {
		System.out.println(EditorViewPlugIn.class.getCanonicalName());
	}

	public boolean isEnabled() {
		return true;
	}

	public boolean isSelected() {
		boolean isSelected = false;
		isSelected = getPlugInContext().viewIsOpen(getId());
		menuItem.setSelected(isSelected);
		return isSelected;
	}

	public String getName() {
		return I18N.getString("org.orbisgis.org.ui.editorViewPlugIn.editorView"); //$NON-NLS-1$
	}
}
