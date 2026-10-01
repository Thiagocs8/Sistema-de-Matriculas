package br.pucminas.matriculas;

import java.nio.file.Path;
import java.nio.file.Paths;

import br.pucminas.matriculas.external.SistemaCobrancaArquivo;
import br.pucminas.matriculas.persistence.RepositorioArquivos;
import br.pucminas.matriculas.service.SistemaMatriculas;
import br.pucminas.matriculas.ui.MenuConsole;

public class Main {

    private static final String LOGIN_PADRAO = "admin";
    private static final String SENHA_PADRAO = "admin";

    public static void main(String[] args) {
        Path dados = Paths.get(args.length > 0 ? args[0] : "dados");

        SistemaMatriculas sistema = new SistemaMatriculas(new SistemaCobrancaArquivo(dados.resolve("cobrancas.txt")));
        RepositorioArquivos repositorio = new RepositorioArquivos(dados);
        repositorio.carregar(sistema);

        if (sistema.getUsuarios().isEmpty()) {
            sistema.cadastrarSecretaria("Secretaria", LOGIN_PADRAO, SENHA_PADRAO);
            repositorio.salvar(sistema);
            System.out.println("Primeiro acesso: usuário da secretaria criado (login \"" + LOGIN_PADRAO
                    + "\", senha \"" + SENHA_PADRAO + "\").");
        }

        new MenuConsole(sistema, repositorio).iniciar();
    }
}
