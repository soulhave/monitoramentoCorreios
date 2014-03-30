package br.com.correios.bean;

import java.util.Date;

public class ItemCorreio {
	private String objeto;
	private String descricao;
	private String situacao;
	private String local;
	private String observacao;
	private String hash;
	private Date dataUltimaAtualizacao;
	private boolean novaAtualizacao;
	
	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public ItemCorreio() {
	}

	public ItemCorreio(String objeto, String descricao) {
		super();
		this.objeto = objeto;
		this.descricao = descricao;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public Date getDataUltimaAtualizacao() {
		return dataUltimaAtualizacao;
	}

	public void setDataUltimaAtualizacao(Date dataUltimaAtualizacao) {
		this.dataUltimaAtualizacao = dataUltimaAtualizacao;
	}

	public String getObjeto() {
		return objeto;
	}

	public void setObjeto(String objeto) {
		this.objeto = objeto;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	public boolean isNovaAtualizacao() {
		return novaAtualizacao;
	}

	public void setNovaAtualizacao(boolean novaAtualizacao) {
		this.novaAtualizacao = novaAtualizacao;
	}


}
