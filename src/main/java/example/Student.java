package example;

public class Student {

   private String name;
   private Integer age;

   public Student(String name, Integer age) {
      this.name = name;
      this.age = age;
   }

   // 必须要有无参构造方法, 否则会出现异常
   // java.lang.NoSuchMethodException: Student.<init>()
   public Student() {}

   public String getName() { return name; }

   public void setName(String name) { this.name = name; }

   public Integer getAge() { return age; }

   public void setAge(Integer age) { this.age = age; }

   @Override
   public String toString() {
      return "Student{name='" + name + '\'' + ", age=" + age + '}';
   }
}
