package br.pucminas.matriculas.service;

/**
 * Lancada quando uma operacao viola uma regra de negocio; a mensagem e
 * exibida diretamente ao usuario.
 */
public class RegraNegocioException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
