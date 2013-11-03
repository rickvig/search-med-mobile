package com.uem.searchmed.app;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.uem.searchmed.SearchMedApp;

public class DescritorRepository {

	private static String TAG = "DESCRITOR_REPOSITORY";
	private static Integer COUNT_MAX_CS = 10;
	protected Context ctx;
	protected SQLiteDatabase database;

	public DescritorRepository(Context ctx) {
		this.ctx = ctx;
		SearchMedApp app = (SearchMedApp) ctx.getApplicationContext();
		this.database = app.getDatabase();
	}

	public void salve(Descritor descritor) {
		Log.d(TAG, "salve() - descritor: "+descritor.toStringFull());
		if (findByIdDecs(descritor.idDecs) == null) {
			database.insert(TabelaDescritor.nomeTabela, "", getValues(descritor));
		} else{
			update(descritor);
		}
	}
	
	public void update(Descritor descritor) {
		Log.d(TAG, "update() - descritor: "+descritor.toStringFull());
		database.update(TabelaDescritor.nomeTabela, getValues(descritor), TabelaDescritor._ID.getCampo() + " = ?", new String[] { Long.toString(descritor.id) });
	}
	
	private void delete(Descritor descritor) {
		Log.d(TAG, "delete() - descritor: "+descritor.toStringFull());
		database.delete(TabelaDescritor.nomeTabela, TabelaDescritor._ID.getCampo() +" = ?", new String[] { descritor.id.toString() });
	}
	
	public List<Descritor> listar() {
		Log.d(TAG, "listar() - ");
		Cursor cursor = database.query(TabelaDescritor.nomeTabela, TabelaDescritor.getAllColumns(), null, null, null, null, null);
		return factoryListDescritores(cursor);
	}

	private List<Descritor> factoryListDescritores(Cursor cursor) {
		Log.d(TAG, "factoryListDescritores() - ");
		List<Descritor> descritores = new LinkedList<Descritor>();
		if (cursor.moveToFirst()) {
			do {
				Descritor descritor = montaDescritor(cursor);
				descritores.add(descritor);
			} while (cursor.moveToNext());
		}
		return descritores;
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
		Log.d(TAG, "processaPoliticaCS() - descritor: "+descritor.toStringFull());
		Cursor cursor = database.rawQuery("select count(*) from " + TabelaDescritor.nomeTabela, null);
		
		cursor.moveToFirst();
		int count = cursor.getInt(0);
		
		Log.d(TAG, "COUNT_MAX_CS: "+COUNT_MAX_CS+" - count BD: "+count);
		
		if (count <= COUNT_MAX_CS) {
			salve(descritor);
		} else {
			while (count > COUNT_MAX_CS) {
				removeDescritorComMenosAcessoMaisAntigo();
				count--;
			}
			salve(descritor);
		}
	}

	/**
	 * Remove o registro com menor numero de acesso e com a data ultimo acesso 
	 * mais distante de hoje, ou seja o mais antigo.
	 * 
	 * @author henrique
	 */
	private void removeDescritorComMenosAcessoMaisAntigo() {
		Log.d(TAG, "removedescritorComMenosAcessoMaisAntigo() - ");
		String query = "select * from " + TabelaDescritor.nomeTabela +
						" where " +TabelaDescritor.DATA_ULTIMO_ACESSO.getCampo()+ " = ( " +
						"	select min( d1." +TabelaDescritor.DATA_ULTIMO_ACESSO.getCampo()+ " ) from " +TabelaDescritor.nomeTabela+ " d1 where d1." +TabelaDescritor._ID.getCampo()+ " in ( " +
						"		select d2." +TabelaDescritor._ID.getCampo()+ " from " +TabelaDescritor.nomeTabela+ " d2" +
						"		where d2." +TabelaDescritor.NUM_ACESSO.getCampo()+ " = ( select min( d3." +TabelaDescritor.NUM_ACESSO.getCampo()+ " ) from " +TabelaDescritor.nomeTabela+ " d3 ) " +
						"		) )";
		
		Log.d(TAG, "removedescritorComMenosAcessoMaisAntigo() - query de politica: "+query);
		
		Cursor cursor = database.rawQuery(query, null);
		Log.d(TAG, "removedescritorComMenosAcessoMaisAntigo() - cursor.getCount(): "+cursor.getCount()+" cursor.isClosed(): "+cursor.isClosed());
		
		Descritor descritorRemove = new Descritor();
		if (cursor.moveToFirst()) {
			descritorRemove = montaDescritor(cursor);
			removeDescritor(descritorRemove);
		}
	}

	/**
	 * Remover um descritor e também o envia o registro para remoção do arquivo
	 * relacionado a este descritor, caso exista.
	 * 
	 * @param cursor
	 */
	private void removeDescritor(Descritor descritor) {
		Log.d(TAG, "removeDescritor() - descritor: "+descritor.toStringFull());
		if(descritor.arquivo != null){
			ArquivoRepository arquivoRepository = new ArquivoRepository(ctx);
			arquivoRepository.removerArquivo(descritor.arquivo);
		}
		delete(descritor);
	}

	/**
	 * Busca descritor segundo seu IdDecs
	 * 
	 * @author henrique
	 * @param String idDecs
	 * @return Descritor
	 */
	public Descritor findByIdDecs(String idDecs) {
		Log.d(TAG, "findByIdDecs() - idDecs: "+idDecs);
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
		Log.d(TAG, "findWithCacheSemantic() - palavraChave: "+palavraChave);
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
		
		Long arquivoId = cursor.getLong(cursor.getColumnIndex(TabelaDescritor.ARQUIVO_ID.getCampo()));
		Arquivo arquivo = new ArquivoRepository(ctx).findById(arquivoId);

		return new Descritor(id, idDecs, descritor, definicao, sinonimos, termosRelacionados, indicesAnotacoes, numAcesso, dataUltimoAcesso, arquivo);
	}

	private ContentValues getValues(Descritor descritor) {
		Log.d(TAG, "getValues() - descritor: "+descritor.toStringFull());
		
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
		
		if(descritor.arquivo != null){
			values.put(TabelaDescritor.ARQUIVO_ID.getCampo(), descritor.arquivo.getId());
		}
		return values;
	}
	
}
