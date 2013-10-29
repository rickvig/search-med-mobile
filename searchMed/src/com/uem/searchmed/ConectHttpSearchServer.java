package com.uem.searchmed;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import com.uem.searchmed.app.Arquivo;
import com.uem.searchmed.app.Descritor;

public class ConectHttpSearchServer {

	static final String TAG = "Conect_HTTP_SearchServer";

	String url = "";

	public ConectHttpSearchServer(String url) {
		this.url = url;
	}

	public Arquivo executar() throws URISyntaxException, IOException, JSONException {
		HttpGet request = new HttpGet();
		request.setURI(new URI(url));

		Log.d(TAG, "URL: " + url);

		HttpClient client = HttpClientFactory.getHttpClient();
		HttpResponse response = client.execute(request);
		String responseContext = EntityUtils.toString(response.getEntity());

		return processJSON(responseContext);
	}

	private Arquivo processJSON(String jsonStr) throws JSONException, IOException  {
		Log.d(TAG, "JSON: \n" + jsonStr);

		/* Processa o JSON */
		JSONTokener tokener = new JSONTokener(jsonStr);
		JSONObject jsonObject = (JSONObject) tokener.nextValue();

		Boolean error = jsonObject.getBoolean("error");
		
		if(!error){
			JSONObject jsonArquivo = jsonObject.getJSONObject("arquivo");
			
			Arquivo arquivo = new Arquivo(jsonArquivo.getString("contentType"), jsonArquivo.getString("nomeOriginal"), jsonArquivo.getInt("tamanhoArquivo"));
			Log.d(TAG, "arquivo: "+arquivo);
			
			Log.d(TAG, "ARRAY STR: \n"+jsonObject.getJSONArray("filebytes").join(","));
			
			// TODO ARQUIVO ESTÁ CORROMPIDO
			
			byte[] fileBytes = Base64.decode(jsonObject.getJSONArray("filebytes").join(","), Base64.DEFAULT);
			Log.d(TAG, "jsonFileBytes: "+ fileBytes.toString());
			
			// salvando o arquivo
			Log.d(TAG, "pasta local: "+ Environment.getExternalStorageDirectory().getAbsolutePath().toString());
			arquivo.setUriFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + arquivo.getNomeOriginal());
			
			File arq = new File(Environment.getExternalStorageDirectory(), arquivo.getNomeOriginal());
			FileOutputStream fos;
            fos = new FileOutputStream(arq);
             
            //escreve os dados e fecha o arquivo
            fos.write(fileBytes);
            fos.flush();
            fos.close();
            
            return arquivo;
		}
		return null;
	}
}
