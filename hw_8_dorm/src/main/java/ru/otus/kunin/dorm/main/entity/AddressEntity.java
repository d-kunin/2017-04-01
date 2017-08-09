package ru.otus.kunin.dorm.main.entity;

import ru.otus.kunin.dorm.api.DormEntity;

import javax.persistence.Entity;
import java.util.Objects;

@Entity
public class AddressEntity extends DormEntity {

  private String street;
  private int postalCode;

  /* required */
  public AddressEntity() {
  }

  public AddressEntity(String street, int postalCode) {
    this.street = street;
    this.postalCode = postalCode;
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public int getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(int postalCode) {
    this.postalCode = postalCode;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AddressEntity that = (AddressEntity) o;
    return postalCode == that.postalCode &&
        Objects.equals(id, that.id) &&
        Objects.equals(street, that.street);
  }

  @Override
  public int hashCode() {
    return Objects.hash(street, postalCode, id);
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("AddressEntity{");
    sb.append("id='").append(id).append('\'');
    sb.append(", street=").append(street);
    sb.append(", postalCode=").append(postalCode);
    sb.append('}');
    return sb.toString();
  }
}
