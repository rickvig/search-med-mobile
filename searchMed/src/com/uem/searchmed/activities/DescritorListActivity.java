package com.uem.searchmed.activities;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.uem.searchmed.ConectHttp;
import com.uem.searchmed.R;
import com.uem.searchmed.app.Descritor;
import com.uem.searchmed.app.DescritorRepository;

public class DescritorListActivity extends ListActivity {

	static final String TAG = "DESCRITOR_LIST";

	ProgressDialog pd;
	public String palavraChave = "";
	public ArrayList descritores;
	public ArrayAdapter<Descritor> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_descritor_list);

		Intent intent = this.getIntent();
		if (intent != null && intent.getExtras() != null) {
			Bundle bundle = intent.getExtras();

			descritores = new ArrayList<Descritor>();
			adapter = new ArrayAdapter<Descritor>(this, android.R.layout.simple_list_item_1, descritores);
			setListAdapter(adapter);
			palavraChave = bundle.getString("seachWord");

			DescritorRepository repositorio = new DescritorRepository(this);
			List<Descritor> descritoresExistentes = repositorio.findWithCacheSemantic(palavraChave);

			Log.d(TAG, "Descritores existentes: " + descritoresExistentes.toString());
			descritores.addAll(descritoresExistentes);
		}
		handleList();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.descritor_list, menu);
		return true;
	}

	public void voltar(View view) {
		Intent i = new Intent(this, SearchActivity.class);
		startActivity(i);
	}

	public void pesquisaWebService(View view) {
		try {
			new Executa().execute(palavraChave);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void handleList() {
		getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> lista, View view, int i, long l) {
				Descritor d = (Descritor) lista.getItemAtPosition(i);
				Log.d(TAG, "Descritor clicado: " + d);
				showDescritor(d);
			}
		});
	}

	public void showDescritor(Descritor descritor) {
		Intent i = new Intent(this, DescritorShowActivity.class);
		descritor.numAcesso = ++descritor.numAcesso;
		new DescritorRepository(this).salvar(descritor);
		i.putExtra("descritor", descritor);
		startActivity(i);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == 999) {
			pd = new ProgressDialog(this);
			pd.setIndeterminate(true);
			pd.setMessage("pesquisando");
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
		protected ArrayList doInBackground(Object... objects) {
			String url = "http://decs.bvsalud.org/cgi-bin/mx/cgi=@vmx/decs/?words=" + palavraChave;
			ConectHttp connect = new ConectHttp(url);
			try {
				descritores.clear();
				descritores.addAll(connect.executar());
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Object o) {
			super.onPostExecute(o);
			dismissDialog(999);
			Log.d(TAG, "Descritores web-service: " + descritores.toString());
			adapter = new ArrayAdapter<Descritor>(DescritorListActivity.this, android.R.layout.simple_list_item_1, descritores);
			setListAdapter(adapter);
		}
	}

}
