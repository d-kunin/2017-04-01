package ru.otus.kunin.dorm;

import org.junit.Test;
import ru.otus.kunin.dorm.main.User;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TypeMapperImplTest {

  TypeMapperImpl typeMapper = new TypeMapperImpl(new FieldMapperImpl());

  @Test
  public void mappingForClass() throws Exception {
    final TypeMapping typeMapping = typeMapper.mappingForClass(User.class);

    assertEquals(User.class, typeMapping.getType()
    );
    final FieldMapping idField = typeMapping.getIdField();
    assertTrue(idField.isId());

    final List<FieldMapping> nonIdFieldMappings = typeMapping.getNonIdFieldMappings();
    assertEquals(3, nonIdFieldMappings.size());
    nonIdFieldMappings.forEach(fieldMapping -> assertFalse(fieldMapping.isId()));

    assertEquals("dima_users", typeMapping.getSqlTable());
  }

}