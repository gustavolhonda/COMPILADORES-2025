package br.ufscar.dc.compiladores.alguma.semantico;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.antlr.v4.runtime.CommonTokenStream;

import br.ufscar.dc.compiladores.alguma.semantico.LAParser.ProgramaContext;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import java.util.Iterator;

public class Principal {
    public static void main(String args[]) throws IOException {
        // Lexer
        CharStream cs = CharStreams.fromFileName(args[0]);
        LALexer lexer = new LALexer(cs);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        
        // Parser
        LAParser parser = new LAParser(tokens);
        ProgramaContext arvore = parser.programa();
        
        // Semântico
        LASemantico as = new LASemantico();
        as.visitPrograma(arvore);
        Auxiliar.listaDeErrosSemanticos.forEach((s) -> System.out.println(s));
        
        // Gerador
        if(Auxiliar.listaDeErrosSemanticos.isEmpty()) {
            Gerador agc = new Gerador();
            agc.visitPrograma(arvore);
                try(PrintWriter pw = new PrintWriter(args[1])) {
                pw.print(agc.saida.toString());
            }
        }

    }
}