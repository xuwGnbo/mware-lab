package example;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class JDomParse {
   public JDomParse(){
      String xmlpath="library.xml";
      SAXBuilder builder=new SAXBuilder(false);
      try {
         Document doc=builder.build(xmlpath);
         Element books=doc.getRootElement();
         List booklist=books.getChildren("book");
         for (Iterator iter = booklist.iterator(); iter.hasNext();) {
            Element book = (Element) iter.next();
            String email=book.getAttributeValue("bookid");
            System.out.println(email);
            String name=book.getChildTextTrim("name");
            System.out.println(name);
            book.getChild("name").setText("Pride and Prejudice");
         }
         XMLOutputter outputter=new XMLOutputter();      outputter.output(doc,new FileOutputStream(xmlpath));
      } catch (JDOMException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
   public static void main(String[] args) {
      new JDomParse();
   }
}
