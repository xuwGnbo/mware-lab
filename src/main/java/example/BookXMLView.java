package example;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaders;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class BookXMLView {
   public static void main(String[] args) {
      String xmlPath = "res/books.xml";
      try {
         BookXMLView bookXMLView = new BookXMLView(xmlPath);
         System.out.println("cmd: list|add|set|save|exit");

         Scanner reader = new Scanner(System.in);
         System.out.print("cmd> ");
         LOOP: // 根据输入的指令执行操作
         while (reader.hasNextLine()) {
            String cmd = reader.nextLine();
            switch (cmd.toLowerCase().trim()) {
               case "list": bookXMLView.list(); break;
               case "add" : bookXMLView.add();  break;
               case "save": bookXMLView.save(); break;
               case "set" : bookXMLView.set();  break;
               case "exit": break LOOP;
            }
            System.out.print("cmd> ");
         }
      } catch (JDOMException | IOException e) {
         e.printStackTrace();
      }
   }

   String xmlPath;
   Document doc;
   Element books;

   BookXMLView(String xmlPath) throws JDOMException, IOException {
      this.xmlPath = xmlPath;
      SAXBuilder builder = new SAXBuilder(XMLReaders.NONVALIDATING);
      doc = builder.build(xmlPath);
      books = doc.getRootElement();
   }

   // 读取XML中所有书目并显示
   public void list() {
      List<Element> bookList = books.getChildren("book");
      Element[] element = new Element[bookList.size()];
      this.list(books.getChildren("book").toArray(element));
   }

   // 按照列表格式输出当前书目信息
   private void list(Element[] bookList) {
      System.out.println("=========================================================");
      System.out.println("  ID                     NAME                     PRICE  ");
      System.out.println(" ----  ----------------------------------------  ------- ");
      for (Element book : bookList) {
         // ID是XML元素属性，name和price是子元素
         String id = book.getAttributeValue("id");
         String name = book.getChildTextTrim("name");
         String price = book.getChildTextTrim("price");
         try {
            System.out.printf(" %4s  %-40s  %7.2f ", id, name, Double.valueOf(price));
         } catch (NumberFormatException ex) { // 价格格式错误（不能转换成Double型数据）
            System.out.printf(" %4s  %-40s  %s(!IS NOT DOUBLE) ", id, name, price);
         }
         System.out.println();
      }
      System.out.println("=========================================================");
   }

   // 确认是否执行操作
   private boolean wantToQuit() {
      System.out.print(" no|(yes) ?: "); // 默认为 yes
      String ans = new Scanner(System.in).nextLine();
      if ("no".equals(ans.toLowerCase().trim())) {
         System.out.println("quit!");
         return true;
      }
      return false;
   }

   // 根据ID修改XML中相应书目信息
   public void set() {
      // 输入要修改书目的ID
      Scanner reader = new Scanner(System.in);
      System.out.print("\t- ID ?: ");
      String id = reader.nextLine();
      // 查找相应ID的书目
      List<Element> bookList = books.getChildren("book");
      for (Element book : bookList) {
         if (!id.equals(book.getAttributeValue("id"))) continue;
         // 找到了，输入要修改的子元素
         System.out.print("\t- name|price ?: ");
         String childName = reader.nextLine();
         // 显示旧值
         System.out.print("\t- Old Value := " + book.getChild(childName).getText() + '\n');
         // 输入新值
         System.out.print("\t- New Value ?: ");
         String value = reader.nextLine();

         // 确认操作
         System.out.println("|-You input = '" + value + '\'');
         if (wantToQuit()) return; // 取消操作

         // 执行操作: 修改相应信息
         book.getChild(childName).setText(value);
         this.list(new Element[]{book});
         System.out.println("OK!");
         return;
      }
      // 没有找到相应的ID
      System.out.println("Sorry, Not Found!");
   }

   // 向XML中添加一条书目信息
   public void add() {
      // 添加新书，需要输入 ID, 书名和价格
      Scanner reader = new Scanner(System.in);
      System.out.print("\t- ID    : ");
      String newID = reader.nextLine();
      System.out.print("\t- NAME  : ");
      String newName = reader.nextLine();
      System.out.print("\t- PRICE : ");
      String newPrice = reader.nextLine();

      // 确认操作
      System.out.println("|-ID    = '" + newID + '\'');
      System.out.println("|-name  = '" + newName + '\'');
      System.out.println("|-price = '" + newPrice + '\'');
      if (wantToQuit()) return; // 取消操作

      // 执行操作: 将新内容添加到XML列表中并显示
      Element newBook = new Element("book").setAttribute("id", newID);
      newBook.addContent(new Element("name").addContent(newName));
      newBook.addContent(new Element("price").addContent(newPrice));
      books.addContent(newBook);
      this.list(new Element[]{newBook});
   }

   // 保存操作后的XML信息（覆盖原文件）
   public void save() throws IOException {
      System.out.print("saving...");
      XMLOutputter out = new XMLOutputter();
      Format format = Format.getPrettyFormat();
      format.setIndent("    "); // 写入时进行格式化：添加4个空格的缩进
      out.setFormat(format);
      out.output(doc, new FileOutputStream(xmlPath));
      System.out.println("OK!");
   }
}
