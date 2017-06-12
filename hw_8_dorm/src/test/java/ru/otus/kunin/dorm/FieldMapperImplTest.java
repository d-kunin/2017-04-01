package ru.otus.kunin.dorm;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

import java.util.List;
import org.junit.Test;
import ru.otus.kunin.dorm.main.User;

public class FieldMapperImplTest {

  FieldMapperImpl fieldMapper = new FieldMapperImpl();

  @Test
  public void mapField() throws Exception {
    List<FieldMapping> fieldMappings = ReflectionUtils.getAllFields(User.class).stream()
        .map(fieldMapper::mapField)
        .collect(toList());
    assertEquals(3, fieldMappings.size());
  }

}