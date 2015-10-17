/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stamboom.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

import stamboom.domain.Administratie;
import stamboom.storage.DatabaseMediator;
import stamboom.storage.IStorageMediator;
import stamboom.storage.SerializationMediator;

public class StamboomController {

    private Administratie admin;
    private IStorageMediator storageMediator;

    /**
     * creatie van stamboomcontroller met lege administratie en onbekend
     * opslagmedium
     */
    public StamboomController() {
        admin = new Administratie();
        storageMediator = null;
    }

    public Administratie getAdministratie() {
        return admin;
    }

    /**
     * administratie wordt leeggemaakt (geen personen en geen gezinnen)
     */
    public void clearAdministratie() {
        admin = new Administratie();
    }

    /**
     * administratie wordt in geserialiseerd bestand opgeslagen
     *
     * @param bestand
     * @throws IOException
     */
    public void serialize(File bestand) throws IOException {
        SerializationMediator serializationMediator = new SerializationMediator();

        Properties props = new Properties();

        props.setProperty("file", String.valueOf(bestand.getAbsoluteFile()));

        if(serializationMediator.configure(props) == false)
            throw new IOException("Properties could not be loaded");

        serializationMediator.save(this.admin);

    }

    /**
     * administratie wordt vanuit geserialiseerd bestand gevuld
     *
     * @param bestand
     * @throws IOException
     */
    public void deserialize(File bestand) throws IOException {
        SerializationMediator serializationMediator = new SerializationMediator();

        Properties props = new Properties();

        props.setProperty("file", String.valueOf(bestand.getAbsoluteFile()));

        serializationMediator.configure(props);

        this.admin = serializationMediator.load();
    }


    /**
     * administratie wordt vanuit standaarddatabase opgehaald
     *
     * @throws IOException
     * @throws java.sql.SQLException
     * @throws java.lang.ClassNotFoundException
     */
    public void loadFromDatabase() throws IOException, SQLException, ClassNotFoundException {
        DatabaseMediator databaseMediator = new DatabaseMediator();
        Properties p = new Properties();

        FileInputStream fin = new FileInputStream("database.properties");

        p.load(fin);

        databaseMediator.configure(p);

        this.admin = databaseMediator.load();
    }

    /**
     * administratie wordt in standaarddatabase bewaard
     *
     * @throws IOException
     */
    public void saveToDatabase() throws IOException, SQLException, ClassNotFoundException {
        if(this.storageMediator == null)
        {
            DatabaseMediator databaseMediator = new DatabaseMediator();
            Properties p = new Properties();

            FileInputStream fin = new FileInputStream("database.properties");

            p.load(fin);

            databaseMediator.configure(p);

            databaseMediator.save(admin);
        }
    }

}
