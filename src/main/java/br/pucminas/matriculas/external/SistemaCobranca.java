package br.pucminas.matriculas.external;

import br.pucminas.matriculas.model.Aluno;
import br.pucminas.matriculas.model.Disciplina;

/**
 * Representa o sistema externo de cobranca, notificado pelo sistema de
 * matriculas (RN04) sempre que um aluno se matricula em uma disciplina.
 */
public interface SistemaCobranca {

    void notificarCobranca(Aluno a, Disciplina d);
}
