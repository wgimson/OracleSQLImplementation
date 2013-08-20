GrammarDriver.class:					GrammarDriver.java Condition.class Relation.class Lexer.class parser.class
	javac GrammarDriver.java
Lexer.class:							Lexer.java sym.class
	javac Lexer.java
Lexer.java:								MOracle.flex
	jflex MOracle.flex
sym.class:								sym.java
	javac sym.java
sym.java:								MOracle.cup 
	java java_cup.Main MOracle.cup
parser.class:							parser.java
	javac parser.java
parser.java:							MOracle.cup
	java java_cup.Main MOracle.cup
Condition.class:						Condition.java
	javac Condition.java
Relation.class:							Relation.java Tuple.class
	javac Relation.java
Tuple.class:							Tuple.java
	javac Tuple.java
clean:
	rm Lexer.java Lexer.java~ sym.java parser.java *.class
