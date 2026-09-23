package br.pucminas.matriculas.model;

import java.util.ArrayList;
import java.util.List;

public class Secretaria extends Usuario {

    private List<PeriodoMatricula> periodosAdministrados = new ArrayList<>();

    public Secretaria(int id, String nome, String login, String senha) {
        super(id, nome, login, senha);
    }

    public void cadastrarCurso(Curso c) {
        // TODO: implementar cadastro de curso
        throw new UnsupportedOperationException("Nao implementado");
    }

    public void cadastrarDisciplina(Disciplina d) {
        // TODO: implementar cadastro de disciplina
        throw new UnsupportedOperationException("Nao implementado");
    }

    public Curriculo gerarCurriculo(String semestre) {
        // TODO: implementar geracao do curriculo do semestre
        throw new UnsupportedOperationException("Nao implementado");
    }

    public void encerrarPeriodoMatricula(PeriodoMatricula p) {
        // TODO: implementar encerramento do periodo de matriculas
        throw new UnsupportedOperationException("Nao implementado");
    }

    public List<PeriodoMatricula> getPeriodosAdministrados() {
        return periodosAdministrados;
    }
}
