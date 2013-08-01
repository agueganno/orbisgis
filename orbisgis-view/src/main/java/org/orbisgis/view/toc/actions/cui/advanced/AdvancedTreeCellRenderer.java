package org.orbisgis.view.toc.actions.cui.advanced;

import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.view.components.renderers.TreeLaFRenderer;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * TreeCellRenderer for the advanced style editor.
 * @author Alexis Guéganno
 */
public class AdvancedTreeCellRenderer extends TreeLaFRenderer {

    private static final I18n I18N = I18nFactory.getI18n(AdvancedTreeCellRenderer.class);

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
            StringBuilder sb = new StringBuilder();
            SymbolizerNode sn = ((NodeWrapper)value).getNode();
            SymbolizerNode par = sn.getParent();
            if(par != null){
                List<String> props = par.getPropertyNames();
                for(String prop : props){
                    if(sn == par.getProperty(prop)){
                        sb.append(I18N.tr(prop));
                        sb.append(": ");
                    }
                }
            }
            sb.append(value.toString());
            ((JLabel)comp).setText(sb.toString());
        }
        return comp;
    }
}
