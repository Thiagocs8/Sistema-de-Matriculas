package br.pucminas.matriculas.model;

import java.util.ArrayList;
import java.util.List;

public class Curso {

    private String nome;
    private int numeroCreditos;
    private List<Disciplina> disciplinas = new ArrayList<>();

    public Curso(String nome, int numeroCreditos) {
        this.nome = nome;
        this.numeroCreditos = numeroCreditos;
    }

    public void adicionarDisciplina(Disciplina d) {
        // TODO: implementar adicao de disciplina ao curso
        throw new UnsupportedOperationException("Nao implementado");
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getNumeroCreditos() {
        return numeroCreditos;
    }

    public void setNumeroCreditos(int numeroCreditos) {
        this.numeroCreditos = numeroCreditos;
    }

    public List<Disciplina> getDisciplinas() {
        return disciplinas;
    }
}
