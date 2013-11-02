package com.uem.searchmed.app;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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
		Log.d(TAG, "Arquivo para salvar: " + arquivo);

		if (findByUriFileAndTamanhoArquivo(arquivo.getUriFile(), arquivo.getTamanhoArquivo()) == null) {
			database.insert(TabelaArquivo.nomeTabela, "", getValues(arquivo));
		} else {
			// TODO verificar de manda para o update
		}
	}

	public Arquivo findById(Long id) {
		if (id != null) {
			Cursor cursor = database.query(true, TabelaArquivo.nomeTabela, TabelaArquivo.getAllColumns(), TabelaArquivo._ID.getCampo() + " = ? ", new String[] { Long.toString(id) }, null, null, null, null);
			if (cursor.moveToFirst()) {
				return montaArquivo(cursor);
			}
		}
		return null;
	}

	private int delete(Arquivo arquivoRemove) {
		return database.delete(TabelaArquivo.nomeTabela, TabelaArquivo._ID.getCampo() + " = ?", new String[] { arquivoRemove.getId().toString() });
	}

	public List<Arquivo> listar() {
		Cursor cursor = database.query(TabelaArquivo.nomeTabela, TabelaArquivo.getAllColumns(), null, null, null, null, null);
		return factoryListDescritores(cursor);
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

	/**
	 * Remover arquivo da base de dados e remover file do device.
	 * 
	 * @param cursor
	 * @return true sucesso, false fail
	 */
	public Boolean removerArquivo(Arquivo arquivo) {
		boolean deleted = false;
		File file = new File(arquivo.getUriFile());

		if (file.exists()) {
			deleted = file.delete() && delete(arquivo) > 0;
		}
		return deleted;
	}

	/**
	 * Busca uma arquivo segundo sua uri e e seu tamanho
	 * 
	 * @param uriFile
	 * @param tamanhoArquivo
	 * @return arquivo
	 */
	public Arquivo findByUriFileAndTamanhoArquivo(String uriFile, Integer tamanhoArquivo) {
		Cursor cursor = database.query(true, TabelaArquivo.nomeTabela
				, TabelaArquivo.getAllColumns(), TabelaArquivo.URI_FILE.getCampo() + " = ? and "+TabelaArquivo.TAMANHO_ARQUIVO.getCampo() +" = ? "
				, new String[] { uriFile, Integer.toString(tamanhoArquivo) }, null, null, null, null);
		
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

}
