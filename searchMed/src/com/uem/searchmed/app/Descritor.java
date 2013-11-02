package com.uem.searchmed.app;

import java.io.Serializable;
import java.util.Date;

public class Descritor implements Serializable {

	public static final long serialVersionUID = 1L;

	Long id;
	public String idDecs = "";
	public String descritor = "";
	public String definicao = ""; // clob - lenght=1024
	public String sinonimos = "";
	public String termosRelacionados = ""; // clob - lenght=1024
	public String indicesAnotacoes = "";
	public Integer numAcesso = 1;
	public Date dataUltimoAcesso;
	public Arquivo arquivo;
	
	public Descritor() {
	}

	public Descritor(String idDecs) {
		this.idDecs = idDecs;
	}

	public Descritor(Long id, String idDecs, String descritor
			, String definicao, String sinonimos, String termosRelacionados
			, String indicesAnotacoes, Integer numAcesso
			, Date dataUltimaAlteracao
			, Arquivo arquivo) {
		this.id = id;
		this.idDecs = idDecs;
		this.descritor = descritor;
		this.definicao = definicao;
		this.sinonimos = sinonimos;
		this.termosRelacionados = termosRelacionados;
		this.indicesAnotacoes = indicesAnotacoes;
		this.numAcesso = numAcesso;
		this.dataUltimoAcesso = dataUltimaAlteracao;
		this.arquivo = arquivo;
	}
	
	public Arquivo getArquivo() {
		return arquivo;
	}

	public void setArquivo(Arquivo arquivo) {
		this.arquivo = arquivo;
	}

	public Integer getNumAcesso() {
		return numAcesso;
	}

	public void setNumAcesso() {
		this.numAcesso = ++numAcesso;
	}

	public Date getDataUltimoAcesso() {
		return dataUltimoAcesso;
	}

	public void setDataUltimoAcesso(Date dataUltimoAcesso) {
		this.dataUltimoAcesso = dataUltimoAcesso;
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
	
	public String toStringFull() {
		return "Descritor [id=" + id + ", idDecs=" + idDecs + ", descritor=" + descritor + ", numAcesso=" + numAcesso + ", dataUltimoAcesso=" + dataUltimoAcesso + ", arquivo=" + arquivo + "]";
	}
}
