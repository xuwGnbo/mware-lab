package example.banner;

public class FormData {
   public String contentType; // 如果是文件则为文件的类型，否则为null
   public String name;        // 表单项名称
   public String value;       // 表单项的值，如果是文件，则为文件名
   public byte[] file;        // 把文件保存为字节数组
}
