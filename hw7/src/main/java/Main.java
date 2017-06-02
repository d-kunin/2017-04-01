import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;

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
        return new Cat(name, age, Optional.ofNullable(friends).orElse(Lists.newArrayList()));
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
    final ImmutableMap<Integer, String> int2StrMap = ImmutableMap.<Integer, String>builder()
            .put(1, "cat")
            .put(2, "dog")
            .build();
    System.out.println(DSON.toJsonObject(int2StrMap));
    final ImmutableMap<String, List<String>> str2ListMap = ImmutableMap.<String, List<String>>builder()
            .put("cat_breeds", Lists.newArrayList("scottish fold", "siberian"))
            .put("dog_breeds", Lists.newArrayList("german shepard", "border collie"))
            .build();
    System.out.println(DSON.toJsonObject(str2ListMap));

    final Cat cat = new Cat.Builder().setName("Saffran").setAge(1).build();
    final Cat aCat = new Cat.Builder()
            .setName("Fluffy")
            .setAge(3)
            .setFriends(Lists.newArrayList(
                    new Cat.Builder()
                            .setName("Stanford")
                            .setAge(3)
                            .setFriends(Lists.newArrayList(cat))
                            .build(),
                    cat))
            .build();
    System.out.println(DSON.toJsonObject(aCat));
  }

}
