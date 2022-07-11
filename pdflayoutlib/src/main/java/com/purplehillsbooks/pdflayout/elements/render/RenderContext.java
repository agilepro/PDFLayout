package com.purplehillsbooks.pdflayout.elements.render;

import java.awt.Color;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import com.purplehillsbooks.pdflayout.elements.ControlElement;
import com.purplehillsbooks.pdflayout.elements.Dimension;
import com.purplehillsbooks.pdflayout.elements.PDFDoc;
import com.purplehillsbooks.pdflayout.elements.Element;
import com.purplehillsbooks.pdflayout.elements.Orientation;
import com.purplehillsbooks.pdflayout.elements.PageFormat;
import com.purplehillsbooks.pdflayout.text.DrawContext;
import com.purplehillsbooks.pdflayout.text.DrawListener;
import com.purplehillsbooks.pdflayout.text.Position;
import com.purplehillsbooks.pdflayout.text.StyledText;
import com.purplehillsbooks.pdflayout.text.annotations.AnnotationDrawListener;
import com.purplehillsbooks.pdflayout.util.CompatibilityHelper;

/**
 * The render context is a container providing all state of the current
 * rendering process.
 */
public class RenderContext implements DrawContext, DrawListener {
    
    static public float HEADER_SIZE = 8;
    //static public PDType1Font HEADER_FONT = PDType1Font.TIMES_ROMAN;
    static public PDType1Font HEADER_FONT = PDType1Font.HELVETICA;
    //static public PDType1Font HEADER_FONT = PDType1Font.COURIER;

    private final PDFDoc document;
    public final PDDocument pdDocument;
    private PDPage currentPage;
    private int pageIndex = 0;
    public PDPageContentStream contentStream;
    private Position currentPosition;
    private Position markedPosition;
    private Position maxPositionOnPage;
    private Layout layout = new VerticalLayout();

    private PageFormat nextPageFormat;
    private PageFormat pageFormat;

    private AnnotationDrawListener annotationDrawListener;
    
    public String headerLeft;
    public String headerCenter;
    public String headerRight;
    
    public String footerLeft;
    public String footerCenter;
    public String footerRight;

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
    
    private void writeStyledTextAtPosition(StyledText txt, float x, float y, float width) throws Exception {
        contentStream.beginText();
        CompatibilityHelper.setTextTranslation(contentStream, x, y);
        contentStream.showText(txt.getText());
        contentStream.endText();
    }
    
    
    private String doPageNumbers(String source) {
        int tokenPos = source.indexOf("{#}");
        if (tokenPos<0) {
            return source;
        }
        String before = source.substring(0,tokenPos);
        String after = source.substring(tokenPos+3);
        return before + Integer.toString(pageIndex+1) + after;
    }
    
    private void drawHeaders() throws Exception {
        PageFormat pf = document.getPageFormat();
        PDRectangle mediaBox = pf.getMediaBox();
        

        float headerLine = mediaBox.getUpperRightY()-(pf.getMarginTop()/2)-HEADER_SIZE;
        float footerLine = mediaBox.getLowerLeftY()+(pf.getMarginBottom()/2);
        
        float leftSide = mediaBox.getLowerLeftX()+pf.getMarginLeft();
        float rightSide = mediaBox.getUpperRightX()-pf.getMarginRight();
        float center = (leftSide+rightSide)/2;
        
        if (document.showMargins) {
            contentStream.setStrokingColor(Color.red);
            contentStream.setLineWidth(0.5f);
            contentStream.addRect(leftSide, headerLine,
                    rightSide-leftSide, HEADER_SIZE);
            contentStream.addRect(leftSide, footerLine,
                    rightSide-leftSide, HEADER_SIZE);
            contentStream.stroke();
        }
        
        
        

        
        contentStream.setFont(HEADER_FONT, HEADER_SIZE);
        
        if (headerLeft!=null && headerLeft.length()>0) {
            StyledText t = new StyledText(doPageNumbers(headerLeft), HEADER_SIZE, HEADER_FONT);
            float width = t.getWidth();
            writeStyledTextAtPosition(t, leftSide, headerLine, width);
        }
        if (headerCenter!=null && headerCenter.length()>0) {
            StyledText t = new StyledText(doPageNumbers(headerCenter), HEADER_SIZE, HEADER_FONT);
            float width = t.getWidth();
            writeStyledTextAtPosition(t,center-(width/2), headerLine, width);
        }
        if (headerRight!=null && headerRight.length()>0) {
            StyledText t = new StyledText(doPageNumbers(headerRight), HEADER_SIZE, HEADER_FONT);
            float width = t.getWidth();
            writeStyledTextAtPosition(t, rightSide-width, headerLine, width);
        }
        if (footerLeft!=null && footerLeft.length()>0) {
            StyledText t = new StyledText(doPageNumbers(footerLeft), HEADER_SIZE, HEADER_FONT);
            float width = t.getWidth();
            writeStyledTextAtPosition(t, leftSide, footerLine, width);
        }
        if (footerCenter!=null && footerCenter.length()>0) {
            StyledText t = new StyledText(doPageNumbers(footerCenter), HEADER_SIZE, HEADER_FONT);
            float width = t.getWidth();
            writeStyledTextAtPosition(t, center-(width/2), footerLine, width);
        }
        if (footerRight!=null && footerRight.length()>0) {
            StyledText t = new StyledText(doPageNumbers(footerRight), HEADER_SIZE, HEADER_FONT);
            float width = t.getWidth();
            writeStyledTextAtPosition(t, rightSide-width, footerLine, width);
        }
        
    }

    /**
     * Closes the current page.
     *
     * @return <code>true</code> if the current page has not been closed before.
     * @throws Exception
     *             by pdfbox
     */
    private boolean closePage() throws Exception {
        if (contentStream != null) {
            
            drawHeaders();
            
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
        catch (Exception e) {
            throw new IOException("Unable to close the current page.", e);
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
