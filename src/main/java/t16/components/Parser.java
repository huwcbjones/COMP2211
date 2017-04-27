package t16.components;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import t16.exceptions.ParseException;
import t16.model.ClickLog;
import t16.model.ImpressionLog;
import t16.model.Query;
import t16.model.ServerLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static t16.model.Query.*;

/**
 * Parses CSV Files into the relevant Log format
 *
 * @author Huw Jones
 * @since 06/03/2017
 */
public class Parser {
    protected static final Logger log = LogManager.getLogger(Parser.class);
    protected static final HashMap<String, Query.GENDER> genderMap = new HashMap<>(2);
    protected static final HashMap<String, Query.INCOME> incomeMap = new HashMap<>(3);
    protected static final HashMap<String, Query.CONTEXT> contextMap = new HashMap<>(6);
    protected static final List<String> ages = Arrays.asList("<25", "25-34", "35-44", "45-54", ">54");

    static {
        for (Query.GENDER t : Query.GENDER.values()) {
            genderMap.put(t.toString().toLowerCase(), t);
        }

        for (Query.INCOME t : Query.INCOME.values()) {
            incomeMap.put(t.toString().toLowerCase(), t);
        }

        for (Query.CONTEXT t : Query.CONTEXT.values()) {
            contextMap.put(t.toString().toLowerCase().replace("_", " "), t);
        }
    }

    protected File file;
    protected Type type = null;
    protected List<ClickLog> clickLog = null;
    protected List<ImpressionLog> impressionLog = null;
    protected List<ServerLog> serverLog = null;
    protected BufferedReader br;

    public Parser(File csvFile) {
        log.debug("File set to {}", csvFile);
        file = csvFile;
    }

    /**
     * Parses the Log File
     *
     * @throws ParseException Thrown if a parse error occurred
     * @throws IOException    Thrown if something failed whilst reading the file
     */
    public void parse() throws ParseException, IOException {
        if (type == null) parseHeader();
        log.debug("Parsing {} log...", type.toString());
        switch (type) {
            case CLICK:
                parseClick();
                break;
            case IMPRESSION:
                parseImpression();
                break;
            case SERVER:
                parseServer();
                break;
        }
    }

    /**
     * Parses the file header and returns the file type
     *
     * @return Type of file parsed
     * @throws ParseException Thrown if no header <b>valid</b> header was found
     * @throws IOException    Thrown if something failed whilst reading the file
     */
    public Type parseHeader() throws ParseException, IOException {
        br = new BufferedReader(new FileReader(file));
        String header = br.readLine();
        String[] elements = header.split(",");
        switch (elements.length) {
            case 7:
                if (!elements[0].toLowerCase().trim().equals("date")) {
                    throw new ParseException("Date header not found.", 0, header.indexOf(elements[0]));
                }
                if (!elements[1].toLowerCase().trim().equals("id")) {
                    throw new ParseException("ID header not found.", 0, header.indexOf(elements[1]));
                }
                if (!elements[2].toLowerCase().trim().equals("gender")) {
                    throw new ParseException("Gender header not found.", 0, header.indexOf(elements[2]));
                }
                if (!elements[3].toLowerCase().trim().equals("age")) {
                    throw new ParseException("Age header not found.", 0, header.indexOf(elements[3]));
                }
                if (!elements[4].toLowerCase().trim().equals("income")) {
                    throw new ParseException("Income header not found.", 0, header.indexOf(elements[4]));
                }
                if (!elements[5].toLowerCase().trim().equals("context")) {
                    throw new ParseException("Context header not found.", 0, header.indexOf(elements[5]));
                }
                if (!elements[6].toLowerCase().trim().equals("impression cost")) {
                    throw new ParseException("Impression Cost header not found.", 0, header.indexOf(elements[6]));
                }
                type = Type.IMPRESSION;
                break;
            case 3:
                if (!elements[0].toLowerCase().trim().equals("date")) {
                    throw new ParseException("Date header not found.", 0, header.indexOf(elements[0]));
                }
                if (!elements[1].toLowerCase().trim().equals("id")) {
                    throw new ParseException("ID header not found.", 0, header.indexOf(elements[1]));
                }
                if (!elements[2].toLowerCase().trim().equals("click cost")) {
                    throw new ParseException("Click Cost header not found.", 0, header.indexOf(elements[2]));
                }
                type = Type.CLICK;
                break;
            case 5:
                if (!elements[0].toLowerCase().trim().equals("entry date")) {
                    throw new ParseException("Entry Date header not found.", 0, header.indexOf(elements[0]));
                }
                if (!elements[1].toLowerCase().trim().equals("id")) {
                    throw new ParseException("ID header not found.", 0, header.indexOf(elements[1]));
                }
                if (!elements[2].toLowerCase().trim().equals("exit date")) {
                    throw new ParseException("Exit Date header not found.", 0, header.indexOf(elements[2]));
                }
                if (!elements[3].toLowerCase().trim().equals("pages viewed")) {
                    throw new ParseException("Pages Views header not found.", 0, header.indexOf(elements[3]));
                }
                if (!elements[4].toLowerCase().trim().equals("conversion")) {
                    throw new ParseException("Conversion header not found.", 0, header.indexOf(elements[4]));
                }
                type = Type.SERVER;
                break;
        }
        return type;
    }

    protected void parseClick() throws ParseException, IOException {
        long startTime = System.currentTimeMillis();
        log.info("Started parsing Clicks...");
        clickLog = new ArrayList<>();
        String line;
        int lineNumber = 1;

        String[] elements;

        // Properties
        Timestamp t;
        long ID;
        BigDecimal cost;

        while ((line = br.readLine()) != null) {
            lineNumber++;

            elements = line.split(",");
            for (int i = 0; i < elements.length; i++) {
                elements[i] = elements[i].trim().toLowerCase();
            }

            if (elements.length != 3) throw new ParseException("Invalid entry - 3 elements expected, got " + elements.length, lineNumber, 0);

            try {
                t = Timestamp.valueOf(elements[0]);
            } catch (IllegalArgumentException e) {
                log.catching(e);
                throw new ParseException("Invalid date format. Got '" + elements[0] + "'.", lineNumber, line.indexOf(elements[0]));
            }

            try {
                ID = Long.valueOf(elements[1]);
            } catch (NumberFormatException e) {
                log.catching(e);
                throw new ParseException("Invalid ID format. Got '" + elements[1] + "'.", lineNumber, line.indexOf(elements[1]));
            }

            try {
                cost = new BigDecimal(elements[2]);
            } catch (NumberFormatException e) {
                log.catching(e);
                throw new ParseException("Invalid Click Cost format. Got '" + elements[2] + "'.", lineNumber, line.indexOf(elements[2]));
            }

            clickLog.add(new ClickLog(t, ID, cost));
            if(clickLog.size() % 1000 == 0) log.debug("Click Parser: {} entries parsed.", clickLog.size());
        }

        br.close();

        double time = (System.currentTimeMillis() - startTime) / 1000d;
        log.info("Parsed {} clicks in {} ({} per click).", clickLog.size(), DecimalFormat.getInstance().format(time), time / clickLog.size());
    }

    protected void parseImpression() throws ParseException, IOException {
        long startTime = System.currentTimeMillis();
        log.info("Started parsing Impressions...");
        impressionLog = new ArrayList<>();
        String line;
        int lineNumber = 1;

        String[] elements;

        // Properties
        Timestamp t;
        long ID;
        GENDER gender;
        String age;
        CONTEXT context;
        INCOME income;
        BigDecimal cost;

        while ((line = br.readLine()) != null) {
            lineNumber++;

            elements = line.split(",");
            for (int i = 0; i < elements.length; i++) {
                elements[i] = elements[i].trim().toLowerCase();
            }

            if (elements.length != 7) throw new ParseException("Invalid entry - 7 elements expected, got " + elements.length, lineNumber, 0);

            try {
                t = Timestamp.valueOf(elements[0]);
            } catch (IllegalArgumentException e) {
                log.catching(e);
                throw new ParseException("Invalid date format. Got '" + elements[0] + "'.", lineNumber, line.indexOf(elements[0]));
            }

            try {
                ID = Long.valueOf(elements[1]);
            } catch (NumberFormatException e) {
                log.catching(e);
                throw new ParseException("Invalid ID format. Got '" + elements[1] + "'.", lineNumber, line.indexOf(elements[1]));
            }

            gender = genderMap.get(elements[2]);
            if (gender == null) {
                throw new ParseException("Invalid Gender format. Got '" + elements[2] + "', expected 'male' or 'female'.", lineNumber, line.indexOf(elements[2]));
            }

            age = elements[3];
            if (!ages.contains(age)) {
                throw new ParseException("Invalid Age format. Got '" + age + "'.", lineNumber, line.indexOf(elements[3]));
            }

            income = incomeMap.get(elements[4]);
            if (income == null) {
                throw new ParseException("Invalid Income format. Got '" + elements[4] + "'.", lineNumber, line.indexOf(elements[4]));
            }

            context = contextMap.get(elements[5]);
            if (context == null) {
                throw new ParseException("Invalid Context format. Got '" + elements[5] + "'.", lineNumber, line.indexOf(elements[5]));
            }


            try {
                cost = new BigDecimal(elements[6]);
            } catch (NumberFormatException e) {
                log.catching(e);
                throw new ParseException("Invalid Impression Cost format. Got '" + elements[6] + ".", lineNumber, line.indexOf(elements[6]));
            }

            impressionLog.add(new ImpressionLog(t, ID, gender, age, income, context, cost));
            if(impressionLog.size() % 10000 == 0) log.debug("Impression Parser: {} entries parsed.", impressionLog.size());
        }

        br.close();

        double time = (System.currentTimeMillis() - startTime) / 1000d;
        log.info("Parsed {} impressions in {} ({} per impression).", impressionLog.size(), DecimalFormat.getInstance().format(time), time / impressionLog.size());
    }

    protected void parseServer() throws ParseException, IOException {
        long startTime = System.currentTimeMillis();
        log.info("Started parsing Server...");
        serverLog = new ArrayList<>();
        String line;
        int lineNumber = 1;

        String[] elements;

        // Properties
        Timestamp entryDate;
        long ID;
        Timestamp exitDate;
        long pageViews;
        boolean conversion;

        while ((line = br.readLine()) != null) {
            lineNumber++;

            elements = line.split(",");
            for (int i = 0; i < elements.length; i++) {
                elements[i] = elements[i].trim().toLowerCase();
            }

            if (elements.length != 5) throw new ParseException("Invalid entry - 5 elements expected, got " + elements.length, lineNumber, 0);

            try {
                entryDate = Timestamp.valueOf(elements[0]);
            } catch (IllegalArgumentException e) {
                log.catching(e);
                throw new ParseException("Invalid Entry Date format. Got '" + elements[0] + "'.", lineNumber, line.indexOf(elements[0]));
            }

            try {
                ID = Long.valueOf(elements[1]);
            } catch (NumberFormatException e) {
                log.catching(e);
                throw new ParseException("Invalid ID format. Got '" + elements[1] + "'.", lineNumber, line.indexOf(elements[1]));
            }

            try {
                exitDate = (elements[2].equals("n/a") ? null : Timestamp.valueOf(elements[2]));
            } catch (IllegalArgumentException e) {
                log.catching(e);
                throw new ParseException("Invalid Entry Date format. Got '" + elements[2] + "'.", lineNumber, line.indexOf(elements[2]));
            }

            try {
                pageViews = Long.valueOf(elements[3]);
            } catch (NumberFormatException e) {
                log.catching(e);
                throw new ParseException("Invalid Pages Viewed format. Got '" + elements[3] + "'.", lineNumber, line.indexOf(elements[3]));
            }

            if (!(elements[4].equals("yes") || elements[4].equals("no"))) {
                throw new ParseException("Invalid Conversion format. Got '" + elements[4] + "', expected 'Yes', or 'No'.", lineNumber, line.indexOf(elements[4]));
            }
            conversion = elements[4].equals("yes");

            serverLog.add(new ServerLog(entryDate, ID, exitDate, pageViews, conversion));
            if(serverLog.size() % 1000 == 0) log.debug("Server Parser: {} entries parsed.", serverLog.size());
        }

        br.close();

        double time = (System.currentTimeMillis() - startTime) / 1000d;
        log.info("Parsed {} server logs in {} ({} per log).", serverLog.size(), DecimalFormat.getInstance().format(time), time / serverLog.size());
    }

    /**
     * Returns the click log. Will be null if the type is not CLICk
     *
     * @return Click Log
     */
    public List<ClickLog> getClickLog() {
        return clickLog;
    }

    /**
     * Returns the Impression log. Will be null if the type is not IMPRESSION
     *
     * @return Impression Log
     */
    public List<ImpressionLog> getImpressionLog() {
        return impressionLog;
    }

    /**
     * Returns the server log. Will be null if the type is not SERVER
     *
     * @return Server Log
     */
    public List<ServerLog> getServerLog() {
        return serverLog;
    }

    /**
     * Gets the type of log
     *
     * @return
     */
    public Type getType() {
        return type;
    }

    /**
     * Valid Log Types
     */
    public enum Type {
        CLICK,
        IMPRESSION,
        SERVER
    }
}
