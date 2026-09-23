package br.pucminas.matriculas.model;

import java.util.Date;

import br.pucminas.matriculas.model.enums.StatusMatricula;
import br.pucminas.matriculas.model.enums.TipoMatricula;

public class Matricula {

    private TipoMatricula tipo;
    private Date dataMatricula;
    private StatusMatricula status;
    private Aluno aluno;
    private Disciplina disciplina;

    public Matricula(TipoMatricula tipo, Date dataMatricula, Aluno aluno, Disciplina disciplina) {
        this.tipo = tipo;
        this.dataMatricula = dataMatricula;
        this.aluno = aluno;
        this.disciplina = disciplina;
        this.status = StatusMatricula.ATIVA;
    }

    public void cancelar() {
        // TODO: implementar cancelamento da matricula
        throw new UnsupportedOperationException("Nao implementado");
    }

    public TipoMatricula getTipo() {
        return tipo;
    }

    public void setTipo(TipoMatricula tipo) {
        this.tipo = tipo;
    }

    public Date getDataMatricula() {
        return dataMatricula;
    }

    public void setDataMatricula(Date dataMatricula) {
        this.dataMatricula = dataMatricula;
    }

    public StatusMatricula getStatus() {
        return status;
    }

    public void setStatus(StatusMatricula status) {
        this.status = status;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }
}
