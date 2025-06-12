# 📘 Trabalho 5 - Gerador de código C

Este projeto implementa a segunda parte do analisador semântico para a linguagem LA (Linguagem Algorítmica), desenvolvido como parte da disciplina de Compiladores no DC/UFSCar.  

Esta etapa expande a análise semântica, incluindo a implementação de um **gerador de código** que converte o código-fonte em C a partir de um programa escrito em LA.

---

## Novas funcionalidades implementadas

O **GERADOR** foi implementado como uma adição crucial à etapa do analisador semântico. Ele realiza a geração de código em C a partir do programa LA. As funcionalidades incluem:

- **Geração de código em C**: O GERADOR transforma a estrutura semântica do código LA para um código C válido, incluindo a declaração de bibliotecas, funções, e variáveis.
- **Declarações locais e globais**: O GERADOR identifica e gera as declarações adequadas de variáveis e funções, tratando-as de forma local ou global.
- **Estrutura do programa**: O código gerado começa com a inclusão de bibliotecas padrão em C (`stdio.h`, `stdlib.h`), seguidas pelas declarações de variáveis globais e o corpo do programa.
- **Atribuições e expressões**: O GERADOR converte as expressões de LA para as equivalentes em C, mantendo a integridade dos tipos e operações aritméticas.

Exemplo de código gerado:

```c
#include <stdio.h>
#include <stdlib.h>

int main() {
    int x;
    float y;

    x = 10;
    y = 3.14;

    printf("%d %f\n", x, y);
    return 0;
}
```

---

### Requisitos do GERADOR

- O GERADOR não deve interromper a execução do programa, mesmo que encontre erros.
- A geração do código deve continuar até o final do arquivo fonte.
- A saída gerada pelo GERADOR deve ser em um arquivo .c, que pode ser compilado por um compilador C.

---

## Execução

O analisador é um arquivo `.jar` executável via linha de comando, que recebe dois argumentos obrigatórios:

1. Caminho completo do arquivo de entrada (arquivo fonte `.txt`).
2. Caminho completo do arquivo de saída (arquivo onde os erros serão gravados).

**Exemplo de execução:**

```bash
java -jar ./target/alguma-semantico-1.0-SNAPSHOT-jar-with-dependencies.jar caminho/entrada.txt caminho/saida.c
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
t5
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