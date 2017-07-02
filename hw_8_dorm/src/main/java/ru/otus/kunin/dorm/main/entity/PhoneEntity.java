package ru.otus.kunin.dorm.main.entity;

import ru.otus.kunin.dorm.api.DormEntity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Objects;

@Entity
public class PhoneEntity extends DormEntity {

  /* required */ public PhoneEntity() {
  }

  public PhoneEntity(int code, String number) {
    this.code = code;
    this.number = number;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getNumber() {
    return number;
  }

  public void setNumber(String number) {
    this.number = number;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PhoneEntity that = (PhoneEntity) o;
    return code == that.code &&
        Objects.equals(id, that.id) &&
        Objects.equals(number, that.number);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, number, id);
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("PhoneEntity{");
    sb.append("id=").append(id);
    sb.append(", code='").append(code).append('\'');
    sb.append(", number='").append(number).append('\'');
    sb.append('}');
    return sb.toString();
  }

  private int code;
  private String number;
  @ManyToOne
  @JoinColumn(name = "user_id")
  private UserWithAddressAndPhoneEntity user;
}
