package br.pucminas.matriculas.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.pucminas.matriculas.model.enums.StatusMatricula;
import br.pucminas.matriculas.model.enums.TipoMatricula;

public class Aluno extends Usuario {

    private String matricula;
    private List<Matricula> matriculas = new ArrayList<>();

    public Aluno(int id, String nome, String login, String senha, String matricula) {
        super(id, nome, login, senha);
        this.matricula = matricula;
    }

    public Matricula matricularEmDisciplina(Disciplina d, TipoMatricula tipo) {
        if (d == null || tipo == null) {
            return null;
        }

        long obrigatoriasAtivas = matriculas.stream()
                .filter(m -> m.getStatus() == StatusMatricula.ATIVA && m.getTipo() == TipoMatricula.OBRIGATORIA)
                .count();

        long optativasAtivas = matriculas.stream()
                .filter(m -> m.getStatus() == StatusMatricula.ATIVA && m.getTipo() == TipoMatricula.OPTATIVA)
                .count();

        if (tipo == TipoMatricula.OBRIGATORIA && obrigatoriasAtivas >= 4) {
            return null;
        }

        if (tipo == TipoMatricula.OPTATIVA && optativasAtivas >= 2) {
            return null;
        }

        boolean sucesso = d.matricular(this);
        if (!sucesso) {
            return null;
        }

        Matricula novaMatricula = new Matricula(tipo, new Date(), this, d);
        this.matriculas.add(novaMatricula);
        return novaMatricula;
    }

    public void cancelarMatricula(Matricula m) {
        if (m != null && matriculas.contains(m) && m.getStatus() == StatusMatricula.ATIVA) {
            m.cancelar();
            m.getDisciplina().cancelarMatricula(this);
        }
    }

    public List<Matricula> consultarSituacao() {
        return new ArrayList<>(this.matriculas);
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