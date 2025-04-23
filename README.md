# Trabalhos da disciplina de Compiladores — 2025.1

**Universidade Federal de São Carlos (UFSCar)**  
**Autores:** Gustavo Lamin Honda, Jakson Huang Zheng e Vitor Rodrigues Vechin

Este repositório reúne os trabalhos desenvolvidos para a disciplina de **Compiladores**, ofertada no primeiro semestre de 2025 no curso de Ciência da Computação da UFSCar. Ao longo da disciplina, exploramos os principais estágios da construção de compiladores, com foco inicial na análise léxica e evoluindo para etapas mais avançadas.

Todos os trabalhos são baseados na **linguagem LA (Linguagem Algorítmica)**, desenvolvida pelo professor **Jander** no âmbito do Departamento de Computação (DC/UFSCar), com o objetivo de introduzir os conceitos fundamentais de compiladores a partir de uma linguagem didática.

A seguir, listamos cada trabalho com uma breve descrição e o link correspondente para o diretório ou repositório no GitHub.

---

## T1 – Analisador Léxico com ANTLR para a linguagem LA  
Implementação de um **analisador léxico** para a linguagem LA. O analisador foi gerado com o uso da ferramenta **ANTLR (Another Tool for Language Recognition)** e é capaz de identificar tokens válidos, além de reportar erros léxicos.  
📂 [Repositório do T1](https://github.com/gustavolhonda/COMPILADORES-2025/tree/main/T1)

---

## T2 – Analisador Sintático para a linguagem LA  
Desenvolvimento de um **analisador sintático** para a linguagem LA. Este trabalho expande o analisador léxico anterior, permitindo a identificação de estruturas gramaticais com base na gramática livre de contexto da linguagem.  
📂 [Repositório do T2](./T2)

---

## T3 – Analisador Semântico para a linguagem LA (Parte 1)  
Implementação da **primeira parte da análise semântica**, com foco na detecção de erros como declaração múltipla de identificadores, uso de variáveis não declaradas e verificação de tipos.  
📂 [Repositório do T3](./T3)

---

## T4 – Analisador Semântico para a linguagem LA (Parte 2)  
Continuação da análise semântica com implementação da **geração de código intermediário**, onde o programa-fonte é traduzido para uma representação intermediária em forma de três endereços.  
📂 [Repositório do T4](./T4)

---

## T5 – Gerador de Código para a linguagem LA  
Geração do **código final executável** a partir do código intermediário, permitindo a execução dos programas escritos em LA diretamente em uma máquina alvo simulada.  
📂 [Repositório do T5](./T5)

---

## T6 – Compilador completo para uma linguagem de nossa escolha
📂 [Repositório separado do T6](./T6)

---

### 📌 Como compilar e executar

Cada diretório (T1 a T5) contém um arquivo `README.md` próprio com **instruções detalhadas** para compilação, execução e testes. Recomendamos a leitura de cada um para garantir o correto uso das ferramentas e scripts fornecidos.
