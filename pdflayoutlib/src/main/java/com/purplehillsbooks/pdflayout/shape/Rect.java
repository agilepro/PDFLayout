package com.purplehillsbooks.pdflayout.shape;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import com.purplehillsbooks.pdflayout.elements.render.RenderContext;
import com.purplehillsbooks.pdflayout.text.Position;

/**
 * A simple rectangular shape.
 */
public class Rect extends Shape {

    @Override
    public void add(RenderContext renderContext,
            Position upperLeft, float width, float height) throws Exception {
        renderContext.contentStream.addRect(upperLeft.getX(), upperLeft.getY() - height,
                width, height);
    }

}
