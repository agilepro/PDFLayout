package com.purplehillsbooks.pdflayout.elements;

/**
 * Base (tagging) interface for elements in a {@link PDFDoc}.
 */
public abstract class Element {
    
    public boolean isDrawable() {
        return false;
    }
    public boolean canBeDivided() {
        return false;
    }
    public void propagateMaxWidthToChildren() throws Exception {
        //nothing to do by default
    }

}
