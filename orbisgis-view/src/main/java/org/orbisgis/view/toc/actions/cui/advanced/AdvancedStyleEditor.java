package org.orbisgis.view.toc.actions.cui.advanced;

import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.UIPanel;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.beans.EventHandler;
import java.net.URL;

/**
 * The advanced style editor. This editor intends to present the raw structure
 * of styles. It's up to the user to know how to build a style that match
 * cartography requirements.
 * @author Alexis Guéganno
 */
public class AdvancedStyleEditor extends JPanel implements UIPanel{

    private static final I18n I18N = I18nFactory.getI18n(AdvancedStyleEditor.class);
    private JPanel right;
    private JTree tree;
    private AdvancedEditorPanelFactory factory;
    private Style style;

    /**
     * Builds an advanced editor that will handle {@code st}.
     * @param st The {@link Style} handled by this instance of {@code AdvancedStyleEditor}.
     */
    public AdvancedStyleEditor(Style st){
        super(new BorderLayout());
        style = st;
        tree = new JTree();
        AdvancedTreeModel atm = new AdvancedTreeModel(tree, st);
        tree.setModel(atm);
        tree.setExpandsSelectedPaths(true);
        tree.setCellRenderer(new AdvancedTreeCellRenderer(tree));
        tree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.SINGLE_TREE_SELECTION);
        TreeSelectionListener tsl = EventHandler.create(TreeSelectionListener.class,this,"treeSelectionChanged");
        tree.addTreeSelectionListener(tsl);
        right = new JPanel();
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tree, right);
        split.setDividerLocation(260);
        Dimension minimumSize = new Dimension(100, 50);
        tree.setMinimumSize(minimumSize);
        add(split);
        this.setPreferredSize(new Dimension(640, 420));
        factory = new AdvancedEditorPanelFactory(tree);
    }

    /**
     * Called when the selected node of the inner tree change.
     */
    public void treeSelectionChanged(){
        TreePath sp = tree.getSelectionPath();
        if(sp!=null){
            Object last = sp.getLastPathComponent();
            JPanel pan = factory.getPanel(((NodeWrapper) last).getNode());
            right.removeAll();
            right.add(pan);
            right.repaint();
            right.revalidate();
        }
    }

    @Override
    public URL getIconURL() {
        return UIFactory.getDefaultIcon();
    }

    @Override
    public String getTitle() {
        return I18N.tr("Advanced editor for styles.");
    }

    @Override
    public String validateInput() {
        return null;
    }

    @Override
    public Component getComponent() {
        return this;
    }

    /**
     * Gets the style that is being edited.
     * @return The inner style.
     */
    public Style getStyle(){
        return style;
    }
}
