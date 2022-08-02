package norn;

import java.util.*;


public class Definition implements ListExpression {

    private final Listname listname;
    private final ListExpression expression;
    
    // Abstraction function
    //    AF(listname, expression): an expression representing 
    // Rep invariant
    //    
    // Safety from rep exposure
    //    all fields are immutable and final
    // Thread Safety argument
    //    all fields immutable and final
    
    /**
     * Constructor for a Definition object
     * @param listname the name of the list represented by this object
     * @param expression the expression of the list represented by this object
     */
    public Definition(Listname listname, ListExpression expression) {
        this.listname = listname;
        this.expression = expression;
        checkRep();
    }
    
    private void checkRep() {
        assert this.listname != null;
        assert this.expression != null;
    }
    
    @Override
    public String toString() {
        return "(" + this.listname.toString() + " = " + this.expression.toString() + ")";
    }
    
    @Override
    public boolean equals(Object that) {
        this.checkRep();
        return that instanceof Definition && this.sameExpression((Definition) that);
    }
    
    private boolean sameExpression(Definition that) {
        this.checkRep();
        return this.listname().equals(that.listname()) && this.expression().equals(that.expression());
    }
    
    @Override
    public int hashCode() {
        return this.listname.hashCode() + this.expression.hashCode();
    }
    
    /**
     * Get the listname of this Definition
     * @return listname
     */
    public ListExpression listname() {
        return this.listname;
    }
    
    /**
     * Get the expression
     * @return expression
     */
    public ListExpression expression() {
        return this.expression;
    }
    
    @Override
    public Set<Email> recipients(Map<String, ListExpression> definedLists, Set<Listname> listnamesUsed){
        //get all defs for this Definition
        Map<String, ListExpression> myDefs = this.definitions();  //add this def's def to map
        for(String key : definedLists.keySet()) {
            if(!myDefs.containsKey(key))
                myDefs.put(key, definedLists.get(key));
        }
        
        Map<String,ListExpression> defs = new HashMap<String,ListExpression>(); //add definedLists's defs
        for(String key : definedLists.keySet()) {
            defs.put(key, definedLists.get(key));
        } 
        
        listnamesUsed.add(this.listname);
        return this.expression.recipients(defs, listnamesUsed);
    }
    
    @Override
    public Map<String, ListExpression> definitions(){
        this.checkRep();
        
        Map<String, ListExpression> defs = new HashMap<String, ListExpression>();
        
        defs.put(this.listname.listname(), this.expression); //put this definition in defs map
        
        Map<String,ListExpression> exprDefs = this.expression.definitions();
        
        for(String key : exprDefs.keySet()) { //add this def's expr's defs into defs map
            defs.put(key, exprDefs.get(key));
        }
        
        return defs;
    }
    
    @Override
    public List<ListExpression> visualizeExpressions(){
        List<ListExpression> expressions = new ArrayList<ListExpression>();
        expressions.add(listname());
        expressions.add(expression());
        return expressions;
    }
}
