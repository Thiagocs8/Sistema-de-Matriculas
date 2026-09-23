package br.pucminas.matriculas.model;

import java.util.ArrayList;
import java.util.List;

import br.pucminas.matriculas.model.enums.StatusDisciplina;

public class Disciplina {

    private String codigo;
    private String nome;
    private int capacidadeMaxima = 60;
    private int minimoParaAtivar = 3;
    private StatusDisciplina status;
    private List<Professor> professores = new ArrayList<>();
    private List<Matricula> matriculas = new ArrayList<>();

    public Disciplina(String codigo, String nome) {
        this.codigo = codigo;
        this.nome = nome;
        this.status = StatusDisciplina.EM_ABERTO;
    }

    public boolean matricular(Aluno a) {
        // TODO: implementar matricula de aluno, respeitando capacidadeMaxima
        throw new UnsupportedOperationException("Nao implementado");
    }

    public void cancelarMatricula(Aluno a) {
        // TODO: implementar cancelamento de matricula do aluno
        throw new UnsupportedOperationException("Nao implementado");
    }

    public void verificarSituacaoFinal() {
        // TODO: ativar ou cancelar a disciplina conforme minimoParaAtivar
        throw new UnsupportedOperationException("Nao implementado");
    }

    public List<Aluno> listarAlunosMatriculados() {
        // TODO: retornar lista de alunos com matricula ativa na disciplina
        throw new UnsupportedOperationException("Nao implementado");
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    public void setCapacidadeMaxima(int capacidadeMaxima) {
        this.capacidadeMaxima = capacidadeMaxima;
    }

    public int getMinimoParaAtivar() {
        return minimoParaAtivar;
    }

    public void setMinimoParaAtivar(int minimoParaAtivar) {
        this.minimoParaAtivar = minimoParaAtivar;
    }

    public StatusDisciplina getStatus() {
        return status;
    }

    public void setStatus(StatusDisciplina status) {
        this.status = status;
    }

    public List<Professor> getProfessores() {
        return professores;
    }

    public List<Matricula> getMatriculas() {
        return matriculas;
    }
}
