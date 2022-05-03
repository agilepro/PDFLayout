package com.purplehillsbooks.pdflayout.elements.render;

import java.awt.Color;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import com.purplehillsbooks.pdflayout.elements.ControlElement;
import com.purplehillsbooks.pdflayout.elements.Dimension;
import com.purplehillsbooks.pdflayout.elements.PDFDoc;
import com.purplehillsbooks.pdflayout.elements.Element;
import com.purplehillsbooks.pdflayout.elements.Orientation;
import com.purplehillsbooks.pdflayout.elements.PageFormat;
import com.purplehillsbooks.pdflayout.elements.PositionControl;
import com.purplehillsbooks.pdflayout.elements.PositionControl.MarkPosition;
import com.purplehillsbooks.pdflayout.elements.PositionControl.MovePosition;
import com.purplehillsbooks.pdflayout.elements.PositionControl.SetPosition;
import com.purplehillsbooks.pdflayout.text.DrawContext;
import com.purplehillsbooks.pdflayout.text.DrawListener;
import com.purplehillsbooks.pdflayout.text.Position;
import com.purplehillsbooks.pdflayout.text.annotations.AnnotationDrawListener;
import com.purplehillsbooks.pdflayout.util.CompatibilityHelper;

/**
 * The render context is a container providing all state of the current
 * rendering process.
 */
public class RenderContext implements DrawContext, DrawListener {

    private final PDFDoc document;
    private final PDDocument pdDocument;
    private PDPage currentPage;
    private int pageIndex = 0;
    private PDPageContentStream contentStream;
    private Position currentPosition;
    private Position markedPosition;
    private Position maxPositionOnPage;
    private Layout layout = new VerticalLayout();

    private PageFormat nextPageFormat;
    private PageFormat pageFormat;

    private AnnotationDrawListener annotationDrawListener;

    /**
     * Creates a render context.
     *
     * @param document
     *            the document to render.
     * @param pdDocument
     *            the underlying pdfbox document.
     * @throws Exception
     *             by pdfbox.
     */
    public RenderContext(PDFDoc document, PDDocument pdDocument)
            throws Exception {
        this.document = document;
        this.pdDocument = pdDocument;
        this.pageFormat = document.getPageFormat();
        this.annotationDrawListener = new AnnotationDrawListener(this);
        newPage();
    }

    /**
     * @return the current {@link Layout} used for rendering.
     */
    public Layout getLayout() {
        return layout;
    }

    /**
     * Sets the current {@link Layout} used for rendering.
     *
     * @param layout
     *            the new layout.
     */
    public void setLayout(Layout layout) {
        this.layout = layout;
        resetPositionToLeftEndOfPage();
    }

    public void setPageFormat(final PageFormat pageFormat) {
        if (pageFormat == null) {
            this.pageFormat = document.getPageFormat();
        } else {
            this.pageFormat = pageFormat;
        }
    }

    public PageFormat getPageFormat() {
        return pageFormat;
    }

    /**
     * @return the upper left position in the document respecting the
     *         {@link PDFDoc document} margins.
     */
    public Position getUpperLeft() {
        return new Position(getPageFormat().getMarginLeft(), getPageHeight()
                - getPageFormat().getMarginTop());
    }
    
    /**
     * This is intended to test whether anything has been written to the 
     * page so that rendering can treat things at the top of the page
     * differently than those positions further down.  The assumption 
     * is that every write will leave the position moved.  If there ever
     * is a situation where writing does not move the position, then a 
     * better test will be needed.
     * 
     * @return true if nothing written to the current page yet
     */
    public boolean isTopOfPage() {
        return currentPosition.equals(getUpperLeft());
    }

    /**
     * @return the lower right position in the document respecting the
     *         {@link PDFDoc document} margins.
     */
    public Position getLowerRight() {
        return new Position(getPageWidth() - getPageFormat().getMarginRight(),
                getPageFormat().getMarginBottom());
    }

    /**
     * @return the current rendering position in pdf coord space (origin in
     *         lower left corner).
     */
    public Position getCurrentPosition() {
        return currentPosition;
    }

    /**
     * @return the {@link PositionControl#MARKED_POSITION}.
     */
    public Position getMarkedPosition() {
        return markedPosition;
    }

    protected void setMarkedPosition(Position markedPosition) {
        this.markedPosition = markedPosition;
    }

    /**
     * Moves the {@link #getCurrentPosition() current position} relatively by
     * the given offset.
     *
     * @param x
     *            to move horizontally.
     * @param y
     *            to move vertically.
     */
    public void movePositionBy(final float x, final float y) {
        currentPosition = currentPosition.add(x, y);
    }

    /**
     * Resets the position to {@link #getUpperLeft()}.
     */
    public void resetPositionToUpperLeft() {
        currentPosition = getUpperLeft();
    }

    /**
     * Resets the position to the x of {@link #getUpperLeft()} while keeping the
     * current y.
     */
    public void resetPositionToLeft() {
        currentPosition = new Position(getUpperLeft().getX(),
                currentPosition.getY());
    }

    /**
     * Resets the position to the x of {@link #getUpperLeft()} and the
     * y of {@link #getMaxPositionOnPage()}.
     */
    protected void resetPositionToLeftEndOfPage() {
        currentPosition = new Position(getUpperLeft().getX(),
                getMaxPositionOnPage().getY());
    }

    /**
     * @return the orientation of the current page
     */
    protected Orientation getPageOrientation() {
        if (getPageWidth() > getPageHeight()) {
            return Orientation.Landscape;
        }
        return Orientation.Portrait;
    }

    /**
     * @return <code>true</code> if the page is rotated by 90/270 degrees.
     */
    public boolean isPageTilted() {
        return CompatibilityHelper.getPageRotation(currentPage) == 90
                || CompatibilityHelper.getPageRotation(currentPage) == 270;
    }

    /**
     * @return the page' width, or - if {@link #isPageTilted() rotated} - the
     *         height.
     */
    public float getPageWidth() {
        if (isPageTilted()) {
            return currentPage.getMediaBox().getHeight();
        }
        return currentPage.getMediaBox().getWidth();
    }

    /**
     * @return the page' height, or - if {@link #isPageTilted() rotated} - the
     *         width.
     */
    public float getPageHeight() {
        if (isPageTilted()) {
            return currentPage.getMediaBox().getWidth();
        }
        return currentPage.getMediaBox().getHeight();
    }

    /**
     * @return the {@link #getPageWidth() width of the page} respecting the
     *         margins.
     */
    public float getWidth() {
        return getPageWidth() - getPageFormat().getMarginLeft()
                - getPageFormat().getMarginRight();
    }

    /**
     * @return the {@link #getPageHeight() height of the page} respecting the
     *         margins.
     */
    public float getHeight() {
        return getPageHeight() - getPageFormat().getMarginTop()
                - getPageFormat().getMarginBottom();
    }

    /**
     * @return the remaining height on the page.
     */
    public float getRemainingHeight() {
        return getCurrentPosition().getY() - getPageFormat().getMarginBottom();
    }

    /**
     * @return the document.
     */
    public PDFDoc getDocument() {
        return document;
    }

    /**
     * @return the PDDocument.
     */
    @Override
    public PDDocument getPdDocument() {
        return pdDocument;
    }

    @Override
    public PDPage getCurrentPage() {
        return currentPage;
    }

    @Override
    public PDPageContentStream getCurrentPageContentStream() {
        return getContentStream();
    }

    /**
     * @return the current PDPageContentStream.
     */
    public PDPageContentStream getContentStream() {
        return contentStream;
    }

    /**
     * @return the current page index (starting from 0).
     */
    public int getPageIndex() {
        return pageIndex;
    }


    public boolean startRendering(Element element, LayoutHint layoutHint) throws Exception {
        
        boolean success = layout.renderWithHint(this, element, layoutHint);
        
        if (success) {
            return true;
        }
        if (element == ControlElement.NEWPAGE) {
            newPage();
            return true;
        }
        if (element instanceof PositionControl) {
            return render((PositionControl) element);
        }
        if (element instanceof PageFormat) {
            nextPageFormat = (PageFormat) element;
            return true;
        }
        if (element instanceof Layout) {
            setLayout((Layout) element);
            return true;
        }
        return false;
    }

    protected boolean render(final PositionControl positionControl) {
        if (positionControl instanceof MarkPosition) {
            setMarkedPosition(getCurrentPosition());
            return true;
        }
        if (positionControl instanceof SetPosition) {
            SetPosition setPosition = (SetPosition) positionControl;
            Float x = setPosition.getX();
            if (x == PositionControl.MARKED_POSITION) {
                x = getMarkedPosition().getX();
            }
            if (x == null) {
                x = getCurrentPosition().getX();
            }
            Float y = setPosition.getY();
            if (y == PositionControl.MARKED_POSITION) {
                y = getMarkedPosition().getY();
            }
            if (y == null) {
                y = getCurrentPosition().getY();
            }
            Position newPosition = new Position(x, y);
            currentPosition = newPosition;
            return true;
        }
        if (positionControl instanceof MovePosition) {
            MovePosition movePosition = (MovePosition) positionControl;
            movePositionBy(movePosition.getX(), movePosition.getY());
            return true;
        }
        return false;
    }

    /**
     * Triggers a new page.
     *
     * @throws Exception
     *             by pdfbox
     */
    public void newPage() throws Exception {
        if (closePage()) {
            ++pageIndex;
        }
        if (nextPageFormat != null) {
            setPageFormat(nextPageFormat);
        }

        this.currentPage = new PDPage(getPageFormat().getMediaBox());
        this.pdDocument.addPage(currentPage);
        this.contentStream = CompatibilityHelper
                .createAppendablePDPageContentStream(pdDocument, currentPage);

        // fix orientation
        if (getPageOrientation() != getPageFormat().getOrientation()) {
            if (isPageTilted()) {
                currentPage.setRotation(0);
            } else {
                currentPage.setRotation(90);
            }
        }

        if (isPageTilted()) {
            CompatibilityHelper.transform(contentStream, 0, 1, -1, 0,
                    getPageHeight(), 0);
        }

        resetPositionToUpperLeft();
        resetMaxPositionOnPage();
        document.beforePage(this);
        annotationDrawListener.beforePage(this);
        
        if (document.showMargins) {
            PageFormat pf = document.getPageFormat();
            Dimension dim = pf.getInteriorDimension();
            contentStream.setStrokingColor(Color.red);
            //contentStream.setLineDashPattern(new float[]{9}, 0);
            contentStream.setLineWidth(0.5f);
            contentStream.addRect(pf.getMarginLeft(), pf.getMarginBottom(),
                    dim.getWidth(), dim.getHeight());
            contentStream.stroke();
        }
    }

    /**
     * Closes the current page.
     *
     * @return <code>true</code> if the current page has not been closed before.
     * @throws Exception
     *             by pdfbox
     */
    public boolean closePage() throws Exception {
        if (contentStream != null) {

            annotationDrawListener.afterPage(this);
            document.afterPage(this);

            if (getPageFormat().getRotation() != 0) {
                int currentRotation = CompatibilityHelper
                        .getPageRotation(getCurrentPage());
                getCurrentPage().setRotation(
                        currentRotation + getPageFormat().getRotation());
            }

            contentStream.close();
            contentStream = null;
            return true;
        }
        return false;
    }


    public void close() throws IOException {
        try {
            closePage();
            annotationDrawListener.afterRender();
        }
        catch (IOException ioe) {
            throw ioe;
        }
        catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public void drawn(Object drawnObject, Position upperLeft, float width,
            float height) {
        updateMaxPositionOnPage(upperLeft, width, height);
        annotationDrawListener.drawn(drawnObject, upperLeft, width, height);
    }

    /**
     * Updates the maximum right resp. bottom position on the page.
     * @param upperLeft obvious
     * @param width obvious
     * @param height obvious
     */
    protected void updateMaxPositionOnPage(Position upperLeft, float width,
            float height) {
        maxPositionOnPage = new Position(Math.max(maxPositionOnPage.getX(),
                upperLeft.getX() + width), Math.min(maxPositionOnPage.getY(),
                upperLeft.getY() - height));
    }

    /**
     * Resets the maximumn position to upper left.
     */
    protected void resetMaxPositionOnPage() {
        maxPositionOnPage = getUpperLeft();
    }

    /**
     * @return the maximum right and bottom position of all
     * objects rendered on this page so far.
     */
    protected Position getMaxPositionOnPage() {
        return maxPositionOnPage;
    }

}
