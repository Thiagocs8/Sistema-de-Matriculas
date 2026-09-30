package br.pucminas.matriculas.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.pucminas.matriculas.model.enums.StatusPeriodo;

public class PeriodoMatricula {

    private Date dataInicio;
    private Date dataFim;
    private StatusPeriodo status;
    private List<Matricula> matriculas = new ArrayList<>();

    public PeriodoMatricula(Date dataInicio, Date dataFim) {
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.status = StatusPeriodo.ABERTO;
    }

    public void encerrar() {
        this.status = StatusPeriodo.ENCERRADO;
        processarSituacaoDisciplinas();
    }

    public void processarSituacaoDisciplinas() {
        matriculas.stream()
                .map(Matricula::getDisciplina)
                .distinct()
                .forEach(Disciplina::verificarSituacaoFinal);
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public StatusPeriodo getStatus() {
        return status;
    }

    public void setStatus(StatusPeriodo status) {
        this.status = status;
    }

    public List<Matricula> getMatriculas() {
        return matriculas;
    }
}