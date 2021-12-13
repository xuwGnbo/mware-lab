package example.banner;

import group14.JDBCUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

public class showBanner extends HttpServlet {
   private static final long serialVersionUID = 1L;

   @Override
   protected void doGet(HttpServletRequest req, HttpServletResponse resp)
           throws ServletException, IOException {
      resp.setContentType("text/html; charset=utf-8");
      PrintWriter out = resp.getWriter();
      // 连接数据库
      try (Connection conn = JDBCUtils.getConnection()) {
         if (null == conn) {
            out.println("<html><body><h3>连接数据库失败，请联系管理员处理。</h3></body></html>");
            return;
         }
         // SQL查询
         String sql = "SELECT id, link FROM banner" +
                 " WHERE link!='' AND type!='' AND file!=''";
         try (Statement stmt = conn.createStatement();
              ResultSet rs = stmt.executeQuery(sql)) {
            if (!rs.next()) {
               out.println("<html><head><style type='text/css'>a{text-decoration: none;}</style></head>" +
                     "<body><h3><a href='/demo/'>&gt;.&lt;</a> 数据库没有图片</h3></body></html>");
               return;
            }
            // 随机选择一张图片
            rs.last(); // 定位到最后一条，以获取图片数量
            int selected = new Random().nextInt(rs.getRow()) + 1;
            rs.absolute(selected); // 定位到选中的图片
            int id = rs.getInt("id");
            String link = rs.getString("link");
            // 使用绝对路径
            out.println(String.format(
                    "<a href='%s'><img src='/demo/banners/showImage?id=%s' alt='%s'></a>",
                    link, id, link));
         }
      } catch (SQLException ex) {
         out.println("连接数据库时发生异常: "+ex);
      }
   }
}
