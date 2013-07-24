package org.orbisgis.view.toc.actions.cui.advanced;

import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.view.components.resourceTree.AbstractTreeModel;

import javax.swing.*;
import javax.swing.tree.TreePath;

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
     * @param tree
     * @param sym
     */
    public AdvancedTreeModel(JTree tree, Style sym){
        super(tree);
        wrappedStyle = new NodeWrapper(sym);
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
}
