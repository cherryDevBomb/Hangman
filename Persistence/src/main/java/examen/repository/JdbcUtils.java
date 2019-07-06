package examen.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class JdbcUtils {

    private static final Logger logger = LogManager.getLogger();

    private Properties jdbcProperties;
    private Connection connection = null;


    JdbcUtils(Properties properties){
        jdbcProperties = properties;
    }


    /**
     * method that opens a new database connection
     * @return new connection
     */
    private Connection getNewConnection(){
        logger.trace("Getting a new connection");

        String driver = jdbcProperties.getProperty("jdbc.driver");
        String url = jdbcProperties.getProperty("jdbc.url");

        logger.info("Trying to connect to database ... {}" , url);

        Connection con = null;

        try {
            Class.forName(driver);
            logger.info("Loaded driver ...{}", driver);
            con = DriverManager.getConnection(url);
        }
        catch (ClassNotFoundException e) {
            logger.error("Error loading driver {}", e);
        }
        catch (SQLException e) {
            logger.error("Error getting connection {}", e);
        }
        return con;
    }


    /**
     * method that gets the saved database connection
     * @return connection
     */
    Connection getConnection(){
        logger.traceEntry("Getting connection");
        try {
            if (connection == null || connection.isClosed())
                connection = getNewConnection();

        } catch (SQLException e) {
            logger.error("Database error {}", e);
        }
        logger.traceExit(connection);
        return connection;
    }
}
