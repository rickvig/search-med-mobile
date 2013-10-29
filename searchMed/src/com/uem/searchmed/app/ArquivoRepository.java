package com.uem.searchmed.app;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.uem.searchmed.SearchMedApp;

public class ArquivoRepository {

	private static String TAG = "ARQUIVO_REPOSITORY";
	protected Context ctx;
	protected SQLiteDatabase database;

	public ArquivoRepository(Context ctx) {
		this.ctx = ctx;
		SearchMedApp app = (SearchMedApp) ctx.getApplicationContext();
		this.database = app.getDatabase();
	}

	public void salvar(Arquivo arquivo) {
		if(arquivo.getId() == null){
			database.insert(TabelaArquivo.nomeTabela, "", getValues(arquivo));
		} else{
			// TODO verificar de manda para o update
		}
	}
	
	public Arquivo findById(Long id) {
		Cursor cursor = database.query(true, TabelaArquivo.nomeTabela, TabelaArquivo.getAllColumns(), TabelaArquivo._ID.getCampo() + " = ? ", new String[] { Long.toString(id) }, null, null, null, null);
		if (cursor.moveToFirst()) {
			return montaArquivo(cursor);
		}
		return null;
	}
	
	private Arquivo montaArquivo(Cursor cursor) {
		Long id = cursor.getLong(cursor.getColumnIndex(TabelaArquivo._ID.getCampo()));
		String contentType = cursor.getString(cursor.getColumnIndex(TabelaArquivo.CONTENT_TYPE.getCampo()));
		String nomeOriginal = cursor.getString(cursor.getColumnIndex(TabelaArquivo.NOME_ORIGINAL.getCampo()));
		Integer tamanhoArquivo = cursor.getInt(cursor.getColumnIndex(TabelaArquivo.TAMANHO_ARQUIVO.getCampo()));
		String uriFile = cursor.getString(cursor.getColumnIndex(TabelaArquivo.URI_FILE.getCampo()));

		return new Arquivo(id, contentType, nomeOriginal, tamanhoArquivo, uriFile);
	}

	private ContentValues getValues(Arquivo arquivo) {
		ContentValues values = new ContentValues();
		values.put(TabelaArquivo._ID.getCampo(), arquivo.getId());
		values.put(TabelaArquivo.CONTENT_TYPE.getCampo(), arquivo.getContentType());
		values.put(TabelaArquivo.NOME_ORIGINAL.getCampo(), arquivo.getNomeOriginal());
		values.put(TabelaArquivo.TAMANHO_ARQUIVO.getCampo(), arquivo.getTamanhoArquivo());
		values.put(TabelaArquivo.URI_FILE.getCampo(), arquivo.getUriFile());
		
		return values;
	}
	
	public List<Arquivo> listar() {
		Cursor cursor = database.query(TabelaArquivo.nomeTabela, TabelaArquivo.getAllColumns(), null, null, null, null, null);
		
		return factoryListDescritores(cursor);
	}

	private void delete(Arquivo arquivoRemove) {
		database.delete(TabelaArquivo.nomeTabela, TabelaArquivo._ID.getCampo() +" = ?", new String[] { arquivoRemove.getId().toString() });
	}

	private List<Arquivo> factoryListDescritores(Cursor cursor) {
		List<Arquivo> arquivos = new LinkedList<Arquivo>();
		if (cursor.moveToFirst()) {
			do {
				arquivos.add(montaArquivo(cursor));
			} while (cursor.moveToNext());
		}
		return arquivos;
	}

}
