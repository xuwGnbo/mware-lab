package group14;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.Element;
import org.jdom2.input.sax.XMLReaders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyXMLReader {

   public static List<Student> readStudentsFromXML(String xmlPath) throws JDOMException, IOException {
      SAXBuilder builder = new SAXBuilder(XMLReaders.NONVALIDATING);

      Document document = builder.build(xmlPath);

      List<Student> students = new ArrayList<>();
      // 获取每个学生的信息并添加到结果列表中
      Element root = document.getRootElement();
      List<Element> elements = root.getChildren("student");
      for (Element element : elements) {
         Student student = new Student();
         student.setId(element.getChildText("id"));
         student.setName(element.getChildText("name"));
         student.setGender(element.getChildText("gender"));
         student.setMajor(element.getChildText("major"));
         students.add(student);
      }
      return students;
   }
}
