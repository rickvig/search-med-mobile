package com.uem.searchmed.webservice;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.util.Log;

import com.uem.searchmed.app.Descritor;

public class ConectHttp {

    static final String TAG = "Conect_HTTP";

    String url = "";

    public ConectHttp(String url) {
        this.url = url;
    }

    public ArrayList<Descritor> executar() throws URISyntaxException, IOException, ParserConfigurationException, SAXException {
        HttpGet request = new HttpGet();
        request.setURI(new URI(url));

        Log.d(TAG, "URL: "+url);

        HttpClient client = HttpClientFactory.getHttpClient();
        HttpResponse response = client.execute(request);
        String answer = EntityUtils.toString(response.getEntity());

        ArrayList<Descritor> descritores = processXML(answer);

        return descritores;
    }

    private ArrayList<Descritor> processXML(String xmlStr) throws ParserConfigurationException, IOException, SAXException {

        Log.d(TAG, "XML: \n"+xmlStr);

        /* Processa o XML*/
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = factory.newDocumentBuilder();
        InputSource is = new InputSource();

        is.setCharacterStream(new StringReader(xmlStr));
        Document doc = db.parse(is);

        ArrayList<Descritor> descritores = new ArrayList<Descritor>();

        NodeList nl = doc.getElementsByTagName("decsws_response");
        for(int i = 0; i < nl.getLength(); i++){
            Node node = nl.item(i);
            NamedNodeMap nodeAttrs = node.getAttributes();
            Node nodeName = nodeAttrs.getNamedItem("tree_id");

            Descritor descritor = new Descritor(nodeName.getTextContent().toString());
            varreFilhos(node, descritor);
            //Log.d(TAG, descritor.toString());
            descritores.add(descritor);
        }

        return descritores;
    }

    /**
     * Chamada recursiva para varrer filhos de um nó na arvore XML
     * @param node
     * @param descritor
     */
    private void varreFilhos(Node node, Descritor descritor) {
        NodeList childre = node.getChildNodes();
        for(int i = 0; i < childre.getLength(); i++){
            Node nodeChild = childre.item(i);
            //Log.d(TAG, "NODE_CHILD_NAME - >" + nodeChild.getNodeName());
            if(childre.item(i).getNodeName() != null){
                if(nodeChild.getNodeName().equalsIgnoreCase("term")){
                    //Log.d(TAG, "TERM-> "+nodeChild.getTextContent());
                    descritor.setTermosRelacionados(nodeChild.getTextContent().toString());

                } else if(nodeChild.getNodeName().equalsIgnoreCase("descriptor")) {
                    if(nodeChild.getAttributes().getNamedItem("lang").getTextContent().equals("pt")){
                        //Log.d(TAG, "DESC-> "+nodeChild.getTextContent());
                    	descritor.setDescritor(nodeChild.getTextContent().toString());
                    }
                } else if(nodeChild.getNodeName().equalsIgnoreCase("synonym")){
                    //Log.d(TAG, "SYNO-> "+nodeChild.getTextContent());
                	descritor.setSinonimos(nodeChild.getTextContent().toString());

                } else if(nodeChild.getNodeName().equalsIgnoreCase("occ")){
                    //Log.d(TAG, "DEFI-> "+nodeChild.getTextContent());
                	descritor.setDefinicao(nodeChild.getAttributes().getNamedItem("n").getTextContent());

                } else if(nodeChild.getNodeName().equalsIgnoreCase("indexing_annotation")){
                    //Log.d(TAG, "INDA-> "+nodeChild.getTextContent());
                	descritor.setIndicesAnotacoes(nodeChild.getTextContent().toString());

                } else{
                    varreFilhos(nodeChild, descritor);
                }
            }
        }
    }

}

