package br.pucminas.matriculas.external;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.pucminas.matriculas.model.Aluno;
import br.pucminas.matriculas.model.Disciplina;

/**
 * Simula o sistema externo de cobranca: cada notificacao recebida e
 * registrada em um arquivo, que faz o papel da fila de cobrancas.
 */
public class SistemaCobrancaArquivo implements SistemaCobranca {

    private final Path arquivo;

    public SistemaCobrancaArquivo(Path arquivo) {
        this.arquivo = arquivo;
    }

    @Override
    public void notificarCobranca(Aluno a, Disciplina d) {
        String data = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
        String linha = data + ";" + a.getMatricula() + ";" + a.getNome() + ";"
                + d.getCodigo() + ";" + d.getNome() + System.lineSeparator();
        try {
            if (arquivo.getParent() != null) {
                Files.createDirectories(arquivo.getParent());
            }
            Files.writeString(arquivo, linha, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao notificar o sistema de cobrança", e);
        }
        System.out.println("[Cobrança] Sistema de cobrança notificado: " + a.getNome()
                + " (" + a.getMatricula() + ") - " + d.getCodigo() + " " + d.getNome());
    }
}
