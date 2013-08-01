package org.orbisgis.core.renderer.se;

import org.orbisgis.core.renderer.se.common.Uom;

/**
 * Default implementation for UomNode.
 * @author Alexis Guéganno
 */
public abstract class AbstractUomNode  extends AbstractSymbolizerNode implements UomNode{

    private Uom uom;

    @Override
    public Uom getUom() {
        if (uom != null) {
            return uom;
        } else if(getParent() instanceof UomNode){
            return ((UomNode)getParent()).getUom();
        } else {
            return Uom.PX;
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
}
