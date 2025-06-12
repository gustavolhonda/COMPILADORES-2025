package br.ufscar.dc.compiladores.alguma.semantico;

import java.util.ArrayList;
import java.util.Arrays;
import org.antlr.v4.runtime.tree.TerminalNode;
import br.ufscar.dc.compiladores.alguma.semantico.LAParser.*;

public class Gerador extends LABaseVisitor<Void> {
    StringBuilder saida;
    TabelaDeSimbolos tabela;

    public Gerador() {
        saida = new StringBuilder();
        this.tabela = new TabelaDeSimbolos(null);
    }

    @Override
    public Void visitPrograma(LAParser.ProgramaContext ctx) {
        saida.append("#include <stdio.h>\n");
        saida.append("#include <stdlib.h>\n");
        saida.append("\n");

        // Declarar e gerar código das declarações
        ctx.declaracoes().decl_local_global().forEach(dec -> visitDecl_local_global(dec));
        
        saida.append("\n");
        saida.append("int main() {\n");

        // Gerar o corpo
        visitCorpo(ctx.corpo());
        
        saida.append("return 0;\n");
        saida.append("}\n");

        return null;
    }

    // Visita a declaração local ou global
    @Override
    public Void visitDecl_local_global(Decl_local_globalContext ctx) {
        if (ctx.declaracao_local() != null) {
            visitDeclaracao_local(ctx.declaracao_local());
        } else if (ctx.declaracao_global() != null) {
            visitDeclaracao_global(ctx.declaracao_global());
        }
        return null;
    }

    // Gerar código do corpo do programa
    @Override
    public Void visitCorpo(CorpoContext ctx) {
        for (LAParser.Declaracao_localContext dec : ctx.declaracao_local()) {
            visitDeclaracao_local(dec);
        }

        for (LAParser.CmdContext com : ctx.cmd()) {
            visitCmd(com);
        }
        return null;
    }

    // Para cada tipo de declaração global (funções, procedimentos)
    @Override
    public Void visitDeclaracao_global(Declaracao_globalContext ctx) {
        if (ctx.getText().contains("procedimento")) {
            saida.append("void " + ctx.IDENT().getText() + "(");
        } else {
            String cTipo = Auxiliar.getCType(ctx.tipo_estendido().getText().replace("^", ""));
            EntradaTabelaDeSimbolos.TiposDados tipo = Auxiliar.converterStringParaTipo(ctx.tipo_estendido().getText());
            visitTipo_estendido(ctx.tipo_estendido());
            if (cTipo.equals("char")) {
                saida.append("[80]");
            }
            saida.append(" " + ctx.IDENT().getText() + "(");
            tabela.adicionarEntrada(ctx.IDENT().getText(), tipo, EntradaTabelaDeSimbolos.Categorias.FUNCAO);
        }
        ctx.parametros().parametro().forEach(var -> visitParametro(var));
        saida.append(") {\n");

        // Gerar código para variáveis locais e comandos dentro da função/procedimento
        ctx.declaracao_local().forEach(var -> visitDeclaracao_local(var));
        ctx.cmd().forEach(var -> visitCmd(var));

        saida.append("}\n");
        return null;
    }

    // Imprime identificadores
    @Override
    public Void visitIdentificador(IdentificadorContext ctx) {
        saida.append(" ");
        int i = 0;
        for (TerminalNode id : ctx.IDENT()) {
            if (i++ > 0)
                saida.append(".");
            saida.append(id.getText());
        }
        visitDimensao(ctx.dimensao());

        return null;
    }

    // Imprime dimensões
    @Override
    public Void visitDimensao(DimensaoContext ctx) {
        for (Exp_aritmeticaContext exp : ctx.exp_aritmetica()) {
            saida.append("[");
            visitExp_aritmetica(exp);
            saida.append("]");
        }

        return null;
    }

    // Gerar código para parâmetros de funções
    @Override
    public Void visitParametro(ParametroContext ctx) {
        int i = 0;
        String cTipo = Auxiliar.getCType(ctx.tipo_estendido().getText().replace("^", ""));
        EntradaTabelaDeSimbolos.TiposDados tipo = Auxiliar.converterStringParaTipo(ctx.tipo_estendido().getText());
        for (IdentificadorContext id : ctx.identificador()) {
            if (i++ > 0)
                saida.append(", ");
            visitTipo_estendido(ctx.tipo_estendido());
            visitIdentificador(id);
            if (cTipo.equals("char")) {
                saida.append("[80]");
            }
            tabela.adicionarEntrada(id.getText(), tipo, EntradaTabelaDeSimbolos.Categorias.VARIAVEL);
        }
        return null;
    }

    // Gerar código para as variáveis locais
    @Override
    public Void visitDeclaracao_variavel(Declaracao_variavelContext ctx) {
        visitVariavel(ctx.variavel());
        return null;
    }

    // Verifica o tipo de declaração e redireciona
    @Override
    public Void visitDeclaracao_local(Declaracao_localContext ctx) {
        if (ctx.declaracao_variavel() != null) {
            visitDeclaracao_variavel(ctx.declaracao_variavel());
        } else if (ctx.declaracao_constante() != null) {
            visitDeclaracao_constante(ctx.declaracao_constante());
        } else if (ctx.declaracao_tipo() != null) {
            visitDeclaracao_tipo(ctx.declaracao_tipo());
        }

        return null;
    }

    // Declara um novo tipo
    @Override
    public Void visitDeclaracao_tipo(Declaracao_tipoContext ctx) {
        saida.append("typedef ");
        EntradaTabelaDeSimbolos.TiposDados tipo = Auxiliar.converterStringParaTipo(ctx.tipo().getText());
        if (ctx.tipo().getText().contains("registro")) {
            for (VariavelContext sub : ctx.tipo().registro().variavel()) {
                for (IdentificadorContext idIns : sub.identificador()) {
                    EntradaTabelaDeSimbolos.TiposDados tipoIns = Auxiliar.converterStringParaTipo(sub.tipo().getText());
                    tabela.adicionarEntrada(ctx.IDENT().getText() + "." + idIns.getText(), tipoIns, EntradaTabelaDeSimbolos.Categorias.VARIAVEL);
                    tabela.adicionarEntrada(ctx.IDENT().getText(), tipoIns, EntradaTabelaDeSimbolos.Categorias.TIPO_DEFINIDO);
                }
            }
        }
        tabela.adicionarEntrada(ctx.IDENT().getText(), tipo, EntradaTabelaDeSimbolos.Categorias.VARIAVEL);
        visitTipo(ctx.tipo());
        saida.append(ctx.IDENT() + ";\n");

        return null;
    }

    // Para variáveis, insere na tabela de símbolos e gera a declaração
    @Override
    public Void visitVariavel(VariavelContext ctx) {
        String cTipo = Auxiliar.getCType(ctx.tipo().getText().replace("^", ""));
        EntradaTabelaDeSimbolos.TiposDados tipo = Auxiliar.converterStringParaTipo(ctx.tipo().getText());
        for (LAParser.IdentificadorContext id : ctx.identificador()) {
            if (ctx.tipo().getText().contains("registro")) {
                for (VariavelContext sub : ctx.tipo().registro().variavel()) {
                    for (IdentificadorContext idIns : sub.identificador()) {
                        EntradaTabelaDeSimbolos.TiposDados tipoIns = Auxiliar.converterStringParaTipo(sub.tipo().getText());
                        tabela.adicionarEntrada(id.getText() + "." + idIns.getText(), tipoIns, EntradaTabelaDeSimbolos.Categorias.VARIAVEL);
                    }
                }
            } else if (cTipo == null && tipo == null) {
                ArrayList<EntradaTabelaDeSimbolos> arg = tabela.obterMembrosTipo(ctx.tipo().getText());
                if (arg != null) {
                    for (EntradaTabelaDeSimbolos val : arg) {
                        tabela.adicionarEntrada(id.getText() + "." + val.nomeIdentificador, val.tipoDado, EntradaTabelaDeSimbolos.Categorias.VARIAVEL);
                    }
                }
            }
            if (id.getText().contains("[")) {
                int ini = id.getText().indexOf("[", 0);
                int end = id.getText().indexOf("]", 0);
                String tam = end - ini == 2 ? String.valueOf(id.getText().charAt(ini + 1)) : id.getText().substring(ini + 1, end - 1);
                String name = id.IDENT().get(0).getText();
                for (int i = 0; i < Integer.parseInt(tam); i++) {
                    tabela.adicionarEntrada(name + "[" + i + "]", tipo, EntradaTabelaDeSimbolos.Categorias.VARIAVEL);
                }
            } else {
                tabela.adicionarEntrada(id.getText(), tipo, EntradaTabelaDeSimbolos.Categorias.VARIAVEL);
            }
            visitTipo(ctx.tipo());
            visitIdentificador(id);
            if (cTipo == "char") {
                saida.append("[80]");
            }
            saida.append(";\n");
        }

        return null;
    }

    // Visita e define tipos
     public Void visitTipo(TipoContext ctx) {
        String cTipo = Auxiliar.getCType(ctx.getText().replace("^", ""));
        boolean pointer = ctx.getText().contains("^");
        if (cTipo != null) {
            saida.append(cTipo);
        } else if (ctx.registro() != null) {
            visitRegistro(ctx.registro());
        } else {
            visitTipo_estendido(ctx.tipo_estendido());
        }
        if (pointer)
            saida.append("*");
        saida.append(" ");

        return null;
    }

    // visita ponteiros
    @Override
    public Void visitTipo_estendido(Tipo_estendidoContext ctx) {
        visitTipo_basico_ident(ctx.tipo_basico_ident());
        if (ctx.getText().contains("^"))
            saida.append("*");

        return null;
    }

    // imprime tipos básicos
     @Override
    public Void visitTipo_basico_ident(Tipo_basico_identContext ctx) {
        if (ctx.IDENT() != null) {
            saida.append(ctx.IDENT().getText());
        } else {
            saida.append(Auxiliar.getCType(ctx.getText().replace("^", "")));
        }

        return null;
    }

    // Cria um struct para registros
     @Override
    public Void visitRegistro(RegistroContext ctx) {
        saida.append("struct {\n");
        ctx.variavel().forEach(var -> visitVariavel(var));
        saida.append("} ");

        return null;
    }

    // Declara constantes
    @Override
    public Void visitDeclaracao_constante(Declaracao_constanteContext ctx) {
        String type = Auxiliar.getCType(ctx.tipo_basico().getText());
        EntradaTabelaDeSimbolos.TiposDados typeVar = Auxiliar.converterStringParaTipo(ctx.tipo_basico().getText());
        tabela.adicionarEntrada(ctx.IDENT().getText(), typeVar, EntradaTabelaDeSimbolos.Categorias.VARIAVEL);
        saida.append("const " + type + " " + ctx.IDENT().getText() + " = ");
        visitValor_constante(ctx.valor_constante());
        saida.append(";\n");

        return null;
    }

    // retorna o valor constante
    @Override
    public Void visitValor_constante(Valor_constanteContext ctx) {
        if (ctx.getText().equals("verdadeiro")) {
            saida.append("true");
        } else if (ctx.getText().equals("falso")) {
            saida.append("false");
        } else {
            saida.append(ctx.getText());
        }

        return null;
    }

    // Converte e gera código para expressões e operações
    @Override
    public Void visitExpressao(ExpressaoContext ctx) {
        if (ctx.termo_logico() != null) {
            visitTermo_logico(ctx.termo_logico(0));

            for (int i = 1; i < ctx.termo_logico().size(); i++) {
                LAParser.Termo_logicoContext termo = ctx.termo_logico(i);
                saida.append(" || ");
                visitTermo_logico(termo);
            }
        }

        return null;
    }

    // vê o comando correto
     @Override
    public Void visitCmd(CmdContext ctx) {
        if (ctx.cmdLeia() != null) {
            visitCmdLeia(ctx.cmdLeia());
        } else if (ctx.cmdEscreva() != null) {
            visitCmdEscreva(ctx.cmdEscreva());
        } else if (ctx.cmdAtribuicao() != null) {
            visitCmdAtribuicao(ctx.cmdAtribuicao());
        } else if (ctx.cmdSe() != null) {
            visitCmdSe(ctx.cmdSe());
        } else if (ctx.cmdCaso() != null) {
            visitCmdCaso(ctx.cmdCaso());
        } else if (ctx.cmdPara() != null) {
            visitCmdPara(ctx.cmdPara());
        } else if (ctx.cmdEnquanto() != null) {
            visitCmdEnquanto(ctx.cmdEnquanto());
        } else if (ctx.cmdFaca() != null) {
            visitCmdFaca(ctx.cmdFaca());
        } else if (ctx.cmdChamada() != null) {
            visitCmdChamada(ctx.cmdChamada());
        } else if (ctx.cmdRetorne() != null) {
            visitCmdRetorne(ctx.cmdRetorne());
        }

        return null;
    }
    
    // return
    @Override
    public Void visitCmdRetorne(CmdRetorneContext ctx) {
        saida.append("return ");
        visitExpressao(ctx.expressao());
        saida.append(";\n");

        return null;
    }

    // chamada de função
     @Override
    public Void visitCmdChamada(CmdChamadaContext ctx) {
        saida.append(ctx.IDENT().getText() + "(");
        int i = 0;
        for (ExpressaoContext exp : ctx.expressao()) {
            if (i++ > 0)
                saida.append(", ");
            visitExpressao(exp);
        }
        saida.append(");\n");

        return null;
    }

    // Comandos para manipulação de variáveis
    @Override
    public Void visitCmdLeia(CmdLeiaContext ctx) {
        for (LAParser.IdentificadorContext id : ctx.identificador()) {
            EntradaTabelaDeSimbolos.TiposDados idType = tabela.consultarTipo(id.getText());
            if (idType != EntradaTabelaDeSimbolos.TiposDados.TEXTO) {
                saida.append("scanf(\"%");
                saida.append(Auxiliar.getCTypeSymbol(idType));
                saida.append("\", &");
                saida.append(id.getText());
                saida.append(");\n");
            } else {
                saida.append("gets(");
                visitIdentificador(id);
                saida.append(");\n");
            }
        }

        return null;
    }

    // Gerar código para a impressão de variáveis
    @Override
    public Void visitCmdEscreva(CmdEscrevaContext ctx) {
         for (LAParser.ExpressaoContext exp : ctx.expressao()) {
            Escopo escopo = new Escopo(tabela);
            String cType = Auxiliar.getCTypeSymbol(Auxiliar.obterTipoExpressao(escopo, exp));
            if (tabela.contem(exp.getText())) {
                EntradaTabelaDeSimbolos.TiposDados tipo = tabela.consultarTipo(exp.getText());
                cType = Auxiliar.getCTypeSymbol(tipo);
            }
            saida.append("printf(\"%");
            saida.append(cType);
            saida.append("\", ");
            saida.append(exp.getText());
            saida.append(");\n");
        }

        return null;
    }

    // Gerar código de atribuição
    @Override
    public Void visitCmdAtribuicao(CmdAtribuicaoContext ctx) {
        if (ctx.getText().contains("^"))
            saida.append("*");
        EntradaTabelaDeSimbolos.TiposDados tipo = tabela.consultarTipo(ctx.identificador().getText());

        if (tipo != null && tipo == EntradaTabelaDeSimbolos.TiposDados.TEXTO) {
            saida.append("strcpy(");
            visitIdentificador(ctx.identificador());
            saida.append("," + ctx.expressao().getText() + ");\n");
        } else {
            visitIdentificador(ctx.identificador());
            saida.append(" = ");
            saida.append(ctx.expressao().getText());
            saida.append(";\n");
        }

        return null;
    }

    // Implementação de estruturas de controle (if, switch)
    @Override
    public Void visitCmdSe(CmdSeContext ctx) {
       saida.append("if (");
        visitExpressao(ctx.expressao());
        saida.append(") {\n");
        for (CmdContext cmd : ctx.cmd()) {
            visitCmd(cmd);
        }
        saida.append("}\n");
        if (ctx.cmdSenao() != null) {
            saida.append("else {\n");
            for (CmdContext cmd : ctx.cmdSenao().cmd()) {
                visitCmd(cmd);
            }
            saida.append("}\n");
        }

        return null;
    }

    // visita termos lógicos
     @Override
    public Void visitTermo_logico(Termo_logicoContext ctx) {
        visitFator_logico(ctx.fator_logico(0));

        for (int i = 1; i < ctx.fator_logico().size(); i++) {
            LAParser.Fator_logicoContext fator = ctx.fator_logico(i);
            saida.append(" && ");
            visitFator_logico(fator);
        }

        return null;
    }

    // visita fatores lógicos
     @Override
    public Void visitFator_logico(Fator_logicoContext ctx) {
        if (ctx.getText().startsWith("nao")) {
            saida.append("!");
        }
        visitParcela_logica(ctx.parcela_logica());

        return null;
    }

    // visita parcela lógica
    @Override
    public Void visitParcela_logica(Parcela_logicaContext ctx) {
        if (ctx.exp_relacional() != null) {
            visitExp_relacional(ctx.exp_relacional());
        } else {
            if (ctx.getText() == "verdadeiro") {
                saida.append("true");
            } else {
                saida.append("false");
            }
        }

        return null;
    }

    // Visita expressões relacionais e converte para C
    @Override
    public Void visitExp_relacional(Exp_relacionalContext ctx) {
        visitExp_aritmetica(ctx.exp_aritmetica(0));
        for (int i = 1; i < ctx.exp_aritmetica().size(); i++) {
            LAParser.Exp_aritmeticaContext termo = ctx.exp_aritmetica(i);
            if (ctx.op_relacional().getText().equals("=")) {
                saida.append(" == ");
            } else {
                saida.append(ctx.op_relacional().getText());
            }
            visitExp_aritmetica(termo);
        }

        return null;
    }

    // Visita expressões aritméticas
    @Override
    public Void visitExp_aritmetica(Exp_aritmeticaContext ctx) {
        visitTermo(ctx.termo(0));

        for (int i = 1; i < ctx.termo().size(); i++) {
            LAParser.TermoContext termo = ctx.termo(i);
            saida.append(ctx.op1(i - 1).getText());
            visitTermo(termo);
        }

        return null;
    }

    // Visita termos aritméticos
    @Override
    public Void visitTermo(TermoContext ctx) {
        visitFator(ctx.fator(0));

        for (int i = 1; i < ctx.fator().size(); i++) {
            LAParser.FatorContext fator = ctx.fator(i);
            saida.append(ctx.op2(i - 1).getText());
            visitFator(fator);
        }

        return null;
    }

    // Visita fatores aritméticos
    @Override
    public Void visitFator(FatorContext ctx) {
        visitParcela(ctx.parcela(0));

        for (int i = 1; i < ctx.parcela().size(); i++) {
            LAParser.ParcelaContext parcela = ctx.parcela(i);
            saida.append(ctx.op3(i - 1).getText());
            visitParcela(parcela);
        }

        return null;
    }

    // Redireciona parcelas para unárias ou não-unárias
    @Override
    public Void visitParcela(ParcelaContext ctx) {
        if (ctx.parcela_unario() != null) {
            if (ctx.op_unario() != null) {
                saida.append(ctx.op_unario().getText());
            }
            visitParcela_unario(ctx.parcela_unario());
        } else {
            visitParcela_nao_unario(ctx.parcela_nao_unario());
        }

        return null;
    }

    // Visita parcelas unárias
    @Override
    public Void visitParcela_unario(Parcela_unarioContext ctx) {
        if (ctx.IDENT() != null) {
            saida.append(ctx.IDENT().getText());
            saida.append("(");
            for (int i = 0; i < ctx.expressao().size(); i++) {
                visitExpressao(ctx.expressao(i));
                if (i < ctx.expressao().size() - 1) {
                    saida.append(", ");
                }
            }
        } else if (ctx.parentesis_expressao() != null) {
            saida.append("(");
            visitExpressao(ctx.parentesis_expressao().expressao());
            saida.append(")");
        } else {
            saida.append(ctx.getText());
        }

        return null;
    }

    // Visita parcelas não-unárias
    @Override
    public Void visitParcela_nao_unario(Parcela_nao_unarioContext ctx) {
        saida.append(ctx.getText());

        return null;
    }

    // caso -> switch case 
     @Override
    public Void visitCmdCaso(CmdCasoContext ctx) {
        saida.append("switch (");
        visit(ctx.exp_aritmetica());
        saida.append(") {\n");
        visit(ctx.selecao());
        if (ctx.cmdSenao() != null) {
            visit(ctx.cmdSenao());
        }
        saida.append("}\n");

        return null;
    }

     @Override
    public Void visitSelecao(SelecaoContext ctx) {
        ctx.item_selecao().forEach(var -> visitItem_selecao(var));

        return null;
    }

     @Override
    public Void visitItem_selecao(Item_selecaoContext ctx) {
        ArrayList<String> intervalo = new ArrayList<>(Arrays.asList(ctx.constantes().getText().split("\\.\\.")));
        String first = intervalo.size() > 0 ? intervalo.get(0) : ctx.constantes().getText();
        String last = intervalo.size() > 1 ? intervalo.get(1) : intervalo.get(0);
        for (int i = Integer.parseInt(first); i <= Integer.parseInt(last); i++) {
            saida.append("case " + i + ":\n");
            ctx.cmd().forEach(var -> visitCmd(var));
            saida.append("break;\n");
        }

        return null;
    }

    // Implementa o comando 'senao' como 'default' no switch-case
    @Override
    public Void visitCmdSenao(CmdSenaoContext ctx) {
        saida.append("default:\n");
        ctx.cmd().forEach(var -> visitCmd(var));
        saida.append("break;\n");

        return null;
    }

    // Cria o loop for até o valor passado
    @Override
    public Void visitCmdPara(CmdParaContext ctx) {
        String id = ctx.IDENT().getText();
        saida.append("for (" + id + " = ");
        visitExp_aritmetica(ctx.exp_aritmetica(0));
        saida.append("; " + id + " <= ");
        visitExp_aritmetica(ctx.exp_aritmetica(1));
        saida.append("; " + id + "++) {\n");
        ctx.cmd().forEach(var -> visitCmd(var));
        saida.append("}\n");

        return null;
    }

    // Implementa o loop while em C
    @Override
    public Void visitCmdEnquanto(CmdEnquantoContext ctx) {
        saida.append("while (");
        visitExpressao(ctx.expressao());
        saida.append(") {\n");
        ctx.cmd().forEach(var -> visitCmd(var));
        saida.append("}\n");

        return null;
    }

    // Implementa o loop do-while em C
    @Override
    public Void visitCmdFaca(CmdFacaContext ctx) {
        saida.append("do {\n");
        ctx.cmd().forEach(var -> visitCmd(var));
        saida.append("} while (");
        visitExpressao(ctx.expressao());
        saida.append(");\n");

        return null;
    }

    // Ajudar na geração do código final
    public String gerarCodigo() {
        return saida.toString();
    }
}
