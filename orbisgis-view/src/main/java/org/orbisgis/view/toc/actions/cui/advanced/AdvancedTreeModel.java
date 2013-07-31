package org.orbisgis.view.toc.actions.cui.advanced;

import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.view.components.resourceTree.AbstractTreeModel;

import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;

/**
 * The tree model used in the advanced style editor. {@link SymbolizerNode} instances are
 * wrapped in {@link NodeWrapper} instances by this class.
 * @author Alexis Guéganno
 */
public class AdvancedTreeModel extends AbstractTreeModel {

    private final NodeWrapper wrappedStyle;

    /**
     * Builds a new {@code AdvancedTreeModel} instance associated to the given
     * tree and which root will be the given {@link Style}, wrapped in a
     * {@link NodeWrapper}.
     * @param tree The associated JTree.
     * @param style The original Style.
     */
    public AdvancedTreeModel(JTree tree, Style style){
        super(tree);
        wrappedStyle = new NodeWrapper(style);
    }

    @Override
    public Object getRoot() {
        return wrappedStyle;
    }

    @Override
    public Object getChild(Object o, int i) {
        if(o instanceof NodeWrapper){
            NodeWrapper node = (NodeWrapper) o;
            return new NodeWrapper(node.getNode().getChildren().get(i));
        }
        return null;
    }

    @Override
    public int getChildCount(Object o) {
        return ((NodeWrapper) o).getNode().getChildren().size();
    }

    @Override
    public boolean isLeaf(Object o) {
        return getChildCount(o)==0;
    }

    @Override
    public void valueForPathChanged(TreePath treePath, Object o) {
    }

    @Override
    public int getIndexOfChild(Object o, Object o2) {
        return ((NodeWrapper) o).getNode().getChildren().indexOf(((NodeWrapper) o2).getNode());
    }

    /**
     * Replace property named {@code property} in {@code par} with {@code ch}.
     * @param par The container node
     * @param ch The new value for {@code property}
     * @param property The name of the property.
     */
    public void setProperty(SymbolizerNode par, SymbolizerNode ch, String property){
        //now null, now not null, never null.
        SymbolizerNode prev = par.getProperty(property);
        int index;
        if(prev != null){
            index = par.getChildren().indexOf(prev);
            par.setProperty(property, ch);
            if(ch != null){
                TreeModelEvent tmc = new TreeModelEvent(this,
                        getPathForNode(par),
                        new int[]{index},
                        new Object[]{ch});
                //This is a replacement
                fireStructureChanged(tmc);
            } else {
                TreeModelEvent tmc = new TreeModelEvent(this,
                        getPathForNode(par),
                        new int[]{index},
                        new Object[]{prev});
                //This is a deletion
                fireNodeRemoved(tmc);
            }
        } else {
            //this is an insertion
            par.setProperty(property, ch);
            index = par.getChildren().indexOf(ch);
            TreeModelEvent tme = new TreeModelEvent(
                    this,
                    getPathForNode(par),
                    new int[]{index},
                    new Object[]{ch}
            );
            fireNodeInserted(tme);
        }
        fireEvent();
    }

    /**
     * Builds the path of NodeWrapper instances from the styling tree root to SymbolizerNode
     * @param sn The ending node of the path.
     * @return The path as an array of {@link NodeWrapper} instances.
     */
    public NodeWrapper[] getPathForNode(SymbolizerNode sn){
        List<SymbolizerNode> par = getParents(sn,new ArrayList<SymbolizerNode>());
        int s = par.size();
        NodeWrapper[] nws = new NodeWrapper[par.size()];
        for(int i=0; i<s; i++){
            nws[s-i-1] = new NodeWrapper(par.get(i));
        }
        return nws;
    }

    /**
     * Recursively retrieve the parents of sn and put them in {@code list}.
     * @param sn The child.
     * @param list The list we'll feed.
     * @return The parents path in a list.
     */
    public List<SymbolizerNode> getParents(SymbolizerNode sn, List<SymbolizerNode> list){
        list.add(sn);
        if(sn.getParent()==null){
            return list;
        } else {
            return getParents(sn.getParent(), list);
        }

    }
}
