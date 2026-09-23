package br.pucminas.matriculas.model;

import java.util.ArrayList;
import java.util.List;

public class Professor extends Usuario {

    private List<Disciplina> disciplinas = new ArrayList<>();

    public Professor(int id, String nome, String login, String senha) {
        super(id, nome, login, senha);
    }

    public List<Aluno> consultarAlunosMatriculados(Disciplina d) {
        // TODO: retornar os alunos matriculados na disciplina informada
        throw new UnsupportedOperationException("Nao implementado");
    }

    public List<Disciplina> getDisciplinas() {
        return disciplinas;
    }
}
