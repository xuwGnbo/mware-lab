package group14;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class JDBCUtils {
   // 配置文件路径
   private static final String DB_CONFIG = "/DB_config.properties";
   private static final Properties prop = new Properties();

   // 加载配置文件
   static {
      try {
         prop.load(JDBCUtils.class.getResourceAsStream(DB_CONFIG));
      } catch (IOException | NullPointerException ex) {
         System.err.println("请检查"+DB_CONFIG+"配置文件!");
         ex.printStackTrace();
      }
   }

   /**
    * 从DB_config.properties配置文件中获取driverClassName, url, username, password数据库连接信息。
    * 如果没有配置信息则使用默认配置: driverClassName='com.mysql.cj.jdbc.Driver';
    * url='jdbc:mysql://localhost:3306/test?useSSL=false&serverTimezone=UTC
    * &useUnicode=true&characterEncoding=UTF-8';
    * username='root'; password='';
    * 
    * @return 数据库连接
    * @throws SQLException 连接到数据库时发生错误或者连接配置信息正确
    */
   public static Connection getConnection() throws SQLException {
      // 加载JDBC驱动
      String driverClassName = prop.getProperty("driverClassName");
      if (null == driverClassName) driverClassName = "com.mysql.cj.jdbc.Driver";
      try {
         Class.forName(driverClassName);
      } catch (ClassNotFoundException ex) {
         throw new SQLException("driver class name error: "+driverClassName);
      }

      // 获取配置信息
      String url = prop.getProperty("url");
      String username = prop.getProperty("username");
      String password = prop.getProperty("password");
      // 没有则使用默认配置
      if (null == url) url = "jdbc:mysql://localhost:3306/test?"+
         "useSSL=false&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8";
      if (null == username) username = "root";
      if (null == password) password = "";

      // 连接数据库
      return DriverManager.getConnection(url, username, password);
   }
}
