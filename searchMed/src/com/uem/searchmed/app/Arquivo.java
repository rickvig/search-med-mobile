package com.uem.searchmed.app;

import java.io.Serializable;

public class Arquivo implements Serializable {

	public static final long serialVersionUID = 1L;
	
	private Long id;
	private String contentType = "";
	private String nomeOriginal = "";
	private Integer tamanhoArquivo = 0;
	private String uriFile = "";
	
	public Arquivo(String contentType, String nomeOriginal, Integer tamanhoArquivo){
		this.contentType = contentType;
		this.nomeOriginal = nomeOriginal;
		this.tamanhoArquivo = tamanhoArquivo;		
	}
	
	public Arquivo(Long id, String contentType, String nomeOriginal, Integer tamanhoArquivo, String uriFile) {
		super();
		this.id = id;
		this.contentType = contentType;
		this.nomeOriginal = nomeOriginal;
		this.tamanhoArquivo = tamanhoArquivo;
		this.uriFile = uriFile;
	}
	

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUriFile() {
		return uriFile;
	}
	public void setUriFile(String uriFile) {
		this.uriFile = uriFile;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getNomeOriginal() {
		return nomeOriginal;
	}
	public void setNomeOriginal(String nomeOriginal) {
		this.nomeOriginal = nomeOriginal;
	}
	public Integer getTamanhoArquivo() {
		return tamanhoArquivo;
	}
	public void setTamanhoArquivo(Integer tamanhoArquivo) {
		this.tamanhoArquivo = tamanhoArquivo;
	}

	@Override
	public String toString() {
		return "Arquivo [id=" + id + ", contentType=" + contentType + ", nomeOriginal=" + nomeOriginal + ", tamanhoArquivo=" + tamanhoArquivo + ", uriFile=" + uriFile + "]";
	}



}
