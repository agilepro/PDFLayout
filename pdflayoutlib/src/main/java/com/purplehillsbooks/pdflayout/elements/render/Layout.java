package com.purplehillsbooks.pdflayout.elements.render;

import com.purplehillsbooks.pdflayout.elements.Element;

/**
 * A layout is used to size and position the elements of a document according to
 * a specific strategy.
 */
public abstract class Layout extends Element {

    
    abstract boolean renderWithHint(final RenderContext renderContext, final Element element,
            final LayoutHint layoutHint) throws Exception;
}
