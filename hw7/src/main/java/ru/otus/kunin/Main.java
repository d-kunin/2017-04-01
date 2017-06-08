package ru.otus.kunin;

import com.google.common.collect.Lists;
import ru.otus.kunin.dson.ConvertToJsonValue;
import ru.otus.kunin.dson.DSON;

import javax.json.Json;

public class Main {

  public static void main(String[] args) {
    System.out.println("<For more test see DSONTest.java>");

    // An example of custom serializer that saves string in reverse: cat -> tac
    final ConvertToJsonValue reverseStringConverter = o -> {
      final String reversString = new StringBuilder(o.toString()).reverse().toString();
      return Json.createValue(reversString);
    };
    final DSON dson = new DSON.Builder()
            .addCustomConverter(String.class, reverseStringConverter)
            .setMaxDepth(42)
            .build();

    final CatPOJO cat = new CatPOJO.Builder().setName("Saffran").setAge(1).build();
    final CatPOJO aCat = new CatPOJO.Builder()
            .setName("Fluffy")
            .setAge(3)
            .setFriends(Lists.newArrayList(
                    new CatPOJO.Builder()
                            .setName("Stanford")
                            .setAge(3)
                            .setFriends(Lists.newArrayList(cat))
                            .build(),
                    cat))
            .build();
    System.out.println(dson.toJsonObject(aCat));
  }

}
