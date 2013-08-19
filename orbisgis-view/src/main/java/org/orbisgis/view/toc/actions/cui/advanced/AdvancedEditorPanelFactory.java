package org.orbisgis.view.toc.actions.cui.advanced;

import net.miginfocom.swing.MigLayout;
import org.apache.log4j.Logger;
import org.orbisgis.core.renderer.se.AreaSymbolizer;
import org.orbisgis.core.renderer.se.LineSymbolizer;
import org.orbisgis.core.renderer.se.PointSymbolizer;
import org.orbisgis.core.renderer.se.PropertiesCollectionNode;
import org.orbisgis.core.renderer.se.Rule;
import org.orbisgis.core.renderer.se.Symbolizer;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.TextSymbolizer;
import org.orbisgis.core.renderer.se.UomNode;
import org.orbisgis.core.renderer.se.common.Halo;
import org.orbisgis.core.renderer.se.common.OnlineResource;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.common.VariableOnlineResource;
import org.orbisgis.core.renderer.se.fill.DensityFill;
import org.orbisgis.core.renderer.se.fill.DotMapFill;
import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.fill.GraphicFill;
import org.orbisgis.core.renderer.se.fill.HatchedFill;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.graphic.AxisChart;
import org.orbisgis.core.renderer.se.graphic.AxisScale;
import org.orbisgis.core.renderer.se.graphic.Category;
import org.orbisgis.core.renderer.se.graphic.ExternalGraphic;
import org.orbisgis.core.renderer.se.graphic.Graphic;
import org.orbisgis.core.renderer.se.graphic.GraphicCollection;
import org.orbisgis.core.renderer.se.graphic.MarkGraphic;
import org.orbisgis.core.renderer.se.graphic.PieChart;
import org.orbisgis.core.renderer.se.graphic.PointTextGraphic;
import org.orbisgis.core.renderer.se.graphic.Slice;
import org.orbisgis.core.renderer.se.graphic.ViewBox;
import org.orbisgis.core.renderer.se.label.ExclusionRadius;
import org.orbisgis.core.renderer.se.label.ExclusionRectangle;
import org.orbisgis.core.renderer.se.label.ExclusionZone;
import org.orbisgis.core.renderer.se.label.Label;
import org.orbisgis.core.renderer.se.label.LineLabel;
import org.orbisgis.core.renderer.se.label.PointLabel;
import org.orbisgis.core.renderer.se.label.StyledText;
import org.orbisgis.core.renderer.se.parameter.Literal;
import org.orbisgis.core.renderer.se.parameter.color.Categorize2Color;
import org.orbisgis.core.renderer.se.parameter.color.ColorAttribute;
import org.orbisgis.core.renderer.se.parameter.color.ColorLiteral;
import org.orbisgis.core.renderer.se.parameter.color.ColorParameter;
import org.orbisgis.core.renderer.se.parameter.color.Interpolate2Color;
import org.orbisgis.core.renderer.se.parameter.color.Recode2Color;
import org.orbisgis.core.renderer.se.parameter.geometry.GeometryAttribute;
import org.orbisgis.core.renderer.se.parameter.real.Categorize2Real;
import org.orbisgis.core.renderer.se.parameter.real.Interpolate2Real;
import org.orbisgis.core.renderer.se.parameter.real.RealAttribute;
import org.orbisgis.core.renderer.se.parameter.real.RealFunction;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.Recode2Real;
import org.orbisgis.core.renderer.se.parameter.string.Categorize2String;
import org.orbisgis.core.renderer.se.parameter.string.Number2String;
import org.orbisgis.core.renderer.se.parameter.string.Recode2String;
import org.orbisgis.core.renderer.se.parameter.string.StringAttribute;
import org.orbisgis.core.renderer.se.parameter.string.StringConcatenate;
import org.orbisgis.core.renderer.se.parameter.string.StringLiteral;
import org.orbisgis.core.renderer.se.parameter.string.StringParameter;
import org.orbisgis.core.renderer.se.stroke.AlternativeStrokeElements;
import org.orbisgis.core.renderer.se.stroke.CompoundStroke;
import org.orbisgis.core.renderer.se.stroke.CompoundStrokeElement;
import org.orbisgis.core.renderer.se.stroke.GraphicStroke;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.core.renderer.se.stroke.Stroke;
import org.orbisgis.core.renderer.se.stroke.TextStroke;
import org.orbisgis.core.renderer.se.transform.Matrix;
import org.orbisgis.core.renderer.se.transform.Rotate;
import org.orbisgis.core.renderer.se.transform.Scale;
import org.orbisgis.core.renderer.se.transform.Transform;
import org.orbisgis.core.renderer.se.transform.Transformation;
import org.orbisgis.core.renderer.se.transform.Translate;
import org.orbisgis.sif.common.ContainerItem;
import org.orbisgis.sif.common.ContainerItemProperties;
import org.orbisgis.sif.components.WideComboBox;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.beans.EventHandler;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * This factory is used to create the panels that can be used to
 * edit all the SymbolizerNode instances that can be found in a
 * rendering tree.
 * @author Alexis Guéganno
 */
public class AdvancedEditorPanelFactory {
    private static final ContainerItemProperties[] UOMS = getUomProperties();
    public static final String UNIT_OF_MEASURE = I18n.marktr("Unit of measure");

    private static final I18n I18N = I18nFactory.getI18n(AdvancedEditorPanelFactory.class);
    private static final Logger LOGGER = Logger.getLogger(AdvancedEditorPanelFactory.class);
    public static final String NONE = I18n.marktr("None");
    private final JTree tree;
    private AdvancedTreeModel model;

    /**
     * Builds a new panel factory associated to the given model.
     * @param tree The JTree we are handling. Must be defined with an AdvancedTreeModel.
     */
    public AdvancedEditorPanelFactory(JTree tree){
        this.tree = tree;
        this.model = (AdvancedTreeModel) tree.getModel();
    }

    /**
     * Gets the associated AdvancedTreeModel
     * @return The associated tree model.
     */
    public AdvancedTreeModel getModel(){
        return model;
    }

    /**
     * Gets the {@link JPanel} used to edit {@code sn}.
     * @param sn The input {@link SymbolizerNode}.
     * @return The needed JPanel.
     */
    public JPanel getPanel(SymbolizerNode sn){
        JPanel ret;
        if(sn instanceof Literal){
            ret = getPanel((Literal)sn);
        } else {
            ret = new JPanel(new MigLayout("wrap 3"));
            getCombos(sn, ret);
        }
        if(sn instanceof PropertiesCollectionNode){
            for(String prop : ((PropertiesCollectionNode) sn).getPropertiesNames()){
                JPanel col = new PropertiesPanel((PropertiesCollectionNode) sn, prop, this, tree);
                ret.add(col, "span 2");
            }
        }
        return ret;
    }

    /**
     * getPanel dedicated to SeParameter instances.
     * @param sp The input SeParameter
     * @return The JPanel that can be used to edit sp.
     */
    public JPanel getPanel(Literal sp){
        JPanel ret = new JPanel(new MigLayout("wrap 2","[::][100::]",""));
        if(sp instanceof RealLiteral){
            ret.add(new JLabel(I18N.tr("Value")));
            final RealLiteral rl = (RealLiteral) sp;
            double min = rl.getContext().getMin() == null ? Double.NEGATIVE_INFINITY : rl.getContext().getMin();
            double max = rl.getContext().getMax() == null ? Double.POSITIVE_INFINITY : rl.getContext().getMax();
            Double val = ((RealLiteral) sp).getValue(null);
            double init = val == null || Double.isInfinite(val) ? 0.0 : val;
            final JSpinner spin = new JSpinner(new SpinnerNumberModel(init, min, max, 0.1));
            ChangeListener cl = EventHandler.create(ChangeListener.class, rl, "setValue", "source.value");
            spin.addChangeListener(cl);
            ret.add(spin, "growx");
        } else if(sp instanceof StringLiteral){
            ret.add(new JLabel(I18N.tr("Value")));
            StringLiteral sl = (StringLiteral) sp;
            JTextField field = new JTextField(sl.getValue(null), 25);
            FocusListener fl = EventHandler.create(FocusListener.class, sl, "setValue", "source.text", "focusLost");
            field.addFocusListener(fl);
            ret.add(field);
        } else if(sp instanceof ColorLiteral){
            ret.add(new JLabel(I18N.tr("Value")));
            ret.add(new JLabel(((ColorLiteral) sp).getColor(null).toString()));

        }
        return ret;
    }

    public JPanel getPanel(RealFunction rf){
        throw new UnsupportedOperationException();
    }

    /**
     * Builds and fill JComboBox instances from the properties
     * advertised by {@code sn}. Changing the selected elements in
     * these JComboBox updates {@code sn}.
     * @param sn The input SymbolizerNode
     */
    private void getCombos(SymbolizerNode sn, JPanel dest) {
        List<String> req = sn.getPropertyNames();
        List<String> optional = sn.getOptionalPropertyNames();
        if(sn instanceof UomNode){
            JComboBox cu = getUomComboBox((UomNode)sn);
            dest.add(new JLabel(I18N.tr(UNIT_OF_MEASURE)));
            dest.add(cu,"growx, wrap");
        }
        for(int i=0; i<req.size(); i++){
            String name = req.get(i);
            boolean opt = optional.contains(name);
            final JComboBox combo = getComboForProperty(sn, name, opt);
            dest.add(new JLabel(I18N.tr(name)));
            dest.add(combo, "growx");
            final GoToNodeButton but = new GoToNodeButton(tree, sn.getProperty(name));
            dest.add(but);
            if(opt){
                ContainerItem ci = (ContainerItem) combo.getSelectedItem();
                but.setEnabled(!NONE.equals(ci.getKey()));
                combo.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        ContainerItem ci = (ContainerItem) combo.getSelectedItem();
                        System.out.println(ci.getKey());
                        System.out.println(ci.getLabel());
                        but.setEnabled(!NONE.equals(ci.getKey()));
                    }
                });
            }
        }
    }

    private JComboBox getUomComboBox(final UomNode sn) {
        final WideComboBox ret = new WideComboBox(Uom.getStrings());
        ret.setSelectedItem(sn.getUom().toString());
        ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JComboBox source = (JComboBox) actionEvent.getSource();
                sn.setUom(Uom.fromString((String) source.getSelectedItem()));
            }
        };
        ret.addActionListener(al);
        return ret;
    }

    /**
     * Builds a JComboBox containing all the SymbolizerNode types that can
     * be put in {@code sn} for property {@code name}.
     * @param sn The parent {@link SymbolizerNode}.
     * @param name The name of the property we're handling
     * @param optional If true, the property can be set to null, so a "None" entry
     *                 is added in the combo box.
     * @return The wanted JComboBox.
     */
    public JComboBox getComboForProperty(SymbolizerNode sn, String name, boolean optional) {
        JComboBox ret = getComboForClass(sn.getPropertyClass(name), optional, sn.getProperty(name));
        ActionListener l = new ChildListener(sn, name, this);
        ret.addActionListener(l);
        return ret;
    }

    /**
     * Builds a JComboBox for the given class.
     * @param propertyClass The class of the symbolizer node we want to manage.
     * @param optional If true, the combo will contain an additional entry in order
     *                 to set the property to null
     * @param current The currently set node. If null, the first entry will be selected.
     * @return The wanted JComboBox.
     */
    public JComboBox getComboForClass(Class<?extends SymbolizerNode> propertyClass,
                                      boolean optional,
                                      SymbolizerNode current){
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
        for(ContainerItem<Class<? extends SymbolizerNode>> ci : cips){
            ret.addItem(ci);
            if(current != null && current.getClass().equals(ci.getKey())){
                ind = c;
            }
            c++;
        }
        if(optional){
            ret.addItem(new ContainerItemProperties(NONE, I18N.tr(NONE)));
        }
        if(ind>-1){
            ret.setSelectedIndex(ind);
        } else if(optional){
            ret.setSelectedIndex(cips.size());
        } else if(ret.getItemCount() > 0){
            ret.setSelectedIndex(0);
        }
        return ret;
    }

    /**
     * Gets the value contained in the {@code Uom} enum with their
     * internationalized representation in a {@code
     * ContainerItemProperties} array.
     * @return Uoms in an array of containers.
     */
    public static ContainerItemProperties[] getUomProperties(){
        Uom[] us = Uom.values();
        ContainerItemProperties[] ret = new ContainerItemProperties[us.length];
        for(int i = 0; i<us.length; i++){
            Uom u = us[i];
            ContainerItemProperties cip = new ContainerItemProperties(u.toString(), u.toLocalizedString());
            ret[i] = cip;
        }
        return ret;
    }

    /**
     * Tries to build an instance of the given class using the default constructor.
     * @param input A SymbolizerNode class.
     * @return The instance created by the class' default constructor,
     *         null if a problem is encountered.
     */
    public SymbolizerNode getInstance(Class<? extends SymbolizerNode> input){
        try {
                Constructor<? extends SymbolizerNode> c = input.getConstructor();
                return c.newInstance();
        } catch (NoSuchMethodException e) {
            LOGGER.error("Can't' find a default constructor for type "+input);
        } catch (InvocationTargetException e) {
            LOGGER.error("Can't' find use the default constructor for type " + input, e);
        } catch (InstantiationException e) {
            LOGGER.error("Can't' find use the default constructor for type " + input, e);
        } catch (IllegalAccessException e) {
            LOGGER.error("Can't' find use the default constructor for type " + input, e);
        }
        return null;
    }

    /**
     * ActionListener that is used to know if one of the properties managed
     * for a SymbolizerNode is changed through a selection in a JComboBox.
     */
    public static class ChildListener implements ActionListener{
        private final String property;
        private final AdvancedEditorPanelFactory factory;
        private SymbolizerNode parent;

        /**
         * Listens for the property {@code property} in order to update {@code parent}.
         * @param parent The parent node.
         * @param property The property we're interesting in.
         */
        public ChildListener(SymbolizerNode parent, String property, AdvancedEditorPanelFactory factory){
            this.parent = parent;
            this.property = property;
            this.factory = factory;
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            AdvancedTreeModel model = factory.getModel();
            ContainerItem ci =
                    (ContainerItem) ((JComboBox)actionEvent.getSource()).getSelectedItem();
            Object key = ci.getKey();
            if(key instanceof String){
                model.setProperty(parent, null, property);
            } else {
                Class<? extends SymbolizerNode> ke = (Class<? extends SymbolizerNode>)key;
                SymbolizerNode inst = factory.getInstance(ke);
                if(inst != null){
                    model.setProperty(parent, inst, property);
                }
            }
        }
    }
}
