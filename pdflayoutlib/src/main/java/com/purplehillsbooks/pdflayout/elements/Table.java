package com.purplehillsbooks.pdflayout.elements;

import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import com.purplehillsbooks.pdflayout.elements.render.RenderContext;
import com.purplehillsbooks.pdflayout.text.DrawListener;
import com.purplehillsbooks.pdflayout.text.Position;

/**
 * <p>This class defines a table.  You can specify the number of columns and the 
 * fixed width of those columns.  Then each row will refer to Table Definition
 * in order to interpret the contents of the row.   The Row is a collection of 
 * Frames one for each cell.  The Row object controls the layout of the cells.
 * </p>
 * <p>
 * What this is not:  The table will have fixed column widths set in advance.
 * There is no attempt to automatically figure out what the column widths should
 * be.  Also, column spanning cells are not supported: just regular tables with
 * equal numbers of cells on each row.
 * </p>
 * <p>
 * Columns are numbers zero-based.   So the first column is column 0.  Standard for
 * Java lists.
 * </p>
 * <p>
 * Set up all the columns before you create TableRow objects.  This allows the rows
 * to be initialized correctly.
 * </p>
 * 
 * @author keith
 *
 */
public class Table extends Dividable {
    
    List<ColumnDefinition> columns;
    List<TableRow> rows;
    
    public Table(int numCols) {
        columns = new ArrayList<ColumnDefinition>();
        rows = new ArrayList<TableRow>();

        for (int i=0; i<numCols; i++) {
            columns.add(new ColumnDefinition());
        }
    }
    public Table cloneTable() {
        Table clone = new Table(getRowSize());
        for (int i=0; i<getRowSize(); i++) {
            clone.setColumnWidth(i, columns.get(i).width);
        }
        return clone;
    }
    
    public void setColumnWidth(int columnNum, float width) {
        
        ColumnDefinition cd = columns.get(columnNum);
        cd.width = width;
    }
    
    public float getColumnWidth(int columnNum) {
        if (columnNum>=columns.size()) {
            throw new RuntimeException("Table has only "+columns.size()+" columns, so there is no column #"+ columnNum);
        }
        ColumnDefinition cd = columns.get(columnNum);
        return cd.width;
    }
    
    public int getRowSize() {
        return columns.size();
    }
    
    /**
     * @param rowNumber Rows are zero based, so the first row is 0, etc.
     * @return TableRow already existing retrieved from the list in the table
     */
    public TableRow getRow(int rowNumber) {
        return rows.get(rowNumber);
    }
    public void addRow(TableRow newRow) {
        rows.add(newRow);
    }
    
    /**
     * @return newly created TableRow which has also been added to the table
     */
    public TableRow createNewRow() {
        TableRow newRow = new TableRow(this);
        rows.add(newRow);
        return newRow;
    }
    
    public float getWidth() {
        float totalWidth = 0;
        for (ColumnDefinition cd : columns) {
            totalWidth += cd.width;
        }
        return totalWidth;
    }

    @Override
    public float getHeight() throws Exception {
        float total = 0;
        for (TableRow tr : rows) {
            total = total + tr.getHeight();
        }
        return total;
    }

    @Override
    public void draw(RenderContext renderContext, Position upperLeft,
            DrawListener drawListener) throws Exception {
        Position position = upperLeft;
        for (TableRow tr : rows) {
            renderContext.contentStream.moveTo(position.getX(), position.getY());
            tr.draw(renderContext, position, drawListener);
            position = position.add(0, -tr.getHeight());
            renderContext.contentStream.moveTo(position.getX(), position.getY());
        }
        
    }

    @Override
    public void removeLeadingEmptyVerticalSpace() throws Exception {
        if (rows.size()>0) {
            rows.get(0).removeLeadingEmptyVerticalSpace();
        }
    }

    @Override
    public Divided divide(float remainingHeight, RenderContext renderContext, boolean topOfPage) throws Exception {
        Table headTable = this.cloneTable();
        Table tailTable = this.cloneTable();
        
        for (int i = 0; i<rows.size(); i++) {
            TableRow tr = rows.get(i);
            if (remainingHeight>tr.getHeight()) {
                headTable.addRow(tr);
            }
            else {
                tailTable.addRow(tr);
            }
            remainingHeight -= tr.getHeight();
        }
        return new Divided(headTable, tailTable);
    }
    
    public void propagateMaxWidthToChildren() {
        for (TableRow row : rows) {
            for (int i=0; i<columns.size(); i++) {
                Frame cell = row.getCell(i);
                cell.setMaxWidth(getColumnWidth(i));
            }
        }
    }

}
