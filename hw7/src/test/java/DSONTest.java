package ru.otus.kunin;


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Test;
import ru.otus.kunin.dson.DSON;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class DSONTest {

    @Test
    public void serializeObjectArray() throws Exception {
        verifySameJson(new Object[]{1, false, 2.3, "hello", null});
    }

    @Test
    public void serializePrimitiveByteArray() throws Exception {
        verifySameJson(new byte[]{1, 2, 3});
    }

    @Test
    public void serializePrimitives() throws Exception {
        verifySameJson(1);
        verifySameJson(1.1);
        verifySameJson(false);
        verifySameJson(true);
    }

    @Test
    public void serializeIntToStrMap() throws Exception {
        final ImmutableMap<Integer, String> int2StrMap = ImmutableMap.<Integer, String>builder()
                .put(1, "cat")
                .put(2, "dog")
                .build();
        verifySameJson(int2StrMap);
    }

    @Test
    public void serializeStringToListMap() throws Exception {
        final ImmutableMap<String, List<String>> str2ListMap = ImmutableMap.<String, List<String>>builder()
                .put("cat_breeds", Lists.newArrayList("scottish fold", "siberian"))
                .put("dog_breeds", Lists.newArrayList("german shepard", "border collie"))
                .build();
        verifySameJson(str2ListMap);
    }

    @Test
    public void serializedPOJO() throws Exception {
        final CatPOJO simpleCat = new CatPOJO.Builder().setName("Saffran").setAge(1).build();
        final CatPOJO complexCat = new CatPOJO.Builder()
                .setName("Fluffy")
                .setAge(3)
                .setFriends(Lists.newArrayList(
                        new CatPOJO.Builder()
                                .setName("Stanford")
                                .setAge(3)
                                .setFriends(Lists.newArrayList(simpleCat))
                                .build(),
                        simpleCat))
                .build();
        verifySameJson(complexCat);
    }

    static <T> void verifySameJson(T value) {
        final Gson gson = new GsonBuilder().create();
        final DSON dson = new DSON.Builder().build();
        final String dsonJson = dson.toJsonObject(value).toString();
        final String gsonJson = gson.toJson(value);
        final String normalizedDson = dsonJson.replaceAll("\\s+", "");
        final String normalizedGson = gsonJson.replaceAll("\\s+", "");
        assertEquals(normalizedGson, normalizedDson);
    }
}