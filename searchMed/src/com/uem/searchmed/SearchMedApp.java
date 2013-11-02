package com.uem.searchmed;

import java.io.File;

import com.uem.searchmed.app.Arquivo;
import com.uem.searchmed.app.TabelaArquivo;
import com.uem.searchmed.app.TabelaDescritor;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

public class SearchMedApp extends Application {

	private static final String TAG = "SEARCH_MED_APP";
    private static final String NOME_BANCO = "search_med.db";
	public static final String PATH_FILE_DAFAULT = "/SearchMed";
    
    SQLiteDatabase database;


    @Override
    public void onCreate() {
        super.onCreate();
        database = openOrCreateDatabase(NOME_BANCO, Context.MODE_PRIVATE, null);
        criarTabelas();
        criaDiretorio();
    }

    
	@Override
    public void onTerminate() {
        super.onTerminate();
        database.close();
    }

    private void criarTabelas(){
        Log.d(TAG, "criando tabelas");
        
        Log.d(TAG, "table arquivo"+TabelaArquivo.nomeTabela);
        database.execSQL(TabelaArquivo.createTable());
        
        Log.d(TAG, "table descrito"+TabelaDescritor.nomeTabela);
        database.execSQL(TabelaDescritor.createTable());
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }
    
    private void criaDiretorio() {
		new File(Environment.getExternalStorageDirectory().getAbsolutePath() + PATH_FILE_DAFAULT + "/").mkdir();
	}

}
