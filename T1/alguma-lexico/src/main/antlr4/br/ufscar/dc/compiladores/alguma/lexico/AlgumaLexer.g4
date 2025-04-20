// Criação da gramática da linguagem LA do professor Jander - Expressões Regulares

lexer grammar AlgumaLexer;

// ==========================
// Palavras-chave estruturais
// ==========================
ALGORITMO : 'algoritmo';
FIM_ALGORITMO : 'fim_algoritmo';
DECLARE : 'declare';
TIPO: 'tipo';
VAR: 'var';
CONSTANTE: 'constante';
REGISTRO: 'registro';
FIM_REGISTRO: 'fim_registro';

// ==========================
// Tipos primitivos de dados
// ==========================
INTEIRO : 'inteiro';
REAL : 'real';
LITERAL : 'literal';
LOGICO : 'logico';

// ==========================
// Valores booleanos
// ==========================
FALSO: 'falso';
VERDADEIRO: 'verdadeiro';

// ==========================
// Operadores lógicos
// ==========================
E: 'e';
OU: 'ou';
NAO: 'nao';

// ==========================
// Estruturas de controle
// ==========================

// Condicional
SE: 'se';
SENAO: 'senao';
ENTAO: 'entao';
FIM_SE: 'fim_se';
CASO: 'caso';
SEJA: 'seja';
FIM_CASO: 'fim_caso';

// Laços
ENQUANTO: 'enquanto';
FIM_ENQUANTO: 'fim_enquanto';
PARA: 'para';
FIM_PARA: 'fim_para';
FACA: 'faca';
ATE: 'ate';

// ==========================
// Subprogramas
// ==========================
PROCEDIMENTO: 'procedimento';
FIM_PROCEDIMENTO: 'fim_procedimento';
FUNCAO: 'funcao';
FIM_FUNCAO: 'fim_funcao';
RETORNE: 'retorne';

// ==========================
// Entrada e saída
// ==========================
LEIA : 'leia';
ESCREVA : 'escreva';

// ==========================
// Operadores diversos
// ==========================

// Atribuição e comparação
ATRIBUICAO: '<-';
IGUAL: '=';
DIFERENTE: '<>';
MAIOR: '>';
MENOR: '<';
MAIOR_IGUAL: '>=';
MENOR_IGUAL: '<=';

// Aritméticos
MAIS: '+';
MENOS: '-';
MULTIPLICACAO: '*';
DIVISAO: '/';
MOD: '%';

// Ponteiros
PONTEIRO: '^';
ENDERECO: '&';

// ==========================
// Delimitadores e símbolos
// ==========================
ABREPAR: '(';
FECHAPAR: ')';
ABRECOL: '[';
FECHACOL: ']';
DOIS_PONTOS: ':';
VIRGULA: ',';
PONTO_PONTO: '..';
PONTO: '.';

// ==========================
// Literais
// ==========================

// Cadeias de caracteres com e sem fechamento
CADEIA_NAO_FECHADA : '"' ( ~["\r\n] | '""' )*;
CADEIA : '"' ( ~["\r\n] | '""' )* '"';

// Números inteiros e reais
NUM_INT : ('0'..'9')+;
NUM_REAL : ('0'..'9')+ ('.' ('0'..'9')+)?;

// ==========================
// Identificadores
// ==========================
IDENT : [a-zA-Z_][a-zA-Z_0-9]*;

// ==========================
// Comentários e espaços
// ==========================

// Comentário fechado é ignorado
COMENTARIO_FECHADO : '{' ~('\n' | '\r' | '}')+ '}' -> skip;

// Comentário não fechado é erro léxico
COMENTARIO_NAO_FECHADO : '{' ~('}')+;

// Espaços em branco são ignorados
WS : [ \t\r\n]+ -> skip;

// ==========================
// Qualquer outro símbolo inválido
// ==========================
ERR : . ;