package example.banner;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class HttpServletUtils {
   private static final int DEFAULT_BUFFER_SIZE = 4096;
   private static final int FILE_BUFFER_SIZE = 2048000;
   private static final int LINE_BUFFER_SIZE = 4096;

   // 从src中提取键值对key=value或key="value"中的value
   // 如果src为null或src中不存在'key='串, 则返回defaultValue
   private static String getParameter(String src, String key, String defaultValue) {
      if (null == src) return defaultValue;
      int pos = src.indexOf(key + '=');
      if (-1 == pos) return defaultValue;
      src = src.substring(pos + key.length() + 1);
      if (src.length() == 0) return defaultValue;

      // value被'"'包括，则去除首尾的'"'，否则以';'或空白字符结束
      if (src.charAt(0) == '"') {
         for (pos = 1; pos < src.length(); ++pos) {
            if (src.charAt(pos) == '"') break;
         }
         return src.substring(1, pos);
      } else {
         for (pos = 0; pos < src.length(); ++pos) {
            char c = src.charAt(pos);
            if (c == ';' || c == ' ' || c == '\n' || c == '\r') break;
         }
         return src.substring(0, pos);
      }
   }

   // 解析出表单中的每项输入
   public static List<FormData> pareseRequest(HttpServletRequest req) throws IOException {
      // 获取字符编码方式
      Charset charset = Charset.defaultCharset();
      try {
         charset = Charset.forName(req.getCharacterEncoding());
      } catch (IllegalArgumentException ignored) {
      }

      // 获取表单项的分界符
      String boundary = getParameter(req.getContentType(), "boundary", null);
      if (null == boundary) return null;
      else boundary = "--" + boundary;

      // 读取表单输入
      List<FormData> list = new ArrayList<>();
      ServletInputStream sin = req.getInputStream();
      ServletInputBuffer lineBuffer = new ServletInputBuffer(LINE_BUFFER_SIZE, charset);
      // HTTP表单正文以boundary开始
      lineBuffer.readLines(sin, boundary);
      // 每次读取一个表单项的内容
      for (String line; ; ) {
         // 读取表单项描述
         lineBuffer.readLine(sin);
         line = lineBuffer.toString();
         if (null == line) break;
         // 解析表单项描述
         FormData form = new FormData();
         form.name = getParameter(line, "name", "");
         form.value = getParameter(line, "filename", "");
         // 是文件
         if (!"".equals(form.value)) {
            // 下一行是文件类型 Content-Type
            lineBuffer.readLine(sin);
            line = lineBuffer.toString();
            if (line.startsWith("Content-Type:")) {
               form.contentType = line.substring(13).trim();
            }
            // 跳过分隔空行
            lineBuffer.readLine(sin);
            // 提取二进制文件
            ServletInputBuffer fileBuffer = new ServletInputBuffer(FILE_BUFFER_SIZE, charset);
            fileBuffer.readLines(sin, boundary);
            form.file = fileBuffer.getBytes();
            // 输入为空: 输入结束
            if (null == form.file) break;
            // 添加到结果列表中
            list.add(form);
         } 
         // 不是文件
         else {
            // 跳过分隔空行
            lineBuffer.readLine(sin);
            // 提取表单输入为字符串类型
            lineBuffer.readLines(sin, boundary);
            form.value = lineBuffer.toString();
            // 输入为空: 输入结束
            if (null == form.value) break;
            // 输入不全是空白字符才添加到结果列表中 
            if (!form.value.isBlank()) {
               list.add(form);
            }
         }
      } // for
      return list;
   }

   static class ServletInputBuffer {
      int len = 0;
      Charset charset;
      byte[] buffer;

      ServletInputBuffer(int bufferSize, Charset charset) {
         buffer = new byte[bufferSize > 0 ? bufferSize : DEFAULT_BUFFER_SIZE];
         this.charset = charset;
      }

      // 使用sin.readLine读取一行输入
      void readLine(ServletInputStream sin) throws IOException {
         len = sin.readLine(buffer, 0, buffer.length);
      }

      // 使用sin.readLine读取多行输入直到单次调用readLine的输入为boundary为止（首尾至多可以多4个字符）
      // 如果boundary为null, 则调用readLine(ServletInputStream sin)函数只读入一行
      void readLines(ServletInputStream sin, String boundary) throws IOException {
         if (null == boundary) readLine(sin);
         // 为减少不必要的转换, 只有输入长度在minLen~maxLen之间的,
         // 才转换成字符串与boundary进行比较
         int n, minLen = boundary.length(), maxLen = boundary.length() + 4;
         for (len = 0; len < buffer.length; len += n) {
            n = sin.readLine(buffer, len, buffer.length - len);
            if (n <= maxLen) {
               if (n < 0) break; // n == -1
               // n==-1可以通过toString()或getBytes()返回null来判断
               if (n >= minLen && new String(buffer, len, n, charset).contains(boundary)) break;
            }
         }
      }

      // 去除输入末尾的\r\n之后的新byte数组
      byte[] getBytes() {
         // HTTP以 \r\n 分隔每'行'
         int realLen = len - (len > 0 && buffer[len - 1] == '\n' ?
                     (len > 1 && buffer[len - 2] == '\r' ? 2 : 1) : 0);
         if (realLen < 0) return null;
         byte[] ret = new byte[realLen];
         System.arraycopy(buffer, 0, ret, 0, realLen);
         return ret;
      }

      // 去除输入末尾的\r\n之后转换为字符串
      @Override
      public String toString() {
         // HTTP以 \r\n 分隔每'行'
         int realLen = len - (len > 0 && buffer[len - 1] == '\n' ?
                     (len > 1 && buffer[len - 2] == '\r' ? 2 : 1) : 0);
         if (realLen < 0) return null;
         return new String(buffer, 0, realLen, charset);
      }
   }
}
