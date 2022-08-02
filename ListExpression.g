// Grammar for Norn mailing list expressions
@skip whitespace {
    expression ::= sequence;
    sequence ::= (definition ';')* definition;
    definition ::= (listname '=')? union;
    union ::= difference (',' difference)*; 
    difference ::= intersection ('!' intersection)*;
    intersection ::= primitive ('*' primitive)*;
    primitive ::= empty | email | listname | '(' expression ')';
    email ::= username '@' domain;
}

listname ::= [A-Za-z0-9\._\-+]+;
username ::= [A-Za-z0-9\._\-+]+;
domain ::= [A-Za-z0-9\._\-]+;
empty ::= '';
whitespace ::= [ \t\r\n]+;