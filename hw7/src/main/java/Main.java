import com.google.common.collect.Lists;
import java.util.List;
import ru.otus.kunin.DSON;

public class Main {

  private static class Cat {

    public final String name;
    public final int age;
    public final List<Cat> friends;

    private Cat(final String name, final int age, final List<Cat> friends) {
      this.name = name;
      this.age = age;
      this.friends = friends;
    }

    static class Builder {

      private String name;
      private int age;
      private List<Cat> friends;

      Builder setName(String name) {
        this.name = name;
        return this;
      }

      Builder setAge(int age) {
        this.age = age;
        return this;
      }

      Builder setFriends(List<Cat> friends) {
        this.friends = friends;
        return this;
      }

      Cat build() {
        return new Cat(name, age, friends);
      }

    }
  }

  public static void main(String[] args) {
    System.out.println("<json>");
    Object[] objects = {1, false, 2.3, "hello", null};
    System.out.println(DSON.toJsonObject(new byte[]{1, 2, 3}).toString());
    System.out.println(DSON.toJsonObject(objects).toString());
    System.out.println(DSON.toJsonObject("mew").toString());
    System.out.println(DSON.toJsonObject(1).toString());
    System.out.println(DSON.toJsonObject(1.1).toString());
    System.out.println(DSON.toJsonObject(false).toString());

    final Cat aLonelyCat = new Cat.Builder()
        .setName("Fluffy")
        .setAge(3)
        .setFriends(Lists.newArrayList())
        .build();
    System.out.println(DSON.toJsonObject(aLonelyCat));
  }

}
