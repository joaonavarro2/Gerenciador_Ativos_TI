package br.com.stecar.stecar_backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "departamento")
public class Departamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "escritorio_id")
    private Long escritorioId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getEscritorioId() { return escritorioId; }
    public void setEscritorioId(Long escritorioId) { this.escritorioId = escritorioId; }
}
