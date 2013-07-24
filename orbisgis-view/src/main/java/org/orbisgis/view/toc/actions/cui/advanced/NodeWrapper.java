package org.orbisgis.view.toc.actions.cui.advanced;

import org.orbisgis.core.renderer.se.SymbolizerNode;

/**
 * <p>A Wrapper for SymbolizerNode instances. The main goal is to have a workaround
 * to the following behaviour : http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4465854 </p>
 * Thanks to this (really simple) wrapper, we're not stuck because of some implementation
 * of the equals method (in RealLiteral, for instance). These implementations are useful to
 * the renderer internals but not for the JTree building... so let's wrap them all !
 * @author Alexis Guéganno
 */
public class NodeWrapper{
    private SymbolizerNode sn;

    /**
     * Constructor - simply takes a SymbolizerNode.
     * @param sn the wrapped node.
     */
    public NodeWrapper(SymbolizerNode sn){
        this.sn = sn;
    }

    /**
     * String representation of the node.
     * @return {@code getNode().getName();}
     */
    public String toString(){
        return sn.getName();
    }

    /**
     * Retrieve the inner {@link SymbolizerNode}.
     * @return  The inner {@link SymbolizerNode}.
     */
    public SymbolizerNode getNode(){
        return sn;
    }
}
