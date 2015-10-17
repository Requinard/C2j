/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stamboom.storage;

import stamboom.domain.Administratie;
import stamboom.domain.Persoon;

import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class DatabaseMediator implements IStorageMediator {

    private Properties props;
    private Connection conn;

    @Override
    public Administratie load() throws IOException {
        return null;
    }

    @Override
    public void save(Administratie admin) throws IOException, SQLException {
        Statement statement;

        conn = DriverManager.getConnection(this.props.getProperty("url"), this.props.getProperty("username"), this.props.getProperty("password"));

        statement = conn.createStatement();

        // Clear database
        statement.execute("TRUNCATE PERSONEN;");

        for(Persoon persoon: admin.getPersonen())
        {
            PreparedStatement preparedStatement = conn.prepareStatement(
                    "INSERT INTO PERSONEN(" +
                            "id," +
                            "achternaam," +
                            "voornamen," +
                            "tussenvoegsel," +
                            "geboortedatum," +
                            "geboorteplaats," +
                            "geslacht," +
                            "ouders)" +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)");

            preparedStatement.setInt(1, persoon.getNr());
            preparedStatement.setString(2, persoon.getAchternaam());
            preparedStatement.setString(3, persoon.getVoornamen());
            preparedStatement.setString(4, persoon.getTussenvoegsel());
            preparedStatement.setDate(5, new Date(persoon.getGebDat().getTimeInMillis()));
            preparedStatement.setString(6, persoon.getGebPlaats());
            preparedStatement.setString(7, persoon.getGeslacht().toString());

            if(persoon.getOuderlijkGezin() != null)
                preparedStatement.setInt(8, persoon.getOuderlijkGezin().getNr());
            else
                preparedStatement.setString(8, null);

            preparedStatement.execute();
        }
    }

    /**
     * Laadt de instellingen, in de vorm van een Properties bestand, en controleert
     * of deze in de correcte vorm is, en er verbinding gemaakt kan worden met
     * de database.
     * @param props
     * @return
     */
    @Override
    public final boolean configure(Properties props) {
        this.props = props;
        if (!isCorrectlyConfigured()) {
            System.err.println("props mist een of meer keys");
            return false;
        }

        try {
            initConnection();
            return true;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            this.props = null;
            return false;
        } finally {
            //closeConnection();
        }
    }

    @Override
    public Properties config() {
        return props;
    }

    @Override
    public boolean isCorrectlyConfigured() {
        if (props == null) {
            return false;
        }
        if (!props.containsKey("driver")) {
            return false;
        }
        if (!props.containsKey("url")) {
            return false;
        }
        if (!props.containsKey("username")) {
            return false;
        }
        if (!props.containsKey("password")) {
            return false;
        }
        return true;
    }

    private void initConnection() throws SQLException {
        String url = props.getProperty("url");
        String username = props.getProperty("username");
        String password = props.getProperty("password");

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Database connected!");
            this.conn = connection;
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database!", e);
        }

    }

    private void closeConnection() {
        try {
            conn.close();
            conn = null;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
