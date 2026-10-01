package br.pucminas.matriculas.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.pucminas.matriculas.external.SistemaCobranca;
import br.pucminas.matriculas.model.Aluno;
import br.pucminas.matriculas.model.Curriculo;
import br.pucminas.matriculas.model.Curso;
import br.pucminas.matriculas.model.Disciplina;
import br.pucminas.matriculas.model.Matricula;
import br.pucminas.matriculas.model.PeriodoMatricula;
import br.pucminas.matriculas.model.Professor;
import br.pucminas.matriculas.model.Secretaria;
import br.pucminas.matriculas.model.Usuario;
import br.pucminas.matriculas.model.enums.StatusDisciplina;
import br.pucminas.matriculas.model.enums.StatusPeriodo;
import br.pucminas.matriculas.model.enums.TipoMatricula;

/**
 * Fachada do sistema: guarda os cadastros, valida as regras de negocio que
 * dependem de mais de um objeto (login, periodo de matriculas, cobranca) e
 * delega o restante as classes do modelo.
 */
public class SistemaMatriculas {

    private static final String IDENTIFICADOR_VALIDO = "[A-Za-z0-9._-]+";

    private final List<Usuario> usuarios = new ArrayList<>();
    private final List<Curso> cursos = new ArrayList<>();
    private final List<Disciplina> disciplinas = new ArrayList<>();
    private final List<Curriculo> curriculos = new ArrayList<>();
    private final List<PeriodoMatricula> periodos = new ArrayList<>();
    private final SistemaCobranca sistemaCobranca;

    public SistemaMatriculas(SistemaCobranca sistemaCobranca) {
        this.sistemaCobranca = sistemaCobranca;
    }

    // ---------- Login (RN05) ----------

    public Usuario login(String login, String senha) {
        Usuario u = buscarUsuario(login);
        if (u != null && u.autenticar(senha)) {
            return u;
        }
        return null;
    }

    // ---------- Cadastros mantidos pela secretaria ----------

    public Secretaria cadastrarSecretaria(String nome, String login, String senha) {
        validarNovoUsuario(nome, login, senha);
        Secretaria s = new Secretaria(proximoId(), nome, login, senha);
        usuarios.add(s);
        atualizarSecretarias();
        return s;
    }

    public Professor cadastrarProfessor(String nome, String login, String senha) {
        validarNovoUsuario(nome, login, senha);
        Professor p = new Professor(proximoId(), nome, login, senha);
        usuarios.add(p);
        return p;
    }

    public Aluno cadastrarAluno(String nome, String login, String senha, String matricula) {
        validarNovoUsuario(nome, login, senha);
        if (matricula == null || !matricula.matches(IDENTIFICADOR_VALIDO)) {
            throw new RegraNegocioException("Número de matrícula inválido (use apenas letras, números, '.', '_' ou '-').");
        }
        for (Aluno a : getAlunos()) {
            if (a.getMatricula().equalsIgnoreCase(matricula)) {
                throw new RegraNegocioException("Já existe um aluno com a matrícula " + matricula + ".");
            }
        }
        Aluno a = new Aluno(proximoId(), nome, login, senha, matricula);
        usuarios.add(a);
        return a;
    }

    public Curso cadastrarCurso(String nome, int numeroCreditos) {
        if (nome == null || nome.isBlank()) {
            throw new RegraNegocioException("O nome do curso é obrigatório.");
        }
        if (numeroCreditos <= 0) {
            throw new RegraNegocioException("O número de créditos deve ser maior que zero.");
        }
        if (buscarCurso(nome) != null) {
            throw new RegraNegocioException("Já existe um curso com o nome " + nome + ".");
        }
        Curso c = new Curso(nome.trim(), numeroCreditos);
        cursos.add(c);
        atualizarSecretarias();
        return c;
    }

    public Disciplina cadastrarDisciplina(String codigo, String nome) {
        if (codigo == null || !codigo.matches(IDENTIFICADOR_VALIDO)) {
            throw new RegraNegocioException("Código inválido (use apenas letras, números, '.', '_' ou '-').");
        }
        if (nome == null || nome.isBlank()) {
            throw new RegraNegocioException("O nome da disciplina é obrigatório.");
        }
        if (buscarDisciplina(codigo) != null) {
            throw new RegraNegocioException("Já existe uma disciplina com o código " + codigo + ".");
        }
        Disciplina d = new Disciplina(codigo, nome.trim());
        disciplinas.add(d);
        atualizarSecretarias();
        return d;
    }

    public void adicionarDisciplinaAoCurso(Curso curso, Disciplina disciplina) {
        if (curso.getDisciplinas().contains(disciplina)) {
            throw new RegraNegocioException("A disciplina já faz parte do curso.");
        }
        curso.adicionarDisciplina(disciplina);
    }

    public void atribuirProfessor(Professor professor, Disciplina disciplina) {
        if (professor.getDisciplinas().contains(disciplina)) {
            throw new RegraNegocioException("O professor já leciona essa disciplina.");
        }
        professor.adicionarDisciplina(disciplina);
    }

    public Curriculo gerarCurriculo(Secretaria secretaria, String semestre, Curso curso) {
        if (semestre == null || semestre.isBlank()) {
            throw new RegraNegocioException("O semestre é obrigatório.");
        }
        for (Curriculo existente : curriculos) {
            if (existente.getCurso() == curso && existente.getSemestre().equalsIgnoreCase(semestre.trim())) {
                return existente;
            }
        }
        Curriculo c = secretaria.gerarCurriculo(semestre.trim(), curso);
        curriculos.add(c);
        return c;
    }

    // ---------- Periodo de matriculas ----------

    public PeriodoMatricula abrirPeriodo(Secretaria secretaria, Date inicio, Date fim) {
        PeriodoMatricula atual = getPeriodoAtual();
        if (atual != null && atual.getStatus() == StatusPeriodo.ABERTO) {
            throw new RegraNegocioException("Já existe um período de matrículas aberto. Encerre-o antes de abrir outro.");
        }
        if (fim.before(inicio)) {
            throw new RegraNegocioException("A data de fim não pode ser anterior à data de início.");
        }
        if (fim.before(new Date())) {
            throw new RegraNegocioException("A data de fim já passou.");
        }

        // Novo semestre: as matriculas do periodo anterior ficam guardadas
        // apenas no periodo encerrado e as disciplinas voltam a aceitar inscricoes.
        for (Aluno a : getAlunos()) {
            a.getMatriculas().clear();
        }
        for (Disciplina d : disciplinas) {
            d.getMatriculas().clear();
            d.setStatus(StatusDisciplina.EM_ABERTO);
        }

        PeriodoMatricula novo = secretaria.abrirPeriodoMatricula(inicio, fim);
        periodos.add(novo);
        atualizarSecretarias();
        return novo;
    }

    public PeriodoMatricula encerrarPeriodo(Secretaria secretaria) {
        PeriodoMatricula atual = getPeriodoAtual();
        if (atual == null || atual.getStatus() != StatusPeriodo.ABERTO) {
            throw new RegraNegocioException("Não há período de matrículas aberto para encerrar.");
        }
        secretaria.encerrarPeriodoMatricula(atual);
        return atual;
    }

    /**
     * Encerra o periodo cuja data de fim ja passou sem que a secretaria o
     * tenha encerrado manualmente. Retorna true se algum periodo foi encerrado.
     */
    public boolean encerrarPeriodoVencido() {
        PeriodoMatricula atual = getPeriodoAtual();
        if (atual == null || atual.getStatus() != StatusPeriodo.ABERTO || !new Date().after(atual.getDataFim())) {
            return false;
        }
        atual.encerrar();
        for (Disciplina d : disciplinas) {
            d.verificarSituacaoFinal();
        }
        return true;
    }

    public PeriodoMatricula getPeriodoAtual() {
        return periodos.isEmpty() ? null : periodos.get(periodos.size() - 1);
    }

    // ---------- Matriculas ----------

    public Matricula matricular(Aluno aluno, Disciplina disciplina, TipoMatricula tipo) {
        PeriodoMatricula periodo = exigirPeriodoAberto();

        if (disciplina.getStatus() != StatusDisciplina.EM_ABERTO) {
            throw new RegraNegocioException("A disciplina não está aberta para matrículas.");
        }
        List<Aluno> inscritos = disciplina.listarAlunosMatriculados();
        if (inscritos.contains(aluno)) {
            throw new RegraNegocioException("Você já está matriculado nessa disciplina.");
        }
        if (inscritos.size() >= disciplina.getCapacidadeMaxima()) {
            throw new RegraNegocioException("As inscrições dessa disciplina estão encerradas: o limite de "
                    + disciplina.getCapacidadeMaxima() + " alunos foi atingido.");
        }
        long doMesmoTipo = aluno.getMatriculas().stream()
                .filter(m -> m.isAtiva() && m.getTipo() == tipo)
                .count();
        if (tipo == TipoMatricula.OBRIGATORIA && doMesmoTipo >= Aluno.MAX_OBRIGATORIAS) {
            throw new RegraNegocioException("Limite de " + Aluno.MAX_OBRIGATORIAS + " disciplinas obrigatórias atingido.");
        }
        if (tipo == TipoMatricula.OPTATIVA && doMesmoTipo >= Aluno.MAX_OPTATIVAS) {
            throw new RegraNegocioException("Limite de " + Aluno.MAX_OPTATIVAS + " disciplinas optativas atingido.");
        }

        Matricula m = aluno.matricularEmDisciplina(disciplina, tipo);
        if (m == null) {
            throw new RegraNegocioException("Não foi possível realizar a matrícula.");
        }
        periodo.getMatriculas().add(m);
        sistemaCobranca.notificarCobranca(aluno, disciplina);
        return m;
    }

    public void cancelarMatricula(Aluno aluno, Matricula matricula) {
        exigirPeriodoAberto();
        if (!aluno.getMatriculas().contains(matricula) || !matricula.isAtiva()) {
            throw new RegraNegocioException("Matrícula não encontrada ou já cancelada.");
        }
        aluno.cancelarMatricula(matricula);
    }

    private PeriodoMatricula exigirPeriodoAberto() {
        PeriodoMatricula atual = getPeriodoAtual();
        if (atual == null || !atual.estaAberto()) {
            throw new RegraNegocioException("Não há período de matrículas aberto no momento.");
        }
        return atual;
    }

    // ---------- Consultas ----------

    public Usuario buscarUsuario(String login) {
        for (Usuario u : usuarios) {
            if (u.getLogin().equalsIgnoreCase(login)) {
                return u;
            }
        }
        return null;
    }

    public Disciplina buscarDisciplina(String codigo) {
        for (Disciplina d : disciplinas) {
            if (d.getCodigo().equalsIgnoreCase(codigo)) {
                return d;
            }
        }
        return null;
    }

    public Curso buscarCurso(String nome) {
        for (Curso c : cursos) {
            if (c.getNome().equalsIgnoreCase(nome == null ? null : nome.trim())) {
                return c;
            }
        }
        return null;
    }

    public List<Aluno> getAlunos() {
        List<Aluno> alunos = new ArrayList<>();
        for (Usuario u : usuarios) {
            if (u instanceof Aluno) {
                alunos.add((Aluno) u);
            }
        }
        return alunos;
    }

    public List<Professor> getProfessores() {
        List<Professor> professores = new ArrayList<>();
        for (Usuario u : usuarios) {
            if (u instanceof Professor) {
                professores.add((Professor) u);
            }
        }
        return professores;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public List<Curso> getCursos() {
        return cursos;
    }

    public List<Disciplina> getDisciplinas() {
        return disciplinas;
    }

    public List<Curriculo> getCurriculos() {
        return curriculos;
    }

    public List<PeriodoMatricula> getPeriodos() {
        return periodos;
    }

    /**
     * A secretaria e um unico setor: todos os usuarios de secretaria enxergam
     * os mesmos cursos, disciplinas e periodos.
     */
    public void atualizarSecretarias() {
        for (Usuario u : usuarios) {
            if (!(u instanceof Secretaria)) {
                continue;
            }
            Secretaria s = (Secretaria) u;
            for (Curso c : cursos) {
                s.cadastrarCurso(c);
            }
            for (Disciplina d : disciplinas) {
                s.cadastrarDisciplina(d);
            }
            for (PeriodoMatricula p : periodos) {
                if (!s.getPeriodosAdministrados().contains(p)) {
                    s.getPeriodosAdministrados().add(p);
                }
            }
        }
    }

    private void validarNovoUsuario(String nome, String login, String senha) {
        if (nome == null || nome.isBlank()) {
            throw new RegraNegocioException("O nome é obrigatório.");
        }
        if (login == null || !login.matches(IDENTIFICADOR_VALIDO)) {
            throw new RegraNegocioException("Login inválido (use apenas letras, números, '.', '_' ou '-').");
        }
        if (senha == null || senha.isEmpty()) {
            throw new RegraNegocioException("A senha é obrigatória.");
        }
        if (buscarUsuario(login) != null) {
            throw new RegraNegocioException("Já existe um usuário com o login " + login + ".");
        }
    }

    private int proximoId() {
        int maior = 0;
        for (Usuario u : usuarios) {
            maior = Math.max(maior, u.getId());
        }
        return maior + 1;
    }
}
