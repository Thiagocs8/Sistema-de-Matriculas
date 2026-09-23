package br.pucminas.matriculas.model;

public class Curriculo {

    private String semestre;
    private Curso curso;

    public Curriculo(String semestre, Curso curso) {
        this.semestre = semestre;
        this.curso = curso;
    }

    public void gerar() {
        // TODO: implementar geracao do curriculo do semestre
        throw new UnsupportedOperationException("Nao implementado");
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
