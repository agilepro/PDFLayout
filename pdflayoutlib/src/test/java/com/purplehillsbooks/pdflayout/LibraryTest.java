/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.purplehillsbooks.pdflayout;

import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.jupiter.api.Test;

import com.purplehillsbooks.pdflayout.elements.Frame;
import com.purplehillsbooks.pdflayout.elements.PDFDoc;
import com.purplehillsbooks.pdflayout.elements.Paragraph;
import com.purplehillsbooks.pdflayout.elements.Table;
import com.purplehillsbooks.pdflayout.elements.TableRow;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LibraryTest {


    File testOutputFolder = new File("/github/PDFLayoutTestResults");
    List<String> allWords = new ArrayList<String>();
    
    @Test public void stupidMainTestMethod() throws Exception {
        if (!testOutputFolder.exists()) {
            if (!testOutputFolder.mkdirs() || !testOutputFolder.exists()) {
                throw new Exception("JUnit forces you to guess what folder is good for writing "+
                       "temporary test results into but you guessed incorrect.  "+
                       "This folder can not be created: "+testOutputFolder.getAbsolutePath());
            }
        }
        
        parseWords();
        
        createTables();
        createDocumentFramesNeedSpace();
        createLongDocument1();
        createLongDocument2(false);
        createLongDocument2(true);
        createLongDocument3();
        createLongDocument4();
        createDocumentDoubleFrames();
        createDocumentTripleFrames();
        createDocumentSmallFrames();
        createDocumentFramesNewPage();

    }
    
    /**
     * This test is to create a long document out of a lot of long paragraphs
     * to see that paragraph breaking works in the right way.
     */
    private void createLongDocument1() throws Exception {
        
        PDFDoc doc = new PDFDoc(50,50,50,50);
        doc.showMargins = true;
        Frame mainFrame = doc.newInteriorFrame();
        
        for (int i=0; i<100; i++) {
            Paragraph para = mainFrame.getNewParagraph();
            para.addTextCarefully(generateParagraph(500), 20, PDType1Font.HELVETICA);
        }
        
        File t1file = new File(testOutputFolder, "Test1-LongDocument.pdf");
        doc.saveToFile(t1file);
        
    }
    
    private void createLongDocument2(boolean keepTogether) throws Exception {
        
        PDFDoc doc = new PDFDoc(50,50,50,50);
        doc.showMargins = true;
        Frame mainFrame = doc.newInteriorFrame();
        
        Paragraph instructions = mainFrame.getNewParagraph();
        instructions.addTextCarefully("This document has a lot of largish frames to see if breaking them "
                +"across page boundaries is working. ", 12, PDType1Font.COURIER);
        if (keepTogether) {
            instructions.addTextCarefully("Frames are NOT allowed to be split, and should be always kept together on a page.", 12, PDType1Font.COURIER);
        }
        else {
            instructions.addTextCarefully("Frames are allowed to be split. ", 12, PDType1Font.COURIER);
        }
        
        for (int i=0; i<100; i++) {
            Frame innerFrame = mainFrame.newInteriorFrame();
            Color thisColor = Color.blue;
            if (i % 2 == 0) {
                thisColor = Color.green;
            }
            innerFrame.setBorderColor(thisColor);
            innerFrame.setMargin(15, 15, 15, 15);
            innerFrame.setPadding(15, 15, 15, 15);
            innerFrame.setKeepTogether(keepTogether);
            String repeatedPara = generateParagraph(100);
            Paragraph para = innerFrame.getNewParagraph();
            para.addTextCarefully(repeatedPara, 12, PDType1Font.HELVETICA);
            para = innerFrame.getNewParagraph();
            para.addTextCarefully(repeatedPara, 12, PDType1Font.HELVETICA);
            para = innerFrame.getNewParagraph();
            para.addTextCarefully(repeatedPara, 12, PDType1Font.HELVETICA);
        }
        
        File t1file = new File(testOutputFolder, "Test2-LongFrame"+ (keepTogether?"_NoSplit":"_Split") + ".pdf");
        doc.saveToFile(t1file);
        
    }

    
    private void createLongDocument3() throws Exception {
        
        PDFDoc doc = new PDFDoc(50,50,50,50);
        doc.showMargins = true;
        Frame mainFrame = doc.newInteriorFrame();
        
        Paragraph instructions = mainFrame.getNewParagraph();
        instructions.addTextCarefully("This document has a lot of frames to see if breaking them "
                +"across page boundaries is working. ", 12, PDType1Font.TIMES_BOLD);
        
        
        for (int i=0; i<200; i++) {
            Frame innerFrame = mainFrame.newInteriorFrame();
            Color thisColor = Color.blue;
            if (i % 2 == 0) {
                thisColor = Color.green;
            }
            innerFrame.setBorderColor(thisColor);
            innerFrame.setMargin(15, 15, 15, 15);
            innerFrame.setPadding(15, 15, 15, 15);
            Paragraph para = innerFrame.getNewParagraph();
            para.addTextCarefully(generateParagraph(100), 12, PDType1Font.HELVETICA);
        }
        
        File t1file = new File(testOutputFolder, "Test3-LongFrames.pdf");
        doc.saveToFile(t1file);
        
    }

    private void createLongDocument4() throws Exception {
        
        PDFDoc doc = new PDFDoc(50,50,50,50);
        doc.showMargins = true;
        Frame mainFrame = doc.newInteriorFrame();
        
        for (int i=0; i<100; i++) {
            Frame innerFrame = mainFrame.newInteriorFrame();
            innerFrame.headerLeft = "FrameLeft "+i;
            innerFrame.headerCenter = "FrameCenter "+i;
            innerFrame.headerRight = "FrameRight "+i;
            innerFrame.footerLeft = "long long FrameLeft "+i;
            innerFrame.footerCenter = "long long long long long long FrameCenter "+i;
            innerFrame.footerRight = "long long FrameRight "+i;
            Color thisColor = Color.blue;
            if (i % 2 == 0) {
                thisColor = Color.green;
            }
            innerFrame.setBorderColor(thisColor);
            innerFrame.setMargin(15, 15, 15, 15);
            innerFrame.setPadding(15, 15, 15, 15);            
            Paragraph para = innerFrame.getNewParagraph();
            para.addTextCarefully(generateParagraph(500), 20, PDType1Font.HELVETICA);
        }
        
        File t1file = new File(testOutputFolder, "Test4-HugeFrames.pdf");
        doc.saveToFile(t1file);
        
    }
    
    private void createDocumentDoubleFrames() throws Exception {
        
        PDFDoc doc = new PDFDoc(50,50,50,50);
        doc.showMargins = true;
        Frame mainFrame = doc.newInteriorFrame();
        
        for (int i=0; i<200; i++) {
            Frame innerFrame = mainFrame.newInteriorFrame();
            innerFrame.headerLeft = "FrameLeft "+i;
            innerFrame.headerCenter = "FrameCenter "+i;
            innerFrame.headerRight = "FrameRight "+i;
            innerFrame.footerLeft = "iiiiiiiiiiiiiiiiiiii FrameLeft "+i;
            innerFrame.footerCenter = "long long long long long long FrameCenter "+i;
            innerFrame.footerRight = "MMMMMMMMMMMMMMMMMMMM Right "+i;
            Color thisColor = Color.blue;
            if (i % 2 == 0) {
                thisColor = Color.green;
            }
            innerFrame.setBorderColor(thisColor);
            innerFrame.setMargin(15, 15, 15, 15);
            innerFrame.setPadding(15, 15, 15, 15);
            Frame innerInnerFrame = innerFrame.newInteriorFrame();
            innerInnerFrame.setBorderColor(Color.pink);
            innerInnerFrame.setMargin(15, 15, 15, 15);
            innerInnerFrame.setPadding(15, 15, 15, 15);
            Paragraph para = innerInnerFrame.getNewParagraph();
            para.addTextCarefully(generateParagraph(100), 12, PDType1Font.HELVETICA);
        }
        
        File t1file = new File(testOutputFolder, "Test5-DoubleFrames.pdf");
        doc.saveToFile(t1file);
        
    }
    private void createDocumentTripleFrames() throws Exception {
        
        PDFDoc doc = new PDFDoc(50,50,50,50);
        doc.showMargins = true;
        Frame mainFrame = doc.newInteriorFrame();
        
        for (int i=0; i<200; i++) {
            Frame innerFrame = mainFrame.newInteriorFrame();
            innerFrame.headerLeft = "FrameLeft "+i;
            innerFrame.headerCenter = "FrameCenter "+i;
            innerFrame.headerRight = "FrameRight "+i;
            innerFrame.footerLeft = "FrameLeft "+i;
            innerFrame.footerCenter = "FrameCenter "+i;
            innerFrame.footerRight = "FrameRight "+i;
            Color thisColor = Color.blue;
            if (i % 2 == 0) {
                thisColor = Color.green;
            }
            innerFrame.setBorderColor(thisColor);
            innerFrame.setMargin(15, 15, 15, 15);
            innerFrame.setPadding(15, 15, 15, 15);
            Frame innerInnerFrame = innerFrame.newInteriorFrame();
            innerInnerFrame.setBorderColor(Color.pink);
            innerInnerFrame.setMargin(15, 15, 15, 15);
            innerInnerFrame.setPadding(15, 15, 15, 15);
            Frame tripleInner = innerInnerFrame.newInteriorFrame();
            tripleInner.setBorderColor(Color.black);
            tripleInner.setMargin(15, 15, 15, 15);
            tripleInner.setPadding(15, 15, 15, 15);
            Paragraph para = tripleInner.getNewParagraph();
            para.addTextCarefully(generateParagraph(60), 12, PDType1Font.HELVETICA);
        }
        
        File t1file = new File(testOutputFolder, "Test6-TripleFrames.pdf");
        doc.saveToFile(t1file);
        
    }
    private void createDocumentSmallFrames() throws Exception {
        
        PDFDoc doc = new PDFDoc(50,50,50,50);
        doc.showMargins = true;
        Frame mainFrame = doc.newInteriorFrame();
        
        for (int i=0; i<200; i++) {
            Frame innerFrame = mainFrame.newInteriorFrame();
            innerFrame.headerLeft = "FrameLeft "+i;
            innerFrame.headerCenter = "FrameCenter "+i;
            innerFrame.headerRight = "FrameRight "+i;
            innerFrame.footerLeft = "FrameLeft "+i;
            innerFrame.footerCenter = "FrameCenter "+i;
            innerFrame.footerRight = "FrameRight "+i;
            Color thisColor = Color.blue;
            if (i % 2 == 0) {
                thisColor = Color.green;
            }
            innerFrame.setBorderColor(thisColor);
            innerFrame.setMargin(15, 15, 15, 15);
            innerFrame.setPadding(15, 15, 15, 15);
            Paragraph para = innerFrame.getNewParagraph();
            para.setSpaceAfter(0);
            para.setSpaceBefore(0);
            para.addTextCarefully("Just one line of text "+i, 12, PDType1Font.HELVETICA);
        }
        
        File t1file = new File(testOutputFolder, "Test7-SmallFrames.pdf");
        doc.saveToFile(t1file);
        
    }
    private void createDocumentFramesNewPage() throws Exception {
        
        PDFDoc doc = new PDFDoc(50,50,50,50);
        doc.showMargins = true;
        Frame mainFrame = doc.newInteriorFrame();
        
        for (int i=0; i<200; i++) {
            Frame innerFrame = mainFrame.newInteriorFrame();
            innerFrame.headerLeft = "FrameLeft "+i;
            innerFrame.headerCenter = "FrameCenter "+i;
            innerFrame.headerRight = "FrameRight "+i;
            innerFrame.footerLeft = "FrameLeft "+i;
            innerFrame.footerCenter = "FrameCenter "+i;
            innerFrame.footerRight = "FrameRight "+i;
            Color thisColor = Color.blue;
            if (i % 2 == 0) {
                thisColor = Color.green;
            }
            innerFrame.setBorderColor(thisColor);
            innerFrame.setMargin(15, 15, 15, 15);
            innerFrame.setPadding(15, 15, 15, 15);
            Paragraph para = innerFrame.getNewParagraph();
            para.setSpaceAfter(0);
            para.setSpaceBefore(0);
            if (r.nextInt(6)==0) {
                innerFrame.setStartNewPage(true);
                innerFrame.setBackgroundColor(Color.yellow);
                para.addTextCarefully("This frame should always be at top of new page "+i, 12, PDType1Font.HELVETICA);
            }
            else {
                para.addTextCarefully("Just one line of text, usually not at top of page "+i, 12, PDType1Font.HELVETICA);
            }
        }
        
        File t1file = new File(testOutputFolder, "Test8-NewPage.pdf");
        doc.saveToFile(t1file);
        
    }
    
    private void createDocumentFramesNeedSpace() throws Exception {
        
        PDFDoc doc = new PDFDoc(50,50,50,50);
        doc.showMargins = true;
        Frame mainFrame = doc.newInteriorFrame();
        
        for (int i=0; i<200; i++) {
            Frame innerFrame = mainFrame.newInteriorFrame();
            Color thisColor = Color.blue;
            if (i % 2 == 0) {
                thisColor = Color.green;
            }
            innerFrame.setBorderColor(thisColor);
            innerFrame.setMargin(5, 5, 5, 5);
            innerFrame.setPadding(5, 5, 5, 5);
            Paragraph para = innerFrame.getNewParagraph();
            para.setSpaceAfter(0);
            para.setSpaceBefore(0);
            if (r.nextInt(6)==0) {
                innerFrame.setNeedSpace(216);
                innerFrame.setBackgroundColor(Color.yellow);
                para.addTextCarefully("This frame should never be seen less than 3 inch from bottom, "+i, 12, PDType1Font.HELVETICA);
            }
            else {
                para.addTextCarefully("Just one line of text, nothing special, "+i, 12, PDType1Font.HELVETICA);
            }
        }
        
        File t1file = new File(testOutputFolder, "Test9-NeedSpace.pdf");
        doc.saveToFile(t1file);
        
    }
    
    private void createTables() throws Exception {
        
        PDFDoc doc = new PDFDoc(50,50,50,50);
        doc.showMargins = true;
        Frame mainFrame = doc.newInteriorFrame();
        
        for (int totalColumns=1; totalColumns<6; totalColumns++) {
            
            for (int totalRows=1; totalRows<15; totalRows=totalRows+3) {
            
                Paragraph betweenTables = mainFrame.getNewParagraph();
                betweenTables.addTextCarefully("Here comes a table with dimensions: ("+totalColumns+","+totalRows+"). ", 9, PDType1Font.HELVETICA);
                betweenTables.addTextCarefully(generateParagraph(30), 9, PDType1Font.HELVETICA);
                
                createRandomTable(mainFrame, totalColumns, totalRows, 0, 5);
                
                
            }
            
        }
        
        for (int trialMargin=1; trialMargin<4; trialMargin++) {
            
            Paragraph betweenTables = mainFrame.getNewParagraph();
            betweenTables.addTextCarefully("Next table has margin: "+(5*trialMargin)+".  ", 9, PDType1Font.HELVETICA);
            betweenTables.addTextCarefully(generateParagraph(30), 9, PDType1Font.HELVETICA);
            
            createRandomTable(mainFrame, 4, 5, (trialMargin*5), 5);
            
        }
        
        File t1file = new File(testOutputFolder, "Test10-Tables.pdf");
        doc.saveToFile(t1file);
        
    }
        
    
    static int colorCount = 0;
    private void createRandomTable(Frame mainFrame, int totalColumns, 
            int totalRows, float margin, float padding) throws Exception {
        Table table = mainFrame.getNewTable(totalColumns);
        for (int i=0; i<totalColumns; i++) {
            table.setColumnWidth(i, 100);
        }
        
        Color thisColor = Color.blue;
        if (++colorCount % 2 == 0) {
            thisColor = Color.green;
        }
        
        for (int row=0; row<totalRows; row++) {
            TableRow tRow = table.createNewRow();
            
            for (int i=0; i<totalColumns; i++) {
                
                Frame cell = tRow.getCell(i);
                cell.setBorderColor(thisColor);
                cell.setMargin(margin, margin, margin, margin);
                cell.setPadding(padding, padding, padding, padding);
                cell.setMaxWidth(100);
                
                Paragraph para = cell.getNewParagraph();
                int x = i+1;
                int y = row+1;
                para.addTextCarefully("This cell ("+x+","+y+") of table with "+totalRows+" rows.", 9, PDType1Font.HELVETICA);
            }
        }        
    }
    
    
    
    Random r = new Random();
    
    
    String generateParagraph(int words) {
        StringBuilder sb = new StringBuilder();
        
        for (int i=0; i<5; i++) {
            sb.append(allWords.get(r.nextInt(allWords.size())));
            sb.append(" ");
        }
        sb.append("(Random paragraph with "+words+" words) ");
        for (int i=0; i<(words-11); i++) {
            sb.append(allWords.get(r.nextInt(allWords.size())));
            int pType = r.nextInt(20);
            if (pType==0) {
                //1:20 chance of adding a period
                sb.append(". ");
            }
            else if (pType==1) {
                //1:20 chance of adding a comma
                sb.append(", ");
            }
            else {
                sb.append(" ");
            }
        }
        sb.append(allWords.get(r.nextInt(allWords.size())));
        sb.append(".");
        return sb.toString();
    }
    
    private void parseWords() throws Exception {
        int start = 0;
        for (int i=0; i<sampleWords.length(); i++) {
            char ch = sampleWords.charAt(i);
            if (  (ch>='a' && ch <='z') || (ch>='A' && ch <='Z') || (ch>='0' && ch <='9') || ch=='\'' || ch=='-') {
                // nothing to do
            }
            else {
                if (start < i-1) {
                    allWords.add(sampleWords.substring(start, i));
                }
                start = i+1;   //skip the current letter
            }
        }
        
        File wordDump = new File(this.testOutputFolder, "sample-words.txt");
        Writer w = new OutputStreamWriter( new FileOutputStream(wordDump), "UTF-8");
        int i = 0;
        for (String word : allWords) {
            w.write(word);
            if (++i>9) {
                w.write(",\n");
                i=0;
            }
            else {
                w.write(", ");
            }
        }
        w.close();    
    }
    
    String sampleWords = " Kant(a) (22 April 1724 – 12 February 1804) was a German philosopher and one of the central Enlightenment thinkers. Born in Königsberg , Kant's comprehensive and systematic works in epistemology , metaphysics , ethics , and aesthetics have made him one of the most influential and controversial figures in modern Western philosophy , being called the \"father of modern ethics\", \"father of modern aesthetics\" and, by bringing together rationalism and empiricism, the \"father of modern philosophy\".(7) (8) (9) (10)\n" + 
            "\n" + 
            "In his doctrine of transcendental idealism , Kant argued space and time are mere \"forms of intuition\" that structure all experience and that the objects of experience are mere \"appearances\". The nature of things as they are in themselves is unknowable to us. In an attempt to counter the philosophical doctrine of skepticism , he wrote the Critique of Pure Reason (1781/1787), his best-known work. Kant drew a parallel to the Copernican revolution in his proposal to think of the objects of experience as conforming to our spatial and temporal forms of intuition and the categories of our understanding, so that we have a priori cognition of those objects. These claims have proved especially influential in the social sciences, particularly sociology and anthropology, which regard human activities as pre-oriented by cultural norms.(11)\n" + 
            "\n" + 
            "Kant believed that reason is the source of morality , and that aesthetics arises from a faculty of disinterested judgment. Kant's religious views were deeply connected to his moral theory. Their exact nature, however, remains in dispute. He hoped that perpetual peace could be secured through an international federation of republican states and international cooperation . His cosmopolitan reputation, however, is called into question by his promulgation of scientific racism for much of his career, although he altered his views on the subject in the last decade of his life.\n" + 
            "\n" + 
            "Immanuel Kant was born on 22 April 1724 into a Prussian German family of Lutheran faith in Königsberg , East Prussia (since 1946 the Russian city of Kaliningrad ). His mother, Anna Regina Reuter, was born in Königsberg to a father from Nuremberg.(12) Her surname is sometimes erroneously given as Porter. Kant's father, Johann Georg Kant, was a German harness-maker from Memel , at the time Prussia's most northeastern city (now Klaipėda , Lithuania ). It is possible that the Kants got their name from the village of Kantvainiai (German: Kantwaggen – today part of Priekulė ) and were of Kursenieki origin.(13) (14)\n" + 
            "\n" + 
            "Emanuel was baptized and later changed the spelling of his name to Immanuel after learning Hebrew.(15) He was the fourth of nine children (six of whom reached adulthood).(16)\n" + 
            "\n" + 
            "The Kant household stressed the pietist values of religious devotion, humility, and a literal interpretation of the Bible.(17) The young Immanuel's education was strict, punitive and disciplinary, and focused on Latin and religious instruction over mathematics and science.(18)\n" + 
            "\n" + 
            "In his later years, Kant lived a strictly ordered life. It was said that neighbors would set their clocks by his daily walks. He never married but seems to have had a rewarding social life; he was a popular teacher as well as a modestly successful author, even before starting on his major philosophical works.(19)\n" + 
            "\n" + 
            "Kant showed a great aptitude for study at an early age. He first attended the Collegium Fridericianum , from which he graduated at the end of the summer of 1740. In 1740, aged 16, he enrolled at the University of Königsberg , where he would later remain for the rest of his professional life.(20) He studied the philosophy of Gottfried Leibniz and Christian Wolff under Martin Knutzen (Associate Professor of Logic and Metaphysics from 1734 until he died in 1751), a rationalist who was also familiar with developments in British philosophy and science and introduced Kant to the new mathematical physics of Isaac Newton . Knutzen dissuaded Kant from the theory of pre-established harmony , which he regarded as \"the pillow for the lazy mind\".(21) He also dissuaded Kant from idealism , the idea that reality is purely mental, which most philosophers in the 18th century regarded negatively. The theory of transcendental idealism that Kant later included in the Critique of Pure Reason was developed partially in opposition to traditional idealism.\n" + 
            "\n" + 
            "Kant had contacts with students, colleagues, friends and diners who frequented the local Masonic lodge.(22)\n" + 
            "\n" + 
            "His father's stroke and subsequent death in 1746 interrupted his studies. Kant left Königsberg shortly after August 1748;(23) he would return there in August 1754.(24) He became a private tutor in the towns surrounding Königsberg, but continued his scholarly research. In 1749, he published his first philosophical work, Thoughts on the True Estimation of Living Forces (written in 1745–1747).(25)\n" + 
            "\n" + 
            "Kant is best known for his work in the philosophy of ethics and metaphysics, but he made significant contributions to other disciplines. In 1754, while contemplating on a prize question by the Berlin Academy about the problem of Earth's rotation, he argued that the Moon's gravity would slow down Earth's spin and he also put forth the argument that gravity would eventually cause the Moon's tidal locking to coincide with the Earth's rotation.(b) (27) The next year, he expanded this reasoning to the formation and evolution of the Solar System in his Universal Natural History and Theory of the Heavens.(27) In 1755, Kant received a license to lecture in the University of Königsberg and began lecturing on a variety of topics including mathematics, physics, logic, and metaphysics. In his 1756 essay on the theory of winds, Kant laid out an original insight into the Coriolis force .\n" + 
            "\n" + 
            "In 1756, Kant also published three papers on the 1755 Lisbon earthquake.(28) Kant's theory, which involved shifts in huge caverns filled with hot gases, though inaccurate, was one of the first systematic attempts to explain earthquakes in natural rather than supernatural terms. In 1757, Kant began lecturing on geography making him one of the first lecturers to explicitly teach geography as its own subject.(29) (30) Geography was one of Kant's most popular lecturing topics and, in 1802, a compilation by Friedrich Theodor Rink of Kant's lecturing notes, Physical Geography, was released. After Kant became a professor in 1770, he expanded the topics of his lectures to include lectures on natural law, ethics, and anthropology, along with other topics.(29)\n" + 
            "\n" + 
            "In the Universal Natural History, Kant laid out the nebular hypothesis , in which he deduced that the Solar System had formed from a large cloud of gas, a nebula . Kant also correctly deduced that the Milky Way was a large disk of stars , which he theorized formed from a much larger spinning gas cloud. He further suggested that other distant \"nebulae\" might be other galaxies. These postulations opened new horizons for astronomy, for the first time extending it beyond the solar system to galactic and intergalactic realms.(31)\n" + 
            "\n" + 
            "From then on, Kant turned increasingly to philosophical issues, although he continued to write on the sciences throughout his life. In the early 1760s, Kant produced a series of important works in philosophy. The False Subtlety of the Four Syllogistic Figures , a work in logic, was published in 1762. Two more works appeared the following year: Attempt to Introduce the Concept of Negative Magnitudes into Philosophy and The Only Possible Argument in Support of a Demonstration of the Existence of God . By 1764, Kant had become a notable popular author, and wrote Observations on the Feeling of the Beautiful and Sublime ; he was second to Moses Mendelssohn in a Berlin Academy prize competition with his Inquiry Concerning the Distinctness of the Principles of Natural Theology and Morality (often referred to as \"The Prize Essay\"). In 1766 Kant wrote a critical piece on Emanuel Swedenborg 's Dreams of a Spirit-Seer.\n" + 
            "\n" + 
            "In 1770, Kant was appointed Full Professor of Logic and Metaphysics at the University of Königsberg. In defense of this appointment, Kant wrote his inaugural dissertation On the Form and Principles of the Sensible and the Intelligible World(c) This work saw the emergence of several central themes of his mature work, including the distinction between the faculties of intellectual thought and sensible receptivity. To miss this distinction would mean to commit the error of subreption , and, as he says in the last chapter of the dissertation, only in avoiding this error does metaphysics flourish.\n" + 
            "\n" + 
            "It is often claimed that Kant was a late developer, that he only became an important philosopher in his mid-50's after rejecting his earlier views. While it is true that Kant wrote his greatest works relatively late in life, there is a tendency to underestimate the value of his earlier works. Recent Kant scholarship has devoted more attention to these \"pre-critical\" writings and has recognized a degree of continuity with his mature work.(32)\n" + 
            "\n" + 
            "At age 46, Kant was an established scholar and an increasingly influential philosopher, and much was expected of him. In correspondence with his ex-student and friend Markus Herz , Kant admitted that, in the inaugural dissertation, he had failed to account for the relation between our sensible and intellectual faculties.(33) He needed to explain how we combine what is known as sensory knowledge with the other type of knowledge—that is, reasoned knowledge—these two being related, but having very different processes. \n" + 
            "\n";
}
