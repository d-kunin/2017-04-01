package ru.otus.kunin.dorm;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public abstract class DormEntity {

  @Id
  private Long id;

  public long getId() {
    return id;
  }

}
