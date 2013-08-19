package org.orbisgis.view.toc.actions.cui.advanced;

import net.miginfocom.swing.MigLayout;
import org.orbisgis.core.renderer.se.PropertiesCollectionNode;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.sif.common.ContainerItem;
import org.orbisgis.view.icons.OrbisGISIcon;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.util.Collection;
import java.util.List;

/**
 * This JPanel intends to display the nodes contained in the collection
 * associated to a property of a {@link PropertiesCollectionNode}. It displays
 * them one per line. Updates here have an effect in the associated model.
 * @author Alexis Guéganno
 */
public class PropertiesPanel extends JPanel {
    private final JTree tree;
    private PropertiesCollectionNode parent;
    private String property;
    private AdvancedEditorPanelFactory factory;
    private final AdvancedTreeModel model;

    /**
     * We need to link this object to a parent node and its property, a factory used
     * to build some inner components and a tree model representing the global
     * styling tree.
     * @param parent The parent node.
     * @param property The property we want to manage
     * @param factory The factory that builds UI for SymbolizerNodes.
     * @param tree The JTree we're working with.
     */
    public PropertiesPanel(PropertiesCollectionNode parent,
                           String property,
                           AdvancedEditorPanelFactory factory,
                           JTree tree){
        super(new MigLayout("wrap 4"));
        this.parent = parent;
        this.property = property;
        this.factory = factory;
        this.model = (AdvancedTreeModel) tree.getModel();
        this.tree = tree;
        refill();
    }

    /**
     * Empties and fill the whole panel.
     */
    //TODO : find a more efficient way to do that.
    public void refill(){
        this.removeAll();
        Collection<SymbolizerNode> properties = parent.getProperties(property);
        for(SymbolizerNode sn : properties){
            PropertiesLine pl = new PropertiesLine(sn);
            add(pl.getAdd());
            add(pl.getRemove());
            add(pl.getCombo());
            add(pl.getGoTo());
        }
        this.revalidate();
    }

    /**
     * This class gathers the components that will be put in a line of the
     * enclosing panel. It creates and adds listeners to the needed components.
     */
    public class PropertiesLine{
        private final SymbolizerNode child;
        private JButton add;
        private JButton remove;
        private JComboBox combo;
        private JButton go;

        /**
         * Builds a PropertiesLine associated to the given SymbolizerNode.
         * @param child The associated SymbolizerNode.
         */
        public PropertiesLine(SymbolizerNode child){
            this.child = child;
            add = new JButton(OrbisGISIcon.getIcon("add"));
            remove = new JButton(OrbisGISIcon.getIcon("delete"));
            combo = factory.getComboForClass(parent.getPropertiesClass(property), false, this.child);
            go = new JButton(OrbisGISIcon.getIcon("execute"));
            ActionListener alAdd = EventHandler.create(ActionListener.class, this, "add");
            add.addActionListener(alAdd);
            ActionListener alRemove = EventHandler.create(ActionListener.class, this, "remove");
            remove.addActionListener(alRemove);
            ActionListener alCombo = EventHandler.create(ActionListener.class, this, "replaceChild","source.selectedItem");
            combo.addActionListener(alCombo);
            ActionListener alGo = EventHandler.create(ActionListener.class, this, "goToNode");
            go.addActionListener(alGo);
        }

        /**
         * Called when the user clicks on the add button.
         */
        public void add(){
            List<SymbolizerNode> props = parent.getProperties(property);
            int index = props.indexOf(child);
            ContainerItem<Class<? extends SymbolizerNode>> def =
                    (ContainerItem<Class<? extends SymbolizerNode>>)
                            factory.getComboForClass(parent.getPropertiesClass(property), false, null).getSelectedItem();
            SymbolizerNode brother = factory.getInstance(def.getKey());
            model.addPropertyInCollection(parent, property, brother,index+1);
            refill();
        }


        /**
         * Called when the user clicks on the remove button.
         */
        public void remove(){
            List<SymbolizerNode> props = parent.getProperties(property);
            int index = props.indexOf(child);
            model.removePropertyFromCollection(parent, property, index);
            refill();

        }

        /**
         * Gets the add button
         * @return The add button.
         */
        public JButton getAdd(){
            return add;
        }

        /**
         * Gets the remove button.
         * @return The remove button.
         */
        public JButton getRemove(){
            return remove;
        }

        /**
         * Gets the combo to change the type of the object.
         * @return The combo.
         */
        public JComboBox getCombo(){
            return combo;
        }

        /**
         * Gets the "go to" button.
         * @return The go to button
         */
        public JButton getGoTo(){
            return go;
        }

        /**
         * Called when the user changes selection in the combo box.
         */
        public void replaceChild(ContainerItem ci){
            List<SymbolizerNode> props = parent.getProperties(property);
            int index = props.indexOf(child);
            Object key = ci.getKey();
            if(key instanceof String){
                model.setProperty(parent, null, property);
            } else {
                Class<? extends SymbolizerNode> ke = (Class<? extends SymbolizerNode>)key;
                SymbolizerNode inst = factory.getInstance(ke);
                if(inst != null){
                    model.setPropertyInCollection(parent, inst, property, index);
                }
            }
        }

        public void goToNode(){
            NodeWrapper[] pathForNode = model.getPathForNode(child);
            TreePath tp = new TreePath(pathForNode);
            tree.expandPath(tp);
            tree.setSelectionPath(tp);
            tree.makeVisible(tp);
        }
    }

}
