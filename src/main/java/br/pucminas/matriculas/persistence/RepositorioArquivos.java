package br.pucminas.matriculas.persistence;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
import br.pucminas.matriculas.model.enums.StatusMatricula;
import br.pucminas.matriculas.model.enums.StatusPeriodo;
import br.pucminas.matriculas.model.enums.TipoMatricula;
import br.pucminas.matriculas.service.SistemaMatriculas;

/**
 * Persistencia em arquivos texto (um por tipo de dado, campos separados por
 * ';'). As referencias entre objetos sao gravadas pelas chaves: login do
 * usuario, codigo da disciplina e nome do curso.
 */
public class RepositorioArquivos {

    private static final String SEPARADOR = ";";
    private static final String SEPARADOR_LISTA = ",";

    private final Path diretorio;

    public RepositorioArquivos(Path diretorio) {
        this.diretorio = diretorio;
    }

    // ---------- Gravacao ----------

    public void salvar(SistemaMatriculas sistema) {
        List<String> usuarios = new ArrayList<>();
        for (Usuario u : sistema.getUsuarios()) {
            String tipo = u instanceof Aluno ? "ALUNO" : u instanceof Professor ? "PROFESSOR" : "SECRETARIA";
            String matricula = u instanceof Aluno ? ((Aluno) u).getMatricula() : "";
            usuarios.add(linha(tipo, u.getId(), u.getNome(), u.getLogin(), u.getSenha(), matricula));
        }

        List<String> disciplinas = new ArrayList<>();
        for (Disciplina d : sistema.getDisciplinas()) {
            String professores = d.getProfessores().stream()
                    .map(Usuario::getLogin)
                    .collect(Collectors.joining(SEPARADOR_LISTA));
            disciplinas.add(linha(d.getCodigo(), d.getNome(), d.getCapacidadeMaxima(),
                    d.getMinimoParaAtivar(), d.getStatus(), professores));
        }

        List<String> cursos = new ArrayList<>();
        for (Curso c : sistema.getCursos()) {
            String codigos = c.getDisciplinas().stream()
                    .map(Disciplina::getCodigo)
                    .collect(Collectors.joining(SEPARADOR_LISTA));
            cursos.add(linha(c.getNome(), c.getNumeroCreditos(), codigos));
        }

        List<String> curriculos = new ArrayList<>();
        for (Curriculo c : sistema.getCurriculos()) {
            curriculos.add(linha(c.getSemestre(), c.getCurso().getNome()));
        }

        List<String> periodos = new ArrayList<>();
        List<String> matriculas = new ArrayList<>();
        for (int i = 0; i < sistema.getPeriodos().size(); i++) {
            PeriodoMatricula p = sistema.getPeriodos().get(i);
            periodos.add(linha(p.getDataInicio().getTime(), p.getDataFim().getTime(), p.getStatus()));
            for (Matricula m : p.getMatriculas()) {
                matriculas.add(linha(i, m.getAluno().getLogin(), m.getDisciplina().getCodigo(),
                        m.getTipo(), m.getDataMatricula().getTime(), m.getStatus()));
            }
        }

        try {
            Files.createDirectories(diretorio);
            escrever("usuarios.txt", usuarios);
            escrever("disciplinas.txt", disciplinas);
            escrever("cursos.txt", cursos);
            escrever("curriculos.txt", curriculos);
            escrever("periodos.txt", periodos);
            escrever("matriculas.txt", matriculas);
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao salvar os dados em " + diretorio, e);
        }
    }

    // ---------- Leitura ----------

    public void carregar(SistemaMatriculas sistema) {
        try {
            for (String[] c : ler("usuarios.txt", 6)) {
                int id = Integer.parseInt(c[1]);
                Usuario u;
                switch (c[0]) {
                    case "ALUNO":
                        u = new Aluno(id, c[2], c[3], c[4], c[5]);
                        break;
                    case "PROFESSOR":
                        u = new Professor(id, c[2], c[3], c[4]);
                        break;
                    default:
                        u = new Secretaria(id, c[2], c[3], c[4]);
                }
                sistema.getUsuarios().add(u);
            }

            for (String[] c : ler("disciplinas.txt", 6)) {
                Disciplina d = new Disciplina(c[0], c[1]);
                d.setCapacidadeMaxima(Integer.parseInt(c[2]));
                d.setMinimoParaAtivar(Integer.parseInt(c[3]));
                d.setStatus(StatusDisciplina.valueOf(c[4]));
                for (String login : lista(c[5])) {
                    Usuario u = sistema.buscarUsuario(login);
                    if (u instanceof Professor) {
                        ((Professor) u).adicionarDisciplina(d);
                    }
                }
                sistema.getDisciplinas().add(d);
            }

            for (String[] c : ler("cursos.txt", 3)) {
                Curso curso = new Curso(c[0], Integer.parseInt(c[1]));
                for (String codigo : lista(c[2])) {
                    curso.adicionarDisciplina(sistema.buscarDisciplina(codigo));
                }
                sistema.getCursos().add(curso);
            }

            for (String[] c : ler("curriculos.txt", 2)) {
                Curso curso = sistema.buscarCurso(c[1]);
                if (curso != null) {
                    sistema.getCurriculos().add(new Curriculo(c[0], curso));
                }
            }

            for (String[] c : ler("periodos.txt", 3)) {
                PeriodoMatricula p = new PeriodoMatricula(
                        new Date(Long.parseLong(c[0])), new Date(Long.parseLong(c[1])));
                p.setStatus(StatusPeriodo.valueOf(c[2]));
                sistema.getPeriodos().add(p);
            }

            int ultimoPeriodo = sistema.getPeriodos().size() - 1;
            for (String[] c : ler("matriculas.txt", 6)) {
                int indicePeriodo = Integer.parseInt(c[0]);
                Usuario u = sistema.buscarUsuario(c[1]);
                Disciplina d = sistema.buscarDisciplina(c[2]);
                if (!(u instanceof Aluno) || d == null || indicePeriodo > ultimoPeriodo) {
                    continue;
                }
                Aluno aluno = (Aluno) u;
                Matricula m = new Matricula(TipoMatricula.valueOf(c[3]),
                        new Date(Long.parseLong(c[4])), aluno, d);
                m.setStatus(StatusMatricula.valueOf(c[5]));
                sistema.getPeriodos().get(indicePeriodo).getMatriculas().add(m);
                // Somente as matriculas do periodo atual contam para o aluno e a disciplina.
                if (indicePeriodo == ultimoPeriodo) {
                    aluno.getMatriculas().add(m);
                    d.getMatriculas().add(m);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao ler os dados de " + diretorio, e);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Arquivo de dados corrompido em " + diretorio + ": " + e.getMessage(), e);
        }

        sistema.atualizarSecretarias();
    }

    // ---------- Auxiliares ----------

    private String linha(Object... campos) {
        List<String> partes = new ArrayList<>();
        for (Object campo : campos) {
            partes.add(String.valueOf(campo).replace(SEPARADOR, ",").replaceAll("[\\r\\n]", " "));
        }
        return String.join(SEPARADOR, partes);
    }

    private void escrever(String nome, List<String> linhas) throws IOException {
        Files.write(diretorio.resolve(nome), linhas, StandardCharsets.UTF_8);
    }

    private List<String[]> ler(String nome, int numeroCampos) throws IOException {
        List<String[]> registros = new ArrayList<>();
        Path arquivo = diretorio.resolve(nome);
        if (!Files.exists(arquivo)) {
            return registros;
        }
        for (String linha : Files.readAllLines(arquivo, StandardCharsets.UTF_8)) {
            if (linha.isBlank()) {
                continue;
            }
            String[] campos = linha.split(SEPARADOR, -1);
            if (campos.length != numeroCampos) {
                throw new IllegalArgumentException("linha inválida em " + nome + ": " + linha);
            }
            registros.add(campos);
        }
        return registros;
    }

    private List<String> lista(String campo) {
        List<String> itens = new ArrayList<>();
        for (String item : campo.split(SEPARADOR_LISTA)) {
            if (!item.isBlank()) {
                itens.add(item);
            }
        }
        return itens;
    }
}
