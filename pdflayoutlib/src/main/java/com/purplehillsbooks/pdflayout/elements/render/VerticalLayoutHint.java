package com.purplehillsbooks.pdflayout.elements.render;

import com.purplehillsbooks.pdflayout.text.Alignment;

/**
 * Layout hint for the {@link VerticalLayout}. You may specify margins to define
 * some extra space around the drawable. If there is still some extra space
 * available vertically, the alignment decides where to position the drawable.
 * The {@link #isResetY() reset Y} indicates if the Y postion should be reset to
 * the value before drawing. Be aware that this only applies to the current page
 * where the remainder of the element has been drawn to. Means, if the elemenent
 * spawns multiple pages, the position is reset to the begin of the last page.
 */
public class VerticalLayoutHint implements LayoutHint {

    public final static VerticalLayoutHint LEFT = new VerticalLayoutHint(
            Alignment.Left);
    public final static VerticalLayoutHint CENTER = new VerticalLayoutHint(
            Alignment.Center);
    public final static VerticalLayoutHint RIGHT = new VerticalLayoutHint(
            Alignment.Right);

    private final Alignment alignment;
    //private final float marginLeft;
    //private final float marginRight;
    //private final float marginTop;
    //private final float marginBottom;
    private final boolean resetY;

    /**
     * Creates a layout hint with {@link Alignment#Left left alignment}.
     */
    public VerticalLayoutHint() {
        this(Alignment.Left);
    }

    /**
     * Creates a layout hint with the given alignment.
     *
     * @param alignment
     *            the element alignment.
     */
    public VerticalLayoutHint(Alignment alignment) {
        this.alignment = alignment;
        this.resetY = false;
    }


    public Alignment getAlignment() {
        return alignment;
    }

    /*
    public float getMarginLeft() {
        return marginLeft;
    }

    public float getMarginRight() {
        return marginRight;
    }

    public float getMarginTop() {
        return marginTop;
    }

    public float getMarginBottom() {
        return marginBottom;
    }
    */

    public boolean isResetY() {
        return resetY;
    }

    @Override
    public String toString() {
        return "VerticalLayoutHint [alignment=" + alignment + ", resetY="
                + resetY + "]";
    }

    /**
     * @return a {@link VerticalLayoutHintBuilder} for creating a
     *         {@link VerticalLayoutHint} using a fluent API.
     */
    /*
    public static VerticalLayoutHintBuilder builder() {
        return new VerticalLayoutHintBuilder();
    }
*/
    
    /**
     * A builder for creating a
     *         {@link VerticalLayoutHint} using a fluent API.
     */
    /*
    public static class VerticalLayoutHintBuilder {
        protected Alignment alignment = Alignment.Left;
        protected float marginLeft = 0;
        protected float marginRight = 0;
        protected float marginTop = 0;
        protected float marginBottom = 0;
        protected boolean resetY = false;

        public VerticalLayoutHintBuilder alignment(final Alignment alignment) {
            this.alignment = alignment;
            return this;
        }
*/
        /*
        public VerticalLayoutHintBuilder marginLeft(final float marginLeft) {
            this.marginLeft = marginLeft;
            return this;
        }

        public VerticalLayoutHintBuilder marginRight(final float marginRight) {
            this.marginRight = marginRight;
            return this;
        }

        public VerticalLayoutHintBuilder marginTop(final float marginTop) {
            this.marginTop = marginTop;
            return this;
        }

        public VerticalLayoutHintBuilder marginBottom(final float marginBottom) {
            this.marginBottom = marginBottom;
            return this;
        }
        */

        /*
        public VerticalLayoutHintBuilder margins(float marginLeft,
                float marginRight, float marginTop, float marginBottom) {
            this.marginLeft = marginLeft;
            this.marginRight = marginRight;
            this.marginTop = marginTop;
            this.marginBottom = marginBottom;
            return this;
        }
        */

        /*
        public VerticalLayoutHintBuilder resetY(final boolean resetY) {
            this.resetY = resetY;
            return this;
        }
        */

        /*
        public VerticalLayoutHint build() {
            return new VerticalLayoutHint(alignment, marginLeft, marginRight,
                    marginTop, marginBottom, resetY);
        }
        */

    //}

}
