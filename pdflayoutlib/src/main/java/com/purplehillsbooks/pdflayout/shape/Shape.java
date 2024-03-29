package com.purplehillsbooks.pdflayout.shape;

import java.awt.Color;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import com.purplehillsbooks.pdflayout.elements.render.RenderContext;
import com.purplehillsbooks.pdflayout.text.DrawListener;
import com.purplehillsbooks.pdflayout.text.Position;
import com.purplehillsbooks.pdflayout.util.CompatibilityHelper;

/**
 * Shapes can be used to either
 * {@link #draw(RenderContext, Position, float, float, Color, Stroke, DrawListener)
 * stroke} or
 * {@link #fill(RenderContext, Position, float, float, Color, DrawListener)
 * fill} the path of the shape, or simply
 * {@link #add(RenderContext, Position, float, float) add the
 * path} of the shape to the drawing context.
 */
public abstract class Shape {

    /**
     * Draws (strokes) the shape.
     *
     * @param renderContext
     *            the context currently drawing to
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
    public void draw(RenderContext renderContext,
            Position upperLeft, float width, float height, Color color,
            Stroke stroke, DrawListener drawListener) throws Exception {

        add(renderContext, upperLeft, width, height);

        if (stroke != null) {
            stroke.applyTo(renderContext.contentStream);
        }
        if (color != null) {
            renderContext.contentStream.setStrokingColor(color);
        }
        renderContext.contentStream.stroke();

        if (drawListener != null) {
            drawListener.drawn(this, upperLeft, width, height);
        }

    }

    /**
     * Fills the shape.
     *
     * @param renderContext
     *            the context currently drawing to
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
    public void fill(RenderContext renderContext,
            Position upperLeft, float width, float height, Color color,
            DrawListener drawListener) throws Exception {

        add(renderContext, upperLeft, width, height);

        if (color != null) {
            renderContext.contentStream.setNonStrokingColor(color);
        }
        CompatibilityHelper.fillNonZero(renderContext.contentStream);

        if (drawListener != null) {
            drawListener.drawn(this, upperLeft, width, height);
        }

    }

    /**
     * Adds (the path of) the shape without drawing anything.
     *
     * @param renderContext
     *            the context currently drawing to
     * @param upperLeft
     *            the upper left position to start drawing.
     * @param width
     *            the width of the bounding box.
     * @param height
     *            the height of the bounding box.
     * @throws Exception
     *             by pdfbox
     */
    public abstract void add(RenderContext renderContext,
            Position upperLeft, float width, float height) throws Exception;

}
