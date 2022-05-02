package com.purplehillsbooks.pdflayout.elements;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import com.purplehillsbooks.pdflayout.elements.render.RenderContext;
import com.purplehillsbooks.pdflayout.shape.Rect;
import com.purplehillsbooks.pdflayout.shape.Shape;
import com.purplehillsbooks.pdflayout.shape.Stroke;
import com.purplehillsbooks.pdflayout.text.DrawListener;
import com.purplehillsbooks.pdflayout.text.Position;
import com.purplehillsbooks.pdflayout.text.WidthRespecting;

/**
 * <p>The frame is a container for {@link Drawable} objects.  
 * Frames are the main device for constructing documents.  A document consists
 * of a list of Frame, and each Frame can have Frames and Paragraphs within it.
 * </p>
 * <p>
 * A Frame can have a fixed width, can have maximum width which limits the 
 * word wrapping within it, and can specify no width where it shrinks to size
 * of whatever it contains, and expands as necessary to the size of whatever
 * it is contained by.
 * </p>
 * <p>
 * In general the height of a frame is determined by what it contains and how
 * that flows due to word wrap, etc.  You can set the height of a Frame, and that 
 * should be considered a minimum height.  
 * (Currently not sure what happens if the contents exceeds the height.)
 * </p>
 * <p>
 * Frames can have margin values (top, bottom, left, right) causing white space 
 * outside the border, and can have padding (top, bottom, left, right) causing
 * white space inside the border around whatever it contains.  The margin allows
 * you to give space between frames, while the padding allows you to have 
 * some space around the contents inside the border.  By default, frames have
 * no margin and no padding, and can be infinitely nestable.
 * </p>
 * <p>
 * Frames can have a border drawn in various styles and any color.  The border
 * is NOT counted in the sizing of the frame, you can think of it as a line 
 * drawn on the edge between the margin and the padding, but if you decide to make
 * a really large line make sure either margin or padding make enough room for
 * the line otherwise you might have overlapping lines.
 * </p>
 * 
 * <pre>
 * +-------------------------+
 * |         Margin          |
 * |   +#################+   |
 * |   #     Padding     #   |
 * |   #   +---------+   #   |
 * |   #   | Content |   #   |
 * |   #   |         |   #   |
 * |   #   +---------+   #   |
 * |   #                 #   |
 * |   +#################+   |
 * |                         |
 * +-------------------------+
 * </pre>
 * 
 * <p>
 * All measurements are in points, defined as 72 points/inch, 0.0138 inch, or 0.3527 mm.
 * </p>
 * <p>
 * From the PDFDoc, you can get a root-most frame using newInteriorFrame which creates
 * a frame and adds it to the document.  From each Frame you can get another frame inside a
 * Frame by using newInteriorFrame as well.  In order to indent a section of the document, 
 * create a frame with left margin/padding.
 * </p>
 * <p>
 * The standard drawable has a width and a height. This is the EXTERIOR width and height.
 * Because the frame has margin and padding, the INTERIOR width and height can be smaller.
 * The border also has its own width and height in between these.
 * 
 */
public class Frame implements Element, Drawable, WidthRespecting, Dividable {

    private List<Drawable> innerList = new CopyOnWriteArrayList<Drawable>();

    private float paddingLeft;
    private float paddingRight;
    private float paddingTop;
    private float paddingBottom;

    private float marginLeft;
    private float marginRight;
    private float marginTop;
    private float marginBottom;

    private Shape shape = new Rect();
    private Stroke borderStroke = new Stroke();
    private Color borderColor;
    private Color backgroundColor;

    private float maxWidth = -1;

    private float givenWidth = 0;
    private float givenHeight = 0;
    private boolean startNewPage = false;
    private boolean keepTogether = false;
    private float needSpace = 0;
    

    private Position absolutePosition;

    /**
     * Creates an empty frame.
     */
    public Frame() {
        this(0, 0);
    }

    /**
     * Creates a frame containing the inner element.
     *
     * @param inner
     *            the item to contain.
     */
    public Frame(final Drawable inner) {
        this(inner, 0, 0);
    }
    public Frame(final Drawable inner, float width) {
        this(inner, width, 0);
    }

    /**
     * Creates a frame containing the inner element, optionally constraint by
     * the given dimensions. These contraints target the border-box of the
     * frame, means: the inner element plus padding plus border width, but not
     * the margin.
     *
     * @param inner
     *            the item to contain.
     * @param width
     *            the width to constrain the border-box of the frame to, or
     *            <code>null</code>.
     * @param height
     *            the height to constrain the border-box of the frame to, or
     *            <code>null</code>.
     */
    public Frame(final Drawable inner, final float width, final float height) {
        this(width, height);
        add(inner);
    }

    /**
     * Creates a frame constraint by the given dimensions. These contraints
     * target the border-box of the frame, means: the inner element plus padding
     * plus border width, but not the margin.
     *
     * @param width
     *            the width to constrain the border-box of the frame to, or
     *            <code>null</code>.
     */
    public Frame(final float width) {
        this(width, 0);
    }

    /**
     * Creates a frame constraint by the given dimensions. These contraints
     * target the border-box of the frame, means: the inner element plus padding
     * plus border width, but not the margin.
     *
     * @param width
     *            the width to constrain the border-box of the frame to, or
     *            <code>null</code>.
     * @param height
     *            the height to constrain the border-box of the frame to, or
     *            <code>null</code>.
     */
    public Frame(final float width, final float height) {
        this.givenWidth = width;
        this.givenHeight = height;
    }

    /**
     * Adds a drawable to the frame.
     * @param drawable
     */
    public void add(final Drawable drawable) {
        innerList.add(drawable);
    }

    protected void addAll(final Collection<Drawable> drawable) {
        innerList.addAll(drawable);
    }

    /**
     * @return the shape to use as border and/or background.
     */
    public Shape getShape() {
        return shape;
    }

    /**
     * Sets the shape to use as border and/or background.
     *
     * @param shape
     *            the shape to use.
     */
    public void setShape(Shape shape) {
        this.shape = shape;
    }

    /**
     * The stroke to use to draw the border.
     *
     * @return the stroke to use.
     */
    public Stroke getBorderStroke() {
        return borderStroke;
    }

    /**
     * Sets the stroke to use to draw the border.
     *
     * @param borderStroke
     *            the stroke to use.
     */
    public void setBorderStroke(Stroke borderStroke) {
        this.borderStroke = borderStroke;
    }

    /**
     * @return the width of the {@link #getBorderStroke()} or <code>0</code>.
     */
    protected float getBorderWidth() {
        return hasBorder() ? getBorderStroke().getLineWidth() : 0;
    }

    /**
     * @return if a {@link #getShape() shape}, a {@link #getBorderStroke()
     *         stroke} and {@link #getBorderColor() color} is set.
     */
    protected boolean hasBorder() {
        return getShape() != null && getBorderStroke() != null
                && getBorderColor() != null;
    }

    /**
     * @return the color to use to draw the border.
     */
    public Color getBorderColor() {
        return borderColor;
    }

    /**
     * Sets the color to use to draw the border.
     *
     * @param borderColor
     *            the border color.
     */
    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    /**
     * Convenience method for setting both border color and stroke.
     *
     * @param borderColor
     *            the border color.
     * @param borderStroke
     *            the stroke to use.
     */
    public void setBorder(Color borderColor, Stroke borderStroke) {
        setBorderColor(borderColor);
        setBorderStroke(borderStroke);
    }

    /**
     * @return the color to use to draw the background.
     */
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Sets the color to use to draw the background.
     *
     * @param backgroundColor
     *            the background color.
     */
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    /**
     * Copies all attributes but the inner drawable and size to the given frame.
     *
     * @param other
     *            the frame to copy the attributes to.
     */
    protected void copyAllButInnerAndSizeTo(final Frame other) {
        other.setShape(this.getShape());
        other.setBorderStroke(this.getBorderStroke());
        other.setBorderColor(this.getBorderColor());
        other.setBackgroundColor(this.getBackgroundColor());

        other.setPaddingBottom(this.getPaddingBottom());
        other.setPaddingLeft(this.getPaddingLeft());
        other.setPaddingRight(this.getPaddingRight());
        other.setPaddingTop(this.getPaddingTop());

        other.setMarginBottom(this.getMarginBottom());
        other.setMarginLeft(this.getMarginLeft());
        other.setMarginRight(this.getMarginRight());
        other.setMarginTop(this.getMarginTop());
    }

    /**
     * @return the left padding
     */
    public float getPaddingLeft() {
        return paddingLeft;
    }

    /**
     * Sets the left padding.
     *
     * @param paddingLeft
     *            left padding.
     */
    public void setPaddingLeft(float paddingLeft) {
        this.paddingLeft = paddingLeft;
    }

    /**
     * @return the right padding
     */
    public float getPaddingRight() {
        return paddingRight;
    }

    /**
     * Sets the right padding.
     *
     * @param paddingRight
     *            right padding.
     */
    public void setPaddingRight(float paddingRight) {
        this.paddingRight = paddingRight;
    }

    /**
     * @return the top padding
     */
    public float getPaddingTop() {
        return paddingTop;
    }

    /**
     * Sets the top padding.
     *
     * @param paddingTop
     *            top padding.
     */
    public void setPaddingTop(float paddingTop) {
        this.paddingTop = paddingTop;
    }

    /**
     * @return the bottom padding
     */
    public float getPaddingBottom() {
        return paddingBottom;
    }

    /**
     * Sets the bottom padding.
     *
     * @param paddingBottom
     *            bottom padding.
     */
    public void setPaddingBottom(float paddingBottom) {
        this.paddingBottom = paddingBottom;
    }

    /**
     * Sets the padding.
     *
     * @param left
     *            left padding.
     * @param right
     *            right padding.
     * @param top
     *            top padding.
     * @param bottom
     *            bottom padding.
     */
    public void setPadding(float left, float right, float top, float bottom) {
        setPaddingLeft(left);
        setPaddingRight(right);
        setPaddingTop(top);
        setPaddingBottom(bottom);
    }

    /**
     * @return the left margin
     */
    public float getMarginLeft() {
        return marginLeft;
    }

    /**
     * Sets the left margin.
     *
     * @param marginLeft
     *            left margin.
     */
    public void setMarginLeft(float marginLeft) {
        this.marginLeft = marginLeft;
    }

    /**
     * @return the right margin
     */
    public float getMarginRight() {
        return marginRight;
    }

    /**
     * Sets the right margin.
     *
     * @param marginRight
     *            right margin.
     */
    public void setMarginRight(float marginRight) {
        this.marginRight = marginRight;
    }

    /**
     * @return the top margin
     */
    public float getMarginTop() {
        return marginTop;
    }

    /**
     * Sets the top margin.
     *
     * @param marginTop
     *            top margin.
     */
    public void setMarginTop(float marginTop) {
        this.marginTop = marginTop;
    }

    /**
     * @return the bottom margin
     */
    public float getMarginBottom() {
        return marginBottom;
    }

    /**
     * Sets the bottom margin.
     *
     * @param marginBottom
     *            bottom margin.
     */
    public void setMarginBottom(float marginBottom) {
        this.marginBottom = marginBottom;
    }

    /**
     * Sets the margin.
     *
     * @param left
     *            left margin.
     * @param right
     *            right margin.
     * @param top
     *            top margin.
     * @param bottom
     *            bottom margin.
     */
    public void setMargin(float left, float right, float top, float bottom) {
        setMarginLeft(left);
        setMarginRight(right);
        setMarginTop(top);
        setMarginBottom(bottom);
    }

    /**
     * @return the sum of left/right padding and border width.
     */
    protected float getHorizontalShapeSpacing() {
        return 2 * getBorderWidth() + getPaddingLeft() + getPaddingRight();
    }

    /**
     * @return the sum of top/bottom padding and border width.
     */
    protected float getVerticalShapeSpacing() {
        return 2 * getBorderWidth() + getPaddingTop() + getPaddingBottom();
    }

    /**
     * @return the sum of left/right margin, padding and border width.
     */
    protected float getHorizontalSpacing() {
        return getMarginLeft() + getMarginRight() + getHorizontalShapeSpacing();
    }

    /**
     * @return the sum of top/bottom margin, padding and border width.  This is specifically
     *         all the vertical spacing that is NOT from the content of the frame.   It is all
     *         the border spacing.
     */
    protected float getVerticalExtraSpace() {
        return getMarginTop() + getMarginBottom() + getVerticalShapeSpacing();
    }
    
    /*
     * @return the sum of whitespace BEFORE any printable content.
     */
    protected float getLeadingWhiteSpace() {
        float myTop = getMarginTop() + getPaddingTop();
        if (innerList.isEmpty())  {
            return myTop;
        }
        Drawable firstChild = innerList.get(0);
        if (firstChild instanceof Frame) {
            return myTop + ((Frame)firstChild).getLeadingWhiteSpace();
        }
        return myTop;
    }

    /**
     * @param val Set the fixed total EXTERIOR height of the frame.  Remember, the border will be 
     * smaller than this by the margin amount, and the content will be smaller by both
     * the margin and the padding.
     */
    public void setGivenHeight(float val) {
        givenHeight = val;
    }
    /**
     * @param val Set the fixed total EXTERIOR width of the frame.  Remember, the border will be 
     * smaller than this by the margin amount, and the content will be smaller by both
     * the margin and the padding.
     */
    public void setGivenWidth(float val) {
        givenWidth = val;
    }

    @Override
    public float getWidth() throws Exception {
        if (givenWidth>0) {
            return givenWidth;
        }
        return getMaxWidth(innerList) + getHorizontalSpacing();
    }

    protected float getMaxWidth(List<Drawable> drawableList) throws Exception {
        float max = 0;
        if (drawableList != null) {
            for (Drawable inner : drawableList) {
                max = Math.max(max, inner.getWidth());
            }
        }
        return max;
    }

    @Override
    public float getHeight() throws Exception {
        if (givenHeight>0) {
            return givenHeight;
        }
        return getHeight(innerList) + getVerticalExtraSpace();
    }

    private static float getHeight(List<Drawable> drawableList) throws Exception {
        float height = 0;
        if (drawableList != null) {
            for (Drawable inner : drawableList) {
                height += inner.getHeight();
            }
        }
        return height;
    }

    @Override
    public Position getAbsolutePosition() throws Exception {
        return absolutePosition;
    }

    /**
     * Sets th absolute position.
     *
     * @param absolutePosition
     *            the absolute position to use, or <code>null</code>.
     */
    public void setAbsolutePosition(Position absolutePosition) {
        this.absolutePosition = absolutePosition;
    }

    @Override
    public float getMaxWidth() {
        return maxWidth;
    }

    @Override
    public void setMaxWidth(float maxWidth) {
        this.maxWidth = maxWidth;
        
        float interiorMaxWidth = maxWidth - this.getHorizontalSpacing();
        if (givenWidth > 0) {
            //not sure what to do when the width has been set too big.
            //for now we just assume given width is sacrosanct
            interiorMaxWidth = givenWidth - this.getHorizontalSpacing();
        }

        for (Drawable inner : innerList) {
            if (inner instanceof WidthRespecting) {
                ((WidthRespecting) inner).setMaxWidth(interiorMaxWidth);
            }
        }
    }
    
    /**
     * Setting StartNewPage causes a page-break before this frame is processed
     * causing this frame to appear as the first thing on a new page.
     * By default a frame will not create a new page.
     * 
     * @param val Passing a 'true' value sets the frame to be on a new page
     *     Passing 'false' turns this off and allow the frame to be placed
     *     immediately after the previous frame (if there is room)
     */
    public void setStartNewPage(boolean val) {
        startNewPage = val;
    }
    public boolean getStartNewPage() {
        return startNewPage;
    }
    
    /**
     * setting KeepTogether causes the formatted height of the frame to 
     * be considered.  If the entire frame can fit on the current page
     * at the current position it will be placed there.  But if the current
     * position is too close to the bottom of the page, then the entire frame
     * will be moved to the next page.
     * 
     * @param val Passing a 'true' value sets so that the frame will always
     *     be kept together
     */
    public void setKeepTogether(boolean val) {
        keepTogether = val;
    }
    public boolean getKeepTogether() {
        return keepTogether;
    }

    
    /**
     * A frame can be set to require a certain amount of space.  If there is 
     * less than that amount of space on the page, then the frame will be moved
     * to the next page.   The space being reserved is not white space, but actually
     * space for the contents of this frame plus potentially other frames and paragraphs.
     * This does not depend on the size of the frame.  It simply allows you to 
     * prevent a frame from appearing at the bottom of the page, and if too close
     * to the bottom, appear on the next page instead.
     * 
     * @param val the measure in points that you would like to reserve for this frame
     *    and subsequent frames.
     */
    public void setNeedSpace(float val) {
        needSpace = val;
    }
    public float getNeedSpace() {
        return needSpace;
    }


    /**
     * Propagates the max width to the inner items if there is a given size, but
     * no absolute position.  Given width will override any maxwidth specified
     * for any other reason.
     */
    protected void propagateMaxWidthToChildren() throws Exception {
        if (getAbsolutePosition() == null && givenWidth>0) {
            setMaxWidth(givenWidth);
        }
    }

    @Override
    public void draw(PDDocument pdDocument, PDPageContentStream contentStream,
            Position upperLeft, DrawListener drawListener) throws Exception {

        propagateMaxWidthToChildren();

        float halfBorderWidth = 0;
        if (getBorderWidth() > 0) {
            halfBorderWidth = getBorderWidth() / 2f;
        }
        upperLeft = upperLeft.add(getMarginLeft() + halfBorderWidth,
                -getMarginTop() - halfBorderWidth);

        if (getShape() != null) {
            float shapeWidth = getWidth() - getMarginLeft() - getMarginRight()
                    - getBorderWidth();
            float shapeHeight = getHeight() - getMarginTop()
                    - getMarginBottom() - getBorderWidth();

            if (getBackgroundColor() != null) {
                getShape().fill(pdDocument, contentStream, upperLeft,
                        shapeWidth, shapeHeight, getBackgroundColor(),
                        drawListener);
            }
            if (hasBorder()) {
                getShape().draw(pdDocument, contentStream, upperLeft,
                        shapeWidth, shapeHeight, getBorderColor(),
                        getBorderStroke(), drawListener);
            }
        }

        Position innerUpperLeft = upperLeft.add(getPaddingLeft()
                + halfBorderWidth, -getPaddingTop() - halfBorderWidth);

        for (Drawable inner : innerList) {
            inner.draw(pdDocument, contentStream, innerUpperLeft, drawListener);
            innerUpperLeft = innerUpperLeft.add(0, -inner.getHeight());
        }
    }

    @Override
    public Drawable removeLeadingEmptyVerticalSpace() throws Exception {
        if (innerList.size() > 0) {
            Drawable drawableWithoutLeadingVerticalSpace = innerList.get(0)
                    .removeLeadingEmptyVerticalSpace();
            innerList.set(0, drawableWithoutLeadingVerticalSpace);
        }
        return this;
    }

    @Override
    public Divided divide(float remainingHeight, RenderContext renderContext, boolean topOfPage) throws Exception {
        propagateMaxWidthToChildren();
        
        float fullHeight = getHeight();
        float nextPageHeight = renderContext.getHeight();

        if (startNewPage || remainingHeight<needSpace  || remainingHeight <= getLeadingWhiteSpace() 
                || (keepTogether && remainingHeight<fullHeight && fullHeight<nextPageHeight)) {
            // in all these cases, fill the rest of the page with white space, and move to new page.
            // either:
            //   1. frame is marked as start new page, causing a new page break
            //   2. frame requires some space, but that space is not available
            //   3. not enough room for even just the margin/padding of the frame.
            //   4. frame marked keepTogether and there is not enough room on this page, 
            //      but there is enough room on the following page (avoid infinite loops)
            // so just move the entire frame to the next page, and fill this page
            // with white vertical space.
            if (!topOfPage) {
                //only do this if not already at the top of the page.  If the page is 
                //clean (empty) then there is no benefit in moving to the next page
                return new Divided(new VerticalSpacer(remainingHeight), this);
            }
        }

        // we have to account for the extra white space at the top of the frame
        float spaceLeft = remainingHeight - getMarginTop() - getPaddingTop();

        DividedList dividedList = divideList(innerList, spaceLeft, renderContext, topOfPage);
        
        List<Drawable> headList = dividedList.getHead();
        if (headList.size()>0) {
            //the element being divided below is not at the top of the page if
            //there is anything in head list before it on this page
            topOfPage = false;
        }

        float spaceLeftForDivided = spaceLeft - getHeight(dividedList.getHead());
        Divided divided = null;

        if (dividedList.getDrawableToDivide() != null) {
            Dividable innerDividable = null;
            if (dividedList.getDrawableToDivide() instanceof Dividable) {
                innerDividable = (Dividable) dividedList.getDrawableToDivide();
            } else {
                innerDividable = new Cutter(dividedList.getDrawableToDivide());
            }
            // some space left on this page for the inner element
            divided = innerDividable.divide(spaceLeftForDivided, renderContext, topOfPage);
        }

        Float firstHeight = givenHeight<=0 ? 0 : remainingHeight;
        Float tailHeight = givenHeight<=0 ? 0 : givenHeight - spaceLeft;

        // create head sub frame
        Frame first = new Frame(givenWidth, firstHeight);
        copyAllButInnerAndSizeTo(first);
        first.setPaddingBottom(0);   //bottom padding eliminated because this is split
        first.setMarginBottom(0);    //bottom margin eliminated because this is split
        if (dividedList.getHead() != null) {
            first.addAll(dividedList.getHead());
        }
        if (divided != null) {
            first.add(divided.getFirst());
        }

        // create tail sub frame
        Frame tail = new Frame(givenWidth, tailHeight);
        copyAllButInnerAndSizeTo(tail);
        tail.setPaddingTop(0);   //top padding eliminated because this is split
        tail.setMarginTop(0);    //top margin eliminated because this is split

        if (divided != null) {
            tail.add(divided.getTail());
        }
        if (dividedList.getTail() != null) {
            tail.addAll(dividedList.getTail());
        }

        return new Divided(first, tail);
    }

    private DividedList divideList(List<Drawable> items, float spaceLeft, RenderContext renderContext, boolean topOfPage)
            throws Exception {
        List<Drawable> head = new ArrayList<Drawable>();
        List<Drawable> tail = null;
        Drawable toDivide = null;

        float tmpHeight = 0;
        int index = 0;
        while (tmpHeight < spaceLeft && index<items.size()) {
            Drawable drawMe = items.get(index);
            float    fullHeight = drawMe.getHeight();
            
            if (!topOfPage && drawMe instanceof Frame) {
                Frame drawMeFrame = (Frame)drawMe;
                if (drawMeFrame.getStartNewPage() || spaceLeft-tmpHeight<drawMeFrame.getNeedSpace()) {
                    //force it to take up more than the rest of the page so it 
                    //gets moved or split.  Don't move if topOfPage already
                    head.add(new VerticalSpacer(fullHeight-tmpHeight));
                    tmpHeight = spaceLeft;
                    break;  //avoid including this in the set
                }
            }

            if (fullHeight + tmpHeight > spaceLeft) {
                break;
            }
            
            head.add(drawMe);
            ++index;
            tmpHeight += fullHeight;
            topOfPage = false;
        }
        
        if (tmpHeight == spaceLeft) {
            // page is filled perfectly, so we can split between two drawables
            // and leave the toDivide null

            if (index < items.size()) {
                tail = items.subList(index, items.size());
            }
            else {
                tail = new ArrayList<Drawable>();
            }
        }
        else {
            if (index < items.size()) {
                toDivide = items.get(index);
            }
            if (index + 1 < items.size()) {
                tail = items.subList(index + 1, items.size());
            }
            else {
                tail = new ArrayList<Drawable>();
            }
        }

        return new DividedList(head, toDivide, tail);
    }

    public static class DividedList {
        private List<Drawable> head;
        private Drawable drawableToDivide;
        private List<Drawable> tail;

        public DividedList(List<Drawable> head, Drawable drawableToDivide,
                List<Drawable> tail) {
            this.head = head;
            this.drawableToDivide = drawableToDivide;
            this.tail = tail;
        }

        public List<Drawable> getHead() {
            return head;
        }

        public Drawable getDrawableToDivide() {
            return drawableToDivide;
        }

        public List<Drawable> getTail() {
            return tail;
        }

    }
    
    /**
     * Returns a frame that would be inside this frame.   If this frame has a set
     * width or height, then the interior frame is set to a width enough smaller that it will
     * fit including margin and padding.   If width and height not set, then new frame has no
     * given width/height which means it will be just larger than whatever it contains.
     * 
     * If maxWidth is set, then this too is reflected into the new frame with enough space
     * that it will fit including margin and padding.
     */
    public Frame newInteriorFrame() {
        float width = 0;
        float height = 0;
        if (givenWidth>0) {
            width = givenWidth - marginLeft - paddingLeft - marginRight - paddingRight;
        }
        if (givenHeight>0) {
            height = givenHeight - marginTop - paddingTop - marginBottom - paddingBottom;
        }
        Frame ret = new Frame(width,height);
        this.add(ret);
        if (maxWidth>0) {
            ret.setMaxWidth(maxWidth - marginLeft - paddingLeft - marginRight - paddingRight);
        }
        return ret;
    }
    
    /**
     * Creates a new Paragraph, adds it to this frame, and returns it.
     */
    public Paragraph getNewParagraph()  {
        Paragraph para = new Paragraph();
        this.add(para);
        return para;
    }
    

}
