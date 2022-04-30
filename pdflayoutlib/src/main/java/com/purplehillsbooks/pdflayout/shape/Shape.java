package com.purplehillsbooks.pdflayout.shape;

import java.awt.Color;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import com.purplehillsbooks.pdflayout.text.DrawListener;
import com.purplehillsbooks.pdflayout.text.Position;
import com.purplehillsbooks.pdflayout.util.CompatibilityHelper;

/**
 * Shapes can be used to either
 * {@link #draw(PDDocument, PDPageContentStream, Position, float, float, Color, Stroke, DrawListener)
 * stroke} or
 * {@link #fill(PDDocument, PDPageContentStream, Position, float, float, Color, DrawListener)
 * fill} the path of the shape, or simply
 * {@link #add(PDDocument, PDPageContentStream, Position, float, float) add the
 * path} of the shape to the drawing context.
 */
public abstract class Shape {

    /**
     * Draws (strokes) the shape.
     *
     * @param pdDocument
     *            the underlying pdfbox document.
     * @param contentStream
     *            the stream to draw to.
     * @param upperLeft
     *            the upper left position to start drawing.
     * @param width
     *            the width of the bounding box.
     * @param height
     *            the height of the bounding box.
     * @param color
     *            the color to use.
     * @param stroke
     *            the stroke to use.
     * @param drawListener
     *            the listener to
     *            {@link DrawListener#drawn(Object, Position, float, float)
     *            notify} on drawn objects.
     * @throws Exception
     *             by pdfbox
     */
    public void draw(PDDocument pdDocument, PDPageContentStream contentStream,
            Position upperLeft, float width, float height, Color color,
            Stroke stroke, DrawListener drawListener) throws Exception {

        add(pdDocument, contentStream, upperLeft, width, height);

        if (stroke != null) {
            stroke.applyTo(contentStream);
        }
        if (color != null) {
            contentStream.setStrokingColor(color);
        }
        contentStream.stroke();

        if (drawListener != null) {
            drawListener.drawn(this, upperLeft, width, height);
        }

    }

    /**
     * Fills the shape.
     *
     * @param pdDocument
     *            the underlying pdfbox document.
     * @param contentStream
     *            the stream to draw to.
     * @param upperLeft
     *            the upper left position to start drawing.
     * @param width
     *            the width of the bounding box.
     * @param height
     *            the height of the bounding box.
     * @param color
     *            the color to use.
     * @param drawListener
     *            the listener to
     *            {@link DrawListener#drawn(Object, Position, float, float)
     *            notify} on drawn objects.
     * @throws Exception
     *             by pdfbox
     */
    public void fill(PDDocument pdDocument, PDPageContentStream contentStream,
            Position upperLeft, float width, float height, Color color,
            DrawListener drawListener) throws Exception {

        add(pdDocument, contentStream, upperLeft, width, height);

        if (color != null) {
            contentStream.setNonStrokingColor(color);
        }
        CompatibilityHelper.fillNonZero(contentStream);

        if (drawListener != null) {
            drawListener.drawn(this, upperLeft, width, height);
        }

    }

    /**
     * Adds (the path of) the shape without drawing anything.
     *
     * @param pdDocument
     *            the underlying pdfbox document.
     * @param contentStream
     *            the stream to draw to.
     * @param upperLeft
     *            the upper left position to start drawing.
     * @param width
     *            the width of the bounding box.
     * @param height
     *            the height of the bounding box.
     * @throws Exception
     *             by pdfbox
     */
    public abstract void add(PDDocument pdDocument, PDPageContentStream contentStream,
            Position upperLeft, float width, float height) throws Exception;

}
