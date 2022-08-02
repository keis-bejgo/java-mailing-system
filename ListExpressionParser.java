package norn;

import java.io.File;
import java.io.IOException;
import java.util.List;

import edu.mit.eecs.parserlib.ParseTree;
import edu.mit.eecs.parserlib.Parser;
import edu.mit.eecs.parserlib.UnableToParseException;
import edu.mit.eecs.parserlib.Visualizer;

/**
 * Based on the staff code of the IntegerExpressionParser from the lecture 18.
 */
public class ListExpressionParser {
    
    // the nonterminals of the grammar
    private static enum ListExpressionGrammar {
        EXPRESSION, SEQUENCE, DEFINITION, UNION, DIFFERENCE, INTERSECTION,
        PRIMITIVE, EMAIL, LISTNAME, USERNAME, DOMAIN, EMPTY, WHITESPACE,
    }

    private static Parser<ListExpressionGrammar> parser = makeParser();
    
    /**
     * Compile the grammar into a parser.
     * 
     * @return parser for the grammar
     * @throws RuntimeException if grammar file can't be read or has syntax errors
     */
    private static Parser<ListExpressionGrammar> makeParser() {
        try {
            // read the grammar as a file, relative to the project root.
            final File grammarFile = new File("src/norn/ListExpression.g");
            return Parser.compile(grammarFile, ListExpressionGrammar.EXPRESSION);
            
        // Parser.compile() throws two checked exceptions.
        // Translate these checked exceptions into unchecked RuntimeExceptions,
        // because these failures indicate internal bugs rather than client errors
        } catch (IOException e) {
            throw new RuntimeException("can't read the grammar file", e);
        } catch (UnableToParseException e) {
            System.out.println("syntax error");
            throw new RuntimeException("the grammar has a syntax error", e);
        }
    }

    /**
     * Parse a string into a list expression.
     * 
     * @param string string to parse
     * @return ListExpression parsed from the string
     * @throws UnableToParseException if the string doesn't match the ListExpression grammar
     */
    public static ListExpression parse(final String string) throws UnableToParseException {
        // parse the example into a parse tree
        final ParseTree<ListExpressionGrammar> parseTree = parser.parse(string);

        // display the parse tree in various ways, for debugging only
        //System.out.println("parse tree " + parseTree);
        // Visualizer.showInBrowser(parseTree);

        // make an AST from the parse tree
        final ListExpression expression = makeAbstractSyntaxTree(parseTree);
        // System.out.println("AST " + expression);
        
        return expression;
    }
    
    /**
     * Convert a parse tree into an abstract syntax tree.
     * 
     * @param parseTree constructed according to the grammar in ListExression.g
     * @return abstract syntax tree corresponding to parseTree
     */
    private static ListExpression makeAbstractSyntaxTree(final ParseTree<ListExpressionGrammar> parseTree) {
        switch (parseTree.name()) {
        case EXPRESSION:  // expression ::= sequence;
            {
                final ParseTree<ListExpressionGrammar> child = parseTree.children().get(0);
                //System.out.println("expression");
                return makeAbstractSyntaxTree(child);
            }
            
        case SEQUENCE:  // sequence ::= (definition ';')* definition;
            {             
                //System.out.println("sequence");
                final List<ParseTree<ListExpressionGrammar>> children = parseTree.children();                
                ListExpression expression = makeAbstractSyntaxTree(children.get(0));
                
                for (int i = 1; i < children.size(); i++) { 
                    expression = new Sequence(expression, makeAbstractSyntaxTree(children.get(i)));
                    //System.out.println(expression.toString()+"sequence's expression");
                }
                return expression;
            }
        
        case DEFINITION:  // definition ::= (listname '=')? union;
            {
                //System.out.println("definition");
                final List<ParseTree<ListExpressionGrammar>> children = parseTree.children();
                final ParseTree<ListExpressionGrammar> child1 = children.get(0);
                // check which alternative was actually matched
                switch (child1.name()) {
                case UNION:
                    return makeAbstractSyntaxTree(child1);
                case LISTNAME:
                    Listname listname = new Listname(child1.text());
                    ListExpression expression = makeAbstractSyntaxTree(children.get(1));
                    //System.out.println(listname+": " + expression + " new Definition");
                    return new Definition(listname, expression);
                default:
                    throw new AssertionError("should never get here");
                }
            }
            
        case UNION:  // union ::= difference (',' difference);
            { 
                //System.out.println("union");
                final List<ParseTree<ListExpressionGrammar>> children = parseTree.children();
                ListExpression expression = makeAbstractSyntaxTree(children.get(0));
                
                for (int i = 1; i < children.size(); i++) { 
                    expression = new Union(expression, makeAbstractSyntaxTree(children.get(i)));
                }
                return expression;
            }
            
        case DIFFERENCE:  // difference ::= intersection ('!' intersection)*;
            {
                //System.out.println("diff");
                final List<ParseTree<ListExpressionGrammar>> children = parseTree.children();
                ListExpression expression = makeAbstractSyntaxTree(children.get(0));

                for (int i = 1; i < children.size(); i++) { 
                    expression = new Difference(expression, makeAbstractSyntaxTree(children.get(i)));
                }
                return expression;
            }
            
        case INTERSECTION:  // intersection ::= primitive ('*' primitive)*;
            {
                //System.out.println("intersection");
                final List<ParseTree<ListExpressionGrammar>> children = parseTree.children();
                ListExpression expression = makeAbstractSyntaxTree(children.get(0));
                
                for (int i = 1; i < children.size(); i++) { 
                    expression = new Intersection(expression, makeAbstractSyntaxTree(children.get(i)));
                }
                return expression;
            }

        case PRIMITIVE:  // primitive ::= email | '(' expression ')';
            {
                //System.out.println("prim");
                final ParseTree<ListExpressionGrammar> child = parseTree.children().get(0);
                // check which alternative (email, listname or expression) was actually matched
                switch (child.name()) {
                case EMPTY:
                    return makeAbstractSyntaxTree(child);
                case EMAIL:
                    return makeAbstractSyntaxTree(child);
                case LISTNAME:
                    return new Listname(child.text());
                case EXPRESSION:
                    return makeAbstractSyntaxTree(child);
                default:
                    throw new AssertionError("should never get here");
                }
            }
            
        case EMAIL:  // email ::= username '@' domain;
            {
                //System.out.println("email");
                final List<ParseTree<ListExpressionGrammar>> children = parseTree.children();
                final String username = children.get(0).text();
                final String domain = children.get(1).text();
                
                return new Email(username, domain);
            }
            
        case EMPTY:  // empty ::= ''
            {
                return new Empty();
            }
        
        default:
            throw new AssertionError("should never get here");
        }

    }
}
