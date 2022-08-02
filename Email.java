package norn;

import java.util.*;


public class Email implements ListExpression {

    private final String username;
    private final String domain;
    
    // Abstraction function
    //    AF(username, domain): an email expression representing the email username@domain
    // Rep invariant
    //    username is a nonempty case-insensitive string of letters, digits, underscores,
    //             dashes, periods, and plus signs
    //    domain is a nonempty case-insensitive string of letters, digits, underscores, dashes, and periods
    // Safety from rep exposure
    //    all fields are immutable and final
    // Thread Safety argument
    //    all fields immutable and final
    
    /**
     * Constructor for an Email object.
     * @param username the username of the email
     * @param domain the domain the of the email
     */
    public Email(String username, String domain) {
        this.username = username.toLowerCase();
        this.domain = domain.toLowerCase();
        checkRep();
    }
    
    private void checkRep() {
        assert this.username.length() > 0;
        assert this.domain.length() > 0;
    }
    
    @Override
    public String toString() {
        return username + "@" + domain;
    }
    
    @Override
    public boolean equals(Object that) {
        this.checkRep();
        return that instanceof Email && this.sameEmail((Email) that);
    }
    
    private boolean sameEmail(Email that) {
        this.checkRep();
        return this.username().equals(that.username()) && this.domain().equals(that.domain());
    }
    
    @Override
    public int hashCode() {
        return username.hashCode() + domain.hashCode();
    }
    
    /**
     * getter for getting the username of this email
     * @return the username of the email
     */
    public String username() {
        return username;
    }
    
    /**
     * getter for getting the domain of this email
     * @return the domain of the email
     */
    public String domain() {
        return domain;
    }
    
    @Override
    public Set<Email> recipients(Map<String, ListExpression> definedLists, Set<Listname> listnamesUsed){
        this.checkRep();
        return Set.of(this);
    }
    
    @Override
    public Map<String, ListExpression> definitions(){
        this.checkRep();
        return Map.of();
    }
    
    @Override
    public List<ListExpression> visualizeExpressions(){
        List<ListExpression> expressions = new ArrayList<ListExpression>();
        expressions.add(new Empty());
        expressions.add(new Empty());
        return expressions;
    }
}
