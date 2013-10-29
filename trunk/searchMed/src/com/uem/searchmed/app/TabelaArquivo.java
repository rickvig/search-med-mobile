package com.uem.searchmed.app;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Joiner;

public enum TabelaArquivo {

	_ID("id", "integer", "primary key autoincrement")
	, CONTENT_TYPE("contentType", "text", "not null")
	, NOME_ORIGINAL("nomeOriginal", "text", "")
	, TAMANHO_ARQUIVO("tamanhoArquivo", "interger", "")
	, URI_FILE("uriFile", "text", "");

	public static String nomeTabela = "arquivo";

	private TabelaArquivo(String campo, String tipo, String opcao) {
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
		for (TabelaArquivo column : values()) {
			columns.add(column.getCampo());
		}
		return columns.toArray(new String[] {});
	}

}
