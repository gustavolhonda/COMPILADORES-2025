lexer grammar AlgumaLexer;

Letra		:	'a'..'z' | 'A'..'Z';
Digito	:	'0'..'9';
PCAgoritmo: 'ALGORITMO' { System.out.print("[Palavra-chave,"+getText()+"]");};
Variavel	:	Letra(Letra|Digito)* { System.out.print("[Var,"+getText()+"]");};