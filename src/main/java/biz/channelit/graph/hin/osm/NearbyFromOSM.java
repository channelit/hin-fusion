package biz.channelit.graph.hin.osm;

import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;


public class NearbyFromOSM {
    private static final String OVERPASS_API = "http://lz4.overpass-api.de/api/interpreter";
    private static RestTemplate restTemplate = new RestTemplate();

    private static Document getNodesViaOverpass(String query) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
        String docStr = restTemplate.postForObject(OVERPASS_API, query, String.class);
        return docBuilder.parse(new InputSource(new StringReader(docStr)));
    }
    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
        Document doc = getNodesViaOverpass("node(around:100.0,40.7584800720214,-73.9377670288085);out;");
        String result = processResponse(doc);
        System.out.println(result);
    }

    private static String processResponse(Document doc) {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = tf.newTransformer();
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            String out = writer.getBuffer().toString();
            return out;
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return null;

    }

}
