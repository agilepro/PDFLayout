package com.purplehillsbooks.pdflayout.elements;


import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import com.purplehillsbooks.pdflayout.elements.render.RenderContext;
import com.purplehillsbooks.pdflayout.text.Alignment;
import com.purplehillsbooks.pdflayout.text.DrawListener;
import com.purplehillsbooks.pdflayout.text.Position;
import com.purplehillsbooks.pdflayout.text.TextFlow;
import com.purplehillsbooks.pdflayout.text.TextFragment;
import com.purplehillsbooks.pdflayout.text.TextLine;
import com.purplehillsbooks.pdflayout.text.TextSequence;
import com.purplehillsbooks.pdflayout.text.TextSequenceUtil;
import com.purplehillsbooks.pdflayout.text.WidthRespecting;

/**
 * <p>
 * A paragraph is the main unit for holding text.  Generally you will get a 
 * a paragraph from a Frame and will be contained by that Frame.
 * Then all the text can be placed into a Paragraph which is will be flowed
 * and word wrapped according to the parameters of the Paragraph.
 * </p>
 * <p>
 * SpaceBefore and SpaceAfter:  you can specify some white space before
 * and after the paragraph.  By default this is set of 6 points, but can 
 * be changed to be anything.
 * </p>
 * <p>
 * Empty Paragraphs are ignored.  You can not add white space with an empty
 * paragraph.  There must be some text in the paragraph for it to effect output.
 * </p>
 * <p>
 * Paragraphs expand left and right to whatever they are contained by, and 
 * as far as needed to include all the text.  There is no way to set the 
 * height either, since this is determined by the amount of text.
 * </p>
 */
public class Paragraph extends Dividable implements WidthRespecting  {

    private Position absolutePosition;
    private Alignment alignment = Alignment.Left;
    private float spaceBefore = 6;
    private float spaceAfter  = 6;
    
    private TextFlow paragraphText;
    
    /**
     * construct a paragraph when you already have the text for it.
     * Adopts the TextFlow object directly, so be careful.
     * Otherwise, you can construct an empty paragraph, and then
     * insert the text.
     * 
     * @param contents the text inside the paragraph
     */
    public Paragraph(TextFlow contents) {
        paragraphText = contents;
    }
    /**
     * construct a paragraph without any text in it
     * 
     */
    public Paragraph() {
        paragraphText = new TextFlow();
    }
    

    @Override
    public Position getAbsolutePosition() {
        return absolutePosition;
    }

    @Override
    public float getHeight() throws Exception {
        if (paragraphText.isEmpty()) {
            //if empty, completely ignore this paragraph
            return 0;
        }
        float textHeight = paragraphText.getHeight();
        return textHeight + spaceBefore + spaceAfter;
    }
    
    
    public float getSpaceBefore() {
        return spaceBefore;
    }
    public void setSpaceBefore(float val) {
        spaceBefore = val;
    }
    public float getSpaceAfter() {
        return spaceAfter;
    }
    public void setSpaceAfter(float val) {
        spaceAfter = val;
    }
    
    /**
     * Sets the absolute position to render at.
     *
     * @param absolutePosition
     *            the absolute position.
     */
    public void setAbsolutePosition(Position absolutePosition) {
        this.absolutePosition = absolutePosition;
    }

    /**
     * @return the text alignment to apply. Default is left.
     */
    public Alignment getAlignment() {
        return alignment;
    }

    /**
     * Sets the alignment Left/Right/Center/Justified to apply.
     *
     * @param alignment
     *            the text alignment.
     */
    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
    }
    

    public void setLineSpacing(float lineSpacing) {
        paragraphText.setLineSpacing(lineSpacing);
    }
    public void setApplyLineSpacingToFirstLine(boolean applyLineSpacingToFirstLine) {
        paragraphText.setApplyLineSpacingToFirstLine(applyLineSpacingToFirstLine);
    }
    public void add(final TextSequence sequence) {
        paragraphText.add(sequence);
    }
    public void add(final TextFragment fragment) {
        paragraphText.add(fragment);
    }

    @Override
    public void draw(RenderContext renderContext,
            Position upperLeft, DrawListener drawListener) throws Exception {
        if (paragraphText.isEmpty()) {
            //if the paragraph has absolutely no text in it, then ignore it
            //so there is no extra white space or anything.
            return;
        }
        //we need to move the paragraph down by the amount of spaceBefore
        Position spacedPosition = upperLeft.add(0, -spaceBefore);
        paragraphText.drawText(renderContext.contentStream, spacedPosition, getAlignment(), drawListener );
        
        //for debug make a rectangle
        /*
        float height = this.getHeight() - spaceBefore - spaceAfter;
        float width = this.getWidth();
        contentStream.setStrokingColor(Color.green);
        contentStream.setLineWidth(1);
        contentStream.addRect(spacedPosition.getX(), spacedPosition.getY() - height,
                width, height);
        contentStream.stroke();
        */
    }

    /**
     * Word-wraps and divides the given text sequence.
     *
     * @return the Divided element containing the parts.
     */
    @Override
    public Divided divide(float remainingHeight, RenderContext renderContext, boolean topOfPage) throws Exception {
        final float maxWidth = getMaxWidth();
        final float maxHeight = remainingHeight;
        TextFlow wrapped = TextSequenceUtil.wordWrap(paragraphText, maxWidth);
        List<TextLine> lines = wrapped.getLines();

        Paragraph first = new Paragraph();
        Paragraph tail = new Paragraph();
        
        first.setMaxWidth(this.getMaxWidth());
        first.setLineSpacing(paragraphText.getLineSpacing());
        first.setAlignment(this.getAlignment());
        first.setApplyLineSpacingToFirstLine(paragraphText.isApplyLineSpacingToFirstLine());
        first.setSpaceBefore(this.getSpaceBefore());
        first.setSpaceAfter(0);
        
        tail.setMaxWidth(this.getMaxWidth());
        tail.setLineSpacing(paragraphText.getLineSpacing());
        tail.setAlignment(this.getAlignment());
        tail.setApplyLineSpacingToFirstLine(paragraphText.isApplyLineSpacingToFirstLine());
        tail.setSpaceBefore(0);
        tail.setSpaceAfter(this.getSpaceAfter());

        int index = 0;
        while (index < lines.size() && first.getHeight() < maxHeight) {
            TextLine line = lines.get(index);
            float newHeight = line.getHeight();
            if (first.getHeight()+newHeight > maxHeight) {
                break;
            }
            first.add(line);
            ++index;
        }

        while (index < lines.size()) {
            tail.add(lines.get(index));
            ++index;
        }
        return new Divided(first, tail);
    }

    @Override
    public void removeLeadingEmptyVerticalSpace() throws Exception {
        paragraphText = paragraphText.removeLeadingEmptyLines();
    }


    
    public Paragraph addTextCarefully(String text, float size, PDType1Font font) throws Exception {
        int start=0;
        for (int end=0; end<text.length(); end++) {
            int codePoint = text.codePointAt(end);
            if (!font.hasGlyph(font.codeToName(codePoint))) {
                if (start<end) {
                    String unprocessed = text.substring(start, end);
                    paragraphText.addText(unprocessed, size, font);
                }
                paragraphText.addText("?", size, font);
                start = end+1;
            }
        }
        if (start<text.length()) {
            String unprocessed = text.substring(start);
            paragraphText.addText(unprocessed, size, font);
        }
        return this;
    }
    @Override
    public float getMaxWidth() {
        return paragraphText.getMaxWidth();
    }
    @Override
    public void setMaxWidth(float maxWidth) {
        paragraphText.setMaxWidth(maxWidth);
        
    }
    @Override
    public float getWidth() throws Exception {
        return paragraphText.getWidth();
    }
    public String toString() {
        return "Paragraph [text=" + paragraphText + "]";
    }

}
