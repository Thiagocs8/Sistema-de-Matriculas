package br.pucminas.matriculas.model;

public class Curriculo {

    private String semestre;
    private Curso curso;

    public Curriculo(String semestre, Curso curso) {
        this.semestre = semestre;
        this.curso = curso;
    }

    public void gerar() {
        if (curso != null) {
            System.out.println("Currículo do curso " + curso.getNome() + " para o semestre " + semestre + ":");
            for (Disciplina d : curso.getDisciplinas()) {
                System.out.println("- " + d.getCodigo() + " : " + d.getNome());
            }
        }
    }

    public String getSemestre() {
        return semestre;
    }

    public void setSemestre(String semestre) {
        this.semestre = semestre;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }
}