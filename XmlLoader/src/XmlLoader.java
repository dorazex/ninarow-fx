import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;


public class XmlLoader {

    public static HashMap<String, Object> getGameBasicInitParameters(String filePath) throws ConfigXmlException {

        if (!filePath.toLowerCase().endsWith(".xml")) throw new ConfigXmlException("File is not an XML file");

        HashMap<String, Object> parametersMap = new HashMap<>();
        try {

            File fXmlFile = new File(filePath);
            if (!fXmlFile.exists()) throw new ConfigXmlException("File does not exist");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();

            NodeList gameNodeList = doc.getElementsByTagName("Game");
            Element gameElement = (Element) gameNodeList.item(0);
            Element boardElement = (Element) gameElement.getElementsByTagName("Board").item(0);
            Element variantElement = (Element) gameElement.getElementsByTagName("Variant").item(0);

            String variant = variantElement.getTextContent();
            String target = gameElement.getAttribute("target");
            String rows = boardElement.getAttribute("rows");
            String columns = boardElement.getAttribute("columns");


            parametersMap.put("variant", variant);
            try {
                parametersMap.put("target", Integer.parseInt(target));
                parametersMap.put("rows", Integer.parseInt(rows));
                parametersMap.put("columns", Integer.parseInt(columns));
            } catch (Exception e){
                throw new ConfigXmlException("Target, rows and columns attributes must be integers");
            }

            if (!(((Integer)parametersMap.get("rows")) >= 5 && ((Integer)parametersMap.get("rows")) <= 50)){
                throw new ConfigXmlException("Rows value must be in range 5-50 inclusive");
            }
            if (!(((Integer)parametersMap.get("columns")) >= 6 && ((Integer)parametersMap.get("columns")) <= 60)){
                throw new ConfigXmlException("Columns value must be in range 6-60 inclusive");
            }
            if (!(((Integer)parametersMap.get("target")) < ((Integer)parametersMap.get("columns")) &&
                    ((Integer)parametersMap.get("target")) < ((Integer)parametersMap.get("rows")))){
                throw new ConfigXmlException("Target value must be less than both rows and columns value");
            }


        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        return parametersMap;
    }

}
