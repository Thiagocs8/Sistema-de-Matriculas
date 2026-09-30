package br.pucminas.matriculas.model;

import java.util.ArrayList;
import java.util.List;

public class Secretaria extends Usuario {

    private List<PeriodoMatricula> periodosAdministrados = new ArrayList<>();
    private List<Curso> cursos = new ArrayList<>();
    private List<Disciplina> disciplinas = new ArrayList<>();

    public Secretaria(int id, String nome, String login, String senha) {
        super(id, nome, login, senha);
    }

    public void cadastrarCurso(Curso c) {
        if (c != null && !cursos.contains(c)) {
            cursos.add(c);
        }
    }

    public void cadastrarDisciplina(Disciplina d) {
        if (d != null && !disciplinas.contains(d)) {
            disciplinas.add(d);
        }
    }

    public Curriculo gerarCurriculo(String semestre) {
        Curriculo curriculo = new Curriculo(semestre);
        for (Disciplina d : disciplinas) {
            curriculo.adicionarDisciplina(d);
        }
        return curriculo;
    }

    public void encerrarPeriodoMatricula(PeriodoMatricula p) {
        if (p != null) {
            p.encerrar();
            for (Disciplina d : disciplinas) {
                d.verificarSituacaoFinal();
            }
        }
    }

    public List<PeriodoMatricula> getPeriodosAdministrados() {
        return periodosAdministrados;
    }

    public List<Curso> getCursos() {
        return cursos;
    }

    public List<Disciplina> getDisciplinas() {
        return disciplinas;
    }
}