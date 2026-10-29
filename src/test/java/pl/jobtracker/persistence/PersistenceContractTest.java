package pl.jobtracker.persistence;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PersistenceContractTest {

    private static Document document;

    @BeforeAll
    static void loadPersistenceXml() throws Exception {
        InputStream input = PersistenceContractTest.class
                .getClassLoader()
                .getResourceAsStream("META-INF/persistence.xml");

        assertNotNull(
                input,
                "META-INF/persistence.xml should be available on the classpath"
        );

        document = DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder()
                .parse(input);

        document.getDocumentElement().normalize();
    }

    @Test
    void persistenceUnitShouldHaveExpectedName() {
        var persistenceUnit = document
                .getElementsByTagName("persistence-unit")
                .item(0);

        assertNotNull(persistenceUnit);

        assertEquals(
                "JobTrackerPU",
                persistenceUnit
                        .getAttributes()
                        .getNamedItem("name")
                        .getNodeValue()
        );
    }

    @Test
    void persistenceUnitShouldUseJtaTransactions() {
        var persistenceUnit = document
                .getElementsByTagName("persistence-unit")
                .item(0);

        assertEquals(
                "JTA",
                persistenceUnit
                        .getAttributes()
                        .getNamedItem("transaction-type")
                        .getNodeValue()
        );
    }

    @Test
    void persistenceUnitShouldUseJobTrackerDatasource() {
        var datasource = document
                .getElementsByTagName("jta-data-source")
                .item(0);

        assertNotNull(datasource);

        assertEquals(
                "java:/jdbc/JobTrackerDS",
                datasource.getTextContent().trim()
        );
    }

    @Test
    void hibernateShouldNotGenerateDatabaseSchema() {
        var properties = document
                .getElementsByTagName("property");

        String schemaAction = null;

        for (int i = 0; i < properties.getLength(); i++) {
            var property = properties.item(i);

            var name = property
                    .getAttributes()
                    .getNamedItem("name")
                    .getNodeValue();

            if ("jakarta.persistence.schema-generation.database.action"
                    .equals(name)) {

                schemaAction = property
                        .getAttributes()
                        .getNamedItem("value")
                        .getNodeValue();
            }
        }

        assertEquals("none", schemaAction);
    }
}
