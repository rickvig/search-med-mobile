package com.uem.searchmed.app;

import android.util.Log;

//import com.google.common.base.Joiner;

import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Joiner;

public enum TabelaDescritor {

	_ID("ID", "integer", "primary key autoincrement")
	, ID_DECS("ID_DECS", "text", "not null")
	, TERMOS_RELACIONADOS("TERMOS_RELACIONADOS", "text", "")
	, DEFINICAO("DEFINICAO", "text", "")
	, DESCRITOR("DESCRITOR", "text", "")
	, SINONIMOS("SINONIMOS", "text", "")
	, INDICES_ANOTACOES("INDICES_ANOTACOES", "text", "")
	, NUM_ACESSO("NUM_ACESSO", "interger", "")
	, DATA_ULTIMO_ACESSO("DATA_ULTIMO_ACESSO", "interger", "")
	, ARQUIVO_ID("ARQUIVO_ID", "integer", "");

	public static String nomeTabela = "descritor";

	private TabelaDescritor(String campo, String tipo, String opcao) {
		this.campo = campo;
		this.tipo = tipo;
		this.opcao = opcao;
	}

	private String campo;
	private String tipo;
	private String opcao;

	public String getCampo() {
		return campo;
	}

	public String getTipo() {
		return tipo;
	}

	public String getOpcao() {
		return opcao;
	}

	@Override
	public String toString() {
		String toString = String.format("%s %s %s", campo, tipo, opcao);
		Log.d("column", toString);
		return toString;
	}

	public static String createTable() {
		String campos = Joiner.on(", ").join(values());
		Log.d("campos", campos);
		// FOREIGN KEY (id_pessoa) REFERENCES tabela_pessoa (id) ON DELETE CASCADE );
		return String.format("CREATE TABLE IF NOT EXISTS %s (%s , FOREIGN KEY (%s) REFERENCES %s (%s) ON DELETE CASCADE );"
				, nomeTabela, campos, ARQUIVO_ID.getCampo(), TabelaArquivo.nomeTabela, TabelaArquivo._ID.getCampo());
	}

	public static String[] getAllColumns() {
		List<String> columns = new LinkedList<String>();
		for (TabelaDescritor column : values()) {
			columns.add(column.getCampo());
		}
		return columns.toArray(new String[] {});
	}

}
