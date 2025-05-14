package br.ufscar.dc.compiladores.alguma.semantico;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import org.antlr.v4.runtime.Token;

import br.ufscar.dc.compiladores.alguma.semantico.LAParser.Exp_aritmeticaContext;
import br.ufscar.dc.compiladores.alguma.semantico.LAParser.ExpressaoContext;
import br.ufscar.dc.compiladores.alguma.semantico.LAParser.FatorContext;
import br.ufscar.dc.compiladores.alguma.semantico.LAParser.Fator_logicoContext;
import br.ufscar.dc.compiladores.alguma.semantico.LAParser.ParcelaContext;
import br.ufscar.dc.compiladores.alguma.semantico.LAParser.TermoContext;
import br.ufscar.dc.compiladores.alguma.semantico.LAParser.Termo_logicoContext;

public class Auxiliar {
    // Registro de erros semânticos
    public static List<String> errosSemanticos = new ArrayList<>();

    // Registra erro na lista
    public static void adicionarErroSemantico(Token token, String mensagem) {
        errosSemanticos.add(String.format("Linha %d: %s", token.getLine(), mensagem));
    }

    // Verifica tipo de expressão
    public static EntradaTabelaDeSimbolos.Tipos verificarTipo(Escopo escopos, ExpressaoContext expr) {
        EntradaTabelaDeSimbolos.Tipos tipo = null;
        
        for (Termo_logicoContext termo : expr.termo_logico()) {
            EntradaTabelaDeSimbolos.Tipos tipoAtual = verificarTipo(escopos, termo);
            
            if (tipo == null) {
                tipo = tipoAtual;
            } else if (tipo != tipoAtual && tipoAtual != EntradaTabelaDeSimbolos.Tipos.INVALIDO) {
                return EntradaTabelaDeSimbolos.Tipos.INVALIDO;
            }
        }
        
        return tipo;
    }

    // Verifica tipo de termo lógico
    public static EntradaTabelaDeSimbolos.Tipos verificarTipo(Escopo escopos, Termo_logicoContext termoLogico) {
        EntradaTabelaDeSimbolos.Tipos tipo = null;
        
        for (Fator_logicoContext fator : termoLogico.fator_logico()) {
            EntradaTabelaDeSimbolos.Tipos tipoAtual = verificarTipo(escopos, fator);
            
            if (tipo == null) {
                tipo = tipoAtual;
            } else if (tipo != tipoAtual && tipoAtual != EntradaTabelaDeSimbolos.Tipos.INVALIDO) {
                return EntradaTabelaDeSimbolos.Tipos.INVALIDO;
            }
        }
        
        return tipo;
    }

    // Verifica tipo de fator lógico
    public static EntradaTabelaDeSimbolos.Tipos verificarTipo(Escopo escopos, Fator_logicoContext fatorLogico) {
        return verificarTipo(escopos, fatorLogico.parcela_logica());
    }

    // Verifica tipo de parcela lógica
    public static EntradaTabelaDeSimbolos.Tipos verificarTipo(Escopo escopos, LAParser.Parcela_logicaContext parcelaLogica) {
        if (parcelaLogica.exp_relacional() != null) {
            return verificarTipo(escopos, parcelaLogica.exp_relacional());
        }
        
        return EntradaTabelaDeSimbolos.Tipos.LOGICO;
    }

    // Verifica tipo de expressão relacional
    public static EntradaTabelaDeSimbolos.Tipos verificarTipo(Escopo escopos, LAParser.Exp_relacionalContext exprRel) {
        if (exprRel.op_relacional() != null) {
            EntradaTabelaDeSimbolos.Tipos tipo = null;
            
            for (Exp_aritmeticaContext expArit : exprRel.exp_aritmetica()) {
                EntradaTabelaDeSimbolos.Tipos tipoAtual = verificarTipo(escopos, expArit);
                boolean tipoAtualNumerico = ehTipoNumerico(tipoAtual);
                boolean tipoResultadoNumerico = ehTipoNumerico(tipo);
                
                if (tipo == null) {
                    tipo = tipoAtual;
                } else if (!(tipoAtualNumerico && tipoResultadoNumerico) && tipoAtual != tipo) {
                    return EntradaTabelaDeSimbolos.Tipos.INVALIDO;
                }
            }
            
            return (tipo != EntradaTabelaDeSimbolos.Tipos.INVALIDO) ? 
                   EntradaTabelaDeSimbolos.Tipos.LOGICO : EntradaTabelaDeSimbolos.Tipos.INVALIDO;
        }
        
        return verificarTipo(escopos, exprRel.exp_aritmetica(0));
    }

    // Verifica tipo de expressão aritmética
    public static EntradaTabelaDeSimbolos.Tipos verificarTipo(Escopo escopos, Exp_aritmeticaContext expArit) {
        EntradaTabelaDeSimbolos.Tipos tipo = null;
        
        for (TermoContext termo : expArit.termo()) {
            EntradaTabelaDeSimbolos.Tipos tipoAtual = verificarTipo(escopos, termo);
            
            if (tipo == null) {
                tipo = tipoAtual;
            } else if (tipo != tipoAtual && tipoAtual != EntradaTabelaDeSimbolos.Tipos.INVALIDO) {
                return EntradaTabelaDeSimbolos.Tipos.INVALIDO;
            }
        }
        
        return tipo;
    }

    // Verifica tipo de termo
    public static EntradaTabelaDeSimbolos.Tipos verificarTipo(Escopo escopos, TermoContext termo) {
        EntradaTabelaDeSimbolos.Tipos tipo = null;

        for (FatorContext fator : termo.fator()) {
            EntradaTabelaDeSimbolos.Tipos tipoAtual = verificarTipo(escopos, fator);
            boolean tipoAtualNumerico = ehTipoNumerico(tipoAtual);
            boolean tipoResultadoNumerico = ehTipoNumerico(tipo);
            
            if (tipo == null) {
                tipo = tipoAtual;
            } else if (!(tipoAtualNumerico && tipoResultadoNumerico) && tipoAtual != tipo) {
                return EntradaTabelaDeSimbolos.Tipos.INVALIDO;
            }
        }
        
        return tipo;
    }

    // Verifica tipo de fator
    public static EntradaTabelaDeSimbolos.Tipos verificarTipo(Escopo escopos, FatorContext fator) {
        EntradaTabelaDeSimbolos.Tipos tipo = null;

        for (ParcelaContext parcela : fator.parcela()) {
            EntradaTabelaDeSimbolos.Tipos tipoAtual = verificarTipo(escopos, parcela);
            
            if (tipo == null) {
                tipo = tipoAtual;
            } else if (tipo != tipoAtual && tipoAtual != EntradaTabelaDeSimbolos.Tipos.INVALIDO) {
                return EntradaTabelaDeSimbolos.Tipos.INVALIDO;
            }
        }
        
        return tipo;
    }

    // Verifica tipo de parcela
    public static EntradaTabelaDeSimbolos.Tipos verificarTipo(Escopo escopos, ParcelaContext parcela) {
        if (parcela.parcela_nao_unario() != null) {
            return verificarTipo(escopos, parcela.parcela_nao_unario());
        }
        
        return verificarTipo(escopos, parcela.parcela_unario());
    }

    // Verifica tipo de parcela não unária
    public static EntradaTabelaDeSimbolos.Tipos verificarTipo(Escopo escopos, LAParser.Parcela_nao_unarioContext parcelaNaoUnario) {
        if (parcelaNaoUnario.identificador() != null) {
            return verificarTipo(escopos, parcelaNaoUnario.identificador());
        }
        
        return EntradaTabelaDeSimbolos.Tipos.CADEIA;
    }

    // Verifica tipo de identificador
    public static EntradaTabelaDeSimbolos.Tipos verificarTipo(Escopo escopos, LAParser.IdentificadorContext identificador) {
        StringBuilder nomeVar = new StringBuilder();
        
        for (int i = 0; i < identificador.IDENT().size(); i++) {
            nomeVar.append(identificador.IDENT(i).getText());
            if (i < identificador.IDENT().size() - 1) {
                nomeVar.append(".");
            }
        }
        
        String varName = nomeVar.toString();
        for (TabelaDeSimbolos tabela : escopos.percorrerEscoposAninhados()) {
            if (tabela.possui(varName)) {
                return verificarTipo(escopos, varName);
            }
        }
        
        return EntradaTabelaDeSimbolos.Tipos.INVALIDO;
    }

    // Verifica tipo de parcela unária
    public static EntradaTabelaDeSimbolos.Tipos verificarTipo(Escopo escopos, LAParser.Parcela_unarioContext parcelaUnario) {
        // Verifica tipo numérico inteiro
        if (parcelaUnario.NUM_INT() != null) {
            return EntradaTabelaDeSimbolos.Tipos.INT;
        }
        
        // Verifica tipo numérico real
        if (parcelaUnario.NUM_REAL() != null) {
            return EntradaTabelaDeSimbolos.Tipos.REAL;
        }
        
        // Verifica tipo de identificador
        if (parcelaUnario.identificador() != null) {
            return verificarTipo(escopos, parcelaUnario.identificador());
        }
        
        // Verifica chamada de função
        if (parcelaUnario.IDENT() != null) {
            EntradaTabelaDeSimbolos.Tipos tipo = verificarTipo(escopos, parcelaUnario.IDENT().getText());
            
            for (ExpressaoContext expr : parcelaUnario.expressao()) {
                EntradaTabelaDeSimbolos.Tipos tipoExpr = verificarTipo(escopos, expr);
                
                if (tipo == null) {
                    tipo = tipoExpr;
                } else if (tipo != tipoExpr && tipoExpr != EntradaTabelaDeSimbolos.Tipos.INVALIDO) {
                    return EntradaTabelaDeSimbolos.Tipos.INVALIDO;
                }
            }
            
            return tipo;
        } 
        
        // Verifica expressão entre parênteses
        EntradaTabelaDeSimbolos.Tipos tipo = null;
        
        for (ExpressaoContext expr : parcelaUnario.expressao()) {
            EntradaTabelaDeSimbolos.Tipos tipoExpr = verificarTipo(escopos, expr);
            
            if (tipo == null) {
                tipo = tipoExpr;
            } else if (tipo != tipoExpr && tipoExpr != EntradaTabelaDeSimbolos.Tipos.INVALIDO) {
                return EntradaTabelaDeSimbolos.Tipos.INVALIDO;
            }
        }
        
        return tipo;
    }

    // Verifica tipo de variável por nome
    public static EntradaTabelaDeSimbolos.Tipos verificarTipo(Escopo escopos, String nomeVar) {
        for (TabelaDeSimbolos tabela : escopos.percorrerEscoposAninhados()) {
            EntradaTabelaDeSimbolos.Tipos tipo = tabela.verificar(nomeVar);
            if (tipo != null) {
                return tipo;
            }
        }
        
        return null;
    }
    
    // Verifica se o tipo é numérico (INT ou REAL)
    private static boolean ehTipoNumerico(EntradaTabelaDeSimbolos.Tipos tipo) {
        return tipo == EntradaTabelaDeSimbolos.Tipos.REAL || tipo == EntradaTabelaDeSimbolos.Tipos.INT;
    }
}