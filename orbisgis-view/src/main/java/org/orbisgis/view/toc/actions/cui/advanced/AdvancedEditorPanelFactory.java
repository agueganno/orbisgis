package org.orbisgis.view.toc.actions.cui.advanced;

import org.apache.log4j.Logger;
import org.orbisgis.core.renderer.se.*;
import org.orbisgis.core.renderer.se.common.Halo;
import org.orbisgis.core.renderer.se.common.OnlineResource;
import org.orbisgis.core.renderer.se.common.VariableOnlineResource;
import org.orbisgis.core.renderer.se.fill.*;
import org.orbisgis.core.renderer.se.graphic.*;
import org.orbisgis.core.renderer.se.label.*;
import org.orbisgis.core.renderer.se.parameter.color.*;
import org.orbisgis.core.renderer.se.parameter.geometry.GeometryAttribute;
import org.orbisgis.core.renderer.se.parameter.real.*;
import org.orbisgis.core.renderer.se.parameter.string.*;
import org.orbisgis.core.renderer.se.stroke.AlternativeStrokeElements;
import org.orbisgis.core.renderer.se.stroke.CompoundStroke;
import org.orbisgis.core.renderer.se.stroke.CompoundStrokeElement;
import org.orbisgis.core.renderer.se.stroke.GraphicStroke;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.core.renderer.se.stroke.Stroke;
import org.orbisgis.core.renderer.se.stroke.TextStroke;
import org.orbisgis.core.renderer.se.transform.*;
import org.orbisgis.sif.common.ContainerItem;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This factory is used to create the panels that can be used to
 * edit all the SymbolizerNode instances that can be found in a
 * rendering tree.
 * @author Alexis Guéganno
 */
public class AdvancedEditorPanelFactory {

    private static final I18n I18N = I18nFactory.getI18n(AdvancedEditorPanelFactory.class);
    private static final Logger LOGGER = Logger.getLogger(AdvancedEditorPanelFactory.class);
    private JTree tree;

    public AdvancedEditorPanelFactory(JTree link){
        tree = link;
    }

    /**
     * Gets the {@link JPanel} used to edit {@code sn}.
     * @param sn The input {@link SymbolizerNode}.
     * @return The needed JPanel.
     */
    public JPanel getPanel(SymbolizerNode sn){
        List<JPanel> req = getCombos(sn);
        JPanel ret = new JPanel();
        BoxLayout bl = new BoxLayout(ret, BoxLayout.Y_AXIS);
        ret.setLayout(bl);
        for(JPanel cb : req){
            ret.add(cb);
        }
        return ret;
    }

    private List<JPanel> getCombos(SymbolizerNode sn) {
        List<String> req = sn.getPropertyNames();
        List<JPanel> ret = new LinkedList<JPanel>();
        for(int i=0; i<req.size(); i++){
            String name = req.get(i);
            JComboBox combo = getComboForProperty(sn, name);
            JPanel jp = new JPanel();
            jp.add(new JLabel(I18N.tr(name)));
            jp.add(combo);
            ret.add(jp);
        }
        return ret;
    }

    private JComboBox getComboForProperty(SymbolizerNode sn, String name) {
        Class<? extends SymbolizerNode> propertyClass = sn.getPropertyClass(name);
        List<ContainerItem<Class<? extends SymbolizerNode>>> cips =
                new ArrayList<ContainerItem<Class<? extends SymbolizerNode>>>();
        if(propertyClass.equals(Rule.class)){
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(Rule.class, I18N.tr("Rule")));
        } else if(propertyClass.equals(Symbolizer.class)){
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(AreaSymbolizer.class, I18N.tr("Area Symbolizer")));
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(LineSymbolizer.class, I18N.tr("Line Symbolizer")));
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(PointSymbolizer.class, I18N.tr("Point Symbolizer")));
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(TextSymbolizer.class, I18N.tr("Text Symbolizer")));
        } else if(propertyClass.equals(Halo.class)){
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(Halo.class, I18N.tr("Halo")));
        } else if(propertyClass.equals(OnlineResource.class)){
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(OnlineResource.class, I18N.tr("Online Resource")));
        } else if(propertyClass.equals(VariableOnlineResource.class)){
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(VariableOnlineResource.class, I18N.tr("Variable Online Resource")));
        } else if(propertyClass.equals(Fill.class)){
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(SolidFill.class, I18N.tr("Solid Fill")));
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(HatchedFill.class, I18N.tr("Hatched Fill")));
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(GraphicFill.class, I18N.tr("Graphic Fill")));
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(DotMapFill.class, I18N.tr("Dot Map Fill")));
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(DensityFill.class, I18N.tr("Density Fill")));
        } else if(propertyClass.equals(Graphic.class)){
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(MarkGraphic.class, I18N.tr("Mark Graphic")));
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(ExternalGraphic.class, I18N.tr("External Graphic")));
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(PointTextGraphic.class, I18N.tr("Point Text Graphic")));
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(PieChart.class, I18N.tr("Pie Chart")));
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(AxisChart.class, I18N.tr("Axis Chart")));
        } else if(propertyClass.equals(AxisScale.class)){
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(AxisScale.class, I18N.tr("Axis Scale")));
        } else if(propertyClass.equals(Category.class)){
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(Category.class, I18N.tr("Category")));
        } else if(propertyClass.equals(GraphicCollection.class)){
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(GraphicCollection.class, I18N.tr("Graphic Collection")));
        } else if(propertyClass.equals(Slice.class)){
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(Slice.class, I18N.tr("Slice")));
        } else if(propertyClass.equals(ViewBox.class)){
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(ViewBox.class, I18N.tr("View Box")));
        } else if(propertyClass.equals(ExclusionZone.class)){
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(ExclusionRectangle.class, I18N.tr("Exclusion Rectangle")));
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(ExclusionRadius.class, I18N.tr("Exclusion Radius")));
        } else if(propertyClass.equals(Label.class)){
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(LineLabel.class, I18N.tr("Line Label")));
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(PointLabel.class, I18N.tr("Point Label")));
        } else if(propertyClass.equals(StyledText.class)){
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(StyledText.class, I18N.tr("Styled Text")));
        } else if(propertyClass.equals(Stroke.class)){
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(PenStroke.class, I18N.tr("Pen Stroke")));
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(TextStroke.class, I18N.tr("Text Stroke")));
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(CompoundStroke.class, I18N.tr("Compound Stroke")));
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(GraphicStroke.class, I18N.tr("Graphic Stroke")));
        } else if(propertyClass.equals(CompoundStroke.class)){
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(CompoundStrokeElement.class,
                    I18N.tr("Compound Stroke Element")));
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(AlternativeStrokeElements.class,
                    I18N.tr("Alternative Stroke Elements")));
        } else if(propertyClass.equals(Transformation.class)){
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(Translate.class, I18N.tr("Translate")));
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(Rotate.class, I18N.tr("Rotate")));
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(Scale.class, I18N.tr("Scale")));
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(Matrix.class, I18N.tr("Matrix")));
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(Transform.class, I18N.tr("Transform")));
        } else if(propertyClass.equals(RealParameter.class)){
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(RealLiteral.class, I18N.tr("Literal")));
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(Recode2Real.class, I18N.tr("Recode")));
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(Categorize2Real.class, I18N.tr("Categorize")));
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(Interpolate2Real.class, I18N.tr("Interpolation")));
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(RealFunction.class, I18N.tr("Function")));
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(RealAttribute.class, I18N.tr("Attribute")));
        } else if(propertyClass.equals(StringParameter.class)){
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(StringLiteral.class, I18N.tr("Literal")));
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(Recode2String.class, I18N.tr("Recode")));
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(Categorize2String.class, I18N.tr("Categorize")));
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(Number2String.class, I18N.tr("Number2String")));
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(StringConcatenate.class, I18N.tr("Concatenate")));
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(StringAttribute.class, I18N.tr("Attribute")));
        } else if(propertyClass.equals(ColorParameter.class)){
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(ColorLiteral.class, I18N.tr("Literal")));
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(Recode2Color.class, I18N.tr("Recode")));
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(Categorize2Color.class, I18N.tr("Categorize")));
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(Interpolate2Color.class, I18N.tr("Interpolation")));
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(ColorAttribute.class, I18N.tr("Attribute")));
        } else if(propertyClass.equals(PenStroke.class)){
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(Rule.class, I18N.tr("PenStroke")));
        } else if(propertyClass.equals(Translate.class)){
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(Translate.class, I18N.tr("Translate")));
        } else if(propertyClass.equals(GeometryAttribute.class)){
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(GeometryAttribute.class, I18N.tr("Geometry Attribute")));
        } else if(propertyClass.equals(Rule.class)){
            cips.add(new ContainerItem<Class<? extends SymbolizerNode>>(Rule.class, I18N.tr("Rule")));
        }
        JComboBox ret = new JComboBox();
        int ind = -1;
        int c = 0;
        SymbolizerNode property = sn.getProperty(name);
        for(ContainerItem<Class<? extends SymbolizerNode>> ci : cips){
            ret.addItem(ci);
            if(property != null && property.getClass().equals(ci.getKey())){
                ind = c;
            }
            c++;
        }
        if(ind>-1){
            ret.setSelectedIndex(ind);
        }
        ActionListener l = new ChildListener(sn, name);
        ret.addActionListener(l);
        return ret;
    }

    public class ChildListener implements ActionListener{
        private final String property;
        private SymbolizerNode parent;

        public ChildListener(SymbolizerNode parent, String property){
            this.parent = parent;
            this.property = property;
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            ContainerItem<Class<? extends SymbolizerNode>> ci =
                    (ContainerItem<Class<? extends SymbolizerNode>>) ((JComboBox)actionEvent.getSource()).getSelectedItem();
            Class<? extends SymbolizerNode> key = ci.getKey();
            try {
                Constructor<? extends SymbolizerNode> c = key.getConstructor();
                SymbolizerNode created = c.newInstance();
                parent.setProperty(property, created);
            } catch (NoSuchMethodException e) {
                LOGGER.error("Can't' find a default constructor for type "+key);
            } catch (InvocationTargetException e) {
                LOGGER.error("Can't' find use the default constructor for type " + key, e);
            } catch (InstantiationException e) {
                LOGGER.error("Can't' find use the default constructor for type " + key, e);
            } catch (IllegalAccessException e) {
                LOGGER.error("Can't' find use the default constructor for type " + key, e);
            }
        }
    }
}
