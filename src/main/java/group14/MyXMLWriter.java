package group14;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class MyXMLWriter {

   public static void writeStudentsToXML(List<Student> students, String xmlPath) {
      // 创建根节点
      Element root = new Element("students");
      // 将根节点添加到文档中
      Document document = new Document(root);
      for (Student student : students) {
         Element element = new Element("student");
         element.addContent(new Element("id").setText(student.getId()));
         element.addContent(new Element("name").setText(student.getName()));
         element.addContent(new Element("gender").setText(student.getGender()));
         element.addContent(new Element("major").setText(student.getMajor()));
         document.addContent(element);
      }

      // 设置格式化方式
      Format format = Format.getPrettyFormat();
      format.setEncoding("utf-8");
      format.setIndent("    "); // 4个空格缩进

      XMLOutputter out = new XMLOutputter();
      out.setFormat(format);
      try {
         out.output(document, new FileOutputStream(xmlPath));
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
}
