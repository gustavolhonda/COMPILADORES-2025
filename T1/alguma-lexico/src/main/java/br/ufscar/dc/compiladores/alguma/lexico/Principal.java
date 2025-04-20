package br.ufscar.dc.compiladores.alguma.lexico;

import java.io.IOException;
import java.io.PrintWriter;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;

public class Principal {

    // Método para tratar os erros
    private static boolean tratarErro(String tipoToken, int linha, String texto, PrintWriter writer) {
        String errorMessage = "";
        
        switch (tipoToken) {
            case "CADEIA_NAO_FECHADA":
                errorMessage = "Linha " + linha + ": " + "cadeia literal nao fechada";
                break;
            case "COMENTARIO_NAO_FECHADO":
                errorMessage = "Linha " + linha + ": " + "comentario nao fechado";
                break;
            case "ERR":
                errorMessage = "Linha " + linha + ": " + texto + " - simbolo nao identificado";
                break;
            default:
                return false; // Não é erro, retorna false para continuar
        }

        writer.println(errorMessage); // Imprime o erro no arquivo
        return true; // Retorna true indicando que houve erro
    }

    public static void main(String[] args) {
        // Verifica se os parâmetros necessários (entrada e saída de arquivos) foram fornecidos
        if (args.length < 2) {
            System.out.println("Forneca o arquivo de entrada e o arquivo de saída.");
            return;
        }
        
        try (PrintWriter writer = new PrintWriter(args[1])) {
            // Cria o CharStream a partir do arquivo de entrada
            CharStream cs = CharStreams.fromFileName(args[0]);
            
            // Inicializa o lexer com o CharStream criado
            AlgumaLexer lex = new AlgumaLexer(cs);

            Token t = null;
            // Loop até que o lexer alcance o fim do arquivo (EOF)
            while ((t = lex.nextToken()).getType() != Token.EOF) { 
                String nomeToken = AlgumaLexer.VOCABULARY.getDisplayName(t.getType());

                // Chama o método para tratar os erros
                if (tratarErro(nomeToken, t.getLine(), t.getText(), writer)) {
                    break;  // Interrompe o loop após tratar o erro
                }

                // Caso não haja erro, cria a string representando o token e sua categoria, e escreve no arquivo de saída
                String tokenStr = "<" + "'" + t.getText() + "'" + "," + AlgumaLexer.VOCABULARY.getDisplayName(t.getType()) + ">";
                writer.println(tokenStr);
            }

        } catch (IOException ex) {
            // Caso ocorra erro ao abrir os arquivos, imprime a mensagem de erro
            System.err.println("Erro ao abrir os arquivos: " + ex.getMessage());
        }
    }
}
