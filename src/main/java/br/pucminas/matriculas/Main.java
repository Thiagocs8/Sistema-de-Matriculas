package br.pucminas.matriculas;

import java.util.Date;

import br.pucminas.matriculas.model.Aluno;
import br.pucminas.matriculas.model.Curso;
import br.pucminas.matriculas.model.Disciplina;
import br.pucminas.matriculas.model.Matricula;
import br.pucminas.matriculas.model.PeriodoMatricula;
import br.pucminas.matriculas.model.Professor;
import br.pucminas.matriculas.model.Secretaria;
import br.pucminas.matriculas.model.enums.TipoMatricula;

public class Main {

    public static void main(String[] args) {
        System.out.println("=== 1. CRIAÇÃO DOS USUÁRIOS E ESTRUTURA ACADÊMICA ===");
        
        Secretaria secretaria = new Secretaria(1, "Ana Secretaria", "ana.sec", "senha123");
        Professor profCarlos = new Professor(2, "Carlos Silva", "carlos.prof", "senha123");
        
        Curso cursoEngenharia = new Curso("Engenharia de Software", 240);
        secretaria.cadastrarCurso(cursoEngenharia);

        Disciplina ds = new Disciplina("BES01", "Desenvolvimento de Software");
        Disciplina bd = new Disciplina("BES02", "Banco de Dados");
        Disciplina ia = new Disciplina("BES03", "Inteligência Artificial");

        ds.setMinimoParaAtivar(3);
        bd.setMinimoParaAtivar(3);
        ia.setMinimoParaAtivar(3);

        secretaria.cadastrarDisciplina(ds);
        secretaria.cadastrarDisciplina(bd);
        secretaria.cadastrarDisciplina(ia);

        cursoEngenharia.adicionarDisciplina(ds);
        cursoEngenharia.adicionarDisciplina(bd);
        cursoEngenharia.adicionarDisciplina(ia);

        profCarlos.getDisciplinas().add(ds);
        profCarlos.getDisciplinas().add(bd);
        
        Aluno aluno1 = new Aluno(10, "João", "joao", "123", "MAT101");
        Aluno aluno2 = new Aluno(11, "Maria", "maria", "123", "MAT102");
        Aluno aluno3 = new Aluno(12, "Pedro", "pedro", "123", "MAT103");

        System.out.println("Cursos e Disciplinas cadastrados com sucesso!\n");


        System.out.println("=== 2. ABERTURA DO PERÍODO DE MATRÍCULA ===");
        PeriodoMatricula periodo = new PeriodoMatricula(new Date(), new Date());
        secretaria.getPeriodosAdministrados().add(periodo);


        System.out.println("=== 3. REALIZANDO MATRÍCULAS DE ALUNOS ===");
        
        realizarMatricula(aluno1, ds, TipoMatricula.OBRIGATORIA, periodo);
        realizarMatricula(aluno1, bd, TipoMatricula.OBRIGATORIA, periodo);

        realizarMatricula(aluno2, ds, TipoMatricula.OBRIGATORIA, periodo);
        realizarMatricula(aluno2, bd, TipoMatricula.OBRIGATORIA, periodo);

        realizarMatricula(aluno3, ds, TipoMatricula.OBRIGATORIA, periodo);
        realizarMatricula(aluno3, ia, TipoMatricula.OPTATIVA, periodo);

        System.out.println();


        System.out.println("=== 4. PROFESSOR CONSULTA ALUNOS MATRICULADOS ===");
        System.out.println("Alunos na disciplina " + ds.getNome() + ":");
        for (Aluno a : profCarlos.consultarAlunosMatriculados(ds)) {
            System.out.println(" - " + a.getNome() + " (Matrícula: " + a.getMatricula() + ")");
        }
        System.out.println();


        System.out.println("=== 5. ENCERRAMENTO DO PERÍODO DE MATRÍCULA ===");
        secretaria.encerrarPeriodoMatricula(periodo);

        System.out.println("Status final do Período: " + periodo.getStatus());
        System.out.println("Status da disciplina " + ds.getNome() + ": " + ds.getStatus());
        System.out.println("Status da disciplina " + bd.getNome() + ": " + bd.getStatus());
        System.out.println("Status da disciplina " + ia.getNome() + " (apenas 1 aluno): " + ia.getStatus());
    }

    private static void realizarMatricula(Aluno aluno, Disciplina disciplina, TipoMatricula tipo, PeriodoMatricula periodo) {
        Matricula m = aluno.matricularEmDisciplina(disciplina, tipo);
        if (m != null) {
            periodo.getMatriculas().add(m);
            System.out.println("✓ Matrícula de " + aluno.getNome() + " em " + disciplina.getNome() + " realizada com sucesso.");
        } else {
            System.out.println("✗ Falha na matrícula de " + aluno.getNome() + " em " + disciplina.getNome() + ".");
        }
    }
}