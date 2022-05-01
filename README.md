# PDFLayout
A set of java classes to perform wordwrap and page layout to produce PDF files.  Essentially a basic word processor.  runs on top of Apache PDFBox.  You construct a document out of frames and paragraphs.  PDFLayout will do all the wordwrap, flow the text into the frames, and then calculate the pagination, ultimately sending this to a PDF file.

# Usage

First, create a PDFDoc object.  Get frames with getInteriorFrame() method.  In each frame you can get nested interior frames or paragraphs.   Put the text into the Paragraphs.   Write the PDF document out.  That is pretty much all you have to do.

Here is a sample code:

```
    PDFDoc doc = new PDFDoc(50,50,50,50);
    
    for ( <--each section you want--> ) {
        Frame innerFrame = doc.newInteriorFrame();
        
        //make a section title
        Paragraph para = innerInnerFrame.getNewParagraph();
        para.addTextCarefully(<section-title>, 18, PDType1Font.HELVETICA);
        
        for ( <--each paragraph you have--> ) {
            Paragraph para = innerFrame.getNewParagraph();
            para.addTextCarefully(<para-text>, 12, PDType1Font.HELVETICA);
        }
    }
    
    File t1file = new File(testOutputFolder, "Test5-DoubleFrames.pdf");
    doc.saveToFile(t1file);
```

Create all the objects for all the sections/text in the document, then write it out.
PDFLayout will first perform wordwrap according to the side of the document/frames which then
determined the lenghts of the paragraphs and frames.   Once layout is done, the resulting 
objects are broken into pages/  Output constucts a PDFFile which is a list of pages, and 
all the objects that ended up on a particular page are written to that page.

# Frame

Frames are the main organizing component, and they are actually quite powerful.  They are 
a rectangular area which text can be flowed into.

Frames can have a border, which is a line around the frame.  You can specify color, line width,
and stroke (so you can make dotted or dashed lines).   The colors are standard AWT colors, and
look to the PDFBox to find out the details about stroke options.  
By default a frame does not have a border.

A Frame can have padding, which is an amount of space inside it that separates the content from 
the border.  A frame can also have a margin which is space outside the border which separates 
the border from other things (such as other frames).   Use this to make a little space between 
the frames.

A Frame can also be shaded with a color, again standard AWT colors.

The margin and padding is ignored when it comes to the page boundary.  That is, margin will keep
frames apart from each other on the page, but it will not prevent the text of a paragraph that has
been cut into half from going all the way to the page margin.

To create vertical whitespace at a point in the file, you can insert a frame with the appropriate 
top margin.

# Paragraph

Paragraphs hold a list of text fragments, each can be styled with a different font/size.
Word-wrap will then flow all these text fragments into a single paragraph.

Paragraphs can have some white space before and after them which provides space between 
paragraphs.  Set the spaceBefore and spaceAfter to zero to disable this.  
When a paragraph is cut by the end of a page, the spaceBefore and spaceAfter have noeffect 
at the point where the paragraph is cut.

There is no left/right margin values.  
You can indent paragraph and make paragraphs narrower by using frames with the desired padding.
There is no paragraph indent and at the current time no easy way to indent the first line of a paragraph.

Empty paragraphs have no effect on the output.

