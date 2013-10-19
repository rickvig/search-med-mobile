package com.uem.searchmed;

import com.uem.searchmed.app.TabelaDescritor;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SearchMedApp extends Application {

    private static final String NOME_BANCO = "search_med.db";
    SQLiteDatabase database;


    @Override
    public void onCreate() {
        super.onCreate();
        database = openOrCreateDatabase(NOME_BANCO, Context.MODE_PRIVATE, null);
        criarTabelas();
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        database.close();
    }

    private void criarTabelas(){
        Log.d("App", "criando tabelas");
        Log.d("table contato", TabelaDescritor.createTable());
        database.execSQL(TabelaDescritor.createTable());
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }

}
