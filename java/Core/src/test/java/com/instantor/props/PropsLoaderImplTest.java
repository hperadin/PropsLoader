package com.instantor.props;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(JUnit4.class)
public class PropsLoaderImplTest {
    @ClassRule
    public static final TemporaryFolder temp = new TemporaryFolder();

    private static final Logger logger = LoggerFactory.getLogger("com.instantor.props.test");

    private static final String projectName = "TestProjectName";
    private static final String globalName = "global";

    private static final String mainConfigContent =
            "nexus = global\n" +
            "dsl = .\n" +
            "missingReference = nothere\n";

    private static final String nexusConfigContent =
            "host = localhost\n" +
            "port = 9000";

    private static final String dslConfigContent = "TestDslConfig";

    private static String propsHome;
    private static File projectFolder;
    private static File globalFolder;
    private static File mainConfigFile;
    private static File nexusConfigFile;
    private static File dslConfigFile;
    private static PropsLoader pl;

    @BeforeClass
    public static void init() throws IOException {
        propsHome = temp.getRoot().getAbsolutePath();
        projectFolder = new File(propsHome, projectName);
        globalFolder = new File(propsHome, globalName);
        mainConfigFile = TestUtil.writeFile(new File(projectFolder, "_"), mainConfigContent);
        nexusConfigFile = TestUtil.writeFile(new File(globalFolder, "nexus.config"), nexusConfigContent);
        dslConfigFile = TestUtil.writeFile(new File(projectFolder, "dsl.props"), dslConfigContent);
        pl = new PropsLoaderImpl(logger, propsHome, mainConfigFile);
    }

    @Test(expected = IllegalArgumentException.class)
    public void resolveMissingKey() throws IOException {
        pl.resolve("missingKey").toString();
    }

    @Test(expected = IllegalArgumentException.class)
    public void resolveMissingReference() throws IOException {
        pl.resolve("missingReference").toString();
    }

    @Test
    public void resolveGlobal() throws IOException {
        final String expected = nexusConfigContent;
        final String actual = pl.resolve("nexus").toString();
        assertEquals(expected, actual);
    }

    @Test
    public void resolveLocal() throws IOException {
        final String expected = dslConfigContent;
        final String actual = pl.resolve("dsl").toString();
        assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getMissing() {
        pl.resolve("nexus").get("missing");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNull() {
        pl.resolve("nexus").get(null);
    }

    @Test
    public void get() {
        final String expected = "localhost";
        final String actual = pl.resolve("nexus").get("host");
        assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getIntMissing() {
        pl.resolve("nexus").getInt("missing");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getIntNull() {
        pl.resolve("nexus").getInt(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getIntInvalid() {
        pl.resolve("nexus").getInt("host");
    }

    @Test
    public void getInt() {
        final int expected = 9000;
        final int actual = pl.resolve("nexus").getInt("port");
        assertEquals(expected, actual);
    }

    @Test
    public void toProps() {
        final Properties expected = new Properties();
        expected.put("host", "localhost");
        expected.put("port", "9000");
        final Properties actual = pl.resolve("nexus").toProps();
        assertEquals(expected, actual);
    }

    @Test
    public void toMap() {
        final Map<String, String> expected = new HashMap<>();
        expected.put("host", "localhost");
        expected.put("port", "9000");
        final Map<String, String> actual = pl.resolve("nexus").toMap();
        assertEquals(expected, actual);
    }

    @Test
    public void toFile() {
        final File expected = new File(temp.getRoot(), projectName + File.separator + "_");
        final File actual = pl.toFile();
        assertEquals(expected, actual);
    }

    @Test
    public void toPath() {
        final String expected = temp.getRoot().toString() + File.separator + projectName + File.separator + "_";
        final String actual = pl.toPath();
        assertEquals(expected, actual);
    }

    @Test
    public void toByteArray() throws UnsupportedEncodingException {
        final byte[] expected = nexusConfigContent.getBytes("ISO-8859-1");
        final byte[] actual = pl.resolve("nexus").toByteArray();
        assertArrayEquals(expected, actual);
    }

    @Test
    public void toStringTest() {
        final String expected = nexusConfigContent;
        final String actual = pl.resolve("nexus").toString();
        assertEquals(expected, actual);
    }
}
