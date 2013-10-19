package com.uem.searchmed.activities;

import com.uem.searchmed.R;
import com.uem.searchmed.R.layout;
import com.uem.searchmed.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SearchActivity extends Activity {

	private EditText searchWord;
	private Button search;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		searchWord = (EditText) findViewById(R.id.searchWord);

		search = (Button) findViewById(R.id.search);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}

	public void pesquisar(View view) {
		Intent i = new Intent(this, DescritorListActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("seachWord", searchWord.getText().toString());
		i.putExtras(bundle);
		startActivity(i);
	}

}
