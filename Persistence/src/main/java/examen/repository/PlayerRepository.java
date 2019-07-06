package examen.repository;

import model.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class PlayerRepository implements IPlayerRepository {

    private JdbcUtils jdbcUtils;

    public PlayerRepository(Properties properties) {
        jdbcUtils = new JdbcUtils(properties);
    }


    @Override
    public Player findPlayer(String username) {
        Connection con = jdbcUtils.getConnection();

        try (PreparedStatement statement = con.prepareStatement("SELECT * FROM Players WHERE username = ?")) {
            statement.setString(1, username);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    String user = result.getString("username");
                    String password = result.getString("password");
                    Player p = new Player(user, password);
                    return p;
                }
            }
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
        return null;
    }


    @Override
    public boolean findByUsernameAndPassword(String username, String password) {
        Connection con = jdbcUtils.getConnection();

        try (PreparedStatement statement = con.prepareStatement("SELECT * FROM Players WHERE username = ? AND password = ?")) {
            statement.setString(1, username);
            statement.setString(2, password);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return true;
                }
            }
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
        return false;
    }
}
