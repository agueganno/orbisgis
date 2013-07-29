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
package org.orbisgis.core.renderer.se;

import java.util.List;
import org.orbisgis.core.renderer.se.visitors.ISymbolizerVisitor;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * SymbolizerNode allow to browse the styling tree. It's part of the visitor pattern
 * implementation of the symbolizing tree.
 *
 * @author Maxence Laurent
 */
public interface SymbolizerNode{

    public static final I18n I18N = I18nFactory.getI18n(SymbolizerNode.class);

    /**
     * get the parent of this current <code>SymbolizerNode</code>
     * @return The symbolizer node owning this one, if any.
     */
    SymbolizerNode getParent();

    /**
     * Set the parent of this <code>SymbolizerNode</code>
     * @param node The new parent of this node.
     */
    void setParent(SymbolizerNode node);

    /**
     * Notify the parent of the node that cached values must be unset.
     */
    void update();

    /**
     * Get all the {@code SymbolizerNode} instances that are direct children
     * of this.
     * @return The children of this node in a list.
     */
    List<SymbolizerNode> getChildren();

    /**
     * Accepts the visit of {@code visitor}.
     * @param visitor The visitor that want to inspect this node.
     */
    void acceptVisitor(ISymbolizerVisitor visitor);

    /**
     * Gets a name describing this node
     * @return A name describing this node.
     */
    String getName();

    /**
     * Gets the names of all the properties this node supports.
     * @return The names of the properties in a list of String.
     */
    List<String> getPropertyNames();

    /**
     * Gets the names of all the required properties this node needs.
     * @return The names of the required properties in a list of String.
     */
    List<String> getRequiredPropertyNames();

    /**
     * Gets the names of all the properties that can be null and that are
     * supported by this node.
     * @return The names of the optional properties in a list of String.
     */
    List<String> getOptionalPropertyNames();

    /**
     * Gets the properties associated to the given name stored in this
     * node.
     * @param name The name of the property.
     * @return The associated SymbolizerNode instances in a List. The
     * list may be empty if the property is optional.
     */
    SymbolizerNode getProperty(String name);

    /**
     * Replace the properties associated to prop with the given value.
     * @param prop The name of the property.
     * @param value The new list of properties. Shall not be null, shall not
     *              be empty if the property is required.
     * @throws IllegalArgumentException If value provide elements that can't be used for prop.
     */
    void setProperty(String prop, SymbolizerNode value);

    /**
     * Gets the extension point associated to the given property name.
     * @param name The property name
     * @return The highest level class that can be used for this property.
     */
    Class<? extends SymbolizerNode> getPropertyClass(String name);



}
