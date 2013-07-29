package org.orbisgis.core.renderer.se;

import java.util.Collection;
import java.util.List;

/**
 * This interface offers a way to manage collection of properties that are
 * collection of nodes like we handle single properties in SymbolizerNode.
 * @author Alexis Guéganno
 */
public interface PropertiesCollectionNode {

    /**
     * Gets the names of properties that gather collections of SymbolizerNode.
     * @return The names of the properties in a list.
     */
    List<String> getPropertiesNames();

    /**
     * Get the properties associated to name.
     * @param name The name of the collection of properties.
     * @return The properties
     */
    Collection<SymbolizerNode> getProperties(String name);

    /**
     * Set the collection of properties associated to name.
     * @param name The name of the collection we want to set
     * @param properties The new set of properties.
     * @throws IllegalArgumentException if {@code properties} is empty or if at least one of the given
     *          {@code SymbolizerNode} can't be used for the collection associated to {@code name}.
     */
    void setProperties(String name, Collection<SymbolizerNode> properties);

    /**
     * Gets the expected class for nodes stored in the collection associated to {@code name}.
     * @param name The name of the collection
     * @return The type of the data stored in the collection associated to {@code name}.
     */
    Class<? extends SymbolizerNode> getPropertiesClass(String name);
}
