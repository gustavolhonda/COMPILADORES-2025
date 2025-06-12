package br.ufscar.dc.compiladores.alguma.semantico;

class EntradaTabelaDeSimbolos {
    public enum TiposDados {
        INTEIRO, REAL, TEXTO, BOOLEANO, INDEFINIDO, REGISTRO, NULO
    }

    public enum Categorias {
        VARIAVEL, CONSTANTE, PROCEDIMENTO, FUNCAO, TIPO_DEFINIDO
    }
    
    String nomeIdentificador;
    TiposDados tipoDado;
    Categorias categoriaIdentificador;

    public EntradaTabelaDeSimbolos(String nomeIdentificador, TiposDados tipoDado, Categorias categoriaIdentificador) {
        this.nomeIdentificador = nomeIdentificador;
        this.tipoDado = tipoDado;
        this.categoriaIdentificador = categoriaIdentificador;
    }
}