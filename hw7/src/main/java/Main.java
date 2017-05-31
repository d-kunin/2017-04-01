import ru.otus.kunin.DSON;

public class Main {

  public static void main(String[] args) {
    System.out.println("<json>");
    Object[] objects = {1, false, 2.3, "hello", null};
    System.out.println(DSON.toJsonObject(objects).toString());
    System.out.println(DSON.toJsonObject("mew").toString());
    System.out.println(DSON.toJsonObject(1).toString());
    System.out.println(DSON.toJsonObject(1.1).toString());
    System.out.println(DSON.toJsonObject(false).toString());
  }

}
