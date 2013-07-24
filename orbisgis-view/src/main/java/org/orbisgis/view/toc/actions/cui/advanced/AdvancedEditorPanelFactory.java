package org.orbisgis.view.toc.actions.cui.advanced;

import org.orbisgis.core.renderer.se.SymbolizerNode;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * This factory is used to create the panels that can be used to
 * edit all the SymbolizerNode instances that can be found in a
 * rendering tree.
 * @author Alexis Guéganno
 */
public class AdvancedEditorPanelFactory {

    /**
     * Gets the {@link JPanel} used to edit {@code sn}.
     * @param sn The input {@link SymbolizerNode}.
     * @return The needed JPanel.
     */
    public JPanel getPanel(SymbolizerNode sn){
        JPanel ret = new JPanel();
        BoxLayout boxLayout = new BoxLayout(ret, BoxLayout.Y_AXIS);
        ret.setLayout(boxLayout);
        List<SymbolizerNode> children = sn.getChildren();
        for(SymbolizerNode child : children){
            ret.add(getComponent(child));
        }
        return ret;
    }

    private Component getComponent(SymbolizerNode sn){
        return new JLabel(sn.getName());
    }
}
