package norn;

import edu.mit.eecs.parserlib.UnableToParseException;
import java.util.*;

/**
 * An immutable data type representing a Norn Mailing List expression, as defined in the project handout. 
 */
public interface ListExpression {

    // Datatype definition
    //   ListExpression = Empty()
    //                    + Email(username:String, domain:String)
    //                    + Listname(listname:String)
    //                    + Intersection(first:ListExpression, second:ListExpression)
    //                    + Difference(first:ListExpression, second:ListExpression)
    //                    + Union(first:ListExpression, second:ListExpression)
    //                    + Definition(listname:Listname, expression:ListExpression)
    //                    + Sequence(first:ListExpression, second:ListExpression)
    
    /**
     * Parse a list expression.
     * @param input expression to parse, as defined in the norn specification
     * @return list expression AST for the input
     * @throws IllegalArgumentException if the expression is syntactically invalid.
     */
    public static ListExpression parse(String input) throws IllegalArgumentException {
        try {
            return ListExpressionParser.parse(input);
        }
        catch (UnableToParseException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("the input is invalid");
        }
    }
    
    
    @Override
    public String toString();
    
    @Override
    public boolean equals(Object that);
       
    @Override
    public int hashCode();
    
    /**
     * Return a Set of the Emails that are the recipients of this ListExpression.
     * 
     * @param definedLists map from list names to their previous definitions; can be empty
     * @param listnamesUsed set of listnames that depend on current expression
     * @return set of emails on this mailing list
     * @throws IllegalArgumentException when the current expression depends on a listname in
     *         listnamesUsed (which means that there is mutual recursion in list definitions)
     */
    public Set<Email> recipients(Map<String, ListExpression> definedLists,
                                 Set<Listname> listnamesUsed) throws IllegalArgumentException;
    
    /**
     * Return a map of all definitions in this expression.
     * 
     * @return a map from list names to definitions
     */
    public Map<String, ListExpression> definitions(); 
    
    /**
     * Get a list of expressions to include for web visualization.
     * 
     * @return a list of list expressions part of web visualization
     */
    public List<ListExpression> visualizeExpressions();
}
