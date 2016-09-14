package br.com.nopredio.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Andersom on 13/07/2016.
 */
public class Usuario {

    @SerializedName("cod")
    @Expose
    private Long cod;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("senha")
    @Expose
    private String senha;
    @SerializedName("condominio")
    @Expose
    private Condominio condominio;
    @SerializedName("perfil")
    @Expose
    private Perfil perfil;
    @SerializedName("facebookId")
    @Expose
    private String facebookId;
    @SerializedName("nome")
    @Expose
    private String nome;
    @SerializedName("telefone")
    @Expose
    private String telefone;
    @SerializedName("apartamento")
    @Expose
    private String apartamento;
    @SerializedName("bloco")
    @Expose
    private String bloco;
    @SerializedName("ativo")
    @Expose
    private String ativo;

    public Long getCod() {
        return cod;
    }
    public void setCod(Long cod) {this.cod = cod;}

    public Condominio getCondominio() {return condominio;}
    public void setCondominio(Condominio condominio) {this.condominio = condominio;}

    public Perfil getPerfil() {return perfil;}
    public void setPerfil(Perfil perfil) {this.perfil = perfil;}

    public String getFacebookId() {return facebookId;}
    public void setFacebookId(String facebookId) {this.facebookId = facebookId;}

    public String getNome() {return nome;}
    public void setNome(String nome) {this.nome = nome;}

    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}

    public String getTelefone() {return telefone;}
    public void setTelefone(String telefone) {this.telefone = telefone;}

    public String getApartamento() {return apartamento;}
    public void setApartamento(String apartamento) {this.apartamento = apartamento;}

    public String getBloco() {return bloco;}
    public void setBloco(String bloco) {this.bloco = bloco;}

    public String getSenha() {return senha;}
    public void setSenha(String senha) {this.senha = senha;}

    public String getAtivo() {return ativo;}
    public boolean isAtivo(){return ativo.equals("Y");}
    public void setAtivo(String ativo) {this.ativo = ativo;}
}
