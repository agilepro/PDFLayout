/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.purplehillsbooks.pdflayout;

import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.Test;

import com.purplehillsbooks.pdflayout.elements.Frame;
import com.purplehillsbooks.pdflayout.elements.PDFDoc;
import com.purplehillsbooks.pdflayout.elements.Paragraph;

import static org.junit.Assert.*;

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
        
        createLongDocument1();
        createLongDocument2();
        createLongDocument3();
        createLongDocument4();
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
    
    private void createLongDocument2() throws Exception {
        
        PDFDoc doc = new PDFDoc(50,50,50,50);
        doc.showMargins = true;
        Frame mainFrame = doc.newInteriorFrame();
        
        for (int i=0; i<100; i++) {
            Frame innerFrame = mainFrame.newInteriorFrame();
            Color thisColor = Color.blue;
            if (i % 2 == 0) {
                thisColor = Color.green;
            }
            innerFrame.setBorderColor(thisColor);
            innerFrame.setMargin(15, 15, 15, 15);
            innerFrame.setPadding(15, 15, 15, 15);
            String repeatedPara = generateParagraph(100);
            Paragraph para = innerFrame.getNewParagraph();
            para.addTextCarefully(repeatedPara, 12, PDType1Font.HELVETICA);
            para = innerFrame.getNewParagraph();
            para.addTextCarefully(repeatedPara, 12, PDType1Font.HELVETICA);
            para = innerFrame.getNewParagraph();
            para.addTextCarefully(repeatedPara, 12, PDType1Font.HELVETICA);
        }
        
        File t1file = new File(testOutputFolder, "Test2-LongFrames.pdf");
        doc.saveToFile(t1file);
        
    }

    
    private void createLongDocument3() throws Exception {
        
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
    
    String sampleWords = " Immanuel Kant was born April 22, 1724 in Konigsberg, near the southeastern shore of the "
            + "Baltic Sea. Today Konigsberg has been renamed Kaliningrad and is part of Russia. But during Kant's "
            + "lifetime Konigsberg was the capital of East Prussia, and its dominant language was German. "
            + "Though geographically remote from the rest of Prussia and other German cities, Konigsberg was "
            + "then a major commercial center, an important military port, and a relatively cosmopolitan university town.\n" + 
            "\n" + 
            "Kant was born into an artisan family of modest means. His father was a master harness maker, "
            + "and his mother was the daughter of a harness maker, though she was better educated than most "
            + "women of her social class. Kant's family was never destitute, but his father's trade was in "
            + "decline during Kant's youth and his parents at times had to rely on extended family for financial support.\n" + 
            "\n" + 
            "Kant's parents were Pietist and he attended a Pietist school, the Collegium Fridericianum, "
            + "from ages eight through fifteen. Pietism was an evangelical Lutheran movement that emphasized conversion, "
            + "reliance on divine grace, the experience of religious emotions, and personal devotion involving "
            + "regular Bible study, prayer, and introspection. Kant reacted strongly against the forced soul-searching "
            + "to which he was subjected at the Collegium Fridericianum, in response to which he sought refuge "
            + "in the Latin classics, which were central to the school's curriculum. Later the mature Kant's emphasis "
            + "on reason and autonomy, rather than emotion and dependence on either authority or grace, may in part "
            + "reflect his youthful reaction against Pietism. But although the young Kant loathed his Pietist schooling, "
            + "he had deep respect and admiration for his parents, especially his mother, whose genuine religiosity "
            + "he described as not at all enthusiastic. According to his biographer, Manfred Kuehn, "
            + "Kant's parents probably influenced him much less through their Pietism than through their "
            + "artisan values of hard work, honesty, cleanliness, and independence, which they taught him by example.\n" + 
            "\n" + 
            "Kant attended college at the University of Konigsberg, known as the Albertina, where his early interest "
            + "in classics was quickly superseded by philosophy, which all first year students studied and which "
            + "encompassed mathematics and physics as well as logic, metaphysics, ethics, and natural law. Kant's "
            + "philosophy professors exposed him to the approach of Christian Wolff (16791750), whose critical "
            + "synthesis of the philosophy of G. W. Leibniz (16461716) was then very influential in German universities. "
            + "But Kant was also exposed to a range of German and British critics of Wolff, and there were strong "
            + "doses of Aristotelianism and Pietism represented in the philosophy faculty as well. Kant's favorite "
            + "teacher was Martin Knutzen (17131751), a Pietist who was heavily influenced by both Wolff and the "
            + "English philosopher John Locke (16321704). Knutzen introduced Kant to the work of "
            + "Isaac Newton (16421727), and his influence is visible in Kant's first published work, "
            + "Thoughts on the True Estimation of Living Forces (1747), which was a critical attempt to "
            + "mediate a dispute in natural philosophy between Leibnizians and Newtonians over the proper measurement "
            + "of force.\n" + 
            "\n" + 
            "After college Kant spent six years as a private tutor to young children outside Konigsberg. By this time "
            + "both of his parents had died and Kant's finances were not yet secure enough for him to pursue an "
            + "academic career. He finally returned to Konigsberg in 1754 and began teaching at the Albertina the "
            + "following year. For the next four decades Kant taught philosophy there, until his retirement from "
            + "teaching in 1796 at the age of seventy-two.\n" + 
            "\n" + 
            "Kant had a burst of publishing activity in the years after he returned from working as a private tutor. "
            + "In 1754 and 1755 he published three scientific works  one of which, Universal Natural History and Theory "
            + "of the Heavens (1755), was a major book in which, among other things, he developed what later became "
            + "known as the nebular hypothesis about the formation of the solar system. Unfortunately, "
            + "the printer went bankrupt and the book had little immediate impact. To secure qualifications for "
            + "teaching at the university, Kant also wrote two Latin dissertations: the first, entitled "
            + "Concise Outline of Some Reflections on Fire (1755), earned him the Magister degree; and the second, "
            + "New Elucidation of the First Principles of Metaphysical Cognition (1755), entitled him to teach as "
            + "an unsalaried lecturer. The following year he published another Latin work, The Employment in "
            + "Natural Philosophy of Metaphysics Combined with Geometry, of Which Sample I Contains the "
            + "Physical Monadology (1756), in hopes of succeeding Knutzen as associate professor of logic "
            + "and metaphysics, though Kant failed to secure this position. Both the New Elucidation, "
            + "which was Kant's first work concerned mainly with metaphysics, and the Physical Monadology further "
            + "develop the position on the interaction of finite substances that he first outlined in Living Forces. "
            + "Both works depart from Leibniz-Wolffian views, though not radically. The New Elucidation in "
            + "particular shows the influence of Christian August Crusius (17151775), a German critic of Wolff.\n" + 
            "\n" + 
            "As an unsalaried lecturer at the Albertina Kant was paid directly by the students who attended his "
            + "lectures, so he needed to teach an enormous amount and to attract many students in order to earn "
            + "a living. Kant held this position from 1755 to 1770, during which period he would lecture an "
            + "average of twenty hours per week on logic, metaphysics, and ethics, as well as mathematics, physics, "
            + "and physical geography. In his lectures Kant used textbooks by Wolffian authors such as Alexander "
            + "Gottlieb Baumgarten (17141762) and Georg Friedrich Meier (17181777), but he followed them loosely "
            + "and used them to structure his own reflections, which drew on a wide range of ideas of "
            + "contemporary interest. These ideas often stemmed from British sentimentalist philosophers "
            + "such as David Hume (17111776) and Francis Hutcheson (16941747), some of whose texts were "
            + "translated into German in the mid-1750s; and from the Swiss philosopher Jean-Jacques Rousseau "
            + "(17121778), who published a flurry of works in the early 1760s. From early in his career Kant "
            + "was a popular and successful lecturer. He also quickly developed a local reputation as a promising "
            + "young intellectual and cut a dashing figure in Konigsberg society.\n" + 
            "\n" + 
            "After several years of relative quiet, Kant unleashed another burst of publications in 17621764, "
            + "including five philosophical works. The False Subtlety of the Four Syllogistic Figures (1762) "
            + "rehearses criticisms of Aristotelian logic that were developed by other German philosophers. "
            + "The Only Possible Argument in Support of a Demonstration of the Existence of God (17623) is a "
            + "major book in which Kant drew on his earlier work in Universal History and New Elucidation to "
            + "develop an original argument for God's existence as a condition of the internal possibility of "
            + "all things, while criticizing other arguments for God's existence. The book attracted several "
            + "positive and some negative reviews. In 1762 Kant also submitted an essay entitled Inquiry "
            + "Concerning the Distinctness of the Principles of Natural Theology and Morality to a prize competition "
            + "by the Prussian Royal Academy, though Kant's submission took second prize to Moses Mendelssohn's "
            + "winning essay (and was published with it in 1764). Kant's Prize Essay, as it is known, departs "
            + "more significantly from Leibniz-Wolffian views than his earlier work and also contains his first "
            + "extended discussion of moral philosophy in print. The Prize Essay draws on British sources to "
            + "criticize German rationalism in two respects: first, drawing on Newton, Kant distinguishes between "
            + "the methods of mathematics and philosophy; and second, drawing on Hutcheson, he claims that "
            + "'an unanalysable feeling of the good' supplies the material content of our moral obligations, "
            + "which cannot be demonstrated in a purely intellectual way from the formal principle of perfection "
            + "alone (2:299). These themes reappear in the Attempt to Introduce the Concept of "
            + "Negative Magnitudes into Philosophy (1763), whose main thesis, however, is that the real "
            + "opposition of conflicting forces, as in causal relations, is not reducible to the logical "
            + "relation of contradiction, as Leibnizians held. In Negative Magnitudes Kant also argues that "
            + "the morality of an action is a function of the internal forces that motivate one to act, "
            + "rather than of the external (physical) actions or their consequences. Finally, Observations on the "
            + "Feeling of the Beautiful and the Sublime (1764) deals mainly with alleged differences in the tastes"
            + " of men and women and of people from different cultures. After it was published, Kant filled his own "
            + "interleaved copy of this book with (often unrelated) handwritten remarks, many of which reflect "
            + "the deep influence of Rousseau on his thinking about moral philosophy in the mid-1760s.";
}
