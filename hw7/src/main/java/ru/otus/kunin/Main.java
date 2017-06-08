package ru.otus.kunin;

import com.google.common.collect.ImmutableMap;
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
    final ImmutableMap<Class<?>, ConvertToJsonValue> customConverters =
            ImmutableMap.of(String.class, reverseStringConverter);
    final DSON.Config config = DSON.Config.builder().setCustomConverters(customConverters).build();
    final DSON dson = DSON.create(config);

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
