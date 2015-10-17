/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stamboom.storage;

import stamboom.domain.Administratie;
import stamboom.domain.Geslacht;
import stamboom.domain.Gezin;
import stamboom.domain.Persoon;

import java.io.IOException;
import java.sql.*;
import java.util.Calendar;
import java.util.Properties;

public class DatabaseMediator implements IStorageMediator {

    private Properties props;
    private Connection conn;

    @Override
    public Administratie load() throws IOException, SQLException {
        Administratie administratie = new Administratie();

        conn = getConnection();

        Statement statement = conn.createStatement();

        ResultSet resultSet = statement.executeQuery("select * from PERSONEN");

        while (resultSet.next()) {
            Geslacht g;
            int id = resultSet.getInt("id");
            String voornamen[] = resultSet.getString("voornamen").split(" ");
            String achternaam = resultSet.getString("achternaam");
            String tussenvoegsel = resultSet.getString("tussenvoegsel");
            String geboorteplaats = resultSet.getString("geboorteplaats");
            String geslacht = resultSet.getString("geslacht");
            if (geslacht.equalsIgnoreCase("MAN"))
                g = Geslacht.MAN;
            else
                g = Geslacht.VROUW;
            int ouder = resultSet.getInt("ouders");
            Calendar geboorteDatum = Calendar.getInstance();

            geboorteDatum.setTimeInMillis(resultSet.getDate("geboortedatum").getTime());


            administratie.addPersoon(g, voornamen, achternaam, tussenvoegsel, geboorteDatum, geboorteplaats, null);
        }

        resultSet = statement.executeQuery("select * from GEZINNEN");

        while (resultSet.next()) {
            int id, ouder1, ouder2;
            Calendar huwelijksDatum = Calendar.getInstance();
            Calendar scheidingsDatum = Calendar.getInstance();

            id = resultSet.getInt("id");
            ouder1 = resultSet.getInt("ouder1");
            ouder2 = resultSet.getInt("ouder2");

            Date huwelijksdate = resultSet.getDate("huwelijksdatum");
            Date scheidingsDate = resultSet.getDate("scheidingsDatum");

            if (huwelijksdate != null)
                huwelijksDatum.setTime(huwelijksdate);
            else
                huwelijksDatum = null;

            if (scheidingsDate != null)
                scheidingsDatum.setTime(scheidingsDate);
            else
                scheidingsDatum = null;

            Persoon pouder1 = administratie.getPersoon(ouder1);
            Persoon pouder2 = administratie.getPersoon(ouder2);


            Gezin g = administratie.addOngehuwdGezin(pouder1, pouder2);


            if (huwelijksDatum != null)
                administratie.setHuwelijk(g, huwelijksDatum);

            if (scheidingsDatum != null)
                administratie.setScheiding(g, scheidingsDatum);
        }

        // Load children

        resultSet = statement.executeQuery("select * from PERSONEN");

        while (resultSet.next()) {
            int persoonNummer = resultSet.getInt("id");
            int ouders = resultSet.getInt("ouders");
            if(ouders != 0)
                administratie.setOuders(administratie.getPersoon(persoonNummer), administratie.getGezin(ouders));
        }

        return administratie;
    }

    @Override
    public void save(Administratie admin) throws IOException, SQLException {
        Statement statement;

        conn = getConnection();

        statement = conn.createStatement();

        // Clear database
        statement.execute("TRUNCATE PERSONEN;");
        statement.execute("TRUNCATE GEZINNEN");

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

        for (Gezin g : admin.getGezinnen()) {
            PreparedStatement preparedStatement = conn.prepareStatement(
                    "INSERT INTO GEZINNEN(" +
                            "id," +
                            "ouder1," +
                            "ouder2," +
                            "huwelijksdatum," +
                            "scheidingsDatum)" +
                            "VALUES(?, ?, ?, ?, ?)");

            preparedStatement.setInt(1, g.getNr());
            preparedStatement.setInt(2, g.getOuder1().getNr());

            if (g.getOuder2() == null)
                preparedStatement.setString(3, null);
            else
                preparedStatement.setInt(3, g.getOuder2().getNr());

            if (g.getHuwelijksdatum() == null)
                preparedStatement.setString(4, null);
            else
                preparedStatement.setDate(4, new Date(g.getHuwelijksdatum().getTimeInMillis()));

            if (g.getScheidingsdatum() == null)
                preparedStatement.setString(5, null);
            else
                preparedStatement.setDate(5, new Date(g.getScheidingsdatum().getTimeInMillis()));

            preparedStatement.execute();
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(this.props.getProperty("url"), this.props.getProperty("username"), this.props.getProperty("password"));
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

        try (Connection connection = getConnection()) {
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
