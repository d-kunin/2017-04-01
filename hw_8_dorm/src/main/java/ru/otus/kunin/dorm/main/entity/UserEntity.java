package ru.otus.kunin.dorm.main.entity;

import ru.otus.kunin.dorm.api.DormEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "dima_users")
public class UserEntity extends DormEntity {

  /* required */
  public UserEntity() {
  }

  public UserEntity(final String name, final int age, final String displayName) {
    this.aNameField = name;
    this.age = age;
    this.displayName = displayName;
  }

  public String getName() {
    return aNameField;
  }

  public int getAge() {
    return age;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setName(final String name) {
    this.aNameField = name;
  }

  public void setAge(final int age) {
    this.age = age;
  }

  public void setDisplayName(final String displayName) {
    this.displayName = displayName;
  }

  @Column(columnDefinition = "varchar(255)", name = "name")
  private String aNameField;

  @Column(columnDefinition = "int(3) not null default 0")
  private int age;

  private String displayName;

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("UserEntity{");
    sb.append("id='").append(getId()).append('\'');
    sb.append(", aNameField='").append(aNameField).append('\'');
    sb.append(", age=").append(age);
    sb.append(", displayName='").append(displayName).append('\'');
    sb.append('}');
    return sb.toString();
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final UserEntity user = (UserEntity) o;
    return age == user.age &&
        Objects.equals(id, user.id) &&
        Objects.equals(aNameField, user.aNameField) &&
        Objects.equals(displayName, user.displayName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(aNameField, age, displayName, id);
  }
}
