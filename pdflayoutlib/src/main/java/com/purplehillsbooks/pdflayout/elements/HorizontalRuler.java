package com.purplehillsbooks.pdflayout.elements;

import java.awt.Color;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import com.purplehillsbooks.pdflayout.elements.render.RenderContext;
import com.purplehillsbooks.pdflayout.shape.Stroke;
import com.purplehillsbooks.pdflayout.text.DrawListener;
import com.purplehillsbooks.pdflayout.text.Position;
import com.purplehillsbooks.pdflayout.text.WidthRespecting;

/**
 * A horizontal ruler that adjust its width to the given
 * {@link WidthRespecting#getMaxWidth() max width}.
 */
public class HorizontalRuler extends Drawable implements WidthRespecting {

    private Stroke stroke;
    private Color color;
    private float maxWidth = -1f;

    public HorizontalRuler(Stroke stroke, Color color) {
        super();
        this.stroke = stroke;
        this.color = color;
    }

    /**
     * @return the stroke to draw the ruler with.
     */
    public Stroke getStroke() {
        return stroke;
    }

    /**
     * @return the color to draw the ruler with.
     */
    public Color getColor() {
        return color;
    }

    @Override
    public float getMaxWidth() {
        return maxWidth;
    }

    @Override
    public void setMaxWidth(float maxWidth) {
        this.maxWidth = maxWidth;
    }

    @Override
    public float getWidth() throws Exception {
        return getMaxWidth();
    }

    @Override
    public float getHeight() throws Exception {
        if (getStroke() == null) {
            return 0f;
        }
        return getStroke().getLineWidth();
    }

    @Override
    public void draw(RenderContext renderContext,
            Position upperLeft, DrawListener drawListener) throws Exception {
        if (getColor() != null) {
            renderContext.contentStream.setStrokingColor(getColor());
        }
        if (getStroke() != null) {
            getStroke().applyTo(renderContext.contentStream);
            float x = upperLeft.getX();
            float y = upperLeft.getY() - getStroke().getLineWidth() / 2;
            renderContext.contentStream.moveTo(x, y);
            renderContext.contentStream.lineTo(x + getWidth(), y);
            renderContext.contentStream.stroke();
        }
        if (drawListener != null) {
            drawListener.drawn(this, upperLeft, getWidth(), getHeight());
        }
    }


}
