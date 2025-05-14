# 📘 Trabalho 3 - Analisador semântico 

Este projeto implementa um analisador semântico para a linguagem LA, desenvolvido como parte da disciplina de Compiladores. O analisador foi construído utilizando [ANTLR4](https://www.antlr.org/) e empacotado via [Maven](https://maven.apache.org/).

## Autores

- Gustavo Lamin Honda - 811716
- Jakson Huang Zheng - 811773
- Vitor Rodrigues Vechin - 812287

## Pré-requisitos

Para compilar e executar este projeto, você precisa ter instalado:

- Java 21 ou superior
- Maven
- GCC (usado pelo corretor automático)
- ANTLR (opcional, se quiser gerar os arquivos a partir do `.g4`)

## Compilação do projeto

A partir da pasta `alguma-semantico`, execute:

```bash
mvn clean package
```

Isso criará o arquivo `.jar` do analisador semântico em:

```
./target/alguma-semantico-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Execução manual do analisador

Para executar o analisador em um arquivo de entrada:

```bash
java -jar ./target/alguma-semantico-1.0-SNAPSHOT-jar-with-dependencies.jar caminho/entrada.txt caminho/saida.txt
```

> **Importante**: A saída **deve ser gravada em um arquivo**, não no terminal.

Exemplo:

```bash
java -jar ./target/alguma-semantico-1.0-SNAPSHOT-jar-with-dependencies.jar ../casos-de-teste/arquivo1.txt ../temp/saida.txt
```

## Testando com o corretor automático

A disciplina fornece um corretor automático que valida a implementação com base nos testes oficiais.

### Comando exemplo:

```bash
java -jar ../../corretor/compiladores-corretor-automatico-1.0-SNAPSHOT-jar-with-dependencies.jar \
"java -jar ./target/alguma-semantico-1.0-SNAPSHOT-jar-with-dependencies.jar" \
gcc \
../../corretor/temp \
../../corretor/casos-de-teste \
"811716, 811773, 812287" \
t3
```

- `"java -jar ..."`: Comando que executa o analisador semântico.
- `gcc`: Compilador exigido pelo corretor.
- `../../corretor/temp`: Diretório onde serão salvos os arquivos de saída.
- `../../corretor/casos-de-teste`: Casos de teste da disciplina.
- `"RAs"`: Matrículas dos membros do grupo.
- `t2`: Indica que se trata de testes de análise sintática.

---

## Execução automatizada com script

Um script (`run.sh`) foi criado para automatizar a compilação e correção. Certifique-se de estar na raíz do diretório para executá-lo:

### Como usar:

```bash
./run.sh
```

Se necessário, torne o script executável:

```bash
chmod +x run.sh
```
