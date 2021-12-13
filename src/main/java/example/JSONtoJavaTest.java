package example;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class JSONtoJavaTest {

   // 将一个student对象转换为JSON字符串并输出
   public static void javaObjToJSONstr() {
      Student student = new Student("许文波", 21);
      JSONObject obj = JSONObject.fromObject(student);
      String jsonStr = obj.toString();

      System.out.println(jsonStr);
   }

   // 将一个含3个student对象元素的数组转换为JSON字符串并输出
   public static void javaArrToJSONstr() {
      Student[] students = new Student[3];
      students[0] = new Student("许文波", 21);
      students[1] = new Student("刘万权", 21);
      students[2] = new Student("潘林朝", 20);
      // 数组需要使用JSONArray
      JSONArray jsonArray = JSONArray.fromObject(students);
      String jsonStr = jsonArray.toString();

      System.out.println(jsonStr);
   }

   // 将JSON字符串转换为Student类对象并输出
   public static void jsonStrToJavaObj() {
      String jsonStr = "{'name': '许文波', 'age': 21}";
      JSONObject object = JSONObject.fromObject(jsonStr);
      // 使用toBean方法
      Student student = (Student) JSONObject.toBean(object, Student.class);

      System.out.println(student);
   }

   // 将JSON字符串转换为java数组并输出
   public static void jsonStrToJavaArr() {
      String jsonStr = "[{'age':21,'name':'许文波'}, " +
              "{'age':21,'name':'刘万权'}," +
              "{'age':20,'name':'潘林朝'}]";
      JSONArray array = JSONArray.fromObject(jsonStr);
      // 使用toArray方法
      Student[] students = (Student[]) JSONArray.toArray(array, Student.class);
      for (Student student : students) {
         System.out.println(student);
      }
   }

   public static void main(String[] args) {
      JSONtoJavaTest.javaObjToJSONstr();
      System.out.println("-------");
      JSONtoJavaTest.javaArrToJSONstr();
      System.out.println("-------");
      JSONtoJavaTest.jsonStrToJavaObj();
      System.out.println("-------");
      JSONtoJavaTest.jsonStrToJavaArr();
   }
}
