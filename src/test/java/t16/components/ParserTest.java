package t16.components;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * {DESCRIPTION}
 *
 * @author Huw Jones
 * @since 06/03/2017
 */
public class ParserTest {
    @Test
    public void parseHeader_Click() throws Exception {
        File f = new File(getClass().getClassLoader().getResource("test_click.csv").getFile());
        Parser p = new Parser(f);
        p.parseHeader();
        assertEquals(p.getType(), Parser.Type.CLICK);
    }

    @Test
    public void parseHeader_Server() throws Exception {
        File f = new File(getClass().getClassLoader().getResource("test_server.csv").getFile());
        Parser p = new Parser(f);
        p.parseHeader();
        assertEquals(p.getType(), Parser.Type.SERVER);
    }

    @Test
    public void parseHeader_Impression() throws Exception {
        File f = new File(getClass().getClassLoader().getResource("test_impression.csv").getFile());
        Parser p = new Parser(f);
        p.parseHeader();
        assertEquals(p.getType(), Parser.Type.IMPRESSION);
    }
    @Test
    public void parse_Click() throws Exception {
        File f = new File(getClass().getClassLoader().getResource("test_click.csv").getFile());
        Parser p = new Parser(f);
        p.parseHeader();
        assertEquals(p.getType(), Parser.Type.CLICK);
        p.parse();
    }

    @Test
    public void parse_Server() throws Exception {
        File f = new File(getClass().getClassLoader().getResource("test_server.csv").getFile());
        Parser p = new Parser(f);
        p.parseHeader();
        assertEquals(p.getType(), Parser.Type.SERVER);
        p.parse();
    }

    @Test
    public void parse_Impression() throws Exception {
        File f = new File(getClass().getClassLoader().getResource("test_impression.csv").getFile());
        Parser p = new Parser(f);
        p.parseHeader();
        assertEquals(p.getType(), Parser.Type.IMPRESSION);
        p.parse();
    }
}