package org.microprofileext.config.source.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.eclipse.microprofile.config.Config;

import lombok.extern.java.Log;

@Log
public class Repository {
    
    private static final String KEY_PREFIX = "configsource.db.";
    private static final String KEY_DATASOURCE = KEY_PREFIX + "datasource";
    private static final String KEY_TABLE = KEY_PREFIX + "table";
    private static final String KEY_KEY_COLUMN = KEY_PREFIX + "key-column";
    private static final String KEY_VALUE_COLUMN = KEY_PREFIX + "value-column";
    
    PreparedStatement selectOne = null;
    PreparedStatement selectAll = null;
    
    public Repository(Config config) {
        DataSource datasource = getDatasource(config.getOptionalValue(KEY_DATASOURCE, String.class).orElse("java:comp/DefaultDataSource"));
        String table = config.getOptionalValue(KEY_TABLE, String.class).orElse("configuration");
        String keyColumn = config.getOptionalValue(KEY_KEY_COLUMN, String.class).orElse("key");
        String valueColumn = config.getOptionalValue(KEY_VALUE_COLUMN, String.class).orElse("value");
        if (datasource != null) {
            String queryOne = "select " + valueColumn + " from " +table + " where " + keyColumn + " = ?";
            String queryAll = "select " + keyColumn + ", " + valueColumn + " from " + table;
            try {
                selectOne = datasource.getConnection().prepareStatement(queryOne);
                selectAll = datasource.getConnection().prepareStatement(queryAll);
            } catch (SQLException e) {
                log.log(Level.FINE, () -> "Configuration query could not be prepared: " + e.getMessage());
            }
        }
    }
    
    public synchronized Map<String, String> getAllConfigValues() throws SQLException {
        Map<String, String> result = new HashMap<>();
        if (selectAll != null) {
            try (ResultSet rs = selectAll.executeQuery()) {
                while (rs.next()) {
                    result.put(rs.getString(1), rs.getString(2));
                }
            }
        }
        return result;
    }
    
    public synchronized String getConfigValue(String key) throws SQLException {
        if (selectOne != null) {
            selectOne.setString(1, key);
            try (ResultSet rs = selectOne.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        } else {
            throw new SQLException("datasource not initialized");
        }
        return null;
    }

    private DataSource getDatasource(String jndi) {
        try {
            return InitialContext.doLookup(jndi);
        } catch (NamingException e) {
            log.log(Level.WARNING, () -> "Could not get datasource: " + e.getMessage());
            return null;
        }
    }

}
