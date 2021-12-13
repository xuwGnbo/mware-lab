package group14;

import org.jdom2.JDOMException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class XMLtoDB extends HttpServlet {
   public static void initTable(String xmlPath) throws JDOMException, IOException {
      List<Student> students = MyXMLReader.readStudentsFromXML(xmlPath);

      try (Connection conn = JDBCUtils.getConnection()) {
         if (null == conn) {
            return; // 数据库连接失败
         }
         String sql = "CALL add_student(?,?,?,?)";
         try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int failed = 0;
            for (Student student : students) {
               pstmt.setString(1, student.getId());
               pstmt.setString(2, student.getName());
               pstmt.setString(3, student.getGender());
               pstmt.setString(4, student.getMajor());
               int rows = pstmt.executeUpdate();
               if (rows != 1) {
                  failed++;
                  System.err.println("数据录入失败: "+student);
               }
            }
            System.out.println("数据录入完成, 成功录入: "+(students.size()-failed)+"/"+students.size());
         }
      } catch (SQLException e) {
         System.err.println("数据连接发生异常!");
         e.printStackTrace();
      }
   }

   @Override
   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      resp.setContentType("text/html;charset=utf-8");
      PrintWriter out = resp.getWriter();
      try {
         XMLtoDB.initTable(this.getClass().getClassLoader().getResource("MyStudentInfo.xml").getPath());
         out.println("<h3>init successfully!</h3>");
      } catch (JDOMException e) {
         e.printStackTrace();
      }
   }
}
