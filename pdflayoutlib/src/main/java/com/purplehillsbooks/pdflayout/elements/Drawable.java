package com.purplehillsbooks.pdflayout.elements;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import com.purplehillsbooks.pdflayout.elements.render.Layout;
import com.purplehillsbooks.pdflayout.elements.render.RenderContext;
import com.purplehillsbooks.pdflayout.text.DrawListener;
import com.purplehillsbooks.pdflayout.text.Position;

/**
 * Common interface for drawable objects.
 */
public abstract class Drawable extends Element {

    public boolean isDrawable() {
        return true;
    }


    /**
     * @return the width of the drawable in points
     * 
     * @throws Exception (forced javadoc text)
     */
    public abstract float getWidth() throws Exception;

    /**
     * @return the height of the drawable in points
     * 
     * @throws Exception (forced javadoc text)
     */
    public abstract float getHeight() throws Exception;

    /**
     * If an absolute position is given, the drawable will be drawn at this
     * position ignoring any {@link Layout}.
     *
     * @return the absolute position.
     */
    public Position getAbsolutePosition() {
        return null;
    }

    /**
     * Draws the object at the given position.
     *
     * @param renderContext
     *            the context currently drawing to
     * @param upperLeft
     *            the upper left position to start drawing.
     * @param drawListener
     *            the listener to
     *            {@link DrawListener#drawn(Object, Position, float, float) notify} on
     *            drawn objects.
     * @throws Exception (forced javadoc nonsense)
     */
    public abstract void draw(RenderContext renderContext,
            Position upperLeft, DrawListener drawListener) throws Exception;

    /**
     * Remove any setting that would cause white space before the content.
     *         This is useful for avoiding leading empty space on a new page.
     * 
     * @throws Exception (forced javadoc text)
     */
    public void removeLeadingEmptyVerticalSpace() throws Exception {
        //by default do nothing
    }
    
    /**
     * Remove any setting that would cause white space before the content.
     *         This is useful for avoiding empty space after the content 
     *         at the bottom of a page.
     */
    public void trimTrailingWhiteSpace() {
        //by default do nothing
    }

    
}
