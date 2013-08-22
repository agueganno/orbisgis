package org.orbisgis.view.toc.actions.cui.advanced;

import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.JButton;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import java.awt.event.ActionListener;
import java.beans.EventHandler;

/**
 * This JButton provides a method that is called when on user clicks. It changes
 * the selected node in the associated JTree
 * @author Alexis Guéganno
 */
public class GoToNodeButton extends JButton {
    private final AdvancedTreeModel model;
    private final JTree tree;
    private final SymbolizerNode child;
    private static final I18n I18N = I18nFactory.getI18n(GoToNodeButton.class);

    /**
     * Builds a new GoToNodeButton.
     * @param tr The associated JTree. Must have been built with and AdvancedTreeModel.
     * @param sn The destination node.
     */
    public GoToNodeButton(JTree tr, SymbolizerNode sn){
        super(OrbisGISIcon.getIcon("execute"));
        this.setToolTipText(I18N.tr("Go to this node"));
        model = (AdvancedTreeModel) tr.getModel();
        tree = tr;
        child = sn;
        ActionListener alGo = EventHandler.create(ActionListener.class, this, "goToNode");
        this.addActionListener(alGo);
    }

    /**
     * Jump to the node associated to this line.
     */
    public void goToNode(){
        NodeWrapper[] pathForNode = model.getPathForNode(child);
        TreePath tp = new TreePath(pathForNode);
        tree.expandPath(tp);
        tree.setSelectionPath(tp);
    }
}
