package example.banner;

import group14.JDBCUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class addBanner extends HttpServlet {
   private static final long serialVersionUID = 1L;

   @Override
   protected void doGet(HttpServletRequest req, HttpServletResponse resp)
           throws ServletException, IOException {
      resp.setContentType("text/html;charset=utf-8");
      PrintWriter out = resp.getWriter();

      // 解析请求，获取表单信息
      List<FormData> list = HttpServletUtils.pareseRequest(req);
      String link = "", contentType=null;
      byte[] image = null;
      assert list != null;
      for (FormData data : list) {
         if (data.name.equals("link")) {
            link = data.value;
         } else if (data.name.equals("image")) {
            image = data.file;
            contentType = data.contentType;
         }
      }

      // 显示提交信息属性
      String formInfo = "list-size: " + list.size() + '\n' +
              "link: " + link + '\n' +
              "content-type: " + contentType + '\n' +
              "image-size: " +
              (null != image ? image.length : -1) + '\n';

      String feedBack;
      // 将信息添加到数据库中
      if (!"".equals(link) && null != contentType && null != image) {
         String sql = "INSERT INTO banner (link,type,file) VALUES (?,?,?)";
         // 连接数据库
         try (Connection conn = JDBCUtils.getConnection()) {
            if (null == conn) {
               out.println("<html><body><h3>连接数据库失败，请联系管理员处理。</h3></body></html>");
               return;
            }
            // 参数化SQL语句
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
               pstmt.setString(1, link);
               pstmt.setString(2, contentType);
               pstmt.setBytes(3, image);
               // 执行SQL
               int rows = pstmt.executeUpdate();
               if (rows == 0) {
                  feedBack = "数据录入失败，共添加 0 张图片";
               } else {
                  feedBack = String.format("数据录入成功，共添加 %d 张图片", rows);
               }
            }
         } catch (SQLException ex) {
            ex.printStackTrace();
            feedBack = "数据录入发生错误: "+ex;
         }
      } else {
         feedBack = "数据不完整，请重新输入图片链接及上传图片文件。";
      }

      // Servlet之间相互调用可以用相对路径
      // 通过HTML调用的Servlet也要在web.xml中添加配置
      // 返回HTML
      out.println("<html><head><meta charset='UTF-8'>"+
               "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
               "<title>广告条示例</title>" +
               "<style type='text/css'>a{text-decoration: none;}</style></head>"+
               "<body><a href='/demo/'><h3>上传图片</h3></a>" +
               "<form action='addBanner' method=POST enctype='multipart/form-data'>" +
               "链接: <input type=text name=link placeholder='链接'><br>" +
               "图片: <input type=file name=image accept='image/*,.pdf' value='上传图片'><br>" +
               "<input type=submit value='提交'><br>" +
               "</form><pre>");
      out.println(feedBack + '\n' + formInfo);
      out.println("</pre></body></html>");
   }

   @Override
   protected void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
      doGet(req, resp);
   }
}
