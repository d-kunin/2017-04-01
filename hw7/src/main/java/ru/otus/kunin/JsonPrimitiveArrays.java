package ru.otus.kunin;

import java.util.Arrays;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;

public class JsonPrimitiveArrays {

  public static JsonArray toJsonArray(int[] array) {
    JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
    Arrays.stream(array).forEach(primitive -> arrayBuilder.add(primitive));
    return arrayBuilder.build();
  }

  public static JsonArray toJsonArray(long[] array) {
    JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
    Arrays.stream(array).forEach(primitive -> arrayBuilder.add(primitive));
    return arrayBuilder.build();
  }

  public static JsonArray toJsonArray(double[] array) {
    JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
    Arrays.stream(array).forEach(primitive -> arrayBuilder.add(primitive));
    return arrayBuilder.build();
  }

  public static JsonArray toJsonArray(boolean[] array) {
    JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
    for (final boolean primitive : array) {
      arrayBuilder.add(primitive);
    }
    return arrayBuilder.build();
  }

  public static JsonArray toJsonArray(byte[] array) {
    JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
    for (final byte primitive : array) {
      arrayBuilder.add(primitive);
    }
    return arrayBuilder.build();
  }

  public static JsonArray toJsonArray(short[] array) {
    JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
    for (final short primitive : array) {
      arrayBuilder.add(primitive);
    }
    return arrayBuilder.build();
  }

  public static JsonArray toJsonArray(float[] array) {
    JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
    for (final float primitive : array) {
      arrayBuilder.add(primitive);
    }
    return arrayBuilder.build();
  }
}
