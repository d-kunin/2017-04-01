package ru.otus.kunin.dorm.main;

import ru.otus.kunin.dorm.DormEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "dima_users")
public class User extends DormEntity {

  public String getName() {
    return aNameField;
  }

  public int getAge() {
    return age;
  }

  public String getDisplayName() {
    return displayName;
  }

  @Column(columnDefinition = "varchar(255)", name = "name")
  private String aNameField;

  @Column(columnDefinition = "int(3) not null default 0")
  private int age;

  private String displayName;
}
