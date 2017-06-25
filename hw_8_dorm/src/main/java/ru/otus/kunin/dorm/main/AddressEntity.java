package ru.otus.kunin.dorm.main;

import ru.otus.kunin.dorm.api.DormEntity;

import javax.persistence.Entity;
import java.util.Objects;

@Entity
public class AddressEntity extends DormEntity {

  private String street;
  private int index;

  public AddressEntity() {
  }

  public AddressEntity(String street, int index) {
    this.street = street;
    this.index = index;
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AddressEntity that = (AddressEntity) o;
    return index == that.index &&
        Objects.equals(id, that.id) &&
        Objects.equals(street, that.street);
  }

  @Override
  public int hashCode() {
    return Objects.hash(street, index, id);
  }
}
