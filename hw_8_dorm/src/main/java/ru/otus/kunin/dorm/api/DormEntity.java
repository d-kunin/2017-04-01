package ru.otus.kunin.dorm.api;

import javax.persistence.*;

@Entity
@MappedSuperclass
public abstract class DormEntity {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Long id;

  public long getId() {
    return id;
  }

  public boolean isNew() {
    return null == id;
  }

}
