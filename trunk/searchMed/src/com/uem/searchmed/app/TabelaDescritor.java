package com.uem.searchmed.app;

import android.util.Log;

//import com.google.common.base.Joiner;

import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Joiner;

public enum TabelaDescritor {

	_ID("id", "integer", "primary key autoincrement")
	, ID_DECS("idDecs", "text", "not null")
	, TERMOS_RELACIONADOS("termosRelacionados", "text", "")
	, DEFINICAO("definicao", "text", "")
	, DESCRITOR("descritor", "text", "")
	, SINONIMOS("sinonimos", "text", "")
	, INDICES_ANOTACOES("indicesAnotacoes", "text", "")
	, URI_FILE("uriFile", "text", "")
	, NUM_ACESSO("numAcesso", "text", "");

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
		return String.format("CREATE TABLE IF NOT EXISTS %s (%s)", nomeTabela, campos);
	}

	public static String[] getAllColumns() {
		List<String> columns = new LinkedList<String>();
		for (TabelaDescritor column : values()) {
			columns.add(column.getCampo());
		}
		return columns.toArray(new String[] {});
	}

}
