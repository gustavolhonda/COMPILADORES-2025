cd ./alguma-lexico
mvn clean package
java -jar ../../corretor/compiladores-corretor-automatico-1.0-SNAPSHOT-jar-with-dependencies.jar "java -jar ./target/alguma-lexico-1.0-SNAPSHOT-jar-with-dependencies.jar" gcc ../../corretor/temp ../../corretor/casos-de-teste "811716, 811773, 812287" t1