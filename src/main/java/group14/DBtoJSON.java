package group14;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBtoJSON extends HttpServlet {
   public static List<Student> getStudents() {
      try (Connection conn = JDBCUtils.getConnection()) {
         if (null == conn) {
            return null; // 数据库连接失败
         }
         String sql = "SELECT id, name, gender, major FROM TstudentInfo a, majors b" +
                 " WHERE a.majorID=b.majorID";
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql);

         List<Student> students = new ArrayList<>();
         while (rs.next()) {
            Student student = new Student();
            student.setId(rs.getString("id"));
            student.setName(rs.getString("name"));
            student.setGender(rs.getString("gender"));
            student.setMajor(rs.getString("major"));
            students.add(student);
         }
         return students;
      } catch (SQLException e) {
         System.err.println("数据连接发生异常!");
         e.printStackTrace();
      }
      return null;
   }

   @Override
   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      resp.setContentType("text/html;charset=utf-8");
      PrintWriter out = resp.getWriter();
      out.println("<pre>");
      List<Student> students = DBtoJSON.getStudents();
      if (null != students) {
         for (Student student : students) {
            out.println(JSONObject.fromObject(student).toString());
         }
      } else {
         System.out.println("empty!");
         out.println("empty table (no data)");
      }
      out.println("</pre>");
   }
}
