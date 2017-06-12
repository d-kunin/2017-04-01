package ru.otus.kunin;

import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Optional;
import ru.otus.kunin.dson.JsonName;

class CatPOJO {

  @JsonName("name_in_json") // my custom annotation
  @SerializedName("name_in_json") // annotation from GSON for test
  public final String name;
  public final int age;
  public final List<CatPOJO> friends;

  private CatPOJO(final String name, final int age, final List<CatPOJO> friends) {
    this.name = name;
    this.age = age;
    this.friends = friends;
  }

  static class Builder {

    private String name;
    private int age;
    private List<CatPOJO> friends;

    Builder setName(String name) {
      this.name = name;
      return this;
    }

    Builder setAge(int age) {
      this.age = age;
      return this;
    }

    Builder setFriends(List<CatPOJO> friends) {
      this.friends = friends;
      return this;
    }

    CatPOJO build() {
      return new CatPOJO(name, age, Optional.ofNullable(friends).orElse(Lists.newArrayList()));
    }

  }
}
