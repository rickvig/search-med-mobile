package com.uem.searchmed.activities;

import com.uem.searchmed.R;
import com.uem.searchmed.R.id;
import com.uem.searchmed.R.layout;
import com.uem.searchmed.R.menu;
import com.uem.searchmed.app.Descritor;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class DescritorShowActivity extends Activity {
	
	TextView descritorNome;
    TextView sinonimos;
    TextView definicao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_descritor_show);

        Descritor d = (Descritor) getIntent().getSerializableExtra("descritor");

        Log.d("2Definicao: ", d.definicao);

        descritorNome = (TextView) findViewById(R.id.descritorNome);
        sinonimos = (TextView) findViewById(R.id.sinonimos);
        definicao = (TextView) findViewById(R.id.definicao);

        descritorNome.setText(d.descritor);
        sinonimos.setText(d.sinonimos);
        definicao.setText(d.definicao);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.descritor_show, menu);
		return true;
	}

}

