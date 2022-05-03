package com.purplehillsbooks.pdflayout.elements.render;


import org.apache.pdfbox.pdmodel.PDPageContentStream;

import com.purplehillsbooks.pdflayout.elements.ControlElement;
import com.purplehillsbooks.pdflayout.elements.Dividable;
import com.purplehillsbooks.pdflayout.elements.Dividable.Divided;
import com.purplehillsbooks.pdflayout.elements.Drawable;
import com.purplehillsbooks.pdflayout.elements.Element;
import com.purplehillsbooks.pdflayout.elements.Frame;
import com.purplehillsbooks.pdflayout.elements.PageFormat;
import com.purplehillsbooks.pdflayout.text.Alignment;
import com.purplehillsbooks.pdflayout.text.Position;
import com.purplehillsbooks.pdflayout.text.WidthRespecting;
import com.purplehillsbooks.pdflayout.util.CompatibilityHelper;

/**
 * Layout implementation that stacks drawables vertically onto the page. If the
 * remaining height on the page is not sufficient for the drawable, it will be
 * {@link Dividable divided}. Any given {@link VerticalLayoutHint} will be taken
 * into account to calculate the position, width, alignment etc.
 */
public class VerticalLayout extends Layout {

    protected boolean removeLeadingEmptyVerticalSpace = true;

    /**
     * See {@link Drawable#removeLeadingEmptyVerticalSpace()}
     *
     * @return <code>true</code> if empty space (e.g. empty lines) should be
     *         removed at the begin of a page.
     */
    public boolean isRemoveLeadingEmptyVerticalSpace() {
        return removeLeadingEmptyVerticalSpace;
    }

    /**
     * Indicates if empty space (e.g. empty lines) should be removed at the
     * begin of a page. See {@link Drawable#removeLeadingEmptyVerticalSpace()}
     *
     * @param removeLeadingEmptyLines
     *            <code>true</code> if space should be removed.
     */
    public void setRemoveLeadingEmptyVerticalSpace(
            boolean removeLeadingEmptyLines) {
        this.removeLeadingEmptyVerticalSpace = removeLeadingEmptyLines;
    }


    /**
     * @param renderContext
     *            the render context.
     * @return the target width to draw to.
     */
    protected float getTargetWidth(final RenderContext renderContext) {
        float targetWidth = renderContext.getWidth();
        return targetWidth;
    }

    @Override
    public boolean renderWithHint(RenderContext renderContext, Element element,
            LayoutHint layoutHint) throws Exception {
        if (element instanceof Drawable) {
            renderDrawable(renderContext, (Drawable) element, layoutHint);
            return true;
        }
        if (element == ControlElement.NEWPAGE) {
            renderContext.newPage();
            return true;
        }

        return false;
    }

    public void renderDrawable(final RenderContext renderContext, Drawable drawable,
            final LayoutHint layoutHint) throws Exception {
        if (drawable.getAbsolutePosition() != null) {
            renderAbsolute(renderContext, drawable,
                    drawable.getAbsolutePosition());
        } else {
            renderReleative(renderContext, drawable, layoutHint);
        }
    }

    /**
     * Draws at the given position, ignoring all layouting rules.
     *
     * @param renderContext
     *            the context providing all rendering state.
     * @param drawable
     *            the drawable to draw.
     * @param position
     *            the left upper position to start drawing at.
     * @throws Exception
     *             by pdfbox
     */
    protected void renderAbsolute(final RenderContext renderContext,
            Drawable drawable,
            final Position position) throws Exception {
        drawable.draw(renderContext.getPdDocument(),
                renderContext.getContentStream(), position, renderContext);
    }

    /**
     * Renders the drawable at the {@link RenderContext#getCurrentPosition()
     * current position}. This method is responsible taking any top or bottom
     * margin described by the (Vertical-)LayoutHint into account. The actual
     * rendering of the drawable is performed by
     * {@link #layoutAndDrawReleative(RenderContext, Drawable, LayoutHint)}.
     *
     * @param renderContext
     *            the context providing all rendering state.
     * @param drawable
     *            the drawable to draw.
     * @param layoutHint
     *            the layout hint used to layout.
     * @throws Exception
     *             by pdfbox
     */
    protected void renderReleative(final RenderContext renderContext,
            Drawable drawable, final LayoutHint layoutHint) throws Exception {
        
        drawable.propagateMaxWidthToChildren();

        /*
        VerticalLayoutHint verticalLayoutHint = null;
        if (layoutHint instanceof VerticalLayoutHint) {
            verticalLayoutHint = (VerticalLayoutHint) layoutHint;8
            if (verticalLayoutHint.getMarginTop() > 0) {
                layoutAndDrawReleative(renderContext, new VerticalSpacer(
                        verticalLayoutHint.getMarginTop()), verticalLayoutHint);
            }
        }
        */

        layoutAndDrawReleative(renderContext, drawable, layoutHint);

        /*
        if (verticalLayoutHint != null) {
            if (verticalLayoutHint.getMarginBottom() > 0) {
                layoutAndDrawReleative(renderContext, new VerticalSpacer(
                        verticalLayoutHint.getMarginBottom()),
                        verticalLayoutHint);
            }
        }
        */
    }

    /**
     * Adjusts the width of the drawable (if it is {@link WidthRespecting}), and
     * divides it onto multiple pages if necessary. Actual drawing is delegated
     * to
     * {@link #drawReletivePartAndMovePosition(RenderContext, Drawable, LayoutHint, boolean)}
     * .
     *
     * @param renderContext
     *            the context providing all rendering state.
     * @param drawable
     *            the drawable to draw.
     * @param layoutHint
     *            the layout hint used to layout.
     * @throws Exception
     *             by pdfbox
     */
    protected void layoutAndDrawReleative(final RenderContext renderContext,
            Drawable drawable, final LayoutHint layoutHint) throws Exception {

        float targetWidth = getTargetWidth(renderContext);
        boolean movePosition = true;
        
        /*
        VerticalLayoutHint verticalLayoutHint = null;
        if (layoutHint instanceof VerticalLayoutHint) {
            verticalLayoutHint = (VerticalLayoutHint) layoutHint;
            targetWidth -= verticalLayoutHint.getMarginLeft();
            targetWidth -= verticalLayoutHint.getMarginRight();
            movePosition = !verticalLayoutHint.isResetY();
        }
        */

        float oldMaxWidth = -1;
        if (drawable instanceof WidthRespecting) {
            WidthRespecting flowing = (WidthRespecting) drawable;
            oldMaxWidth = flowing.getMaxWidth();
            if (oldMaxWidth <= 0) {
                flowing.setMaxWidth(targetWidth);
            }
        }

        removeEmptySpaceIfTopOfPage(drawable, renderContext);
        Drawable drawablePart = drawable;
        boolean topOfPage = renderContext.isTopOfPage();
        float remainingHeight = renderContext.getRemainingHeight();
        
        if (drawablePart instanceof Frame) {
            if (remainingHeight < ((Frame)drawablePart).getNeedSpace()
                    || ((Frame)drawablePart).getStartNewPage()) {
                //this one wants to jump to the next page
                //so fill the rest of this page with white space.
                //VerticalSpacer restOfPage = new VerticalSpacer(renderContext.getRemainingHeight());
                //drawReletivePartAndMovePosition(renderContext, restOfPage,
                //        layoutHint, true);
                renderContext.newPage();
                topOfPage = true;
            }
        }
        
        float thisPartHeight = drawablePart.getHeight();
        while (remainingHeight < thisPartHeight) {
            if (drawablePart.canBeDivided()) {
                Dividable dividable = (Dividable) drawablePart;
                Divided divided = dividable.divide(
                        renderContext.getRemainingHeight(), renderContext, topOfPage);
                divided.getFirst().trimTrailingWhiteSpace();
                drawReletivePartAndMovePosition(renderContext, divided.getFirst(),
                        layoutHint, true);
                drawablePart = divided.getTail();
            }

            // new page
            renderContext.newPage();
            topOfPage = true;
            remainingHeight = renderContext.getRemainingHeight();

            
            removeEmptySpaceIfTopOfPage(drawablePart, renderContext);
            thisPartHeight = drawablePart.getHeight();
        }

        drawReletivePartAndMovePosition(renderContext, drawablePart,
                layoutHint, movePosition);

        if (drawable instanceof WidthRespecting) {
            if (oldMaxWidth < 0) {
                ((WidthRespecting) drawable).setMaxWidth(oldMaxWidth);
            }
        }
    }

    /**
     * Actually draws the (drawble) part at the
     * {@link RenderContext#getCurrentPosition()} and - depending on flag
     * <code>movePosition</code> - moves to the new Y position. Any left or
     * right margin is taken into account to calculate the position and
     * alignment.
     *
     * @param renderContext
     *            the context providing all rendering state.
     * @param drawable
     *            the drawable to draw.
     * @param layoutHint
     *            the layout hint used to layout.
     * @param movePosition
     *            indicates if the position should be moved (vertically) after
     *            drawing.
     * @throws Exception
     *             by pdfbox
     */
    protected void drawReletivePartAndMovePosition(
            final RenderContext renderContext, Drawable drawable,
            final LayoutHint layoutHint, final boolean movePosition)
            throws Exception {
        PDPageContentStream contentStream = renderContext.getContentStream();
        PageFormat pageFormat = renderContext.getPageFormat();
        
        //calculate how much indent is needed for left, center, and right alignment
        float offsetX = 0;
        if (layoutHint instanceof VerticalLayoutHint) {
            VerticalLayoutHint verticalLayoutHint = (VerticalLayoutHint) layoutHint;
            Alignment alignment = verticalLayoutHint.getAlignment();
            float horizontalExtraSpace = getTargetWidth(renderContext)
                    - drawable.getWidth();
            switch (alignment) {
            case Right:
                offsetX = horizontalExtraSpace; // - verticalLayoutHint.getMarginRight();
                break;
            case Center:
                offsetX = horizontalExtraSpace / 2f;
                break;
            default:
                offsetX = 0; //verticalLayoutHint.getMarginLeft();
                break;
            }
        }

        contentStream.saveGraphicsState();
        contentStream.addRect(0, pageFormat.getMarginBottom(), renderContext.getPageWidth(),
                renderContext.getHeight());
        CompatibilityHelper.clip(contentStream);

        drawable.draw(renderContext.getPdDocument(), contentStream,
                renderContext.getCurrentPosition().add(offsetX, 0),renderContext);

        contentStream.restoreGraphicsState();

        if (movePosition) {
            renderContext.movePositionBy(0, -drawable.getHeight());
        }
    }

    @SuppressWarnings("javadoc")
    /**
     * Removes empty space (e.g. empty lines) at the begin of a page. See
     * {@link Drawable#removeLeadingEmptyVerticalSpace()}
     *
     * @param drawable
     *            the drawable to process.
     * @param renderContext
     *            the render context.
     */
    protected void removeEmptySpaceIfTopOfPage(final Drawable drawable,
            final RenderContext renderContext) throws Exception {
        if (isRemoveLeadingEmptyVerticalSpace()
                && renderContext.isTopOfPage()) {
            drawable.removeLeadingEmptyVerticalSpace();
        }
    }

}
