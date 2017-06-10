package ru.otus.kunin.dorm.main;

import ru.otus.kunin.dorm.DormEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "dima_users")
public class User extends DormEntity {

  public String getName() {
    return name;
  }

  public int getAge() {
    return age;
  }

  @Column(columnDefinition = "varchar(255)")
  private String name;

  @Column(columnDefinition = "int(3) not null default 0")
  private int age;

}
