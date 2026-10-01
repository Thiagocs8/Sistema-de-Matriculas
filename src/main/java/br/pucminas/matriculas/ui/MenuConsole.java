package br.pucminas.matriculas.ui;

import java.io.Console;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import br.pucminas.matriculas.model.Aluno;
import br.pucminas.matriculas.model.Curriculo;
import br.pucminas.matriculas.model.Curso;
import br.pucminas.matriculas.model.Disciplina;
import br.pucminas.matriculas.model.Matricula;
import br.pucminas.matriculas.model.PeriodoMatricula;
import br.pucminas.matriculas.model.Professor;
import br.pucminas.matriculas.model.Secretaria;
import br.pucminas.matriculas.model.Usuario;
import br.pucminas.matriculas.model.enums.StatusPeriodo;
import br.pucminas.matriculas.model.enums.TipoMatricula;
import br.pucminas.matriculas.persistence.RepositorioArquivos;
import br.pucminas.matriculas.service.RegraNegocioException;
import br.pucminas.matriculas.service.SistemaMatriculas;

/**
 * Interface em linha de comando: login e um menu para cada perfil de usuario.
 */
public class MenuConsole {

    private static final String FORMATO_DATA = "dd/MM/yyyy";
    private static final long UM_DIA_MS = 24L * 60 * 60 * 1000;

    /** Sinaliza o fim da entrada padrao, para encerrar o programa sem erro. */
    private static class EntradaEncerradaException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

    private final SistemaMatriculas sistema;
    private final RepositorioArquivos repositorio;
    private final Scanner entrada;

    public MenuConsole(SistemaMatriculas sistema, RepositorioArquivos repositorio) {
        this.sistema = sistema;
        this.repositorio = repositorio;
        Console console = System.console();
        Charset charset = console != null ? console.charset() : Charset.defaultCharset();
        this.entrada = new Scanner(System.in, charset);
    }

    public void iniciar() {
        try {
            while (true) {
                System.out.println();
                System.out.println("===== SISTEMA DE MATRÍCULAS =====");
                System.out.println("1 - Entrar");
                System.out.println("0 - Sair");
                String opcao = ler("Opção: ");
                if (opcao.equals("0")) {
                    break;
                }
                if (opcao.equals("1")) {
                    entrar();
                } else {
                    System.out.println("Opção inválida.");
                }
            }
        } catch (EntradaEncerradaException e) {
            System.out.println();
        }
        System.out.println("Até logo!");
    }

    private void entrar() {
        String login = ler("Login: ");
        String senha = lerSenha("Senha: ");
        Usuario usuario = sistema.login(login, senha);
        if (usuario == null) {
            System.out.println("Login ou senha inválidos.");
            return;
        }
        if (sistema.encerrarPeriodoVencido()) {
            repositorio.salvar(sistema);
        }
        System.out.println("Bem-vindo(a), " + usuario.getNome() + "!");
        if (usuario instanceof Secretaria) {
            menuSecretaria((Secretaria) usuario);
        } else if (usuario instanceof Professor) {
            menuProfessor((Professor) usuario);
        } else if (usuario instanceof Aluno) {
            menuAluno((Aluno) usuario);
        }
    }

    // ---------- Secretaria ----------

    private void menuSecretaria(Secretaria secretaria) {
        while (true) {
            System.out.println();
            System.out.println("----- Secretaria (" + descreverPeriodo() + ") -----");
            System.out.println(" 1 - Cadastrar curso");
            System.out.println(" 2 - Cadastrar disciplina");
            System.out.println(" 3 - Adicionar disciplina a um curso");
            System.out.println(" 4 - Cadastrar professor");
            System.out.println(" 5 - Cadastrar aluno");
            System.out.println(" 6 - Cadastrar usuário da secretaria");
            System.out.println(" 7 - Atribuir professor a uma disciplina");
            System.out.println(" 8 - Gerar currículo do semestre");
            System.out.println(" 9 - Abrir período de matrículas");
            System.out.println("10 - Encerrar período de matrículas");
            System.out.println("11 - Listar cursos e disciplinas");
            System.out.println("12 - Listar usuários");
            System.out.println(" 0 - Sair da conta");
            String opcao = ler("Opção: ");
            if (opcao.equals("0")) {
                return;
            }
            try {
                switch (opcao) {
                    case "1":
                        Curso novoCurso = sistema.cadastrarCurso(ler("Nome do curso: "), lerInteiro("Número de créditos: "));
                        sucesso("Curso " + novoCurso.getNome() + " cadastrado.");
                        break;
                    case "2":
                        Disciplina nova = sistema.cadastrarDisciplina(ler("Código: "), ler("Nome da disciplina: "));
                        sucesso("Disciplina " + nova.getCodigo() + " cadastrada.");
                        break;
                    case "3":
                        Curso curso = escolherCurso();
                        Disciplina disciplina = escolherDisciplina(sistema.getDisciplinas());
                        sistema.adicionarDisciplinaAoCurso(curso, disciplina);
                        sucesso(disciplina.getCodigo() + " adicionada ao curso " + curso.getNome() + ".");
                        break;
                    case "4":
                        Professor professor = sistema.cadastrarProfessor(ler("Nome: "), ler("Login: "), lerSenha("Senha: "));
                        sucesso("Professor " + professor.getNome() + " cadastrado.");
                        break;
                    case "5":
                        Aluno aluno = sistema.cadastrarAluno(ler("Nome: "), ler("Login: "), lerSenha("Senha: "),
                                ler("Número de matrícula: "));
                        sucesso("Aluno " + aluno.getNome() + " cadastrado.");
                        break;
                    case "6":
                        Secretaria outra = sistema.cadastrarSecretaria(ler("Nome: "), ler("Login: "), lerSenha("Senha: "));
                        sucesso("Usuário de secretaria " + outra.getNome() + " cadastrado.");
                        break;
                    case "7":
                        Professor escolhido = escolherProfessor();
                        Disciplina alvo = escolherDisciplina(sistema.getDisciplinas());
                        sistema.atribuirProfessor(escolhido, alvo);
                        sucesso(escolhido.getNome() + " agora leciona " + alvo.getCodigo() + ".");
                        break;
                    case "8":
                        Curso cursoDoCurriculo = escolherCurso();
                        Curriculo curriculo = sistema.gerarCurriculo(secretaria, ler("Semestre (ex.: 2026/2): "), cursoDoCurriculo);
                        repositorio.salvar(sistema);
                        curriculo.gerar();
                        break;
                    case "9":
                        Date inicio = lerData("Data de início (" + FORMATO_DATA + "): ");
                        Date fim = new Date(lerData("Data de fim (" + FORMATO_DATA + "): ").getTime() + UM_DIA_MS - 1);
                        sistema.abrirPeriodo(secretaria, inicio, fim);
                        sucesso("Período de matrículas aberto.");
                        break;
                    case "10":
                        sistema.encerrarPeriodo(secretaria);
                        sucesso("Período de matrículas encerrado. Situação das disciplinas:");
                        for (Disciplina d : sistema.getDisciplinas()) {
                            System.out.println(" - " + d.getCodigo() + " " + d.getNome() + ": " + d.getStatus()
                                    + " (" + d.listarAlunosMatriculados().size() + " inscritos)");
                        }
                        break;
                    case "11":
                        listarCursosEDisciplinas();
                        break;
                    case "12":
                        listarUsuarios();
                        break;
                    default:
                        System.out.println("Opção inválida.");
                }
            } catch (RegraNegocioException e) {
                System.out.println("[ERRO] " + e.getMessage());
            }
        }
    }

    private void listarCursosEDisciplinas() {
        System.out.println("Cursos:");
        if (sistema.getCursos().isEmpty()) {
            System.out.println(" (nenhum curso cadastrado)");
        }
        for (Curso c : sistema.getCursos()) {
            System.out.println(" - " + c.getNome() + " (" + c.getNumeroCreditos() + " créditos)");
            for (Disciplina d : c.getDisciplinas()) {
                System.out.println("     " + d.getCodigo() + " " + d.getNome());
            }
        }
        System.out.println("Disciplinas:");
        if (sistema.getDisciplinas().isEmpty()) {
            System.out.println(" (nenhuma disciplina cadastrada)");
        }
        for (Disciplina d : sistema.getDisciplinas()) {
            System.out.println(" - " + descrever(d));
        }
    }

    private void listarUsuarios() {
        for (Usuario u : sistema.getUsuarios()) {
            String perfil = u instanceof Aluno ? "Aluno (matrícula " + ((Aluno) u).getMatricula() + ")"
                    : u instanceof Professor ? "Professor" : "Secretaria";
            System.out.println(" - " + u.getNome() + " | login: " + u.getLogin() + " | " + perfil);
        }
    }

    // ---------- Professor ----------

    private void menuProfessor(Professor professor) {
        while (true) {
            System.out.println();
            System.out.println("----- Professor -----");
            System.out.println("1 - Minhas disciplinas");
            System.out.println("2 - Consultar alunos matriculados em uma disciplina");
            System.out.println("0 - Sair da conta");
            String opcao = ler("Opção: ");
            if (opcao.equals("0")) {
                return;
            }
            try {
                switch (opcao) {
                    case "1":
                        if (professor.getDisciplinas().isEmpty()) {
                            System.out.println("Você não leciona nenhuma disciplina.");
                        }
                        for (Disciplina d : professor.getDisciplinas()) {
                            System.out.println(" - " + descrever(d));
                        }
                        break;
                    case "2":
                        Disciplina d = escolherDisciplina(professor.getDisciplinas());
                        List<Aluno> alunos = professor.consultarAlunosMatriculados(d);
                        System.out.println("Alunos matriculados em " + d.getCodigo() + " " + d.getNome() + ": " + alunos.size());
                        for (Aluno a : alunos) {
                            System.out.println(" - " + a.getNome() + " (matrícula " + a.getMatricula() + ")");
                        }
                        break;
                    default:
                        System.out.println("Opção inválida.");
                }
            } catch (RegraNegocioException e) {
                System.out.println("[ERRO] " + e.getMessage());
            }
        }
    }

    // ---------- Aluno ----------

    private void menuAluno(Aluno aluno) {
        while (true) {
            System.out.println();
            System.out.println("----- Aluno (" + descreverPeriodo() + ") -----");
            System.out.println("1 - Ver disciplinas ofertadas");
            System.out.println("2 - Ver currículos do semestre");
            System.out.println("3 - Matricular em disciplina obrigatória (1ª opção)");
            System.out.println("4 - Matricular em disciplina optativa (alternativa)");
            System.out.println("5 - Cancelar matrícula");
            System.out.println("6 - Consultar minhas matrículas");
            System.out.println("0 - Sair da conta");
            String opcao = ler("Opção: ");
            if (opcao.equals("0")) {
                return;
            }
            try {
                switch (opcao) {
                    case "1":
                        if (sistema.getDisciplinas().isEmpty()) {
                            System.out.println("Nenhuma disciplina cadastrada.");
                        }
                        for (Disciplina d : sistema.getDisciplinas()) {
                            System.out.println(" - " + descrever(d));
                        }
                        break;
                    case "2":
                        if (sistema.getCurriculos().isEmpty()) {
                            System.out.println("Nenhum currículo gerado pela secretaria.");
                        }
                        for (Curriculo c : sistema.getCurriculos()) {
                            c.gerar();
                        }
                        break;
                    case "3":
                        matricular(aluno, TipoMatricula.OBRIGATORIA);
                        break;
                    case "4":
                        matricular(aluno, TipoMatricula.OPTATIVA);
                        break;
                    case "5":
                        cancelar(aluno);
                        break;
                    case "6":
                        consultarSituacao(aluno);
                        break;
                    default:
                        System.out.println("Opção inválida.");
                }
            } catch (RegraNegocioException e) {
                System.out.println("[ERRO] " + e.getMessage());
            }
        }
    }

    private void matricular(Aluno aluno, TipoMatricula tipo) {
        Disciplina d = escolherDisciplina(sistema.getDisciplinas());
        sistema.matricular(aluno, d, tipo);
        sucesso("Matrícula em " + d.getCodigo() + " " + d.getNome() + " realizada.");
    }

    private void cancelar(Aluno aluno) {
        List<Matricula> ativas = new ArrayList<>();
        for (Matricula m : aluno.consultarSituacao()) {
            if (m.isAtiva()) {
                ativas.add(m);
            }
        }
        if (ativas.isEmpty()) {
            throw new RegraNegocioException("Você não possui matrículas ativas.");
        }
        for (int i = 0; i < ativas.size(); i++) {
            Matricula m = ativas.get(i);
            System.out.println((i + 1) + " - " + m.getDisciplina().getCodigo() + " " + m.getDisciplina().getNome()
                    + " (" + m.getTipo() + ")");
        }
        int escolha = lerInteiro("Número da matrícula a cancelar: ");
        if (escolha < 1 || escolha > ativas.size()) {
            throw new RegraNegocioException("Opção inválida.");
        }
        Matricula m = ativas.get(escolha - 1);
        sistema.cancelarMatricula(aluno, m);
        sucesso("Matrícula em " + m.getDisciplina().getCodigo() + " cancelada.");
    }

    private void consultarSituacao(Aluno aluno) {
        List<Matricula> matriculas = aluno.consultarSituacao();
        if (matriculas.isEmpty()) {
            System.out.println("Você não possui matrículas neste período.");
            return;
        }
        SimpleDateFormat formato = new SimpleDateFormat(FORMATO_DATA);
        for (Matricula m : matriculas) {
            System.out.println(" - " + m.getDisciplina().getCodigo() + " " + m.getDisciplina().getNome()
                    + " | " + m.getTipo() + " | matrícula " + m.getStatus()
                    + " | disciplina " + m.getDisciplina().getStatus()
                    + " | feita em " + formato.format(m.getDataMatricula()));
        }
    }

    // ---------- Auxiliares ----------

    private void sucesso(String mensagem) {
        repositorio.salvar(sistema);
        System.out.println("[OK] " + mensagem);
    }

    private String descrever(Disciplina d) {
        List<String> professores = new ArrayList<>();
        for (Professor p : d.getProfessores()) {
            professores.add(p.getNome());
        }
        return d.getCodigo() + " " + d.getNome() + " | " + d.getStatus()
                + " | " + d.listarAlunosMatriculados().size() + "/" + d.getCapacidadeMaxima() + " inscritos"
                + " | prof.: " + (professores.isEmpty() ? "-" : String.join(", ", professores));
    }

    private String descreverPeriodo() {
        PeriodoMatricula p = sistema.getPeriodoAtual();
        if (p == null) {
            return "nenhum período de matrículas";
        }
        SimpleDateFormat formato = new SimpleDateFormat(FORMATO_DATA);
        String datas = formato.format(p.getDataInicio()) + " a " + formato.format(p.getDataFim());
        if (p.estaAberto()) {
            return "período aberto: " + datas;
        }
        if (p.getStatus() == StatusPeriodo.ABERTO) {
            return "período ainda não iniciado: " + datas;
        }
        return "período encerrado: " + datas;
    }

    private Curso escolherCurso() {
        if (sistema.getCursos().isEmpty()) {
            throw new RegraNegocioException("Nenhum curso cadastrado.");
        }
        for (Curso c : sistema.getCursos()) {
            System.out.println(" - " + c.getNome());
        }
        Curso curso = sistema.buscarCurso(ler("Nome do curso: "));
        if (curso == null) {
            throw new RegraNegocioException("Curso não encontrado.");
        }
        return curso;
    }

    private Disciplina escolherDisciplina(List<Disciplina> opcoes) {
        if (opcoes.isEmpty()) {
            throw new RegraNegocioException("Nenhuma disciplina disponível.");
        }
        for (Disciplina d : opcoes) {
            System.out.println(" - " + descrever(d));
        }
        Disciplina d = sistema.buscarDisciplina(ler("Código da disciplina: "));
        if (d == null || !opcoes.contains(d)) {
            throw new RegraNegocioException("Disciplina não encontrada.");
        }
        return d;
    }

    private Professor escolherProfessor() {
        List<Professor> professores = sistema.getProfessores();
        if (professores.isEmpty()) {
            throw new RegraNegocioException("Nenhum professor cadastrado.");
        }
        for (Professor p : professores) {
            System.out.println(" - " + p.getLogin() + " (" + p.getNome() + ")");
        }
        Usuario u = sistema.buscarUsuario(ler("Login do professor: "));
        if (!(u instanceof Professor)) {
            throw new RegraNegocioException("Professor não encontrado.");
        }
        return (Professor) u;
    }

    private String ler(String rotulo) {
        System.out.print(rotulo);
        if (!entrada.hasNextLine()) {
            throw new EntradaEncerradaException();
        }
        return entrada.nextLine().trim();
    }

    // A senha e lida pelo mesmo Scanner das demais entradas (fica visivel):
    // misturar Console.readPassword com o Scanner perde linhas da entrada.
    private String lerSenha(String rotulo) {
        return ler(rotulo);
    }

    private int lerInteiro(String rotulo) {
        try {
            return Integer.parseInt(ler(rotulo));
        } catch (NumberFormatException e) {
            throw new RegraNegocioException("Informe um número inteiro.");
        }
    }

    private Date lerData(String rotulo) {
        SimpleDateFormat formato = new SimpleDateFormat(FORMATO_DATA);
        formato.setLenient(false);
        try {
            return formato.parse(ler(rotulo));
        } catch (ParseException e) {
            throw new RegraNegocioException("Data inválida. Use o formato " + FORMATO_DATA + ".");
        }
    }
}
