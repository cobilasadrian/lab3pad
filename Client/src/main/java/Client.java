
import java.io.IOException;
import java.io.StringReader;
import java.net.InetSocketAddress;

import discovery.DiscoveryClient;
import model.Location;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import transport.TransportClient;
import javax.xml.XMLConstants;

import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

/**
 * Created by Adrian on 11/22/2015.
 */
public class Client {

    private static String xmlSchema ="<xs:schema attributeFormDefault=\"unqualified\" elementFormDefault=\"qualified\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n" +
            "    <xs:element name=\"employees\">\n" +
            "        <xs:complexType>\n" +
            "            <xs:sequence>\n" +
            "                <xs:element name=\"employee\" maxOccurs=\"unbounded\" minOccurs=\"0\">\n" +
            "                    <xs:complexType>\n" +
            "                        <xs:sequence>\n" +
            "                            <xs:element type=\"xs:string\" name=\"firstName\"/>\n" +
            "                            <xs:element type=\"xs:string\" name=\"lastName\"/>\n" +
            "                            <xs:element type=\"xs:string\" name=\"department\"/>\n" +
            "                            <xs:element type=\"xs:float\" name=\"salary\"/>\n" +
            "                        </xs:sequence>\n" +
            "                    </xs:complexType>\n" +
            "                </xs:element>\n" +
            "            </xs:sequence>\n" +
            "        </xs:complexType>\n" +
            "    </xs:element>\n" +
            "</xs:schema>";

    private static boolean validateXML(String xsd, String xml){
        try {
            SchemaFactory factory =
                    SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new SAXSource(new InputSource(new StringReader(xsd))));
            Validator validator = schema.newValidator();
            StringReader reader = new StringReader(xml);
            validator.validate(new StreamSource(reader));
        } catch (IOException e){
            return false;
        }catch(SAXException e1){
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        System.out.println("[INFO] -----------------------------------------\n" +
                "[INFO] Client is running...");
        try {
            Location location = new DiscoveryClient(
                    new InetSocketAddress("127.0.0.1", 33333))
                    .retrieveLocation();
            System.out.println("[INFO] -----------------------------------------\n" +
                    "[INFO] Discovered server: " + location);

            if (location != null) {

                String xml =  new TransportClient()
                        .getEmployeesFrom(location);

                System.out.println("[Result] -----------------------------------------\n"
                        + xml);

                boolean isValid = validateXML(xmlSchema,xml);
                if(isValid){
                    System.out.println("[INFO] -----------------------------------------\n" +
                            "[INFO] Received XML information is validated.");
                }else {
                    System.out.println("[WARNING] -----------------------------------------\n" +
                            "[WARNING] Received XML information is not validated.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
