package com.purplehillsbooks.pdflayout.elements;

import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import com.purplehillsbooks.pdflayout.elements.render.RenderContext;
import com.purplehillsbooks.pdflayout.text.DrawListener;
import com.purplehillsbooks.pdflayout.text.Position;

/**
 * <p>A TableRow has a set of Frames, one for each column.
 * Construct a TableRow with a Table object.
 * Be sure to initialize the Table object before creating
 * the TableRow so that the row is initialized with the right number
 * of cells.
 * 
 * @author keith
 */
public class TableRow extends Drawable {
    
    List<Frame> cells;
    Table table;
    
    public TableRow(Table tableDef) {
        table = tableDef;
        cells = new ArrayList<Frame>();
        for (int i=0; i<tableDef.getRowSize(); i++) {
            Frame cell = new Frame();
            float width = table.getColumnWidth(i);
            cell.setGivenWidth(width);
            cell.setMaxWidth(width);
            cells.add(cell);
        }
    }
    
    public Frame getCell(int columnNum) {
        if (columnNum>=cells.size()) {
            throw new RuntimeException("Table has only "+cells.size()+" columns, so there is no column #"+ columnNum);
        }
        Frame cd = cells.get(columnNum);
        return cd;
    }

    @Override
    public float getWidth() throws Exception {
        return table.getWidth();
    }

    @Override
    public float getHeight() throws Exception {
        float biggest = 0;
        for (Frame cell : cells) {
            if (cell.getHeight()>biggest) {
                biggest = cell.getHeight();
            }
        }
        return biggest;
    }

    @Override
    public void draw(PDDocument pdDocument, PDPageContentStream contentStream, Position upperLeft,
            DrawListener drawListener) throws Exception {
        Position cellStart = new Position(upperLeft.getX(), upperLeft.getY());
        for (int i=0; i<cells.size(); i++) {
            Frame cell = cells.get(i);
            cell.draw(pdDocument, contentStream, cellStart, drawListener);
            cellStart = cellStart.add(table.getColumnWidth(i), 0);
            contentStream.moveTo(cellStart.getX(), cellStart.getY());
        }

        contentStream.moveTo(upperLeft.getX(), upperLeft.getY()-getHeight());
    }

    @Override
    public void removeLeadingEmptyVerticalSpace() throws Exception {
        for (Frame cell : cells) {
            cell.removeLeadingEmptyVerticalSpace();
        }
    }

}
