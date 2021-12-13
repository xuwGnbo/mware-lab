package example.banner;

import group14.JDBCUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class showImage extends HttpServlet {
   private static final long serialVersionUID = 1L;

   // 从数据库中获取对应id图片并返回
   @Override
   protected void doGet(HttpServletRequest req, HttpServletResponse resp)
           throws ServletException, IOException {
      try (Connection conn = JDBCUtils.getConnection()) {
         if (null == conn) return;
         // 从数据库中获取对应id的图片
         String id = req.getParameter("id");
         String sql = "SELECT type, file FROM banner WHERE id=" + id;
         try (Statement stmt = conn.createStatement();
              ResultSet rs = stmt.executeQuery(sql)) {
            if (!rs.next()) return; // 在showBanner中已经做过判断了
            // 输出图片
            resp.setContentType(rs.getString("type"));
            try (InputStream in = rs.getBinaryStream("file");
                 ServletOutputStream sout = resp.getOutputStream()) {
               byte[] bytes = new byte[4096];
               for (int n; -1 != (n = in.read(bytes, 0, bytes.length)); ) {
                  sout.write(bytes, 0, n); // 输出图片
               }
               sout.flush();
            } // catch IOException
         }
      } catch (SQLException ex) {
         System.err.println("数据库操作发生错误: " + ex);
      }
   }

   @Override
   protected void doPost(HttpServletRequest req, HttpServletResponse resp)
           throws ServletException, IOException {
      doGet(req, resp);
   }
}
