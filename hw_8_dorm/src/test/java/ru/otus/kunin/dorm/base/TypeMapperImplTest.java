package ru.otus.kunin.dorm.base;

import org.junit.Test;
import ru.otus.kunin.dorm.main.entity.UserEntity;

import java.util.List;

import static org.junit.Assert.*;

public class TypeMapperImplTest {

  final TypeMapperImpl typeMapper = new TypeMapperImpl(new FieldMapperImpl());

  @Test
  public void testMappingForClass() throws Exception {
    final TypeMapping typeMapping = typeMapper.mappingForClass(UserEntity.class);

    assertEquals(UserEntity.class, typeMapping.getType()
    );
    final FieldMapping idField = typeMapping.getIdField();
    assertTrue(idField.isId());

    final List<FieldMapping> nonIdFieldMappings = typeMapping.getNonIdFieldMappings();
    assertEquals(3, nonIdFieldMappings.size());
    nonIdFieldMappings.forEach(fieldMapping -> assertFalse(fieldMapping.isId()));

    assertEquals("dima_users", typeMapping.getSqlTable());
  }

}