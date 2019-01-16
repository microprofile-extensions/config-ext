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

import lombok.extern.java.Log;

@Log
public class Repository {
    PreparedStatement selectOne = null;
    PreparedStatement selectAll = null;
    
    public Repository(Configuration config) {
        DataSource datasource = getDatasource(config.getDatasourceJndi());
        if (datasource != null) {
            String queryOne = "select " + config.getValueColumn() + " from " + config.getTable() + " where " + config.getKeyColumn() + " = ?";
            String queryAll = "select " + config.getKeyColumn() + ", " + config.getValueColumn() + " from " + config.getTable();
            try {
                selectOne = datasource.getConnection().prepareStatement(queryOne);
                selectAll = datasource.getConnection().prepareStatement(queryAll);
            } catch (SQLException e) {
                log.log(Level.FINE, () -> "Configuration query could not be prepared: " + e.getMessage());
            }
        }
    }
    
    public Map<String, String> getAllConfigValues() {
        Map<String, String> result = new HashMap<>();
        if (selectAll != null) {
            try {
                ResultSet rs = selectAll.executeQuery();
                while (rs.next()) {
                    result.put(rs.getString(1), rs.getString(2));
                }
            } catch (SQLException e) {
                log.log(Level.FINE, () -> "query for config values failed:}" + e.getMessage());
            }
        }
        return result;
    }
    
    public String getConfigValue(String key) {
        if (selectOne != null) {
            try {
                selectOne.setString(1, key);
                ResultSet rs = selectOne.executeQuery();
                if (rs.next()) {
                    return rs.getString(1);
                }
            } catch (SQLException e) {
                log.log(Level.FINE, () -> "query for config value failed: " + e.getMessage());
            }
        }
        return null;
    }
    
    private DataSource getDatasource(String jndi) {
        try {
            return (DataSource) InitialContext.doLookup(jndi);
        } catch (NamingException e) {
            log.log(Level.WARNING, () -> "Could not get datasource: " + e.getMessage());
            return null;
        }
    }

}
