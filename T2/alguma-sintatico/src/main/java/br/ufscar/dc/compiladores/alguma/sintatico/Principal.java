package br.ufscar.dc.compiladores.alguma.sintatico;

import java.io.FileWriter;
import java.io.IOException;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public class Principal {

    public static void main(String[] args) {

        try {
            // Lexer
            CharStream cs = CharStreams.fromFileName(args[0]);
            LALexer lexer = new LALexer(cs);
            
            // Parser
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            LAParser parser = new LAParser(tokens);

            // Arquivo de destino
            String filename = args[1];
            FileWriter writer = new FileWriter(filename);
            
            // Cria um listener personalizado para capturar e formatar os erros sintáticos
            CustomError mcel = new CustomError(writer);

            // Substitui o listener padrão do ANTLR pelo nosso listener personalizado
            parser.removeErrorListeners();
            parser.addErrorListener(mcel);

            // Executa a análise sintática começando pela regra 'programa' da gramática
            parser.programa();

            writer.close();

        } catch (IOException ex) {}
    }
}