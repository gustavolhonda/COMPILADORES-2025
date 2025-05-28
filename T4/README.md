
# 📘 Trabalho 4 - Analisador Semântico (Parte 2)

Este projeto implementa a segunda parte do analisador semântico para a linguagem LA (Linguagem Algorítmica), desenvolvido como parte da disciplina de Compiladores no DC/UFSCar.  

Esta etapa expande a análise semântica do T3, incluindo verificação avançada de tipos, escopos, e uso correto de procedimentos, funções, ponteiros e registros.

---

## Novas funcionalidades implementadas

O analisador semântico agora deve detectar os seguintes erros adicionais:

- **Identificador já declarado no mesmo escopo**, envolvendo variáveis, constantes, procedimentos, funções, tipos, ponteiros e registros.
- **Reutilização do mesmo identificador no mesmo escopo**, mesmo que para categorias diferentes (ex.: variável e função).
- **Identificador não declarado** (variáveis, constantes, procedimentos, funções, ponteiros, registros).
- **Incompatibilidade entre argumentos e parâmetros formais em chamadas de procedimentos/funções**, verificando número, ordem e tipo dos argumentos.
  
  | Tipo do parâmetro | Tipo aceito no argumento |
  |-------------------|-------------------------|
  | ponteiro          | endereço                |
  | real              | real                    |
  | inteiro           | inteiro                 |
  | literal           | literal                 |
  | lógico            | lógico                  |
  | registro          | registro (mesmo tipo)   |

- **Atribuição não compatível com o tipo declarado**, agora abrangendo ponteiros e registros.

  | Tipo da variável  | Tipo aceito na atribuição             |
  |-------------------|-------------------------------------|
  | ponteiro          | endereço                            |
  | real / inteiro    | real ou inteiro                     |
  | literal           | literal                            |
  | lógico            | lógico                            |
  | registro          | registro (mesmo tipo)               |

- **Expressões com tipos incompatíveis** devem resultar em erro de tipo indefinido e invalidar a atribuição.

- **Uso incorreto do comando `retorne`** em escopos não permitidos.

---

## Requisitos do analisador semântico

- Ao encontrar um erro, o analisador **não deve interromper sua execução**.
- Deve continuar reportando erros até o fim do arquivo fonte.
- A saída deve conter a linha do erro e uma descrição clara.
- Exemplo de saída:

```
Linha 7: tipo inteir nao declarado
Linha 11: identificador idades nao declarado
Fim da compilacao
```

---

## Execução

O analisador é um arquivo `.jar` executável via linha de comando, que recebe dois argumentos obrigatórios:

1. Caminho completo do arquivo de entrada (arquivo fonte `.txt`).
2. Caminho completo do arquivo de saída (arquivo onde os erros serão gravados).

**Exemplo de execução:**

```bash
java -jar ./target/alguma-semantico-1.0-SNAPSHOT-jar-with-dependencies.jar caminho/entrada.txt caminho/saida.txt
```

**Importante:** A saída **deve ser gravada em arquivo** e **não impressa no terminal**.

---

## Autores

- Gustavo Lamin Honda - 811716  
- Jakson Huang Zheng - 811773  
- Vitor Rodrigues Vechin - 812287  

---

## Pré-requisitos

- Java 21 ou superior  
- Maven  
- GCC (para integração com o corretor automático)  
- ANTLR (opcional, para geração dos arquivos a partir do `.g4`)  

---

## Compilação

Na pasta do projeto, execute:

```bash
mvn clean package
```

O arquivo `.jar` será gerado em:

```
./target/alguma-semantico-1.0-SNAPSHOT-jar-with-dependencies.jar
```

---

## Teste automático com corretor

A disciplina disponibiliza um corretor automático para validar o trabalho com base nos casos de teste oficiais.

### Comando exemplo:

```bash
java -jar ../../corretor/compiladores-corretor-automatico-1.0-SNAPSHOT-jar-with-dependencies.jar \
"java -jar ./target/alguma-semantico-1.0-SNAPSHOT-jar-with-dependencies.jar" \
gcc \
../../corretor/temp \
../../corretor/casos-de-teste \
"811716, 811773, 812287" \
t4
```

---

## Script para facilitar execução e teste

Um script (`run.sh`) está disponível para compilar e rodar os testes automaticamente.

### Uso:

```bash
./run.sh
```

Caso necessário, torne o script executável:

```bash
chmod +x run.sh
```