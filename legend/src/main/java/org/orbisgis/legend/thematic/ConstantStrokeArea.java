/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.legend.thematic;

import java.awt.Color;
import org.orbisgis.core.renderer.se.AreaSymbolizer;
import org.orbisgis.core.renderer.se.Symbolizer;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.core.renderer.se.stroke.Stroke;
import org.orbisgis.legend.LegendStructure;
import org.orbisgis.legend.analyzer.PenStrokeAnalyzer;
import org.orbisgis.legend.structure.stroke.constant.ConstantPenStroke;
import org.orbisgis.legend.structure.stroke.constant.NullPenStrokeLegend;

/**
 * Represents an {@code AreaSymbolizer} of which the {@code Stroke} is a constant
 * {@code PenStroke} instance, that can be recognized as a {@code
 * ConstantPenStrokeLegend}.
 * @author Alexis Guéganno
 */
public abstract class ConstantStrokeArea extends SymbolizerLegend {

    private AreaSymbolizer areaSymbolizer;

    private ConstantPenStroke strokeLegend;

    /**
     * Build a new default {@code ConstantStrokeArea} from scratch. It contains a
     * default {@code AreaSymbolizer}, which is associated to a constant {@code
     * PenStroke}.
     */
    public ConstantStrokeArea() {
        areaSymbolizer = new AreaSymbolizer();
        Stroke stroke = areaSymbolizer.getStroke();
        strokeLegend = (ConstantPenStroke) new PenStrokeAnalyzer((PenStroke) stroke).getLegend();
    }

    /**
     * Build a new {@code ConstantStrokeArea} directly from the given {@code
     * AreaSymbolizer}.
     * @param symbolizer
     */
    public ConstantStrokeArea(AreaSymbolizer symbolizer){
        areaSymbolizer=symbolizer;
        Stroke stroke = symbolizer.getStroke();
        if(stroke instanceof PenStroke || stroke == null){
            LegendStructure strokeLgd = new PenStrokeAnalyzer((PenStroke) stroke).getLegend();
            if(strokeLgd instanceof ConstantPenStroke){
                strokeLegend = (ConstantPenStroke) strokeLgd;
            } else {
                throw new IllegalArgumentException("The stroke of this AreaSymbolizer "
                        + "can't be recognized as a constant PenStroke.");
            }
        } else {
            throw new IllegalArgumentException("We are not able to process Stroke"
                    + "that are not PenStroke.");
        }
    }

    /**
     * Get the symbolizer associated to this {@code ConstantStrokeArea}.
     * @return
     */
    @Override
    public Symbolizer getSymbolizer() {
        return areaSymbolizer;
    }

    /**
     * Exposes the inner {@link LegendStructure} that represents the analysis
     * made on the {@link PenStroke}.
     * @return
     */
    public ConstantPenStroke getStrokeLegend(){
            return strokeLegend;
    }

    /**
     * Sets the legend describing the structure of the fill contained in the
     * inner {@link AreaSymbolizer}. This will update the fill of the {@code
     * AreaSymbolizer} in question.
     * @param cfl
     */
    public void setStrokeLegend(ConstantPenStroke cfl){
            if(cfl == null){
                    strokeLegend = new NullPenStrokeLegend();
            } else {
                    strokeLegend = cfl;
            }
            AreaSymbolizer symbolizer = (AreaSymbolizer) getSymbolizer();
            symbolizer.setStroke(strokeLegend.getStroke());
    }

    /**
     * Get the width of the line that outlines the inner {@code AreaSymbolizer}.
     * @return
     */
    public Double getLineWidth(){
        return strokeLegend.getLineWidth();
    }

    /**
     * Set the width of the line that outlines the inner {@code AreaSymbolizer}.
     * @param width
     */
    public void setLineWidth(Double width){
        strokeLegend.setLineWidth(width);
    }

    /**
     * Get the colour of the line that outlines the inner {@code AreaSymbolizer}.
     * @return
     */
    public Color getLineColor() {
        return strokeLegend.getLineColor();
    }

    /**
     * Set the colour of the line that outlines the inner {@code AreaSymbolizer}.
     * @param col
     */
    public void setLineColor(Color col) {
        strokeLegend.setLineColor(col);
    }

    /**
     * Gets the dash array used to draw the outer line of this AreaSymbolizer.
     * @return
     */
    public String getDashArray(){
        return strokeLegend.getDashArray();
    }

    /**
     * Sets the dash array used to draw the outer line of this AreaSymbolizer.
     * @param s
     */
    public void setDashArray(String s){
        strokeLegend.setDashArray(s);
    }

}
