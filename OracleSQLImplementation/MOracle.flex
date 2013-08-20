import java_cup.runtime.*;
%%
%class Lexer
%line
%column
%cup
%{
    private Symbol symbol(int type) {
        return new Symbol(type, yyline, yycolumn);
    }

    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline, yycolumn, value);
    }
%}
LineTerminator = \r|\n|\r\n
WhiteSpace     = {LineTerminator} | [ \t\f]
/*Path           = [./A-Za-z0-9_]*/
Name           = [a-z][_A-Za-z0-9]*
Comparison     = [=] | [<] | [>] | [<][>] | [<][=] | [>][=]
Number         = [0-9]+ | [0-9]+"."[0-9]+ | "."[0-9]+
String         = ['][^'\r\n]*[']
Variable       = [_A-Z][_A-Za-z0-9]*
Create         = [cC][rR][eE][aA][tT][eE]
Database       = [dD][aA][tT][aA][bB][aA][sS][eE]
Table          = [tT][aA][bB][lL][eE]
Varchar        = [vV][aA][rR][cC][hH][aA][rR]
Integer        = [iI][nN][tT][eE][gG][eE][rR]
Decimal        = [dD][eE][cC][iI][mM][aA][lL]
Insert         = [iI][nN][sS][eE][rR][tT]
Into           = [iI][nN][tT][oO]
Values         = [vV][aA][lL][uU][eE][sS]
Delete         = [dD][eE][lL][eE][tT][eE]
From           = [fF][rR][oO][mM]
Where          = [wW][hH][eE][rR][eE]
Update         = [uU][pP][dD][aA][tT][eE]
Set            = [sS][eE][tT]
Select         = [sS][eE][lL][eE][cC][tT]
And            = [aA][nN][dD]

%%
/*--------------------------LEXICAL RULES------------------------------------*/
<YYINITIAL> {
    "."                 { return symbol(sym.PERIOD); }
    "("                 { return symbol(sym.LPAREN); }
    ")"                 { return symbol(sym.RPAREN); }
    ","                 { return symbol(sym.COMMA); }
    ";"                 { return symbol(sym.SEMI); }
    "*"                 { return symbol(sym.STAR); }
    /*{Path}              { return symbol(sym.PATH); }*/
    {Create}            { return symbol(sym.CREATE); }
    {Database}          { return symbol(sym.DATABASE); }
    {Table}             { return symbol(sym.TABLE); }
    {Varchar}           { return symbol(sym.VARCHAR); }
    {Integer}           { return symbol(sym.INTEGER); }
    {Decimal}           { return symbol(sym.DECIMAL); }
    {Insert}            { return symbol(sym.INSERT); }
    {Into}              { return symbol(sym.INTO); }
    {Values}            { return symbol(sym.VALUES); }
    {Delete}            { return symbol(sym.DELETE); }
    {From}              { return symbol(sym.FROM); }
    {Where}             { return symbol(sym.WHERE); }
    {Update}            { return symbol(sym.UPDATE); }
    {Set}               { return symbol(sym.SET); }
    {Select}            { return symbol(sym.SELECT); }
    {And}               { return symbol(sym.AND); }
    {Name}              { return symbol(sym.NAME, new String(yytext())); }
    {Comparison}        { return symbol(sym.COMPARISON, new String(yytext())); }
    {Number}            { return symbol(sym.NUMBER, new String(yytext())); }
    {String}            { return symbol(sym.STRING, new String(yytext())); }
    {Variable}          { return symbol(sym.VARIABLE, new String(yytext())); }
    {WhiteSpace}        { /* do nothing */ }
}
[^]                     { System.out.println("Syntax Error - Scanning Error");
                          return symbol(sym.ERROR); }
