package br.ufscar.dc.compiladores.alguma.sintatico;

import java.io.FileWriter;
import java.io.IOException;
import java.util.BitSet;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

// Implementa a interface ANTLRErrorListener para customizar a saída de erros
public class CustomError implements ANTLRErrorListener {

    // Realiza a escrita no arquivo na classe, não mais em Principal como no T1
    private FileWriter file;
    public CustomError(FileWriter file) {
        this.file = file;
    }

    //Analisa o tipo de erro
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        try {
            Token t = (Token) offendingSymbol;
            String nomeToken = LALexer.VOCABULARY.getDisplayName(t.getType());
    
            //Tratamento de erros
            switch (nomeToken) {
                case "ERRO":
                    file.write("Linha " + t.getLine() + ": " + t.getText() + " - simbolo nao identificado\n");
                    break;
                case "COMENTARIO_NAO_FECHADO":
                    file.write("Linha " + t.getLine() + ": comentario nao fechado\n");
                    break;
                case "CADEIA_NAO_FECHADA":
                    file.write("Linha " + t.getLine() + ": cadeia literal nao fechada\n");
                    break;
                case "EOF":
                    file.write("Linha " + t.getLine() + ": erro sintatico proximo a EOF\n");
                    break;
                default:
                    //Erros default
                    file.write("Linha " + t.getLine() + ": erro sintatico proximo a " + t.getText() + "\n");
                    break;
            }
    
            file.write("Fim da compilacao\n");
            file.close();

        } catch (IOException ex) {}
    }

    // Não são necessários no T2
    @Override
    public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact, BitSet ambigAlts, ATNConfigSet configs) {}

    @Override
    public void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex, BitSet conflictingAlts, ATNConfigSet configs) {}

    @Override
    public void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, int prediction, ATNConfigSet configs) {}
}