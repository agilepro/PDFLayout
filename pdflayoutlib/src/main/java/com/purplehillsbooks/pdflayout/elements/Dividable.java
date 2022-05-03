package com.purplehillsbooks.pdflayout.elements;

import com.purplehillsbooks.pdflayout.elements.render.RenderContext;

/**
 * If a drawable is marked as {@link Dividable}, it can be (vertically) divided
 * in case it does not fit on the (remaining) page.
 */
public abstract class Dividable extends Drawable {

    public boolean canBeDivided() {
        return true;
    }
    
    /**
     * Divides the drawable vetically into pieces where the first part is to
     * respect the given remaining height. The page height allows to make better
     * decisions on how to divide best.
     *
     * @param remainingHeight
     *            the remaining height on the page dictating the height of the
     *            first part.
     * @param renderContext
     *            current rendering state
     * @param topOfPage
     *            boolean to tell whether this call should be considered at the top of the page
     * @return the Divided containing the first part and the tail.
     * @throws Exception by pdfbox.
     */
    public abstract Divided divide(final float remainingHeight, RenderContext renderContext, boolean topOfPage)
            throws Exception;

    /**
     * A container for the result of a divide operation.
     * Holds two Drawable: the drawable before the divide,
     * and the drawable after the divide (to go on next page).
     */
    public static class Divided {

        private final Drawable first;
        private final Drawable tail;

        public Divided(Drawable first, Drawable tail) {
            this.first = first;
            this.tail = tail;
        }

        public Drawable getFirst() {
            return first;
        }

        public Drawable getTail() {
            return tail;
        }

    }

}
