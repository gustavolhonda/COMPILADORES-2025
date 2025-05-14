package br.ufscar.dc.compiladores.alguma.semantico;

import java.util.Iterator;

import br.ufscar.dc.compiladores.alguma.semantico.LAParser.CmdAtribuicaoContext;
import br.ufscar.dc.compiladores.alguma.semantico.LAParser.Declaracao_constanteContext;
import br.ufscar.dc.compiladores.alguma.semantico.LAParser.Declaracao_globalContext;
import br.ufscar.dc.compiladores.alguma.semantico.LAParser.Declaracao_variavelContext;
import br.ufscar.dc.compiladores.alguma.semantico.LAParser.IdentificadorContext;
import br.ufscar.dc.compiladores.alguma.semantico.LAParser.Tipo_basico_identContext;

public class LASemantico extends LABaseVisitor<Object> {

    private Escopo escopos = new Escopo();

    @Override
    public Object visitCorpo(LAParser.CorpoContext ctx) {
        // Verifica comandos de retorno no escopo principal
        for (LAParser.CmdContext cmd : ctx.cmd()) {
            if (cmd.cmdRetorne() != null) {
                Auxiliar.adicionarErroSemantico(cmd.getStart(), "comando retorne nao permitido nesse escopo");
            }
        }
        return super.visitCorpo(ctx);
    }

    @Override
    public Object visitDeclaracao_constante(Declaracao_constanteContext ctx) {
        // Processa declaração de constante
        TabelaDeSimbolos tabela = escopos.obterEscopoAtual();
        String nomeConstante = ctx.IDENT().getText();
        
        if (tabela.possui(nomeConstante)) {
            Auxiliar.adicionarErroSemantico(ctx.start, "constante " + nomeConstante + " ja declarado anteriormente");
            return super.visitDeclaracao_constante(ctx);
        }
        
        EntradaTabelaDeSimbolos.Tipos tipo = obterTipo(ctx.tipo_basico().getText());
        tabela.inserir(nomeConstante, tipo);
        
        return super.visitDeclaracao_constante(ctx);
    }

    @Override
    public Object visitDeclaracao_variavel(Declaracao_variavelContext ctx) {
        // Registra variáveis na tabela de símbolos
        TabelaDeSimbolos tabela = escopos.obterEscopoAtual();
        String tipoTexto = ctx.variavel().tipo().getText();
        EntradaTabelaDeSimbolos.Tipos tipo = obterTipo(tipoTexto);
        
        for (IdentificadorContext id : ctx.variavel().identificador()) {
            String nomeVar = id.getText();
            if (tabela.possui(nomeVar)) {
                Auxiliar.adicionarErroSemantico(id.start, "identificador " + nomeVar + " ja declarado anteriormente");
            } else {
                tabela.inserir(nomeVar, tipo);
            }
        }
        return super.visitDeclaracao_variavel(ctx);
    }

    @Override
    public Object visitDeclaracao_global(Declaracao_globalContext ctx) {
        // Processa declaração de tipo global
        TabelaDeSimbolos tabela = escopos.obterEscopoAtual();
        String nome = ctx.IDENT().getText();
        
        if (tabela.possui(nome)) {
            Auxiliar.adicionarErroSemantico(ctx.start, nome + " ja declarado anteriormente");
        } else {
            tabela.inserir(nome, EntradaTabelaDeSimbolos.Tipos.TIPO);
        }
        return super.visitDeclaracao_global(ctx);
    }

    @Override
    public Object visitTipo_basico_ident(Tipo_basico_identContext ctx) {
        // Verifica se o tipo existe
        if (ctx.IDENT() != null) {
            String nomeTipo = ctx.IDENT().getText();
            if (!verificarExistencia(nomeTipo)) {
                Auxiliar.adicionarErroSemantico(ctx.start, "tipo " + nomeTipo + " nao declarado");
            }
        }
        return super.visitTipo_basico_ident(ctx);
    }

    @Override
    public Object visitIdentificador(IdentificadorContext ctx) {
        // Verifica se o identificador foi declarado
        String nome = ctx.IDENT(0).getText();
        if (!verificarExistencia(nome)) {
            Auxiliar.adicionarErroSemantico(ctx.start, "identificador " + nome + " nao declarado");
        }
        return super.visitIdentificador(ctx);
    }

    @Override
    public Object visitCmdAtribuicao(CmdAtribuicaoContext ctx) {
        // Verifica compatibilidade de tipos na atribuição
        EntradaTabelaDeSimbolos.Tipos tipoExpr = Auxiliar.verificarTipo(escopos, ctx.expressao());
        String nomeVar = ctx.identificador().getText();
        
        if (tipoExpr != EntradaTabelaDeSimbolos.Tipos.INVALIDO && verificarExistencia(nomeVar)) {
            EntradaTabelaDeSimbolos.Tipos tipoVar = Auxiliar.verificarTipo(escopos, nomeVar);
            
            boolean tiposCompativeis = tipoVar == tipoExpr || (ehTipoNumerico(tipoVar) && ehTipoNumerico(tipoExpr));
            
            if (!tiposCompativeis) {
                Auxiliar.adicionarErroSemantico(ctx.identificador().start, "atribuicao nao compativel para " + nomeVar);
            }
        } else if (verificarExistencia(nomeVar)) {
            Auxiliar.adicionarErroSemantico(ctx.identificador().start, "atribuicao nao compativel para " + nomeVar);
        }
        
        return super.visitCmdAtribuicao(ctx);
    }
    
    // Métodos auxiliares
    private EntradaTabelaDeSimbolos.Tipos obterTipo(String tipo) {
        switch (tipo) {
            case "logico": return EntradaTabelaDeSimbolos.Tipos.LOGICO;
            case "literal": return EntradaTabelaDeSimbolos.Tipos.CADEIA;
            case "real": return EntradaTabelaDeSimbolos.Tipos.REAL;
            default: return EntradaTabelaDeSimbolos.Tipos.INT;
        }
    }
    
    private boolean verificarExistencia(String nome) {
        for (TabelaDeSimbolos escopo : escopos.percorrerEscoposAninhados()) {
            if (escopo.possui(nome)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean ehTipoNumerico(EntradaTabelaDeSimbolos.Tipos tipo) {
        return tipo == EntradaTabelaDeSimbolos.Tipos.REAL || tipo == EntradaTabelaDeSimbolos.Tipos.INT;
    }
}