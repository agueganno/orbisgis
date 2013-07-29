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
package org.orbisgis.core.renderer.se.stroke;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.opengis.se._2_0.core.AlternativeStrokeElementsType;
import net.opengis.se._2_0.core.StrokeElementType;
import org.orbisgis.core.renderer.se.PropertiesCollectionNode;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.xnap.commons.i18n.I18n;

/**
 * {@code AlternativeStrokeElements} provides the option for the rendering system to 
 * choose from different {@code StrokeElement} definitions.</p>
 * <p>Instances of this class contains a list of {@code StrokeElement} that is used
 * to provide the said options.
 * @author Maxence Laurent
 */
public class AlternativeStrokeElements extends CompoundStrokeElement implements PropertiesCollectionNode {
        public static final String ELEMENTS = I18n.marktr("Elements");

        private List<StrokeElement> elements;

        /**
         * Buld a new, empty, {@code AlternativeStrokeElement}
         */
        public AlternativeStrokeElements() {
                elements = new ArrayList<StrokeElement>();
        }

        /**
         * Build a new {@code AlternativeStrokeElement}, using the information
         * stored in the JaXB {@code AlternativeStrokeElementType}.
         * @param as The input JaXB object
         * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
         */
        public AlternativeStrokeElements(AlternativeStrokeElementsType as) throws InvalidStyle {
                this();
                for (StrokeElementType se : as.getStrokeElement()) {
                        StrokeElement strokeElement = new StrokeElement(se);
                        strokeElement.setParent(this);
                        elements.add(strokeElement);
                }
                if (elements.isEmpty()) {
                        throw new InvalidStyle("Alternative Stroke Element must, at least, contains one stroke element");
                }
        }

        @Override
        public Object getJAXBType() {
                AlternativeStrokeElementsType aset = new AlternativeStrokeElementsType();

                List<StrokeElementType> strokeElement = aset.getStrokeElement();

                for (StrokeElement elem : this.elements) {
                        strokeElement.add((StrokeElementType) elem.getJAXBType());
                }

                return aset;
        }

        @Override
        public List<SymbolizerNode> getChildren() {
                List<SymbolizerNode> ls = new ArrayList<SymbolizerNode>();
                ls.addAll(elements);
                return ls;
        }

        /**
         * Get the elements registered in this {@code AlternativeStrokeElement}.
         * @return 
         * A list of {@link StrokeElement}.
         */
        public List<StrokeElement> getElements() {
                return elements;
        }

        /**
         * Set the elements registered in this {@code AlternativeStrokeElement}.
         * @param elements  The new elements of this alternative.
         */
        public void setElements(ArrayList<StrokeElement> elements) {
                this.elements = elements;
        }

        @Override
        public List<String> getRequiredPropertyNames() {
            return new ArrayList<String>();
        }

        @Override
        public List<String> getOptionalPropertyNames() {
            return new ArrayList<String>();
        }

        @Override
        public SymbolizerNode getProperty(String name) {
            return null;
        }

        @Override
        public void setProperty(String prop, SymbolizerNode value) {
        }

        @Override
        public void setProperties(String prop, Collection<SymbolizerNode> value) {
            if(ELEMENTS.equals(prop)){
                ArrayList<StrokeElement> elements = new ArrayList<StrokeElement>();
                for(SymbolizerNode sn : value){
                    elements.add((StrokeElement) sn);
                }
                setElements(elements);
            }
        }

        @Override
        public Class<? extends SymbolizerNode> getPropertyClass(String name) {
            return CompoundStrokeElement.class;
        }

        @Override
        public List<String> getPropertiesNames() {
            List<String> ret = new ArrayList<String>();
            ret.add(ELEMENTS);
            return ret;
        }

        @Override
        public Collection<SymbolizerNode> getProperties(String name) {
            if(ELEMENTS.equals(name)){
                return new ArrayList<SymbolizerNode>(getElements());
            }
            return null;
        }

        @Override
        public Class<? extends SymbolizerNode> getPropertiesClass(String name) {
            if(ELEMENTS.equals(name)){
                return StrokeElement.class;
            }
            return null;
        }
}
