package org.orbisgis.view.toc.actions.cui.advanced;

import org.orbisgis.view.components.renderers.TreeLaFRenderer;

import javax.swing.*;
import java.awt.*;

/**
 * TreeCellRenderer for the advanced style editor.
 * @author Alexis Guéganno
 */
public class AdvancedTreeCellRenderer extends TreeLaFRenderer {

    /**
     * Builds a new TreeCellRenderer that will be associated to {@code tree}.
     * @param tree The associated tree.
     */
    public AdvancedTreeCellRenderer(JTree tree) {
        super(tree);
    }
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected,
              boolean expanded, boolean leaf, int row, boolean hasFocus) {
        Component comp = lookAndFeelRenderer.getTreeCellRendererComponent(
                tree, value, selected, expanded, leaf, row, hasFocus);
        if (comp instanceof JLabel) {
            ((JLabel)comp).setText(value.toString());
        }
        return comp;
    }
}
