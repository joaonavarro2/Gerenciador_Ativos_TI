package br.com.stecar.stecar_backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "escritorio")
public class Escritorio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, length = 100)
    private String cidade;

    @Column(length = 2)
    private String estado;

    @Column(nullable = false, length = 10)
    private String cep;

    @Column(nullable = false, length = 255)
    private String endereco;

    @Column(length = 20)
    private String telefone;

    @Column(length = 150)
    private String email;

    @Column(name = "gestor_nome", length = 150)
    private String gestor;

    @Column(nullable = false, length = 20)
    private String status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getGestor() { return gestor; }
    public void setGestor(String gestor) { this.gestor = gestor; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
