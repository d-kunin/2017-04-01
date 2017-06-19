package ru.otus.kunin.dorm;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public abstract class DormEntity {

  @Id
  protected Long id;

  public long getId() {
    return id;
  }

  public boolean isNew() {
    return null == id;
  }

}
