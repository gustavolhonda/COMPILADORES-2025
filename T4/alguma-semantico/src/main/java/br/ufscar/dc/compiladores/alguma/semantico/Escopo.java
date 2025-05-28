package br.ufscar.dc.compiladores.alguma.semantico;

import java.util.LinkedList;
import java.util.List;

public class Escopo {
    private LinkedList<TabelaDeSimbolos> pilhaDeTabelas;

    public Escopo(EntradaTabelaDeSimbolos.TiposDados tipoRetorno) {
        pilhaDeTabelas = new LinkedList<>();
        iniciarNovoEscopo(tipoRetorno);
    }

    public void iniciarNovoEscopo(EntradaTabelaDeSimbolos.TiposDados tipoRetorno) {
        pilhaDeTabelas.push(new TabelaDeSimbolos(tipoRetorno));
    }

    public TabelaDeSimbolos pegarEscopoCorrente() {
        return pilhaDeTabelas.peek();
    }

    public List<TabelaDeSimbolos> obterTodosEscopos() {
        return pilhaDeTabelas;
    }

    public void sairDoEscopo() {
        pilhaDeTabelas.pop();
    }
}