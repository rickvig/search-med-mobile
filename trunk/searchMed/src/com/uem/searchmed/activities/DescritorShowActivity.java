package com.uem.searchmed.activities;

import java.io.File;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.uem.searchmed.ConectHttpSearchServer;
import com.uem.searchmed.R;
import com.uem.searchmed.app.Arquivo;
import com.uem.searchmed.app.ArquivoRepository;
import com.uem.searchmed.app.Descritor;
import com.uem.searchmed.app.DescritorRepository;

public class DescritorShowActivity extends Activity {

	public static final String TAG = "DESCRITOR_SHOW";
	TextView descritorNome;
	TextView sinonimos;
	TextView definicao;
	TextView dataUltimoAcesso;
	private ProgressDialog pd;
	
	Descritor descritor = new Descritor();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_descritor_show);

		descritor = (Descritor) getIntent().getSerializableExtra("descritor");

		descritorNome = (TextView) findViewById(R.id.descritorNome);
		sinonimos = (TextView) findViewById(R.id.sinonimos);
		definicao = (TextView) findViewById(R.id.definicao);
		dataUltimoAcesso = (TextView) findViewById(R.id.data_ultimo_acesso);

		descritorNome.setText(descritor.descritor);
		sinonimos.setText(descritor.sinonimos);
		definicao.setText(descritor.definicao);
		
		String dataUltimoAcessoExtra = (String) getIntent().getStringExtra("dataUltimoAcessoExtra");
		dataUltimoAcesso.setText(dataUltimoAcessoExtra);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.descritor_show, menu);
		return true;
	}

	/**
	 * Caso o descritor tenha uri, provavelmente ele já tem um arquivo.
	 * Então buscar primeiro o arquivo no device
	 * @param view
	 */
	public void buscaArquivo(View view) {
		if (descritor.arquivo != null) {
			// busca no device
			try {
				long startTime = System.currentTimeMillis();
				
				Log.d(TAG, "chamou DEVICE, uriFile: " + descritor.arquivo.getUriFile());
				File file = new File(descritor.arquivo.getUriFile());
				showFile(file);
				
				Log.i(TAG, "chamou DEVICE, tamanho do file: " + descritor.arquivo.getTamanhoArquivo() + "bytes.");
				Log.i(TAG, "chamou DEVICE, levou: " + (System.currentTimeMillis()-startTime) + " ms.");
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(this, R.string.fail_on_get_file, Toast.LENGTH_LONG).show();
			}
		} else {
			// busca no servidor
			try {
				new Executa().execute();
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(this, R.string.fail_on_get_file, Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == 999) {
			pd = new ProgressDialog(this);
			pd.setIndeterminate(true);
			pd.setMessage("Buscando Arquivo");
			return pd;
		}
		return super.onCreateDialog(id);
	}

	class Executa extends AsyncTask<Object, Object, Object> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showDialog(999);
		}

		@Override
		protected Object doInBackground(Object... objects) {
			long startTime = System.currentTimeMillis();
			
			Log.d(TAG, "chamou WEB-SERVICE");
			
			String id = descritor.idDecs.replace('.', '_');
			// HOME:
			String url = "http://192.168.1.10:8080/searchMedServer/arquivo/getArquivo/" + id + ".json";
			//String url = "http://10.253.29.20:8080/searchMedServer/arquivo/getArquivo/" + id + ".json";
			ConectHttpSearchServer connect = new ConectHttpSearchServer(url);
			try {
				Arquivo arquivo = connect.executar();
				if(arquivo != null){
					ArquivoRepository arquivoRepository = new ArquivoRepository(DescritorShowActivity.this);
					arquivoRepository.salvar(arquivo);
					
					descritor.arquivo = arquivoRepository.findByUriFileAndTamanhoArquivo(arquivo.getUriFile(), arquivo.getTamanhoArquivo());
					new DescritorRepository(DescritorShowActivity.this).update(descritor);
				} 
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			Log.i(TAG, "chamou WEB-SERVICE, levou: " + (System.currentTimeMillis()-startTime) + " ms.");
			
			return null;
		}

		@Override
		protected void onPostExecute(Object o) {
			super.onPostExecute(o);
			try {
				File file = new File(descritor.arquivo.getUriFile());
				showFile(file);
				Log.i(TAG, "chamou WEB-SERVICE, tamanho do file: " + descritor.arquivo.getTamanhoArquivo() + "bytes.");
			} catch (Exception e) {
				e.printStackTrace();
			}
			dismissDialog(999);
		}
	}
	
	private void showFile(File file) {
		Intent target = new Intent(Intent.ACTION_VIEW);
		target.setDataAndType(Uri.fromFile(file), descritor.arquivo.getContentType());
		target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		Intent intent = Intent.createChooser(target, "Abrir Arquivo");
		startActivity(intent);
	}

}
