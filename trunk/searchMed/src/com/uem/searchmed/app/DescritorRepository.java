package com.uem.searchmed.app;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.uem.searchmed.SearchMedApp;

public class DescritorRepository {

	private static String TAG = "DescritorRepository";
	protected Context ctx;
	protected SQLiteDatabase database;

	public DescritorRepository(Context ctx) {
		this.ctx = ctx;
		SearchMedApp app = (SearchMedApp) ctx.getApplicationContext();
		this.database = app.getDatabase();
	}

	public void salvar(Descritor descritor) {
		if (findByIdDecs(descritor.idDecs) == null) {
			database.insert(TabelaDescritor.nomeTabela, "", getValues(descritor));
		}
	}

	public Descritor findByIdDecs(String idDecs) {
		Cursor cursor = database.query(true, TabelaDescritor.nomeTabela, TabelaDescritor.getAllColumns(), TabelaDescritor.ID_DECS.getCampo() + " = ? ", new String[] { idDecs }, null, null, null, null);
		if (cursor.moveToFirst()) {
			return montaDescritor(cursor);
		}
		return null;
	}

	public List<Descritor> listar() {
		Cursor cursor = database.query(TabelaDescritor.nomeTabela, TabelaDescritor.getAllColumns(), null, null, null, null, null);
		return factoryListDescritores(cursor);
	}

	public List<Descritor> findWithCacheSemantic(String palavraChave) {
		String query = "SELECT * FROM " + TabelaDescritor.nomeTabela + " WHERE " + 
				TabelaDescritor.DESCRITOR.getCampo() + " LIKE ?" + 
				" OR " + TabelaDescritor.DEFINICAO.getCampo() + " LIKE ?" + 
				" OR " + TabelaDescritor.SINONIMOS.getCampo() + " LIKE ?" + 
				" OR " + TabelaDescritor.TERMOS_RELACIONADOS.getCampo() + " LIKE ?" + 
				" OR " + TabelaDescritor.INDICES_ANOTACOES.getCampo() + " LIKE ?" + 
				" ORDER BY " + TabelaDescritor.DESCRITOR.getCampo() + " ASC ";
		Log.d(TAG, query);
		Cursor cursor = database.rawQuery(query, new String[] { "%" + palavraChave + "%" });
		return factoryListDescritores(cursor);
	}

	private Descritor montaDescritor(Cursor cursor) {
		Long id = cursor.getLong(cursor.getColumnIndex(TabelaDescritor._ID.getCampo()));
		String idDecs = cursor.getString(cursor.getColumnIndex(TabelaDescritor.ID_DECS.getCampo()));
		String descritor = cursor.getString(cursor.getColumnIndex(TabelaDescritor.DESCRITOR.getCampo()));
		String definicao = cursor.getString(cursor.getColumnIndex(TabelaDescritor.DEFINICAO.getCampo()));
		String sinonimos = cursor.getString(cursor.getColumnIndex(TabelaDescritor.SINONIMOS.getCampo()));
		String termosRelacionados = cursor.getString(cursor.getColumnIndex(TabelaDescritor.TERMOS_RELACIONADOS.getCampo()));
		String indicesAnotacoes = cursor.getString(cursor.getColumnIndex(TabelaDescritor.INDICES_ANOTACOES.getCampo()));
		String uriFile = cursor.getString(cursor.getColumnIndex(TabelaDescritor.URI_FILE.getCampo()));
		Integer numAcesso = cursor.getInt(cursor.getColumnIndex(TabelaDescritor.NUM_ACESSO.getCampo()));

		return new Descritor(id, idDecs, descritor, definicao, sinonimos, termosRelacionados, indicesAnotacoes, uriFile, numAcesso);
	}

	private ContentValues getValues(Descritor descritor) {
		ContentValues values = new ContentValues();
		values.put(TabelaDescritor._ID.getCampo(), descritor.id);
		values.put(TabelaDescritor.ID_DECS.getCampo(), descritor.idDecs);
		values.put(TabelaDescritor.DESCRITOR.getCampo(), descritor.descritor);
		values.put(TabelaDescritor.DEFINICAO.getCampo(), descritor.definicao);
		values.put(TabelaDescritor.SINONIMOS.getCampo(), descritor.sinonimos);
		values.put(TabelaDescritor.TERMOS_RELACIONADOS.getCampo(), descritor.termosRelacionados);
		values.put(TabelaDescritor.INDICES_ANOTACOES.getCampo(), descritor.indicesAnotacoes);
		values.put(TabelaDescritor.URI_FILE.getCampo(), descritor.uriFile);
		values.put(TabelaDescritor.NUM_ACESSO.getCampo(), descritor.numAcesso);
		return values;
	}

	private List<Descritor> factoryListDescritores(Cursor c) {
		List<Descritor> descritores = new LinkedList<Descritor>();
		if (c.moveToFirst()) {
			do {
				Descritor descritor = montaDescritor(c);
				descritores.add(descritor);
			} while (c.moveToNext());
		}
		return descritores;
	}

}
