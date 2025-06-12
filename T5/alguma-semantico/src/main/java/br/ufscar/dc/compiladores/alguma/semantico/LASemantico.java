package br.ufscar.dc.compiladores.alguma.semantico;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import org.antlr.v4.runtime.tree.TerminalNode;

import br.ufscar.dc.compiladores.alguma.semantico.LAParser.CmdAtribuicaoContext;
import br.ufscar.dc.compiladores.alguma.semantico.LAParser.CmdRetorneContext;
import br.ufscar.dc.compiladores.alguma.semantico.LAParser.Declaracao_constanteContext;
import br.ufscar.dc.compiladores.alguma.semantico.LAParser.Declaracao_globalContext;
import br.ufscar.dc.compiladores.alguma.semantico.LAParser.Declaracao_tipoContext;
import br.ufscar.dc.compiladores.alguma.semantico.LAParser.Declaracao_variavelContext;
import br.ufscar.dc.compiladores.alguma.semantico.LAParser.IdentificadorContext;
import br.ufscar.dc.compiladores.alguma.semantico.LAParser.ParametroContext;
import br.ufscar.dc.compiladores.alguma.semantico.LAParser.Parcela_unarioContext;
import br.ufscar.dc.compiladores.alguma.semantico.LAParser.ProgramaContext;
import br.ufscar.dc.compiladores.alguma.semantico.LAParser.Tipo_basico_identContext;
import br.ufscar.dc.compiladores.alguma.semantico.LAParser.VariavelContext;

public class LASemantico extends LABaseVisitor<Object> {
    
    Escopo gerenciadorDeEscopos = new Escopo(EntradaTabelaDeSimbolos.TiposDados.NULO);

    @Override
    public Object visitPrograma(ProgramaContext ctx) {  
        return super.visitPrograma(ctx);
    }

    @Override
    public Object visitDeclaracao_constante(Declaracao_constanteContext ctx) {
        TabelaDeSimbolos escopoCorrente = gerenciadorDeEscopos.pegarEscopoCorrente();
        
        if (escopoCorrente.contem(ctx.IDENT().getText())) {
            Auxiliar.adicionarErro(ctx.start, "constante " + ctx.IDENT().getText()+ " ja declarado anteriormente");
        } else {
            EntradaTabelaDeSimbolos.TiposDados tipoConst = EntradaTabelaDeSimbolos.TiposDados.INTEIRO;
            EntradaTabelaDeSimbolos.TiposDados tipoInferido = Auxiliar.converterStringParaTipo(ctx.tipo_basico().getText());
            
            if (tipoInferido != null)
                tipoConst = tipoInferido;
            escopoCorrente.adicionarEntrada(ctx.IDENT().getText(), tipoConst, EntradaTabelaDeSimbolos.Categorias.CONSTANTE);
        }

        return super.visitDeclaracao_constante(ctx);
    }

    @Override
    public Object visitDeclaracao_tipo(Declaracao_tipoContext ctx) {
        TabelaDeSimbolos escopoCorrente = gerenciadorDeEscopos.pegarEscopoCorrente();
        
        if (escopoCorrente.contem(ctx.IDENT().getText())) {
             Auxiliar.adicionarErro(ctx.start, "tipo " + ctx.IDENT().getText()+ " declarado duas vezes num mesmo escopo");
        } else {
            EntradaTabelaDeSimbolos.TiposDados tipoDefinido = Auxiliar.converterStringParaTipo(ctx.tipo().getText());
            
            if (tipoDefinido != null)
                escopoCorrente.adicionarEntrada(ctx.IDENT().getText(), tipoDefinido, EntradaTabelaDeSimbolos.Categorias.TIPO_DEFINIDO);
            else 
            if (ctx.tipo().registro() != null) {
                ArrayList<EntradaTabelaDeSimbolos> membrosRegistro = new ArrayList<>();
                Iterator<VariavelContext> iteradorVariaveis = ctx.tipo().registro().variavel().iterator();
                while (iteradorVariaveis.hasNext()) {
                    VariavelContext variavel = iteradorVariaveis.next();
                    EntradaTabelaDeSimbolos.TiposDados tipoMembro = Auxiliar.converterStringParaTipo(variavel.tipo().getText());
                    
                    Iterator<IdentificadorContext> iteradorIdentificadores = variavel.identificador().iterator();
                    while (iteradorIdentificadores.hasNext()) {
                        IdentificadorContext idCampo = iteradorIdentificadores.next();
                        membrosRegistro.add(new EntradaTabelaDeSimbolos(idCampo.getText(), tipoMembro, EntradaTabelaDeSimbolos.Categorias.TIPO_DEFINIDO));
                    }
                }
                
                if (escopoCorrente.contem(ctx.IDENT().getText())) {
                    Auxiliar.adicionarErro(ctx.start, "identificador " + ctx.IDENT().getText() + " ja declarado anteriormente");
                }
                else {
                    escopoCorrente.adicionarEntrada(ctx.IDENT().getText(), EntradaTabelaDeSimbolos.TiposDados.REGISTRO, EntradaTabelaDeSimbolos.Categorias.TIPO_DEFINIDO);
                }

                
                for (EntradaTabelaDeSimbolos membro : membrosRegistro) {
                    String nomeCompletoMembro = ctx.IDENT().getText() + '.' + membro.nomeIdentificador;
                    
                    if (escopoCorrente.contem(nomeCompletoMembro)) {
                        Auxiliar.adicionarErro(ctx.start, "identificador " + nomeCompletoMembro + " ja declarado anteriormente");
                    }
                    else {
                        escopoCorrente.adicionarEntrada(membro);
                        escopoCorrente.adicionarDefinicaoDeTipo(ctx.IDENT().getText(), membro);
                    }
                }
            }
            EntradaTabelaDeSimbolos.TiposDados tipoFinal =  Auxiliar.converterStringParaTipo(ctx.tipo().getText());
            escopoCorrente.adicionarEntrada(ctx.IDENT().getText(), tipoFinal, EntradaTabelaDeSimbolos.Categorias.TIPO_DEFINIDO);
        }   return super.visitDeclaracao_tipo(ctx);
    }

    @Override
    public Object visitTipo_basico_ident(Tipo_basico_identContext ctx) {
        
        if (ctx.IDENT() != null) {
            boolean encontrado = false;
            
            for (TabelaDeSimbolos escopo : gerenciadorDeEscopos.obterTodosEscopos()) {
                
                if (escopo.contem(ctx.IDENT().getText())) {
                    encontrado = true;
                }
            }
            
            if (!encontrado) {
                Auxiliar.adicionarErro(ctx.start, "tipo " + ctx.IDENT().getText() + " nao declarado");
            }
        }   return super.visitTipo_basico_ident(ctx);
    }

    @Override
    public Object visitDeclaracao_global(Declaracao_globalContext ctx) {
        TabelaDeSimbolos escopoCorrente = gerenciadorDeEscopos.pegarEscopoCorrente();
        Object resultadoVisita;
        
        if (escopoCorrente.contem(ctx.IDENT().getText())) {
            Auxiliar.adicionarErro(ctx.start, ctx.IDENT().getText() + " ja declarado anteriormente");
            resultadoVisita = super.visitDeclaracao_global(ctx);
        } else {
            EntradaTabelaDeSimbolos.TiposDados tipoRetornoRotina = EntradaTabelaDeSimbolos.TiposDados.NULO;
            
            if (ctx.getText().startsWith("funcao")) {
                tipoRetornoRotina = Auxiliar.converterStringParaTipo(ctx.tipo_estendido().getText());
                escopoCorrente.adicionarEntrada(ctx.IDENT().getText(), tipoRetornoRotina, EntradaTabelaDeSimbolos.Categorias.FUNCAO);
            }
            else {
                tipoRetornoRotina = EntradaTabelaDeSimbolos.TiposDados.NULO;
                escopoCorrente.adicionarEntrada(ctx.IDENT().getText(), tipoRetornoRotina, EntradaTabelaDeSimbolos.Categorias.PROCEDIMENTO);
            }
            gerenciadorDeEscopos.iniciarNovoEscopo(tipoRetornoRotina);
            TabelaDeSimbolos escopoPai = escopoCorrente;
            escopoCorrente = gerenciadorDeEscopos.pegarEscopoCorrente();
            
            if (ctx.parametros() != null) {
                
                for (ParametroContext p : ctx.parametros().parametro()) {
                    
                    for (IdentificadorContext id : p.identificador()) {
                        String nomeCompletoId = "";
                        int i = 0;
                        
                        for (TerminalNode ident : id.IDENT()) {
                            
                            if (i++ > 0)
                                nomeCompletoId += ".";
                            nomeCompletoId += ident.getText();
                        }
                        
                        if (escopoCorrente.contem(nomeCompletoId)) {
                            Auxiliar.adicionarErro(id.start, "identificador " + nomeCompletoId + " ja declarado anteriormente");
                        } else {
                            EntradaTabelaDeSimbolos.TiposDados tipoParametro = Auxiliar.converterStringParaTipo(p.tipo_estendido().getText());
                            
                            if (tipoParametro != null) {
                                EntradaTabelaDeSimbolos entrada = new EntradaTabelaDeSimbolos(nomeCompletoId, tipoParametro, EntradaTabelaDeSimbolos.Categorias.VARIAVEL);
                                escopoCorrente.adicionarEntrada(entrada);
                                escopoPai.adicionarDefinicaoDeTipo(ctx.IDENT().getText(), entrada);
                            }
                            else {
                                TerminalNode identTipo = p.tipo_estendido().tipo_basico_ident() != null && p.tipo_estendido().tipo_basico_ident().IDENT() != null ? p.tipo_estendido().tipo_basico_ident().IDENT() : null;
                                
                                if (identTipo != null) {
                                    ArrayList<EntradaTabelaDeSimbolos> variaveisRegistro = null;
                                    boolean tipoRegistroEncontrado = false;
                                    
                                    for (TabelaDeSimbolos t: gerenciadorDeEscopos.obterTodosEscopos()) {
                                        
                                        if (!tipoRegistroEncontrado) {
                                            
                                            if (t.contem(identTipo.getText())) {
                                                variaveisRegistro = t.obterMembrosTipo(identTipo.getText());
                                                tipoRegistroEncontrado = true;
                                            }
                                        }
                                    }
                                    
                                    if (escopoCorrente.contem(nomeCompletoId)) {
                                        Auxiliar.adicionarErro(id.start, "identificador " + nomeCompletoId + " ja declarado anteriormente");
                                    } else {
                                        EntradaTabelaDeSimbolos entrada = new EntradaTabelaDeSimbolos(nomeCompletoId, EntradaTabelaDeSimbolos.TiposDados.REGISTRO, EntradaTabelaDeSimbolos.Categorias.VARIAVEL);
                                        escopoCorrente.adicionarEntrada(entrada);
                                        escopoPai.adicionarDefinicaoDeTipo(ctx.IDENT().getText(), entrada);
                                        
                                        for (EntradaTabelaDeSimbolos s: variaveisRegistro) {
                                            escopoCorrente.adicionarEntrada(nomeCompletoId + "." + s.nomeIdentificador, s.tipoDado, EntradaTabelaDeSimbolos.Categorias.VARIAVEL);
                                        }   
                                    }
                                }
                            }
                        }
                    }
                }
            }
            resultadoVisita = super.visitDeclaracao_global(ctx);
            gerenciadorDeEscopos.sairDoEscopo();
        }   
        return resultadoVisita;
    }

    @Override
    public Object visitIdentificador(IdentificadorContext ctx) {
        String nomeCompletoVariavel = "";
        int i = 0;
        
        for (TerminalNode id : ctx.IDENT()) {
            
            if (i++ > 0)
                nomeCompletoVariavel += ".";
            nomeCompletoVariavel += id.getText();
        }
        boolean erroDeDeclaracao = true;
        
        for (TabelaDeSimbolos escopo : gerenciadorDeEscopos.obterTodosEscopos()) {
            
            if (escopo.contem(nomeCompletoVariavel)) {
                erroDeDeclaracao = false;
            }
        }
        
        if (erroDeDeclaracao)
            Auxiliar.adicionarErro(ctx.start, "identificador " + nomeCompletoVariavel + " nao declarado");    return super.visitIdentificador(ctx);
    }

    @Override
    public Object visitDeclaracao_variavel(Declaracao_variavelContext ctx) {
        TabelaDeSimbolos escopoCorrente = gerenciadorDeEscopos.pegarEscopoCorrente();
        
        for (IdentificadorContext id : ctx.variavel().identificador()) {
            String nomeCompletoId = "";
            int i = 0;
            
            for (TerminalNode ident : id.IDENT()) {
                
                if (i++ > 0)
                    nomeCompletoId += ".";
                nomeCompletoId += ident.getText();
            }
            
            if (escopoCorrente.contem(nomeCompletoId)) {
                Auxiliar.adicionarErro(id.start, "identificador " + nomeCompletoId + " ja declarado anteriormente");
            } else {
                EntradaTabelaDeSimbolos.TiposDados tipoVariavel = Auxiliar.converterStringParaTipo(ctx.variavel().tipo().getText());
                
                if (tipoVariavel != null)
                    escopoCorrente.adicionarEntrada(nomeCompletoId, tipoVariavel, EntradaTabelaDeSimbolos.Categorias.VARIAVEL);
                else {
                    TerminalNode identTipo = ctx.variavel().tipo() != null && ctx.variavel().tipo().tipo_estendido() != null && ctx.variavel().tipo().tipo_estendido().tipo_basico_ident() != null && ctx.variavel().tipo().tipo_estendido().tipo_basico_ident().IDENT() != null ? ctx.variavel().tipo().tipo_estendido().tipo_basico_ident().IDENT() : null;
                    
                    if (identTipo != null) {
                        ArrayList<EntradaTabelaDeSimbolos> variaveisRegistro = null;
                        boolean tipoRegistroEncontrado = false;
                        
                        for (TabelaDeSimbolos t: gerenciadorDeEscopos.obterTodosEscopos()) {
                            if (!tipoRegistroEncontrado) {
                                
                                if (t.contem(identTipo.getText())) {
                                    variaveisRegistro = t.obterMembrosTipo(identTipo.getText());
                                    tipoRegistroEncontrado = true;
                                }
                            }
                        }
                        
                        if (escopoCorrente.contem(nomeCompletoId)) {
                            Auxiliar.adicionarErro(id.start, "identificador " + nomeCompletoId
                                        + " ja declarado anteriormente");
                        } else {
                            escopoCorrente.adicionarEntrada(nomeCompletoId, EntradaTabelaDeSimbolos.TiposDados.REGISTRO, EntradaTabelaDeSimbolos.Categorias.VARIAVEL);
                            
                            for (EntradaTabelaDeSimbolos s: variaveisRegistro) {
                                escopoCorrente.adicionarEntrada(nomeCompletoId + "." + s.nomeIdentificador, s.tipoDado, EntradaTabelaDeSimbolos.Categorias.VARIAVEL);
                            }   
                        }
                    }
                    else 
                    if (ctx.variavel().tipo().registro() != null) {
                        ArrayList<EntradaTabelaDeSimbolos> membrosRegistro = new ArrayList<>();
                        
                        for (VariavelContext variavel : ctx.variavel().tipo().registro().variavel()) {
                            EntradaTabelaDeSimbolos.TiposDados tipoMembro =  Auxiliar.converterStringParaTipo(variavel.tipo().getText());
                            
                            for (IdentificadorContext idCampo : variavel.identificador()) {
                                membrosRegistro.add(new EntradaTabelaDeSimbolos(idCampo.getText(), tipoMembro, EntradaTabelaDeSimbolos.Categorias.VARIAVEL));
                            }
                        }  
                        escopoCorrente.adicionarEntrada(nomeCompletoId, EntradaTabelaDeSimbolos.TiposDados.REGISTRO, EntradaTabelaDeSimbolos.Categorias.VARIAVEL);

                        
                        for (EntradaTabelaDeSimbolos membro : membrosRegistro) {
                            String nomeCompletoMembro = nomeCompletoId + '.' + membro.nomeIdentificador;
                            
                            if (escopoCorrente.contem(nomeCompletoMembro)) {
                                Auxiliar.adicionarErro(id.start, "identificador " + nomeCompletoMembro + " ja declarado anteriormente");
                            }
                            else {
                                escopoCorrente.adicionarEntrada(membro);
                                escopoCorrente.adicionarEntrada(nomeCompletoMembro, membro.tipoDado, EntradaTabelaDeSimbolos.Categorias.VARIAVEL);
                            }
                        }
                    }
                    else {
                        escopoCorrente.adicionarEntrada(id.getText(), EntradaTabelaDeSimbolos.TiposDados.INTEIRO, EntradaTabelaDeSimbolos.Categorias.VARIAVEL);
                    }
                }
            }
        }   
        return super.visitDeclaracao_variavel(ctx);
    }

    @Override
    public Object visitCmdRetorne(CmdRetorneContext ctx) {
        
        if (gerenciadorDeEscopos.pegarEscopoCorrente().tipoRetorno == EntradaTabelaDeSimbolos.TiposDados.NULO) {
            Auxiliar.adicionarErro(ctx.start, "comando retorne nao permitido nesse escopo");
        }   return super.visitCmdRetorne(ctx);
    }

    @Override
    public Object visitCmdAtribuicao(CmdAtribuicaoContext ctx) {
        EntradaTabelaDeSimbolos.TiposDados tipoDaExpressao = Auxiliar.obterTipoExpressao(gerenciadorDeEscopos, ctx.expressao());
        boolean erroDeAtribuicao = false;
        String caracterePonteiro = ctx.getText().charAt(0) == '^' ? "^" : "";
        String nomeDaVariavel = "";
        int i = 0;
        
        for (TerminalNode id : ctx.identificador().IDENT()) {
            
            if (i++ > 0)
                nomeDaVariavel += ".";
            nomeDaVariavel += id.getText();
        }
        
        if (tipoDaExpressao != EntradaTabelaDeSimbolos.TiposDados.INDEFINIDO) {
            boolean variavelEncontrada = false;
            
            for (TabelaDeSimbolos escopo : gerenciadorDeEscopos.obterTodosEscopos()) {
                
                if (escopo.contem(nomeDaVariavel) && !variavelEncontrada)  {
                    variavelEncontrada = true;
                    EntradaTabelaDeSimbolos.TiposDados tipoDaVariavel = Auxiliar.obterTipoVariavel(gerenciadorDeEscopos, nomeDaVariavel);
                    Boolean variavelNumerica = tipoDaVariavel == EntradaTabelaDeSimbolos.TiposDados.REAL || tipoDaVariavel == EntradaTabelaDeSimbolos.TiposDados.INTEIRO;
                    Boolean expressaoNumerica = tipoDaExpressao == EntradaTabelaDeSimbolos.TiposDados.REAL || tipoDaExpressao == EntradaTabelaDeSimbolos.TiposDados.INTEIRO;
                    if  (!(variavelNumerica && expressaoNumerica) && tipoDaVariavel != tipoDaExpressao && tipoDaExpressao != EntradaTabelaDeSimbolos.TiposDados.INDEFINIDO) {
                        erroDeAtribuicao = true;
                    }
                } 
            }
        } else {
            erroDeAtribuicao = true;
        }
        
        if (erroDeAtribuicao) {
            nomeDaVariavel = ctx.identificador().getText();
            Auxiliar.adicionarErro(ctx.identificador().start, "atribuicao nao compativel para " + caracterePonteiro + nomeDaVariavel );
        }
        return super.visitCmdAtribuicao(ctx);
    }

    @Override
    public Object visitParcela_unario(Parcela_unarioContext ctx) {
        TabelaDeSimbolos escopoCorrente = gerenciadorDeEscopos.pegarEscopoCorrente();
        
        if (ctx.IDENT() != null) {
            String nomeChamada = ctx.IDENT().getText();
            
            if (escopoCorrente.contem(ctx.IDENT().getText())) {
                List<EntradaTabelaDeSimbolos> parametrosEsperados = escopoCorrente.obterMembrosTipo(nomeChamada);
                boolean erroDeParametros = false;
                
                if (parametrosEsperados.size() != ctx.expressao().size()) {
                    erroDeParametros = true;
                } else {
                    
                    for (int i = 0; i < parametrosEsperados.size(); i++) {
                        
                        if (parametrosEsperados.get(i).tipoDado != Auxiliar.obterTipoExpressao(gerenciadorDeEscopos, ctx.expressao().get(i))) {
                            erroDeParametros = true;
                        }
                    }
                }
                
                if (erroDeParametros) {
                    Auxiliar.adicionarErro(ctx.start, "incompatibilidade de parametros na chamada de " + nomeChamada);
                }
            }
        }
        return super.visitParcela_unario(ctx);
    }
}