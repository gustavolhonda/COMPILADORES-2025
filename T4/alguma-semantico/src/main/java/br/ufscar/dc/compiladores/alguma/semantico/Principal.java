package br.ufscar.dc.compiladores.alguma.semantico;

import java.io.File;
import java.io.PrintWriter;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import java.util.Iterator;

public class Principal {
    public static void main(String[] args) {
        try (PrintWriter escritorSaida = new PrintWriter(new File(args[1]))) {
            CharStream fluxoCaracteres = CharStreams.fromFileName(args[0]);
            LALexer analisadorLexico = new LALexer(fluxoCaracteres);

            CommonTokenStream fluxoTokens = new CommonTokenStream(analisadorLexico);
            LAParser analisadorSintatico = new LAParser(fluxoTokens);

            LAParser.ProgramaContext arvoreContexto = analisadorSintatico.programa();

            LASemantico analisadorSemantico = new LASemantico();
            analisadorSemantico.visitPrograma(arvoreContexto);

            Iterator<String> iteradorErros = Auxiliar.listaDeErrosSemanticos.iterator();
            while (iteradorErros.hasNext()) {
                String erro = iteradorErros.next();
                escritorSaida.println(erro);
            }

            escritorSaida.println("Fim da compilacao");
            escritorSaida.close();

        } catch (Exception e) {
            System.err.println(e);
        }
    }
}