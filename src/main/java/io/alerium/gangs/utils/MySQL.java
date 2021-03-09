package io.alerium.gangs.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@RequiredArgsConstructor
public class MySQL {

    private final String hostname;
    private final String username;
    private final String password;
    private final String database;
    private final int port;

    private HikariDataSource dataSource;
    
    public void connect() throws SQLException {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + hostname + ":" + port + "/" + database);
        config.setUsername(username);
        config.setPassword(password);

        dataSource = new HikariDataSource(config);

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement1 = connection.prepareStatement("CREATE TABLE IF NOT EXISTS gangs (id INTEGER AUTO_INCREMENT PRIMARY KEY, name VARCHAR(16) NOT NULL UNIQUE, owner VARCHAR(36) NOT NULL, open BOOLEAN NOT NULL);");
                PreparedStatement statement2 = connection.prepareStatement("CREATE TABLE IF NOT EXISTS gangs_members (gang_id INTEGER PRIMARY KEY, uuid VARCHAR(36) NOT NULL, PRIMARY KEY (gang_id, uuid));")
        ) {
            statement1.execute();
            statement2.execute();
        }
    }
    
    public void disconnect() {
        dataSource.close();
    }
    
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
    
}
