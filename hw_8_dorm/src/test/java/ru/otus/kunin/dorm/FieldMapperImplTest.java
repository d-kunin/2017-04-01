package ru.otus.kunin.dorm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import org.junit.Test;
import ru.otus.kunin.dorm.main.User;

public class FieldMapperImplTest {

  final FieldMapperImpl fieldMapper = new FieldMapperImpl();

  @Test
  public void testMapsIdField() throws Exception {
    final Field idField = ReflectionUtils.getFieldByName("id", User.class);
    assertNotNull(idField);
    final FieldMapping idFieldMapping = fieldMapper.mapField(idField);
    assertEquals(idField, idFieldMapping.getField());
    assertTrue(idFieldMapping.isId());
    assertEquals(FieldMapperImpl.ID_FIELD_NAME, idFieldMapping.getSqlName());
    assertEquals(FieldMapperImpl.ID_FIELD_DEFINITION, idFieldMapping.getSqlDefinition());
  }

  @Test
  public void testMapsFieldWithCustomNameAndDefinition() throws Exception {
    final Field aNameField = ReflectionUtils.getFieldByName("aNameField", User.class);
    final FieldMapping aNameFieldMapping = fieldMapper.mapField(aNameField);
    assertFalse(aNameFieldMapping.isId());
    assertEquals("name", aNameFieldMapping.getSqlName());
    assertEquals("varchar(255)", aNameFieldMapping.getSqlDefinition());
  }

  @Test
  public void testMapsFieldWithCustomDefinition() throws Exception {
    final Field ageField = ReflectionUtils.getFieldByName("age", User.class);
    final FieldMapping ageFieldMapping = fieldMapper.mapField(ageField);
    assertFalse(ageFieldMapping.isId());
    assertEquals("age", ageFieldMapping.getSqlName());
    assertEquals("int(3) not null default 0", ageFieldMapping.getSqlDefinition());
  }

  @Test
  public void testMapsSimpleField() throws Exception {
    final Field displayNameField = ReflectionUtils.getFieldByName("displayName", User.class);
    final FieldMapping displayNameFieldMapping = fieldMapper.mapField(displayNameField);
    assertFalse(displayNameFieldMapping.isId());
    assertEquals("displayName", displayNameFieldMapping.getSqlName());
    assertEquals("text", displayNameFieldMapping.getSqlDefinition());
  }


}
