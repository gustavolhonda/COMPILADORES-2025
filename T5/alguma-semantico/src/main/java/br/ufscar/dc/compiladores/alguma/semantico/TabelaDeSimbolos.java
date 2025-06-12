package br.ufscar.dc.compiladores.alguma.semantico;

import java.util.ArrayList;
import java.util.HashMap;

public class TabelaDeSimbolos {
    public EntradaTabelaDeSimbolos.TiposDados tipoRetorno;

    private HashMap<String, EntradaTabelaDeSimbolos> entradasGlobais;
    private HashMap<String, ArrayList<EntradaTabelaDeSimbolos>> definicoesDeTipos;

    public TabelaDeSimbolos(EntradaTabelaDeSimbolos.TiposDados tipoRetorno) {
        entradasGlobais = new HashMap<>();
        definicoesDeTipos = new HashMap<>();
        this.tipoRetorno = tipoRetorno;
    }

    public void adicionarEntrada(String nome, EntradaTabelaDeSimbolos.TiposDados tipo, EntradaTabelaDeSimbolos.Categorias categoria) {
        EntradaTabelaDeSimbolos novaEntrada = new EntradaTabelaDeSimbolos(nome, tipo, categoria);
        entradasGlobais.put(nome, novaEntrada);
    }

    public void adicionarEntrada(EntradaTabelaDeSimbolos entrada) {
        entradasGlobais.put(entrada.nomeIdentificador, entrada);

    }

    public void adicionarDefinicaoDeTipo(String nomeTipo, EntradaTabelaDeSimbolos entrada) {
        if (definicoesDeTipos.containsKey(nomeTipo)) {
            definicoesDeTipos.get(nomeTipo).add(entrada);
        } else {
            ArrayList<EntradaTabelaDeSimbolos> listaDeMembros = new ArrayList<>();
            listaDeMembros.add(entrada);
            definicoesDeTipos.put(nomeTipo, listaDeMembros);
        }
    }

    public EntradaTabelaDeSimbolos.TiposDados consultarTipo(String nome) {
        return entradasGlobais.get(nome).tipoDado;
    }

    public boolean contem(String nome) {
        return entradasGlobais.containsKey(nome); 
    }

    public ArrayList<EntradaTabelaDeSimbolos> obterMembrosTipo(String nome) {
        return definicoesDeTipos.get(nome);
    }
}