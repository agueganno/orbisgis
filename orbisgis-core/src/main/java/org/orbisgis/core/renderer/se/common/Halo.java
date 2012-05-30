package org.orbisgis.core.renderer.se.common;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.io.IOException;
import java.util.HashSet;
import net.opengis.se._2_0.core.HaloType;
import org.gdms.data.DataSource;
import org.orbisgis.core.Services;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.FillNode;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.UomNode;
import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.graphic.ViewBox;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;
import org.orbisgis.utils.I18N;

/**
 * A {@code Halo} is a type of {@code Fill} that is applied to the background of font glyphs.
 * It is mainly used to improve the readability of text labels on the map.
 * @author alexis
 */
public final class Halo implements SymbolizerNode, UomNode, FillNode {

        /**
         * The default radius for new {@code Halo} instances. Set to 5.0, and UOM dependant.
         */
    public static final double DEFAULT_RADIUS = 5.0;

    private Uom uom;
    private RealParameter radius;
    private Fill fill;
    private SymbolizerNode parent;
    
    /**
     * Build a new default {@code Halo}, with a solid fill and a radius set to {@code DEFAULT_RADIUS}
     */
    public Halo() {
        setFill(new SolidFill());
        setRadius(new RealLiteral(DEFAULT_RADIUS));
    }

    /**
     * Build a new {@code Halo} with the given {@code Fill} and a radius set to {@code radius}
     * @param fill
     * @param radius 
     */
    public Halo(Fill fill, RealParameter radius) {
        this.fill = fill;
        this.radius = radius;
    }

    /**
     * Build a new {@code Halo} from the given JAXB type element.
     * @param halo
     * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
     */
    public Halo(HaloType halo) throws InvalidStyle {
        if (halo.getFill() != null) {
            this.setFill(Fill.createFromJAXBElement(halo.getFill()));
        }

        if (halo.getRadius() != null) {
            this.setRadius(SeParameterFactory.createRealParameter(halo.getRadius()));
        }

        if (halo.getUom() != null) {
            this.setUom(Uom.fromOgcURN(halo.getUom()));
        }
    }

    @Override
    public Uom getUom() {
        if (uom == null) {
            return getParent().getUom();
        } else {
            return uom;
        }
    }

    @Override
    public Uom getOwnUom() {
        return uom;
    }

    @Override
    public void setUom(Uom uom) {
        this.uom = uom;
    }

    @Override
    public void setFill(Fill fill) {
        this.fill = fill;
        fill.setParent(this);
    }

    @Override
    public Fill getFill() {
        return fill;
    }

    @Override
    public SymbolizerNode getParent() {
        return parent;
    }

    @Override
    public void setParent(SymbolizerNode node) {
        parent = node;
    }

    /**
     * Get the radius of this {@code Halo}.
     * @return 
     * The radius of this {@code Halo} as a {@code RealParameter}.
     */
    public RealParameter getRadius() {
        return radius;
    }

    /**
     * Set the radius of this {@code Halo}.
     * @param radius 
     */
    public void setRadius(RealParameter radius) {
        this.radius = radius;
        if (this.radius != null) {
            this.radius.setContext(RealParameterContext.REAL_CONTEXT);
        }
    }

    /**
     * Return the halo radius in pixel
     * @param sds
     * @param fid
     * @param mt
     * @return
     * @throws ParameterException
     */
    public double getHaloRadius(DataSource sds, long fid, MapTransform mt) throws ParameterException {
        return Uom.toPixel(radius.getValue(sds, fid), getUom(), mt.getDpi(), mt.getScaleDenominator(), null); // TODO 100%
    }

    /**
     * Draw this {@code Halo} in {@code g2}. Basically compute an offseted shape
     * and fill the difference with the original one.
     * @param g2
     * @param sds
     * @param fid
     * @param selected
     * @param shp
     * @param mt
     * @param substract
     * @throws ParameterException
     * @throws IOException
     */
    public void draw(Graphics2D g2, DataSource sds, long fid, boolean selected, 
            Shape shp, MapTransform mt, boolean substract) throws ParameterException, IOException {
        if (radius != null && fill != null) {
            double r = this.getHaloRadius(sds, fid, mt);
            if (r > 0.0) {
                for (Shape halo : ShapeHelper.perpendicularOffset(shp, r)) {
                    fillHalo(halo, shp, g2, sds, fid, selected, mt, substract);
                }
            }
        }
    }
    
    /**
     * In order to improve performance when drawing a halo on a Circle, we use a
     * dedicated method, where we won't compute all the offseted point of the
     * original shape. It's faster to compute directly a new Shape.</p>
     * <p>To achieve this goal, we use the original shape, that is supposed to be
     * an {@code Arc2D}. We compute the new {@code Arc2D} by adding the wanted
     * radius, and finally apply the current AffineTransform obtained from the
     * MapTransform.
     * @param g2
     * The {@code Graphics} where we are going to draw.
     * @param sds
     * Our DataSource
     * @param fid
     * The index of the current feature in sds.
     * @param selected
     * @param shp
     * The original Shape
     * @param atShp
     * The shape obtained by applying mt to shp
     * @param mt
     * The current {@code MapTransform}.
     * @param substract
     * @param viewBox
     * @param at
     * @throws ParameterException
     * @throws IOException 
     */
    public void drawCircle(Graphics2D g2, DataSource sds, long fid, boolean selected, 
            Arc2D shp, Shape atShp, MapTransform mt, boolean substract, 
            ViewBox viewBox, AffineTransform at) throws ParameterException, IOException {
        //We want to make a halo around a WKN.CIRCLE instance. 
        if (radius != null && fill != null) {
            double r = this.getHaloRadius(sds, fid, mt);
            double x = shp.getX() - r/2;
            double y = shp.getY() - r/2;
            double height = shp.getHeight() + r;
            double width = shp.getWidth() + r;
            Shape origin = new Arc2D.Double(x, y, width, height, shp.getAngleStart(), shp.getAngleExtent(), shp.getArcType());
            Shape halo = at.createTransformedShape(origin);
            fillHalo(halo, atShp, g2, sds, fid, selected, mt, substract);
            
        }
    }
    
    private void fillHalo(Shape halo, Shape initialShp, Graphics2D g2, 
                DataSource sds, long fid, boolean selected,MapTransform mt, boolean substract) 
                throws ParameterException, IOException {
        if (halo != null && initialShp != null) {
            Area initialArea = new Area(initialShp);
            Area aHalo = new Area(halo);
            if (substract){
                aHalo.subtract(initialArea);
            }
            fill.draw(g2, sds, fid, aHalo, selected, mt);
        } else {
            Services.getErrorManager().error(
                    I18N.getString("orbisgis-core.orbisgis.org.orbisgis.renderer.cannotCreatePerpendicularOffset"));
        }
    }

    @Override
    public HashSet<String> dependsOnFeature() {
        HashSet<String> ret = new HashSet<String>();
        ret.addAll(radius.dependsOnFeature());
        ret.addAll(fill.dependsOnFeature());
        return ret;
    }

    /**
     * Get a JAXB rperesentation of this object.
     * @return 
     */
    public HaloType getJAXBType() {
        HaloType h = new HaloType();

        if (fill != null) {
            h.setFill(fill.getJAXBElement());
        }

        if (radius != null) {
            h.setRadius(radius.getJAXBParameterValueType());
        }

        if (uom != null) {
            h.setUom(uom.toURN());
        }

        return h;
    }
}