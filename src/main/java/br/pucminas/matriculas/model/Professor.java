package br.pucminas.matriculas.model;

import java.util.ArrayList;
import java.util.List;

public class Professor extends Usuario {

    private List<Disciplina> disciplinas = new ArrayList<>();

    public Professor(int id, String nome, String login, String senha) {
        super(id, nome, login, senha);
    }

    public List<Aluno> consultarAlunosMatriculados(Disciplina d) {
        if (d == null || !disciplinas.contains(d)) {
            return new ArrayList<>();
        }
        return d.listarAlunosMatriculados();
    }

    public void adicionarDisciplina(Disciplina d) {
        if (d == null) {
            return;
        }
        if (!disciplinas.contains(d)) {
            disciplinas.add(d);
        }
        if (!d.getProfessores().contains(this)) {
            d.getProfessores().add(this);
        }
    }

    public List<Disciplina> getDisciplinas() {
        return disciplinas;
    }
}