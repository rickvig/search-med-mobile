package com.uem.searchmed.app;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.DateFormat;
import android.util.Log;

import com.uem.searchmed.SearchMedApp;

public class DescritorRepository {

	private static String TAG = "DESCRITOR_REPOSITORY";
	private static Integer COUNT_MAX_CS = 5;
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

	/**
	 *  Politica do cache semantico (CS) - substituição quando cache estiver 
	 *  cheio manter no minimo de 20% a 30% do cache.
	 *  Exemplo.: registro com menos n° de acesso e com data de acesso mais 
	 *  antigo qnd há impate
	 *  
	 *  @author henrique
	 *  @param Descritor
	 */
	public void processaPoliticaCS(Descritor descritor) {
		Cursor cursor = database.rawQuery("select count(*) from " + TabelaDescritor.nomeTabela, null);
		cursor.moveToFirst();
		int count = cursor.getInt(0);
		
		Log.d(TAG, "COUNT_MAX_CS: "+COUNT_MAX_CS+" - count BD: "+count);
		
		if (count <= COUNT_MAX_CS) {
			salvar(descritor);
		} else {
			removeDescritorComMenosAcessoMaisAntigo();
			salvar(descritor);
		}
	}

	/**
	 * Remove o registro com menor numero de acesso e com a data ultimo acesso 
	 * mais distante de hoje, ou seja o mais antigo.
	 * 
	 * @author henrique
	 */
	private void removeDescritorComMenosAcessoMaisAntigo() {
		String query = "select * from " + TabelaDescritor.nomeTabela +
				" where " + TabelaDescritor.NUM_ACESSO.getCampo() + " = " +
				" (select min(d."+TabelaDescritor.NUM_ACESSO.getCampo()+") from "+TabelaDescritor.nomeTabela+" d) " +
				" and " + TabelaDescritor.DATA_ULTIMO_ACESSO.getCampo() + " = " +
				" (select min(d."+TabelaDescritor.DATA_ULTIMO_ACESSO.getCampo()+") from "+TabelaDescritor.nomeTabela+" d) ";
		
		Log.d(TAG, "query de politica: "+query);
		
		Cursor cursor = database.rawQuery(query, null);
		Descritor descritorRemove = new Descritor();
		
		if (cursor.moveToFirst()) {
			descritorRemove = montaDescritor(cursor);
			Log.d(TAG, "descritorRemove: "+descritorRemove);
			delete(descritorRemove);
		}
	}

	/**
	 * Busca descritor segundo seu IdDecs
	 * 
	 * @author henrique
	 * @param String idDecs
	 * @return Descritor
	 */
	public Descritor findByIdDecs(String idDecs) {
		Cursor cursor = database.query(true, TabelaDescritor.nomeTabela, TabelaDescritor.getAllColumns(), TabelaDescritor.ID_DECS.getCampo() + " = ? ", new String[] { idDecs }, null, null, null, null);
		if (cursor.moveToFirst()) {
			return montaDescritor(cursor);
		}
		return null;
	}

	/**
	 * Busca descritores segundo as regras de de cache semântico.
	 * 
	 * @author henrique
	 * @param String palavra chave
	 * @return Lista de descritores
	 */
	public List<Descritor> findWithCacheSemantic(String palavraChave) {
		String query = "select * from " + TabelaDescritor.nomeTabela +
				" where " + TabelaDescritor.DESCRITOR.getCampo() +" like ?" +
				" or " + TabelaDescritor.DEFINICAO.getCampo() + " like ?" +
				" or " + TabelaDescritor.SINONIMOS.getCampo() + " like ?" +
				" or " + TabelaDescritor.TERMOS_RELACIONADOS.getCampo() + " like ?" +
				" or " + TabelaDescritor.INDICES_ANOTACOES.getCampo() + " like ?" +
				" order by " + TabelaDescritor.DESCRITOR.getCampo() + " asc ";
		Log.d(TAG, "palavra chave: "+palavraChave);
		Log.d(TAG, "query: "+query);
		Cursor cursor = database.rawQuery(query, new String[] { "%"+palavraChave+"%", "%"+palavraChave+"%", "%"+palavraChave+"%", "%"+palavraChave+"%", "%"+palavraChave+"%" });
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
		Integer numAcesso = cursor.getInt(cursor.getColumnIndex(TabelaDescritor.NUM_ACESSO.getCampo()));
		Date dataUltimoAcesso = new Date(cursor.getLong(cursor.getColumnIndex(TabelaDescritor.DATA_ULTIMO_ACESSO.getCampo())));

		return new Descritor(id, idDecs, descritor, definicao, sinonimos, termosRelacionados, indicesAnotacoes, numAcesso, dataUltimoAcesso);
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
		values.put(TabelaDescritor.NUM_ACESSO.getCampo(), descritor.numAcesso);
		values.put(TabelaDescritor.DATA_ULTIMO_ACESSO.getCampo(), new Date().getTime());
		return values;
	}
	
	public List<Descritor> listar() {
		Cursor cursor = database.query(TabelaDescritor.nomeTabela, TabelaDescritor.getAllColumns(), null, null, null, null, null);
		return factoryListDescritores(cursor);
	}

	private void delete(Descritor descritorRemove) {
		database.delete(TabelaDescritor.nomeTabela, TabelaDescritor._ID.getCampo() +" = ?", new String[] { descritorRemove.id.toString() });
	}

	private List<Descritor> factoryListDescritores(Cursor cursor) {
		List<Descritor> descritores = new LinkedList<Descritor>();
		if (cursor.moveToFirst()) {
			do {
				Descritor descritor = montaDescritor(cursor);
				descritores.add(descritor);
			} while (cursor.moveToNext());
		}
		return descritores;
	}

}
