package com.uem.searchmed.app;

public class Arquivo {
	
	private String contentType = "";
	private String nomeOriginal = "";
	private String tamanhoArquivo = "";
	private String uriFile = "";
	
	public Arquivo(){
		
	}
	
	public Arquivo(String contentType, String nomeOriginal, String tamanhoArquivo) {
		super();
		this.contentType = contentType;
		this.nomeOriginal = nomeOriginal;
		this.tamanhoArquivo = tamanhoArquivo;
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
	public String getTamanhoArquivo() {
		return tamanhoArquivo;
	}
	public void setTamanhoArquivo(String tamanhoArquivo) {
		this.tamanhoArquivo = tamanhoArquivo;
	}

	@Override
	public String toString() {
		return "Arquivo [contentType=" + contentType + ", nomeOriginal=" + nomeOriginal + ", tamanhoArquivo=" + tamanhoArquivo + ", uriFile=" + uriFile + "]";
	}
	
	
	

}
