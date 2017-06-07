import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import ru.otus.kunin.ConvertToJsonValue;
import ru.otus.kunin.DSON;

import javax.json.Json;
import java.util.List;

public class Main {

  public static void main(String[] args) {
    System.out.println("<json>");

    final ConvertToJsonValue reverseStringConverter = o -> {
      final String reversString = new StringBuilder(o.toString()).reverse().toString();
      return Json.createValue(reversString);
    };
    final ImmutableMap<Class<?>, ConvertToJsonValue> customConverters =
            ImmutableMap.of(String.class, reverseStringConverter);
    final DSON.Config config = DSON.Config.builder().setCustomConverters(customConverters).build();
    final DSON dson = DSON.create(config);

    Object[] objects = {1, false, 2.3f, "hello", null};
    System.out.println(dson.toJsonObject(new byte[]{1, 2, 3}).toString());
    System.out.println(dson.toJsonObject(objects).toString());
    System.out.println(dson.toJsonObject("mew").toString());
    System.out.println(dson.toJsonObject(1).toString());
    System.out.println(dson.toJsonObject(1.1).toString());
    System.out.println(dson.toJsonObject(false).toString());
    final ImmutableMap<Integer, String> int2StrMap = ImmutableMap.<Integer, String>builder()
            .put(1, "cat")
            .put(2, "dog")
            .build();
    System.out.println(dson.toJsonObject(int2StrMap));
    final ImmutableMap<String, List<String>> str2ListMap = ImmutableMap.<String, List<String>>builder()
            .put("cat_breeds", Lists.newArrayList("scottish fold", "siberian"))
            .put("dog_breeds", Lists.newArrayList("german shepard", "border collie"))
            .build();
    System.out.println(dson.toJsonObject(str2ListMap));

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
