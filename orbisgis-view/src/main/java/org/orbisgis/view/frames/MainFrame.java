/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 *
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
 * info _at_ orbisgis.org
 */

package org.orbisgis.view.frames;

import java.util.Locale;
import javax.swing.JFrame;
import org.orbisgis.utils.I18N;
import org.orbisgis.view.icons.OrbisGISIcon;

/**
 * Host of the {@link DockStation}s, this frame contain 
 * all other dockable frames
 */
public class MainFrame extends JFrame {
        
        /**
	 * Creates a new frame. The content of the frame is not created by
	 * this constructor, clients must call {@link #setup(Core)}.
	 */
	public MainFrame(){
		setTitle( "OrbisGIS "
                        + I18N.getString("orbisgis.org.orbisgis.version") + " - " + I18N.getString("orbisgis.org.orbisgis.versionName") + " - " + Locale.getDefault().getCountry() );
		setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
		setIconImage(OrbisGISIcon.getIconImage("mini_orbisgis"));                
	}
}