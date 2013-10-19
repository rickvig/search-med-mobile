package com.uem.searchmed.app;

import java.io.Serializable;

public class Descritor implements Serializable {

	public static final long serialVersionUID = 1L;

	Long id;
	public String idDecs = "";
	public String descritor = "";
	public String definicao = ""; // clob - lenght=1024
	public String sinonimos = "";
	public String termosRelacionados = ""; // clob - lenght=1024
	public String indicesAnotacoes = "";
	public String uriFile = "";
	public Integer numAcesso = 1;

	public Descritor() {
	}

	public Descritor(String idDecs) {
		this.idDecs = idDecs;
	}

	public Descritor(Long id, String idDecs, String descritor, String definicao, String sinonimos, String termosRelacionados, String indicesAnotacoes, String uriFile, Integer numAcesso) {
		this.id = id;
		this.idDecs = idDecs;
		this.descritor = descritor;
		this.definicao = definicao;
		this.sinonimos = sinonimos;
		this.termosRelacionados = termosRelacionados;
		this.indicesAnotacoes = indicesAnotacoes;
		this.uriFile = uriFile;
		this.numAcesso = numAcesso;
	}

	public void setDescritor(String descritor) {
		this.descritor += addString(this.descritor, descritor);
	}

	public void setDefinicao(String definicao) {
		this.definicao += addString(this.definicao, definicao);
	}

	public void setSinonimos(String sinonimos) {
		this.sinonimos += addString(this.sinonimos, sinonimos);
	}

	public void setTermosRelacionados(String termosRelacionados) {
		this.termosRelacionados += addString(this.termosRelacionados, termosRelacionados);
	}

	public void setIndicesAnotacoes(String indicesAnotacoes) {
		this.indicesAnotacoes += addString(this.indicesAnotacoes, indicesAnotacoes);
	}

	private String addString(String strTarget, String strFinal) {
		return strTarget == "" ? strFinal : ", " + strFinal;
	}

	@Override
	public String toString() {
		return descritor;
	}
}
