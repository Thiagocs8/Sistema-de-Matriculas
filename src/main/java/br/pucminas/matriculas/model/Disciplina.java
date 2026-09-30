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

    public boolean matricular(Matricula m) {
        if (m == null || m.getAluno() == null || status != StatusDisciplina.EM_ABERTO) {
            return false;
        }

        long ativas = matriculas.stream().filter(Matricula::isAtiva).count();
        if (ativas >= capacidadeMaxima) {
            return false;
        }

        boolean jaMatriculado = matriculas.stream()
                .anyMatch(existing -> existing.getAluno().equals(m.getAluno()) && existing.isAtiva());
        if (jaMatriculado) {
            return false;
        }

        return matriculas.add(m);
    }

    public void cancelarMatricula(Aluno a) {
        if (a == null) return;
        
        for (Matricula m : matriculas) {
            if (m.getAluno().equals(a) && m.isAtiva()) {
                m.cancelar();
                break;
            }
        }
    }

    public void verificarSituacaoFinal() {
        long ativas = matriculas.stream().filter(Matricula::isAtiva).count();
        if (ativas >= minimoParaAtivar) {
            this.status = StatusDisciplina.ATIVA;
        } else {
            this.status = StatusDisciplina.CANCELADA;
        }
    }

    public List<Aluno> listarAlunosMatriculados() {
        List<Aluno> alunos = new ArrayList<>();
        for (Matricula m : matriculas) {
            if (m.isAtiva()) {
                alunos.add(m.getAluno());
            }
        }
        return alunos;
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