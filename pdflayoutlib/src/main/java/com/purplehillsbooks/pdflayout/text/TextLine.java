package com.purplehillsbooks.pdflayout.text;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import com.purplehillsbooks.pdflayout.util.CompatibilityHelper;

/**
 * A text of line containing only {@link StyledText}s. It may be terminated by a
 * {@link #getNewLine() new line}.
 */
public class TextLine extends TextSequence {


    private final List<StyledText> styledTextList = new ArrayList<StyledText>();
    private NewLine newLine;
    
    float ascent = 0;
    float height = 0;
    float width = 0;

    private void clearCache() {
        ascent = 0;
        height = 0;
        width = 0;
    }


    /**
     * Adds a styled text.
     *
     * @param fragment
     *            the fagment to add.
     */
    public void add(final StyledText fragment) {
        styledTextList.add(fragment);
        clearCache();
    }

    /**
     * Adds all styled texts of the given text line.
     *
     * @param textLine
     *            the text line to add.
     */
    public void add(final TextLine textLine) {
        for (StyledText fragment : textLine.getStyledTexts()) {
            add(fragment);
        }
    }

    /**
     * @return the terminating new line, may be <code>null</code>.
     */
    public NewLine getNewLine() {
        return newLine;
    }

    /**
     * Sets the new line.
     *
     * @param newLine
     *            the new line.
     */
    public void setNewLine(NewLine newLine) {
        this.newLine = newLine;
        clearCache();
    }

    /**
     * @return the styled texts building up this line.
     */
    public List<StyledText> getStyledTexts() {
        return Collections.unmodifiableList(styledTextList);
    }

    @Override
    public Iterator<TextFragment> iterator() {
        return new TextLineIterator(styledTextList.iterator(), newLine);
    }

    /**
     * @return <code>true</code> if the line contains neither styled text nor a
     *         new line.
     */
    public boolean isEmpty() {
        return styledTextList.isEmpty() && newLine == null;
    }

    @Override
    public float getWidth() throws Exception {
        if (width == 0) {
            for (TextFragment fragment : this) {
                width += fragment.getWidth();
            }
        }
        return width;
    }

    @Override
    public float getHeight() throws Exception {
        if (height == 0) {
            for (TextFragment fragment : this) {
                height = Math.max(height, fragment.getHeight());
            }
        }
        return height;
    }

    /**
     * @return the (max) ascent of this line.
     * @throws Exception
     *             by pdfbox.
     */
    protected float getAscent() throws Exception {
        if (ascent == 0) {
            for (TextFragment fragment : this) {
                float currentAscent = fragment.getFontDescriptor().getSize()
                        * fragment.getFontDescriptor().getFont()
                                .getFontDescriptor().getAscent() / 1000;
                ascent = Math.max(ascent, currentAscent);
            }
        }
        return ascent;
    }

    @Override
    public void drawText(PDPageContentStream contentStream, Position upperLeft,
            Alignment alignment, DrawListener drawListener) throws Exception {
        drawAligned(contentStream, upperLeft, alignment, getWidth(), drawListener);
    }

    public void drawAligned(PDPageContentStream contentStream, Position upperLeft,
            Alignment alignment, float availableLineWidth,
            DrawListener drawListener) throws Exception {
        contentStream.saveGraphicsState();
        contentStream.beginText();

        float x = upperLeft.getX();
        float y = upperLeft.getY() - getAscent(); // the baseline
        float offset = TextSequenceUtil.getOffset(this, availableLineWidth, alignment);
        x += offset;
        CompatibilityHelper.setTextTranslation(contentStream, x, y);
        float extraWordSpacing = 0;
        if (alignment == Alignment.Justify && (getNewLine() instanceof WrappingNewLine) ){
            extraWordSpacing = (availableLineWidth - getWidth()) / (styledTextList.size()-1);
        }

        FontDescriptor lastFontDesc = null;
        float lastBaselineOffset = 0;
        Color lastColor = null;
        float gap = 0;
        for (StyledText styledText : styledTextList) {
            if (!styledText.getFontDescriptor().equals(lastFontDesc)) {
                lastFontDesc = styledText.getFontDescriptor();
                contentStream.setFont(lastFontDesc.getFont(),
                        lastFontDesc.getSize());
            }
            if (!styledText.getColor().equals(lastColor)) {
                lastColor = styledText.getColor();
                contentStream.setNonStrokingColor(lastColor);
            }
            if (styledText.getLeftMargin() > 0) {
                gap += styledText.getLeftMargin();
            }

            boolean moveBaseline = styledText.getBaselineOffset() != lastBaselineOffset;
            if (moveBaseline || gap > 0) {
                float baselineDelta = lastBaselineOffset - styledText.getBaselineOffset();
                lastBaselineOffset = styledText.getBaselineOffset();
                CompatibilityHelper.moveTextPosition(contentStream, gap, baselineDelta);
                x += gap;
            }
            if (styledText.getText().length() > 0) {
                CompatibilityHelper.showText(contentStream,
                        styledText.getText());
            }

            if (drawListener != null) {
                float currentUpperLeft = y + styledText.getAsent();
                drawListener.drawn(styledText,
                        new Position(x, currentUpperLeft),
                        styledText.getWidthWithoutMargin(),
                        styledText.getHeight());
            }
            x += styledText.getWidthWithoutMargin();

            gap = extraWordSpacing;
            if (styledText.getRightMargin() > 0) {
                gap += styledText.getRightMargin();
            }
        }
        contentStream.endText();
        contentStream.restoreGraphicsState();
    }

    @Override
    public String toString() {
        return "TextLine [styledText=" + styledTextList + ", newLine="
                + newLine + "]";
    }

    /**
     * An iterator for the text line. See {@link TextLine#iterator()}.
     */
    private static class TextLineIterator implements Iterator<TextFragment> {

        private Iterator<StyledText> styledText;
        private NewLine newLine;

        /**
         * Creates an iterator of the given styled texts with an optional
         * trailing new line.
         *
         * @param styledText
         *            the text fragments to iterate.
         * @param newLine
         *            the optional trailing new line.
         */
        public TextLineIterator(Iterator<StyledText> styledText, NewLine newLine) {
            super();
            this.styledText = styledText;
            this.newLine = newLine;
        }

        @Override
        public boolean hasNext() {
            return styledText.hasNext() || newLine != null;
        }

        @Override
        public TextFragment next() {
            TextFragment next = null;
            if (styledText.hasNext()) {
                next = styledText.next();
            } else if (newLine != null) {
                next = newLine;
                newLine = null;
            }
            return next;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

}
