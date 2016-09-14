package br.com.nopredio.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Andersom on 13/07/2016.
 */
public class Perfil {

    @SerializedName("cod")
    @Expose
    private Long cod;
    @SerializedName("nome")
    @Expose
    private String nome;
    @SerializedName("ativo")
    @Expose
    private String ativo;

    public Long getCod() {
        return cod;
    }
    public void setCod(Long cod) {this.cod = cod;}

    public String getNome() {return nome;}
    public void setNome(String nome) {this.nome = nome;}

    public String getAtivo() {return ativo;}
    public boolean isAtivo(){return ativo.equals("Y");}
    public void setAtivo(String ativo) {this.ativo = ativo;}
}
