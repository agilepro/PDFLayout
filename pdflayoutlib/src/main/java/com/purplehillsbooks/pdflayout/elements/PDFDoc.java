package com.purplehillsbooks.pdflayout.elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.pdfbox.pdmodel.PDDocument;
import com.purplehillsbooks.pdflayout.elements.render.Layout;
import com.purplehillsbooks.pdflayout.elements.render.LayoutHint;
import com.purplehillsbooks.pdflayout.elements.render.RenderContext;
import com.purplehillsbooks.pdflayout.elements.render.RenderListener;
import com.purplehillsbooks.pdflayout.elements.render.VerticalLayout;
import com.purplehillsbooks.pdflayout.elements.render.VerticalLayoutHint;

/**
 * <p>The central class for creating a document.  Construct the PDFDoc first,
 * and then fill it with Frames and Paragraphs.
 * </p>
 * <p>
 * The PDFDoc has a PageFormat object that specifies the size, orientation and 
 * margins of the page.  The Page has an overall dimensions, and then four margins:
 * left, right, top, and bottom.
 * </p>
 * <p>
 * All measures are in points which are defined here as 72 points/inch.
 * (yes, I know traditionally a point was estimated by Donald
 * Knuth to be 72.27 points per inch, but actually there are probably a dozen
 * different definitions of the point, and it is otherwise a completely 
 * arbitrary value since typefaces of the same point size can in fact be 
 * completely different actual sizes. etc. etc. etc.)
 * </p>
 * <p>
 * The document is a list of elements, mostly Frame objects.  You can get a 
 * top level Frame inside the margins using newInteriorFrame().  Each request
 * creates another Frame after the first.  You can create Frames within Frames, 
 * and then ultimately Paragraphs within Frames.
 * </p>
 * <p>
 * Call saveToFile or saveToStream, and the document will be rendered, and output.
 * </p>
 * <p>
 * Rendering the document is the act of word-wrapping all paragraphs, 
 * calculating the height of all Frame (given the width constraints and contents) 
 * and then broken up into page objects.  Frames or Paragraphs can break across
 * page boundaries and this is accomplished by dividing the Frames and Paragraphs
 * into two objects.  Some objects are not dividable and will be moved to the next
 * page as a whole.  Because render does this dividing of the objects you generally
 * should render only once for each document constructed.  This is not a persistent
 * form for storing a document.
 * 
 * </p>
 *
 */
public class PDFDoc implements RenderListener {

    /**
     * The default page format is LETTER size, portrait orientation, 
     * with 1/2 inch margins on all sides
     */
    public final static PageFormat DEFAULT_PAGE_FORMAT = new PageFormat();

    private final List<Entry<Element, LayoutHint>> elements = new ArrayList<>();
    //private final List<Renderer> customRenderer = new CopyOnWriteArrayList<Renderer>();
    private final List<RenderListener> renderListener = new CopyOnWriteArrayList<RenderListener>();

    private PDDocument pdDocument;
    private PageFormat pageFormat;


    //public setting on whether to produce debug out put of margins
    public boolean showMargins = false;
    
    
    /**
     * Creates a Document using the {@link #DEFAULT_PAGE_FORMAT}.
     */
    public PDFDoc() {
        this(DEFAULT_PAGE_FORMAT);
    }

    /**
     * Creates a Document in A4 with orientation portrait and the given margins.
     * By default, a {@link VerticalLayout} is used.
     *
     * @param marginLeft
     *            the left margin
     * @param marginRight
     *            the right margin
     * @param marginTop
     *            the top margin
     * @param marginBottom
     *            the bottom margin
     */
    public PDFDoc(float marginLeft, float marginRight, float marginTop,
            float marginBottom) {
        this(PageFormat.with()
                .margins(marginLeft, marginRight, marginTop, marginBottom)
                .build());
    }

    /**
     * Creates a Document based on the given page format. By default, a
     * {@link VerticalLayout} is used.
     *
     * @param pageFormat
     *            the page format box to use.
     */
    public PDFDoc(final PageFormat pageFormat) {
        this.pageFormat = pageFormat;
    }

    /**
     * Adds an element to the document using a {@link VerticalLayoutHint}.
     *
     * @param element
     *            the element to add
     */
    public void add(final Element element) {
        add(element, new VerticalLayoutHint());
    }

    /**
     * Adds an element with the given layout hint.
     *
     * @param element
     *            the element to add
     * @param layoutHint
     *            the hint for the {@link Layout}.
     */
    public void add(final Element element, final LayoutHint layoutHint) {
        elements.add(createEntry(element, layoutHint));
    }

    private Entry<Element, LayoutHint> createEntry(final Element element,
            final LayoutHint layoutHint) {
        return new SimpleEntry<Element, LayoutHint>(element, layoutHint);
    }

    /**
     * @return the page format to use as default.
     */
    public PageFormat getPageFormat() {
        return pageFormat;
    }

    /**
     * Returns the {@link PDDocument} to be created by method {@link #renderDocument()}.
     * Beware that this PDDocument is released after rendering. This means each
     * rendering process creates a new PDDocument.
     *
     * @return the PDDocument to be used on the next call to {@link #renderDocument()}.
     */
    public PDDocument getPDDocument() {
        if (pdDocument == null) {
            pdDocument = new PDDocument();
        }
        return pdDocument;
    }
    
    public Dimension getInteriorDimension() {
        return pageFormat.getInteriorDimension();
    }
    
    public Frame newInteriorFrame() {
        Frame ret = new Frame(getInteriorDimension().getWidth());
        this.add(ret);
        return ret;
    }


    /**
     * Called after {@link #renderDocument()} in order to release the current document.
     */
    protected void resetPDDocument() {
        this.pdDocument = null;
    }

    /**
     * Adds a (custom) {@link Renderer} that may handle the rendering of an
     * element. All renderers will be asked to render the current element in the
     * order they have been added. If no renderer is capable, the default
     * renderer will be asked.
     *
     * @param renderer
     *            the renderer to add.
     */
    /* comment out until we have a test for this
    public void addRenderer(final Renderer renderer) {
        if (renderer != null) {
            customRenderer.add(renderer);
        }
    }
    */

    /**
     * Removes a {@link Renderer} .
     *
     * @param renderer
     *            the renderer to remove.
     */

    /* comment out until we have a test for this
    public void removeRenderer(final Renderer renderer) {
        customRenderer.remove(renderer);
    }
    */

    /**
     * Renders all elements and returns the resulting {@link PDDocument}.
     *
     * @return the resulting {@link PDDocument}
     * @throws Exception
     *             by pdfbox
     */
    public PDDocument renderDocument() throws Exception {
        PDDocument document = getPDDocument();
        RenderContext renderContext = new RenderContext(this, document);
        for (Entry<Element, LayoutHint> entry : elements) {
            Element element = entry.getKey();
            LayoutHint layoutHint = entry.getValue();
            boolean success = false;

            // first ask custom renderer to render the element
            

            /* comment out until we have a test for this
            Iterator<Renderer> customRendererIterator = customRenderer.iterator();
            while (!success && customRendererIterator.hasNext()) {
                success = customRendererIterator.next().render(renderContext,
                        element, layoutHint);
            }
            */

            // if none of them felt responsible, let the default renderer do the job.
            if (!success) {
                success = renderContext.startRendering(element, layoutHint);
            }

            if (!success) {
                throw new IllegalArgumentException(
                        String.format(
                                "neither layout %s nor the render context knows what to do with %s",
                                renderContext.getLayout(), element));

            }
        }
        renderContext.close();

        resetPDDocument();
        return document;
    }

    /**
     * {@link #renderDocument() Renders} the document and saves it to the given file.
     *
     * @param file
     *            the file to save to.
     * @throws Exception
     *             by pdfbox
     */
    public void saveToFile(final File file) throws Exception {
        OutputStream out = new FileOutputStream(file);
        saveToStream(out);
        out.close();
    }

    /**
     * {@link #renderDocument() Renders} the document and saves it to the given output
     * stream.
     *
     * @param output
     *            the stream to save to.
     * @throws Exception
     *             by pdfbox
     */
    public void saveToStream(final OutputStream output) throws Exception {
        try (PDDocument document = renderDocument()) {
            try {
                document.save(output);
                output.flush();
            } 
            catch (Exception e) {
                throw new Exception("Unable to save to output stream", e);
            }
        }
    }

    /**
     * Adds a {@link RenderListener} that will be notified during
     * {@link #renderDocument() rendering}.
     *
     * @param listener
     *            the listener to add.
     */
    public void addRenderListener(final RenderListener listener) {
        if (listener != null) {
            renderListener.add(listener);
        }
    }

    /**
     * Removes a {@link RenderListener} .
     *
     * @param listener
     *            the listener to remove.
     */
    public void removeRenderListener(final RenderListener listener) {
        renderListener.remove(listener);
    }

    @Override
    public void beforePage(final RenderContext renderContext) throws Exception {
        for (RenderListener listener : renderListener) {
            listener.beforePage(renderContext);
        }
    }

    @Override
    public void afterPage(final RenderContext renderContext) throws Exception {
        for (RenderListener listener : renderListener) {
            listener.afterPage(renderContext);
        }
    }
    
}
