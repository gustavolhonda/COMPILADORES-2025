package br.ufscar.dc.compiladores.alguma.semantico;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.Token;

import br.ufscar.dc.compiladores.alguma.semantico.LAParser.Exp_aritmeticaContext;
import br.ufscar.dc.compiladores.alguma.semantico.LAParser.ExpressaoContext;
import br.ufscar.dc.compiladores.alguma.semantico.LAParser.FatorContext;
import br.ufscar.dc.compiladores.alguma.semantico.LAParser.Fator_logicoContext;
import br.ufscar.dc.compiladores.alguma.semantico.LAParser.ParcelaContext;
import br.ufscar.dc.compiladores.alguma.semantico.LAParser.TermoContext;
import br.ufscar.dc.compiladores.alguma.semantico.LAParser.Termo_logicoContext;

public class Auxiliar {
    public static List<String> listaDeErrosSemanticos = new ArrayList<>();

    public static void adicionarErro(Token token, String mensagem) {
        int numeroLinha = token.getLine();
        listaDeErrosSemanticos.add(String.format("Linha %d: %s", numeroLinha, mensagem));
    }

    public static EntradaTabelaDeSimbolos.TiposDados obterTipoExpressao(Escopo gerenciaEscopos, LAParser.ExpressaoContext contextoExpressao) {
        EntradaTabelaDeSimbolos.TiposDados tipoRetornado = null;
        int indice = 0;
        while (indice < contextoExpressao.termo_logico().size()) {
            Termo_logicoContext termoLogico = contextoExpressao.termo_logico(indice);
            EntradaTabelaDeSimbolos.TiposDados tipoAuxiliar = obterTipoTermoLogico(gerenciaEscopos, termoLogico);
            if (tipoRetornado == null) {
                tipoRetornado = tipoAuxiliar;
            } else if (tipoRetornado != tipoAuxiliar && tipoAuxiliar != EntradaTabelaDeSimbolos.TiposDados.INDEFINIDO) {
                tipoRetornado = EntradaTabelaDeSimbolos.TiposDados.INDEFINIDO;
            }
            indice++;
        }
        return tipoRetornado;
    }

    public static EntradaTabelaDeSimbolos.TiposDados obterTipoTermoLogico(Escopo gerenciaEscopos, LAParser.Termo_logicoContext contextoTermoLogico) {
        EntradaTabelaDeSimbolos.TiposDados tipoRetornado = null;
        int indice = 0;
        while (indice < contextoTermoLogico.fator_logico().size()) {
            Fator_logicoContext fatorLogico = contextoTermoLogico.fator_logico(indice);
            EntradaTabelaDeSimbolos.TiposDados tipoAuxiliar = obterTipoFatorLogico(gerenciaEscopos, fatorLogico);
            if (tipoRetornado == null) {
                tipoRetornado = tipoAuxiliar;
            } else if (tipoRetornado != tipoAuxiliar && tipoAuxiliar != EntradaTabelaDeSimbolos.TiposDados.INDEFINIDO) {
                tipoRetornado = EntradaTabelaDeSimbolos.TiposDados.INDEFINIDO;
            }
            indice++;
        }
        return tipoRetornado;
    }

    public static EntradaTabelaDeSimbolos.TiposDados obterTipoFatorLogico(Escopo gerenciaEscopos, LAParser.Fator_logicoContext contextoFatorLogico) {
        return obterTipoParcelaLogica(gerenciaEscopos, contextoFatorLogico.parcela_logica());
    }

    public static EntradaTabelaDeSimbolos.TiposDados obterTipoParcelaLogica(Escopo gerenciaEscopos, LAParser.Parcela_logicaContext contextoParcelaLogica) {
        EntradaTabelaDeSimbolos.TiposDados tipoRetornado = null;
        if (contextoParcelaLogica.exp_relacional() != null) {
            tipoRetornado = obterTipoExpressaoRelacional(gerenciaEscopos, contextoParcelaLogica.exp_relacional());
        } else {
            tipoRetornado = EntradaTabelaDeSimbolos.TiposDados.BOOLEANO;
        }

        return tipoRetornado;
    }

    public static EntradaTabelaDeSimbolos.TiposDados obterTipoExpressaoRelacional(Escopo gerenciaEscopos, LAParser.Exp_relacionalContext contextoExpressaoRelacional) {
        EntradaTabelaDeSimbolos.TiposDados tipoRetornado = null;
        if (contextoExpressaoRelacional.op_relacional() != null) {
            int indice = 0;
            while (indice < contextoExpressaoRelacional.exp_aritmetica().size()) {
                Exp_aritmeticaContext expressaoAritmetica = contextoExpressaoRelacional.exp_aritmetica(indice);
                EntradaTabelaDeSimbolos.TiposDados tipoAuxiliar = obterTipoExpressaoAritmetica(gerenciaEscopos, expressaoAritmetica);
                Boolean isAuxNumeric = tipoAuxiliar == EntradaTabelaDeSimbolos.TiposDados.REAL || tipoAuxiliar == EntradaTabelaDeSimbolos.TiposDados.INTEIRO;
                Boolean isRetNumeric = tipoRetornado == EntradaTabelaDeSimbolos.TiposDados.REAL || tipoRetornado == EntradaTabelaDeSimbolos.TiposDados.INTEIRO;

                if (tipoRetornado == null) {
                    tipoRetornado = tipoAuxiliar;
                } else if (!(isAuxNumeric && isRetNumeric) && tipoAuxiliar != tipoRetornado) {
                    tipoRetornado = EntradaTabelaDeSimbolos.TiposDados.INDEFINIDO;
                }
                indice++;
            }
            if (tipoRetornado != EntradaTabelaDeSimbolos.TiposDados.INDEFINIDO) {
                tipoRetornado = EntradaTabelaDeSimbolos.TiposDados.BOOLEANO;
            }
        } else {
            tipoRetornado = obterTipoExpressaoAritmetica(gerenciaEscopos, contextoExpressaoRelacional.exp_aritmetica(0));
        }

        return tipoRetornado;
    }

    public static EntradaTabelaDeSimbolos.TiposDados obterTipoExpressaoAritmetica(Escopo gerenciaEscopos, LAParser.Exp_aritmeticaContext contextoExpressaoAritmetica) {
        EntradaTabelaDeSimbolos.TiposDados tipoRetornado = null;
        int indice = 0;
        while (indice < contextoExpressaoAritmetica.termo().size()) {
            TermoContext termo = contextoExpressaoAritmetica.termo(indice);
            EntradaTabelaDeSimbolos.TiposDados tipoAuxiliar = obterTipoTermo(gerenciaEscopos, termo);
            if (tipoRetornado == null) {
                tipoRetornado = tipoAuxiliar;
            } else if (tipoRetornado != tipoAuxiliar && tipoAuxiliar != EntradaTabelaDeSimbolos.TiposDados.INDEFINIDO) {
                tipoRetornado = EntradaTabelaDeSimbolos.TiposDados.INDEFINIDO;
            }
            indice++;
        }

        return tipoRetornado;
    }

    public static EntradaTabelaDeSimbolos.TiposDados obterTipoTermo(Escopo gerenciaEscopos, LAParser.TermoContext contextoTermo) {
        EntradaTabelaDeSimbolos.TiposDados tipoRetornado = null;

        int indice = 0;
        while (indice < contextoTermo.fator().size()) {
            FatorContext fator = contextoTermo.fator(indice);
            EntradaTabelaDeSimbolos.TiposDados tipoAuxiliar = obterTipoFator(gerenciaEscopos, fator);
            Boolean isAuxNumeric = tipoAuxiliar == EntradaTabelaDeSimbolos.TiposDados.REAL || tipoAuxiliar == EntradaTabelaDeSimbolos.TiposDados.INTEIRO; 
            Boolean isRetNumeric = tipoRetornado == EntradaTabelaDeSimbolos.TiposDados.REAL || tipoRetornado == EntradaTabelaDeSimbolos.TiposDados.INTEIRO;
            if (tipoRetornado == null) {
                tipoRetornado = tipoAuxiliar;
            } else if (!(isAuxNumeric && isRetNumeric) && tipoAuxiliar != tipoRetornado) {
                tipoRetornado = EntradaTabelaDeSimbolos.TiposDados.INDEFINIDO;
            }
            indice++;
        }
        return tipoRetornado;
    }

    public static EntradaTabelaDeSimbolos.TiposDados obterTipoFator(Escopo gerenciaEscopos, LAParser.FatorContext contextoFator) {
        EntradaTabelaDeSimbolos.TiposDados tipoRetornado = null;
    
        int indice = 0;
        while (indice < contextoFator.parcela().size()) {
            ParcelaContext parcela = contextoFator.parcela(indice);
            EntradaTabelaDeSimbolos.TiposDados tipoAuxiliar = obterTipoParcela(gerenciaEscopos, parcela);
            if (tipoRetornado == null) {
                tipoRetornado = tipoAuxiliar;
            } else if (tipoRetornado != tipoAuxiliar && tipoAuxiliar != EntradaTabelaDeSimbolos.TiposDados.INDEFINIDO) {
                tipoRetornado = EntradaTabelaDeSimbolos.TiposDados.INDEFINIDO;
            }
            indice++;
        }
        return tipoRetornado;
    }
    
    public static EntradaTabelaDeSimbolos.TiposDados obterTipoParcela(Escopo gerenciaEscopos, LAParser.ParcelaContext contextoParcela) {
        EntradaTabelaDeSimbolos.TiposDados tipoRetornado = EntradaTabelaDeSimbolos.TiposDados.INDEFINIDO;

        if (contextoParcela.parcela_nao_unario() != null) {
            tipoRetornado = obterTipoParcelaNaoUnaria(gerenciaEscopos, contextoParcela.parcela_nao_unario());
        } else {
            tipoRetornado = obterTipoParcelaUnaria(gerenciaEscopos, contextoParcela.parcela_unario());
        }
        return tipoRetornado;
    }

    public static EntradaTabelaDeSimbolos.TiposDados obterTipoParcelaNaoUnaria(Escopo gerenciaEscopos, LAParser.Parcela_nao_unarioContext contextoParcelaNaoUnaria) {
        if (contextoParcelaNaoUnaria.identificador() != null) {
            return obterTipoIdentificador(gerenciaEscopos, contextoParcelaNaoUnaria.identificador());
        }
        return EntradaTabelaDeSimbolos.TiposDados.TEXTO;
    }

    public static EntradaTabelaDeSimbolos.TiposDados obterTipoIdentificador(Escopo gerenciaEscopos, LAParser.IdentificadorContext contextoIdentificador) {
        String nomeVariavel = "";
        EntradaTabelaDeSimbolos.TiposDados tipoRetornado = EntradaTabelaDeSimbolos.TiposDados.INDEFINIDO;
        int indice = 0;
        while (indice < contextoIdentificador.IDENT().size()) {
            nomeVariavel += contextoIdentificador.IDENT(indice).getText();
            if (indice != contextoIdentificador.IDENT().size() - 1) {
                nomeVariavel += ".";
            }
            indice++;
        }
        for (TabelaDeSimbolos tabela : gerenciaEscopos.obterTodosEscopos()) {
            if (tabela.contem(nomeVariavel)) {
                tipoRetornado = obterTipoVariavel(gerenciaEscopos, nomeVariavel);
            }
        }
        return tipoRetornado;
    }

    public static EntradaTabelaDeSimbolos.TiposDados obterTipoParcelaUnaria(Escopo gerenciaEscopos, LAParser.Parcela_unarioContext contextoParcelaUnaria) {
        if (contextoParcelaUnaria.identificador() != null) {
            return obterTipoIdentificador(gerenciaEscopos, contextoParcelaUnaria.identificador());
        }
        if (contextoParcelaUnaria.NUM_REAL() != null) {
            return EntradaTabelaDeSimbolos.TiposDados.REAL;
        }
        if (contextoParcelaUnaria.NUM_INT() != null) {
            return EntradaTabelaDeSimbolos.TiposDados.INTEIRO;
        }
        if (contextoParcelaUnaria.IDENT() != null) {
            return obterTipoVariavel(gerenciaEscopos, contextoParcelaUnaria.IDENT().getText());
        } else {
            EntradaTabelaDeSimbolos.TiposDados tipoRetornado = null;
            int indice = 0;
            while (indice < contextoParcelaUnaria.expressao().size()) {
                ExpressaoContext expressao = contextoParcelaUnaria.expressao(indice);
                EntradaTabelaDeSimbolos.TiposDados tipoAuxiliar = obterTipoExpressao(gerenciaEscopos, expressao);
                if (tipoRetornado == null) {
                    tipoRetornado = tipoAuxiliar;
                } else if (tipoRetornado != tipoAuxiliar && tipoAuxiliar != EntradaTabelaDeSimbolos.TiposDados.INDEFINIDO) {
                    tipoRetornado = EntradaTabelaDeSimbolos.TiposDados.INDEFINIDO;
                }
                indice++;
            }
            return tipoRetornado;
        }
    }

    public static EntradaTabelaDeSimbolos.TiposDados obterTipoVariavel(Escopo gerenciaEscopos, String nomeVariavel) {
        EntradaTabelaDeSimbolos.TiposDados tipo = EntradaTabelaDeSimbolos.TiposDados.INDEFINIDO;
        int indice = 0;
        while (indice < gerenciaEscopos.obterTodosEscopos().size()) {
            TabelaDeSimbolos tabela = gerenciaEscopos.obterTodosEscopos().get(indice);
            if (tabela.contem(nomeVariavel)) {
                return tabela.consultarTipo(nomeVariavel);
            }
            indice++;
        }

        return tipo;
    }

    // Retorna o tipo em C baseado em uma string
    public static String getCType(String val) {
        String tipo = null;
        switch (val) {
            case "literal": 
                tipo = "char";
                break;
            case "inteiro": 
                tipo = "int";
                break;
            case "real": 
                tipo = "float";
                break;
            default:
                break;
        }
        return tipo;
    }

    public static EntradaTabelaDeSimbolos.TiposDados converterStringParaTipo(String valorString) {
        EntradaTabelaDeSimbolos.TiposDados tipo = null;
        switch (valorString) {
            case "real":
                tipo = EntradaTabelaDeSimbolos.TiposDados.REAL;
                break;
            case "inteiro":
                tipo = EntradaTabelaDeSimbolos.TiposDados.INTEIRO;
                break;
            case "logico":
                tipo = EntradaTabelaDeSimbolos.TiposDados.BOOLEANO;
                break;
            case "literal":
                tipo = EntradaTabelaDeSimbolos.TiposDados.TEXTO;
                break;
            default:
                break;
        }
        return tipo;
    }

    // Retorna o símbolo de tipo em C baseado em um tipo
    public static String getCTypeSymbol(EntradaTabelaDeSimbolos.TiposDados tipo) {
        String type = null;
        switch (tipo) {
            case TEXTO: 
                type = "s";
                break;
            case INTEIRO: 
                type = "d";
                break;
            case REAL: 
                type = "f";
                break;
            default:
                break;
        }
        return type;
    }
}