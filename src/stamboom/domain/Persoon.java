package stamboom.domain;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import stamboom.util.StringUtilities;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Persoon implements Serializable {

    // ********datavelden**************************************
    private final int nr;
    private final String[] voornamen;
    private final String achternaam;
    private final String tussenvoegsel;
    private final Calendar gebDat;
    private final String gebPlaats;
    private Gezin ouderlijkGezin;
    private final List<Gezin> alsOuderBetrokkenIn = new ArrayList<Gezin>();
    private transient ObservableList<Gezin> obsPersoonGezin;

    private final Geslacht geslacht;
    int afmeting = 1;

    // ********constructoren***********************************
    /**
     * er wordt een persoon gecreeerd met persoonsnummer persNr en met als
     * voornamen vnamen, achternaam anaam, tussenvoegsel tvoegsel, geboortedatum
     * gebdat, gebplaats, geslacht g en een gegeven ouderlijk gezin (mag null
     * (=onbekend) zijn); NB. de eerste letter van een voornaam, achternaam en
     * gebplaats wordt naar een hoofdletter omgezet, alle andere letters zijn
     * kleine letters; het tussenvoegsel is zo nodig in zijn geheel
     * geconverteerd naar kleine letters.
     *
     */
    Persoon(int persNr, String[] vnamen, String anaam, String tvoegsel,
            Calendar gebdat, String gebplaats, Geslacht g, Gezin ouderlijkgezin) {
        //todo opgave 1
        nr = persNr;
        voornamen = vnamen;
        achternaam = anaam;
        tussenvoegsel = tvoegsel;
        gebDat = gebdat;
        gebPlaats = gebplaats;
        geslacht = g;
        ouderlijkGezin = ouderlijkgezin;
        obsPersoonGezin = FXCollections.observableList(alsOuderBetrokkenIn);

    }

    // ********methoden****************************************
    /**
     * @return de achternaam van deze persoon
     */
    public ObservableList<Gezin> getPersoonGezin() {
        return (ObservableList<Gezin>) FXCollections.unmodifiableObservableList(obsPersoonGezin);
    }

    public String getAchternaam() {
        String lastname = achternaam.substring(0, 1).toUpperCase() + achternaam.substring(1).toLowerCase();
        return lastname;
    }

    /**
     * @return de geboortedatum van deze persoon
     */
    public Calendar getGebDat() {
        return gebDat;
    }

    /**
     *
     * @return de geboorteplaats van deze persoon
     */
    public String getGebPlaats() {
        String Upper = gebPlaats.substring(0, 1).toUpperCase() + gebPlaats.substring(1).toLowerCase();
        return Upper;
    }

    /**
     *
     * @return het geslacht van deze persoon
     */
    public Geslacht getGeslacht() {
        return geslacht;
    }

    /**
     *
     * @return de voorletters van de voornamen; elke voorletter wordt gevolgd
     * door een punt
     */
    public String getInitialen() {
        //todo opgave 1
        String initialen = "";
        for (String s : voornamen) {
            initialen += s.substring(0, 1) + ".";
            //System.out.println(initialen);
        }
        return initialen;
    }

    /**
     *
     * @return de initialen gevolgd door een eventueel tussenvoegsel en
     * afgesloten door de achternaam; initialen, voorzetsel en achternaam zijn
     * gescheiden door een spatie
     *
     *
     */
    public String getNaam() {
        //todo opgave 1

        String result;

        if (getTussenvoegsel() == "") {
            result = getInitialen() + " " + getAchternaam();
        } else {
            result = getInitialen() + " " + getTussenvoegsel() + " " + getAchternaam();
        }

        return result;
    }

    /**
     * @return het nummer van deze persoon
     */
    public int getNr() {
        return nr;
    }

    /**
     * @return het ouderlijk gezin van deze persoon, indien bekend, anders null
     */
    public Gezin getOuderlijkGezin() {
        return ouderlijkGezin;
    }

    /**
     * @return het tussenvoegsel van de naam van deze persoon (kan een lege
     * string zijn)
     */
    public String getTussenvoegsel() {

        return tussenvoegsel.toLowerCase();
    }

    /**
     * @return alle voornamen onderling gescheiden door een spatie
     */
    public String getVoornamen() {
        StringBuilder init = new StringBuilder();

        for (String s : voornamen) {
            String test = s.trim();
            init.append(test.substring(0, 1).toUpperCase() + test.substring(1).toLowerCase()).append(' ');
        }

        String result = init.toString().trim();

        return result;
    }

    /**
     * @return de standaardgegevens van deze mens: naam (geslacht) geboortedatum
     */
    public String standaardgegevens() {
        return getNaam() + " (" + getGeslacht() + ") " + StringUtilities.datumString(gebDat);
    }

    @Override
    public String toString() {
        return standaardgegevens();
    }

    /**
     * @return de gezinnen waar deze persoon bij betrokken is
     */
    public List<Gezin> getAlsOuderBetrokkenIn() {
        return (List<Gezin>) Collections.unmodifiableList(alsOuderBetrokkenIn);
    }

    /**
     * Als het ouderlijk gezin van deze persoon nog onbekend is dan wordt deze
     * persoon een kind van ouderlijkGezin en tevens wordt deze persoon als kind
     * in dat gezin geregistreerd Als de ouders bij aanroep al bekend zijn,
     * verandert er niets
     *
     * @param ouderlijkGezin
     * @return of ouderlijk gezin kon worden toegevoegd
     */
    /////
    boolean setOuders(Gezin ouderlijkGezin) {
        //todo opgave 1
        if (this.ouderlijkGezin == null) {
            this.ouderlijkGezin = ouderlijkGezin;
            ouderlijkGezin.breidUitMet(this);
            return true;
        }
        return false;
    }

    /**
     * @return voornamen, eventueel tussenvoegsel en achternaam, geslacht,
     * geboortedatum, namen van de ouders, mits bekend, en nummers van de
     * gezinnen waarbij deze persoon betrokken is (geweest)
     */
    public String beschrijving() {
        StringBuilder sb = new StringBuilder();

        sb.append(standaardgegevens());

        if (ouderlijkGezin != null) {
            sb.append("; 1e ouder: ").append(ouderlijkGezin.getOuder1().getNaam());
            if (ouderlijkGezin.getOuder2() != null) {
                sb.append("; 2e ouder: ").append(ouderlijkGezin.getOuder2().getNaam());
            }
        }
        if (!obsPersoonGezin.isEmpty()) {
            sb.append("; is ouder in gezin ");

            for (Gezin g : obsPersoonGezin) {
                sb.append(g.getNr()).append(" ");
            }
        }

        return sb.toString();
    }

    /**
     * als g nog niet bij deze persoon staat geregistreerd wordt g bij deze
     * persoon geregistreerd en anders verandert er niets
     *
     * @param g een nieuw gezin waarin deze persoon een ouder is
     *
     */
    void wordtOuderIn(Gezin g) {
        if (!alsOuderBetrokkenIn.contains(g)) {
            obsPersoonGezin.add(g);
        }
    }

    /**
     *
     *
     * @param andereOuder mag null zijn
     * @return het ongehuwde gezin met de andere ouder ; mits bestaand anders
     * null
     */
    public Gezin heeftOngehuwdGezinMet(Persoon andereOuder) {
        //todo opgave 1
        for (Gezin g : obsPersoonGezin) {
            if (g.isOngehuwd()) {
                if (g.getOuder1() == andereOuder || g.getOuder2() == andereOuder) {
                    return g;
                }
            }
        }
        return null;
    }

    /**
     *
     * @param datum
     * @return true als persoon op datum getrouwd is, anders false
     */
    public boolean isGetrouwdOp(Calendar datum) {
        for (Gezin gezin : obsPersoonGezin) {
            if (gezin.heeftGetrouwdeOudersOp(datum)) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param datum
     * @return true als de persoon kan trouwen op datum, hierbij wordt rekening
     * gehouden met huwelijken in het verleden en in de toekomst Alleen
     * meerderjarige (18+) personen kunnen trouwen.
     */
    public boolean kanTrouwenOp(Calendar datum) {
        Calendar meerderjarigDatum = ((GregorianCalendar) this.gebDat.clone());
        meerderjarigDatum.add(Calendar.YEAR, 18);
        if (datum.compareTo(meerderjarigDatum) < 1) {
            return false;
        }

        for (Gezin gezin : obsPersoonGezin) {
            if (gezin.heeftGetrouwdeOudersOp(datum)) {
                return false;
            } else {
                Calendar huwdatum = gezin.getHuwelijksdatum();
                if (huwdatum != null && huwdatum.after(datum)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     *
     * @param datum
     * @return true als persoon op datum gescheiden is, anders false
     */
    public boolean isGescheidenOp(Calendar datum) {
        //todo opgave 1
        Boolean isGescheiden = false;
        for (Gezin g : obsPersoonGezin) {
            Calendar c = g.getScheidingsdatum();
            if (c != null && c.before(datum)) {
                isGescheiden = true;
            }
        }
        return isGescheiden;
    }

    /**
     * ********* de rest wordt in opgave 2 verwerkt ****************
     */
    /**
     *
     * @return het aantal personen in de stamboom van deze persoon (ouders,
     * grootouders etc); de persoon zelf telt ook mee
     */
    public int afmetingStamboom() {
        //todo opgave2
        int afmeting = 1;
        if (this.ouderlijkGezin != null) {
            if (this.ouderlijkGezin.getOuder1() != null) {
                afmeting += this.ouderlijkGezin.getOuder1().afmetingStamboom();
            }
            if (this.ouderlijkGezin.getOuder2() != null) {
                afmeting += this.ouderlijkGezin.getOuder2().afmetingStamboom();
            }
        }
        return afmeting;
    }

    /**
     * de lijst met de items uit de stamboom van deze persoon wordt toegevoegd
     * aan lijst, dat wil zeggen dat begint met de toevoeging van de
     * standaardgegevens van deze persoon behorende bij generatie g gevolgd door
     * de items uit de lijst met de stamboom van de eerste ouder (mits bekend)
     * en gevolgd door de items uit de lijst met de stamboom van de tweede ouder
     * (mits bekend) (het generatienummer van de ouders is steeds 1 hoger)
     *
     * @param lijst
     * @param g >=0, het nummer van de generatie waaraan deze persoon is
     * toegewezen;
     */
    public void voegJouwStamboomToe(ArrayList<PersoonMetGeneratie> lijst, int g) {
        //todo opgave 2

        lijst.add(new PersoonMetGeneratie(this.standaardgegevens(), g));
        Persoon ouder1 = null;
        Persoon ouder2 = null;
        if (this.getOuderlijkGezin() != null) {
            ouder1 = this.getOuderlijkGezin().getOuder1();
            ouder2 = this.getOuderlijkGezin().getOuder2();
        }
        if (ouder1 != null) {
            ouder1.voegJouwStamboomToe(lijst, g + 1);
        }
        if (ouder2 != null) {
            ouder2.voegJouwStamboomToe(lijst, g + 1);
        }
    }

    /**
     *
     * @return de stamboomgegevens van deze persoon in de vorm van een String:
     * op de eerste regel de standaardgegevens van deze persoon, gevolgd door de
     * stamboomgegevens van de eerste ouder (mits bekend) en gevolgd door de
     * stamboomgegevens van de tweede ouder (mits bekend); formattering: iedere
     * persoon staat op een aparte regel en afhankelijk van het
     * generatieverschil worden er per persoon 2*generatieverschil spaties
     * ingesprongen;
     *
     * bijv voor M.G. Pieterse met ouders, grootouders en overgrootouders,
     * inspringen is in dit geval met underscore gemarkeerd: <br>
     *
     * M.G. Pieterse (VROUW) 5-5-2004<br>
     * __L. van Maestricht (MAN) 27-6-1953<br>
     * ____A.G. von Bisterfeld (VROUW) 13-4-1911<br>
     * ______I.T.M.A. Goldsmid (VROUW) 22-12-1876<br>
     * ______F.A.I. von Bisterfeld (MAN) 27-6-1874<br>
     * ____H.C. van Maestricht (MAN) 17-2-1909<br>
     * __J.A. Pieterse (MAN) 23-6-1964<br>
     * ____M.A.C. Hagel (VROUW) 12-0-1943<br>
     * ____J.A. Pieterse (MAN) 4-8-1923<br>
     */
    public String stamboomAlsString() {
        StringBuilder builder = new StringBuilder();
        String test = "";
        ArrayList<PersoonMetGeneratie> pmg = new ArrayList<>();
        voegJouwStamboomToe(pmg, 0);
        for (PersoonMetGeneratie p : pmg) {
            for (int i = 0; i < p.getGeneratie(); i++) {
                builder.append("  ");
            }
            //System.out.println(p.getGeneratie());
            //System.out.println(builder.toString() + p.getPersoonsgegevens().trim());
            builder.append(p.getPersoonsgegevens().trim());
            builder.append(System.getProperty("line.separator"));
        }
        //System.out.println(builder.toString());
        return builder.toString();
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        obsPersoonGezin = FXCollections.observableList(alsOuderBetrokkenIn);
        //obsPersoonGezin = (ObservableList<Gezin>) ois.readObject();

    }
}
