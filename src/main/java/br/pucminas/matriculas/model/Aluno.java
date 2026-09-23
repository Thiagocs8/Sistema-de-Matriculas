package br.pucminas.matriculas.model;

import java.util.ArrayList;
import java.util.List;

import br.pucminas.matriculas.model.enums.TipoMatricula;

public class Aluno extends Usuario {

    private String matricula;
    private List<Matricula> matriculas = new ArrayList<>();

    public Aluno(int id, String nome, String login, String senha, String matricula) {
        super(id, nome, login, senha);
        this.matricula = matricula;
    }

    public Matricula matricularEmDisciplina(Disciplina d, TipoMatricula tipo) {
        // TODO: implementar matricula do aluno na disciplina, respeitando RN01
        throw new UnsupportedOperationException("Nao implementado");
    }

    public void cancelarMatricula(Matricula m) {
        // TODO: implementar cancelamento de matricula do aluno
        throw new UnsupportedOperationException("Nao implementado");
    }

    public List<Matricula> consultarSituacao() {
        // TODO: retornar as matriculas do aluno no semestre
        throw new UnsupportedOperationException("Nao implementado");
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public List<Matricula> getMatriculas() {
        return matriculas;
    }
}
