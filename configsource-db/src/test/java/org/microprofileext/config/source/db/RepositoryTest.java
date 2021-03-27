package org.microprofileext.config.source.db;

import org.eclipse.microprofile.config.Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RepositoryTest {

    Repository repository;

    @BeforeEach
    public void init() {
        Config config = mock(Config.class);
        when(config.getOptionalValue(Mockito.anyString(), Mockito.any())).thenReturn(Optional.empty());
        repository = new Repository(config);
    }

    @Test
    void testGetConfigValue_exception() throws SQLException {
        repository.selectOne = mock(PreparedStatement.class);
        when(repository.selectOne.executeQuery()).thenThrow(SQLException.class);
        assertThrows(SQLException.class, () -> repository.getConfigValue("test"));
    }

    @Test
    void testGetConfigValue_none() throws SQLException {
        repository.selectOne = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(false);
        when(repository.selectOne.executeQuery()).thenReturn(rs);
        assertNull(repository.getConfigValue("test"));
    }

    @Test
    void testGetConfigValue_no_stmt() throws SQLException {
        assertThrows(SQLException.class, () -> repository.getConfigValue("test"));
    }

    @Test
    void testGetConfigValue() throws SQLException {
        repository.selectOne = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true);
        when(rs.getString(1)).thenReturn("value");
        when(repository.selectOne.executeQuery()).thenReturn(rs);
        assertEquals("value", repository.getConfigValue("test"));
    }

    @Test
    void testGetAllConfigValues_no_stmt() throws SQLException {
        assertEquals(0, repository.getAllConfigValues().size());
    }

    @Test
    void testGetAllConfigValues_exception() throws SQLException {
        repository.selectAll = mock(PreparedStatement.class);
        when(repository.selectAll.executeQuery()).thenThrow(SQLException.class);
        assertThrows(SQLException.class, () -> repository.getAllConfigValues().size());
    }

    @Test
    void testGetAllConfigValues() throws SQLException {
        repository.selectAll = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true).thenReturn(false);
        when(rs.getString(1)).thenReturn("test");
        when(rs.getString(2)).thenReturn("value");
        when(repository.selectAll.executeQuery()).thenReturn(rs);
        assertEquals(1, repository.getAllConfigValues().size());
    }

}
