package org.microprofileext.config.source.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import org.microprofileext.config.source.db.Configuration;
import org.microprofileext.config.source.db.Repository;

class RepositoryTest {

    @Test
    void testGetConfigValue_exception() throws SQLException {
        Repository repository = new Repository(new Configuration());
        repository.selectOne = mock(PreparedStatement.class);
        when(repository.selectOne.executeQuery()).thenThrow(SQLException.class);
        assertNull(repository.getConfigValue("test"));
    }

    @Test
    void testGetConfigValue_none() throws SQLException {
        Repository repository = new Repository(new Configuration());
        repository.selectOne = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(false);
        when(repository.selectOne.executeQuery()).thenReturn(rs);
        assertNull(repository.getConfigValue("test"));
    }

    @Test
    void testGetConfigValue_no_stmt() throws SQLException {
        Repository repository = new Repository(new Configuration());
        assertNull(repository.getConfigValue("test"));
    }

    @Test
    void testGetConfigValue() throws SQLException {
        Repository repository = new Repository(new Configuration());
        repository.selectOne = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true);
        when(rs.getString(1)).thenReturn("value");
        when(repository.selectOne.executeQuery()).thenReturn(rs);
        assertEquals("value", repository.getConfigValue("test"));
    }

    @Test
    void testGetAllConfigValues_no_stmt() throws SQLException {
        Repository repository = new Repository(new Configuration());
        assertEquals(0, repository.getAllConfigValues().size());
    }

    @Test
    void testGetAllConfigValues_exception() throws SQLException {
        Repository repository = new Repository(new Configuration());
        repository.selectAll = mock(PreparedStatement.class);
        when(repository.selectAll.executeQuery()).thenThrow(SQLException.class);
        assertEquals(0, repository.getAllConfigValues().size());
    }

    @Test
    void testGetAllConfigValues() throws SQLException {
        Repository repository = new Repository(new Configuration());
        repository.selectAll = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true).thenReturn(false);
        when(rs.getString(1)).thenReturn("test");
        when(rs.getString(2)).thenReturn("value");
        when(repository.selectAll.executeQuery()).thenReturn(rs);
        assertEquals(1, repository.getAllConfigValues().size());
    }

}
