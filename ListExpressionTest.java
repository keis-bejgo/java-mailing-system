package norn;

import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;


public class ListExpressionTest {

    /* Testing strategy for parse():
     * Partition the input as follows:
     * number of ListExpressions: 1, 2, > 2
     * type of ListExpression: Email, Union, Difference,
     *                         Intersection, Definition, 
     *                         ListName, Sequence
     */
    
    /* Testing strategy for toString():
     * Partition the input as follows:
     * number of ListExpressions: 1, 2, >2
     * type of ListExpression: Email, Union, Difference,
     *                         Intersection, Definition,
     *                         ListName, Sequence
     */
    
    /* Testing strategy for equals():
     * Partition the input as follows:
     * types of ListExpressions: Email == Email,
     *                           Email != Email,
     *                           Email != Union,
     *                           Email != Difference,
     *                           Email != Intersection,
     *                           Union == Union,
     *                           Union != Union,
     *                           Union != Intersection,
     *                           etc.                         
     */
    
    /* Testing strategy for hashCode():
     * Partition the input as follows:
     * types of ListExpressions: Email == Email,
     *                           Email != Email,
     *                           Email != Union,
     *                           Email != Difference,
     *                           Email != Intersection,
     *                           Union == Union,
     *                           Union != Union,
     *                           Union != Intersection,
     *                           etc.                         
     */
    
    /* Testing strategy for recipients(definedLists):
     * Partition the input as follows:
     * number of ListExpressions: 1, 2, > 2

     * type of ListExpression: Email, Union, Difference,
     *                         Intersection, Definition,
     *                         ListName, Sequence

     * size of resulting set: 0, 1, >1
     * definedLists empty, contains definitions not in expression, definitions in expression
     */
   
    /* Testing strategy for definitions():
     * Partition the input as follows:
     * type of ListExpression: Empty, Email, Union, Difference,
     *                         Intersection, Definition, Sequence, Listname
     * ListExpression does not contain definitions, is a Definition, contains definition(s) inside;
     *     there are no lists depending on other lists, there are such lists                    
     * size of resulting set: 0, 1, >1
     */
    
    @Test
    public void testAssertionsEnabled() {
        assertThrows(AssertionError.class, () -> { assert false; },
                "make sure assertions are enabled with VM argument '-ea'");
    }
    
    //// Tests for parse() ////
        
    //Covers partition: single email, valid email
    @Test
    public void testParseSingleValidEmail() {
        ListExpression expected = new Email("kbejgo", "mit.edu");
        assertEquals(expected, ListExpression.parse("kbejgo@mit.edu"), "should be equal");
    }
    
    //Covers partition: single email, invalid email
    @Test
    public void testParseSingleInvalidEmail() {
        String stringToParse = " @mit.edu";
        assertThrows(IllegalArgumentException.class, () -> {
            ListExpression.parse(stringToParse);
        }, "Usernames must be non-empty strings.");
    }
    
    //Covers partition: difference of 2 email lists
    @Test
    public void testParseBasicDifference() {
        ListExpression expected = new Difference(new Email("kbejgo", "mit.edu"), new Email("maristep", "mit.edu"));
        assertEquals(expected, ListExpression.parse("kbejgo@mit.edu!maristep@mit.edu"), "should be equal");
    }
    
    //Covers partition: union of 3 email lists
    @Test
    public void testParseBasicUnion() {
        ListExpression expected = new Union(new Union(new Email("kbejgo", "mit.edu"), new Email("maristep", "mit.edu")), new Email("scfeng", "mit.edu"));
        assertEquals(expected, ListExpression.parse("kbejgo@mit.edu, maristep@mit.edu, scfeng@mit.edu"), "should be equal");
    }
    
    //Covers partition: intersection of 3 email lists
    @Test
    public void testParseBasicIntersection() {
        ListExpression expected = new Intersection(new Intersection(new Email("kbejgo", "mit.edu"), new Email("maristep", "mit.edu")), new Email("scfeng", "mit.edu"));
        assertEquals(expected, ListExpression.parse("kbejgo@mit.edu*maristep@mit.edu*scfeng@mit.edu"), "should be equal");
    }
    
    //Covers partition: difference of union and intersection
    @Test
    public void testDifferenceOfUnionIntersection() {
        ListExpression expected = new Difference(new Union(new Email("kbejgo", "mit.edu"), new Email("maristep", "mit.edu")), new Intersection(new Email("maristep", "mit.edu"), new Email("maristep", "mit.edu")));
        assertEquals(expected, ListExpression.parse("(kbejgo@mit.edu, maristep@mit.edu) ! maristep@mit.edu * maristep@mit.edu"), "should be equal");
    }
    
    //Covers partition: union of intersection and difference
    @Test
    public void testUnionOfIntersectionDifference() {
        ListExpression expected = new Union(new Intersection(new Email("kbejgo", "mit.edu"), new Email("scfeng", "mit.edu")), new Difference(new Email("kbejgo", "mit.edu"), new Email("maristep", "mit.edu")));
        assertEquals(expected, ListExpression.parse("kbejgo@mit.edu*scfeng@mit.edu , kbejgo@mit.edu!maristep@mit.edu"), "should be equal");
    }
    
    //Covers partition: intersection of difference and union
    @Test
    public void testIntersectionOfDifferenceUnion() {
        ListExpression expected = new Intersection(new Difference(new Email("kbejgo", "mit.edu"), new Email("maristep", "mit.edu")), new Union(new Email("kbejgo", "mit.edu"), new Email("maristep", "mit.edu")));
        assertEquals(expected, ListExpression.parse("(kbejgo@mit.edu!maristep@mit.edu)*(kbejgo@mit.edu,maristep@mit.edu)") ,"should be equal");
    }
    
    //Covers partition: difference of difference of union of intersection
    @Test
    public void testDifferenceRecurseUnionIntersection() {
        ListExpression unionOfIntersection = new Union(new Intersection(new Email("maristep", "mit.edu"), new Email("scfeng", "mit.edu")), new Email("scfeng", "mit.edu"));
        ListExpression difference = new Difference(unionOfIntersection, new Email("kbejgo", "mit.edu"));
        ListExpression expected = new Difference(difference, new Email("scfeng", "mit.edu"));
        assertEquals(expected, ListExpression.parse("((maristep@mit.edu*scfeng@mit.edu,scfeng@mit.edu)!kbejgo@mit.edu)!scfeng@mit.edu"), "should be equal");
    }
    
    //Covers partition: union of union of difference of union of intersection
    @Test
    public void testUnionRecurseDifferenceIntersection() {
        ListExpression unionOfIntersection = new Union(new Intersection(new Email("scfeng", "mit.edu"), new Email("scfeng", "mit.edu")), new Email("kbejgo", "mit.edu"));
        ListExpression difference = new Difference(unionOfIntersection, new Email("kbejgo", "mit.edu"));
        ListExpression unionOfDifference = new Union(difference, new Email("maristep", "mit.edu"));
        ListExpression expected = new Union(unionOfDifference, new Email("scfeng", "mit.edu"));
        String stringToParse = "((scfeng@mit.edu*scfeng@mit.edu,kbejgo@mit.edu)!kbejgo@mit.edu), maristep@mit.edu, scfeng@mit.edu";
        assertEquals(expected, ListExpression.parse(stringToParse), "should be equal");
    }
    
    //Covers partition: intersection of intersection of difference of union
    @Test
    public void testIntersectionRecurseDifferenceUnion() {
        ListExpression differenceOfUnion = new Difference(new Union(new Email("maristep", "mit.edu"), new Email("scfeng", "mit.edu")), new Email("kbejgo", "mit.edu"));
        ListExpression intersection = new Intersection(differenceOfUnion, new Email("maristep", "mit.edu"));
        ListExpression expected = new Intersection(intersection, new Email("maristep", "mit.edu"));
        String stringToParse = "((maristep@mit.edu, scfeng@mit.edu)!kbejgo@mit.edu)*maristep@mit.edu*maristep@mit.edu";
        assertEquals(expected, ListExpression.parse(stringToParse), "should be equal");
    }
    
    //Covers partition: difference of intersection of union of union
    @Test
    public void testDifferenceIntersectionRecurse() {
        ListExpression initialUnion = new Union(new Union(new Email("scfeng", "mit.edu"), new Email("maristep", "mit.edu")), new Email("kbejgo", "mit.edu"));
        ListExpression otherUnion = new Union(new Email("kbejgo", "mit.edu"), new Email("scfeng", "mit.edu"));
        ListExpression intersection = new Intersection(initialUnion, otherUnion);
        ListExpression expected = new Difference(intersection, new Email("kbejgo", "mit.edu"));
        String stringToParse = "(scfeng@mit.edu, maristep@mit.edu, kbejgo@mit.edu)*(kbejgo@mit.edu, scfeng@mit.edu)!kbejgo@mit.edu";
        assertEquals(expected, ListExpression.parse(stringToParse), "should be equal");
    }
    
    //Covers partition: definition of intersection of unions
    @Test
    public void testDefinitionIntersectUnion() {
        ListExpression union1 = new Union(new Email("kbejgo", "mit.edu"), new Email("maristep", "mit.edu"));
        ListExpression union2 = new Union(new Email("maristep", "mit.edu"), new Email("scfeng", "mit.edu"));
        ListExpression intersection = new Intersection(union1, union2);
        Listname newListName = new Listname("newListName");
        ListExpression newListDef = new Definition(newListName, intersection);
        String stringToParse = "newListName = (kbejgo@mit.edu, maristep@mit.edu) * (maristep@mit.edu, scfeng@mit.edu)";
        assertEquals(newListDef, ListExpression.parse(stringToParse), "should be equal");
    }
    
    
    //Covers partition, already defined list, differences/unions
    @Test
    public void testDefinitionAlready() {
        Listname initialListName = new Listname("initialListName");
        ListExpression initialList = new Definition(initialListName, new Email("kbejgo", "mit.edu"));
        ListExpression newListItems = new Difference(new Union(new Email("kbejgo", "mit.edu"), new Email("maristep", "mit.edu")), new Email("kbejgo", "mit.edu"));
        ListExpression newList = new Definition(initialListName, newListItems);
        String stringToParse = "initialListName = (kbejgo@mit.edu, maristep@mit.edu)!kbejgo@mit.edu";
        assertEquals(newList, ListExpression.parse(stringToParse), "should be equal");
    }
    
    //Covers partition: valid list name
    @Test
    public void testListNameValid() {
        Listname newListName = new Listname("newListName");
        String stringToParse = "newListName";
        assertEquals(newListName, ListExpression.parse(stringToParse));
    }
    
    
    //Covers partition: (3) sequence of list defs intersect/union/diff
    @Test
    public void testSequenceOfDefs() {
        ListExpression items1 = new Intersection(new Union(new Email("maristep", "mit.edu"), new Email("scfeng", "mit.edu")), new Email("scfeng", "mit.edu"));
        ListExpression items2 = new Union(new Union(new Email("kbejgo", "mit.edu"), new Email("maristep", "mit.edu")), new Email("scfeng", "mit.edu"));
        ListExpression items3 = new Difference(new Union(new Email("scfeng", "mit.edu"), new Email("maristep", "mit.edu")), new Email("maristep", "mit.edu"));
        ListExpression list1 = new Definition(new Listname("list1"), items1);
        ListExpression list2 = new Definition(new Listname("list2"), items2);
        ListExpression list3 = new Definition(new Listname("list3"), items3);
        ListExpression sequence = new Sequence(list1, list2);
        ListExpression finalSequence = new Sequence(sequence, list3);
        String stringToParse = "(list1 = ((maristep@mit.edu, scfeng@mit.edu)*scfeng@mit.edu)); (list2 = ((kbejgo@mit.edu, maristep@mit.edu), scfeng@mit.edu)); (list3 = ((scfeng@mit.edu, maristep@mit.edu)!maristep@mit.edu))";
        assertEquals(finalSequence, ListExpression.parse(stringToParse), "should be equal");
    }
    
    //Covers partition: (3) sequence of list defs, expressions, & list names
    @Test
    public void testSequenceOfDefsExpNames() {
        ListExpression items = new Intersection(new Union(new Email("maristep", "mit.edu"), new Email("scfeng", "mit.edu")), new Email("scfeng", "mit.edu"));
        Listname newListName = new Listname("listName");
        ListExpression definedList = new Definition(newListName, items);
        Listname otherListName = new Listname("otherListName");
        ListExpression sequence = new Sequence(definedList, otherListName);
        ListExpression otherExpression = new Union(new Union(new Email("maristep", "mit.edu"), new Email("kbejgo", "mit.edu")), new Email("scfeng", "mit.edu"));
        ListExpression finalSequence = new Sequence(sequence, otherExpression);
        String stringToParse = "(listName = ((maristep@mit.edu, scfeng@mit.edu) * scfeng@mit.edu)); otherListName; ((maristep@mit.edu, kbejgo@mit.edu), scfeng@mit.edu)";
        assertEquals(finalSequence, ListExpression.parse(stringToParse), "should be equal");
    }
    
    //Covers partition: (2) sequence of list defs, some undefined expression 
    @Test
    public void testSequenceNotNames() {
        Listname name = new Listname("name");
        ListExpression nameExpression = new Difference(new Union(new Email("kbejgo", "mit.edu"), new Email("maristep", "mit.edu")), new Email("maristep", "mit.edu"));
        ListExpression definition = new Definition(name, nameExpression);
        ListExpression otherExpression = new Intersection(new Union(new Email("scfeng", "mit.edu"), new Email("kbejgo", "mit.edu")), new Email("scfeng", "mit.edu"));
        ListExpression sequence = new Sequence(otherExpression, definition);
        String stringToParse = "((scfeng@mit.edu, kbejgo@mit.edu)*scfeng@mit.edu); (name = ((kbejgo@mit.edu, maristep@mit.edu)!maristep@mit.edu))";
        assertEquals(sequence, ListExpression.parse(stringToParse), "should be equal");
        
    }
    
    //// Tests for toString() ////
    
    //Covers partition: email to string
    @Test
    public void testEmailToString() {
        ListExpression email = new Email("kbejgo", "mit.edu");
        String result = "kbejgo@mit.edu";
        assertTrue(result.equals(email.toString()));
    }
    
    //Covers partition: union to string
    @Test
    public void testUnionToString() {
        ListExpression union = new Union(new Union(new Email("kbejgo", "mit.edu"), new Email("scfeng", "mit.edu")), new Email("maristep", "mit.edu"));
        String result = "((kbejgo@mit.edu, scfeng@mit.edu), maristep@mit.edu)";
        assertTrue(result.equals(union.toString()));
    }
    
    //Covers partition: difference to string
    @Test
    public void testDifferenceToString() {
        ListExpression difference = new Difference(new Union(new Email("maristep", "mit.edu"), new Email("scfeng", "mit.edu")), new Email("scfeng", "mit.edu"));
        String result = "((maristep@mit.edu, scfeng@mit.edu) ! scfeng@mit.edu)";
        assertTrue(result.equals(difference.toString()));
    }
    
    //Covers partition: intersection to string
    @Test
    public void testIntersectionToString() {
        ListExpression intersection = new Intersection(new Union(new Email("maristep", "mit.edu"), new Email("scfeng", "mit.edu")), new Email("scfeng", "mit.edu"));
        String result = "((maristep@mit.edu, scfeng@mit.edu) * scfeng@mit.edu)";
        assertTrue(result.equals(intersection.toString()));
    }
    
    //Covers partition: definition to string
    @Test
    public void testDefinitionToString() {
        ListExpression union = new Union(new Union(new Email("maristep", "mit.edu"), new Email("scfeng", "mit.edu")), new Email("kbejgo", "mit.edu"));
        ListExpression definition = new Definition(new Listname("newNames"), union);
        String result = "(newnames = ((maristep@mit.edu, scfeng@mit.edu), kbejgo@mit.edu))";
        assertTrue(result.equals(definition.toString()));
    }
    
    //Covers partition: list name to string
    @Test
    public void testListNameToString() {
        Listname listName = new Listname("6.031-people");
        String result = "6.031-people";
        assertTrue(result.equals(listName.toString()));
    }
    
    //Covers partition: sequence to string
    @Test
    public void testSequenceToString() {
        Listname listName = new Listname("6.031-people");
        ListExpression newExpression = new Difference(new Union(new Email("kbejgo", "mit.edu"), new Email("maristep", "mit.edu")), new Email("maristep", "mit.edu"));
        ListExpression sequence = new Sequence(listName, newExpression);
        String result = "6.031-people; ((kbejgo@mit.edu, maristep@mit.edu) ! maristep@mit.edu)";
        assertTrue(result.equals(sequence.toString()));
    }
    
    //// Tests for equals() ////
        
    //Covers partition: intersection, equal
    @Test
    public void testIntersectionEqual() {
        ListExpression union = new Union(new Email("kbejgo", "mit.edu"), new Email("maristep", "mit.edu"));
        ListExpression otherUnion = new Union(new Email("maristep", "mit.edu"), new Email("scfeng", "mit.edu"));
        ListExpression intersection = new Intersection(union, otherUnion);
        ListExpression expected = new Intersection(new Union(new Email("kbejgo", "mit.edu"), new Email("maristep", "mit.edu")), new Union(new Email("maristep", "mit.edu"), new Email("scfeng", "mit.edu")));
        assertTrue(expected.equals(intersection));
    }
    
    //Covers partition: intersection, not equal
    @Test
    public void testIntersectionNotEqual() {
        ListExpression union = new Union(new Email("kbejgo", "mit.edu"), new Email("maristep", "mit.edu"));
        ListExpression otherUnion = new Union(new Email("scfeng", "mit.edu"), new Email("scfeng", "mit.edu"));
        ListExpression intersection = new Intersection(union, otherUnion);
        ListExpression expected = new Intersection(new Union(new Email("kbejgo", "mit.edu"), new Email("maristep", "mit.edu")), new Union(new Email("maristep", "mit.edu"), new Email("scfeng", "mit.edu")));
        assertFalse(expected.equals(intersection));
    }
    
    //Covers partition: difference, equal
    @Test 
    public void testDifferenceEqual() {
        ListExpression difference = new Difference(new Email("maristep", "mit.edu"), new Email("scfeng", "mit.edu"));
        ListExpression otherDifference = new Difference(new Email("maristep", "mit.edu"), new Email("scfeng", "mit.edu"));
        assertTrue(difference.equals(otherDifference));
    }
    
    //Covers partition: difference, not equal
    @Test
    public void testDifferenceNotEqual() {
        ListExpression difference = new Difference(new Email("maristep", "mit.edu"), new Email("scfeng", "mit.edu"));
        ListExpression otherDifference = new Difference(new Email("scfeng", "mit.edu"), new Email("scfeng", "mit.edu"));
        assertFalse(difference.equals(otherDifference));
    }
    
    //Covers partition: union, equal
    @Test
    public void testUnionEqual() {
        ListExpression union = new Union(new Union(new Email("kbejgo", "mit.edu"), new Email("maristep", "mit.edu")), new Email("scfeng", "mit.edu"));
        ListExpression otherUnion = new Union(new Union(new Email("kbejgo", "mit.edu"), new Email("maristep", "mit.edu")), new Email("scfeng", "mit.edu"));
        assertTrue(union.equals(otherUnion));
    }
    
    //Covers partition: union, not equal
    @Test
    public void testUnionNotEqual() {
        ListExpression union = new Union(new Union(new Email("kbejgo", "mit.edu"), new Email("maristep", "mit.edu")), new Email("scfeng", "mit.edu"));
        ListExpression otherUnion = new Union(new Union(new Email("maristep", "mit.edu"), new Email("maristep", "mit.edu")), new Email("scfeng", "mit.edu"));
        assertFalse(union.equals(otherUnion));
    }
    
    //Covers partition: list def, equal
    @Test
    public void testListDefEqual() {
        Listname name1 = new Listname("name1");
        Listname name2 = new Listname("name1");
        ListExpression expression1 = new Union(new Union(new Email("kbejgo", "mit.edu"), new Email("maristep", "mit.edu")), new Email("scfeng", "mit.edu"));
        ListExpression expression2 = new Union(new Union(new Email("kbejgo", "mit.edu"), new Email("maristep", "mit.edu")), new Email("scfeng", "mit.edu"));
        ListExpression definition1 = new Definition(name1, expression1);
        ListExpression definition2 = new Definition(name2, expression2);
        assertTrue(definition1.equals(definition2));
    }
    
    //Covers partition: list def, not equal
    @Test
    public void testListDefNotEqual() {
        Listname name1 = new Listname("name1");
        Listname name2 = new Listname("name2");
        ListExpression expression1 = new Union(new Union(new Email("kbejgo", "mit.edu"), new Email("maristep", "mit.edu")), new Email("scfeng", "mit.edu"));
        ListExpression expression2 = new Union(new Union(new Email("kbejgo", "mit.edu"), new Email("maristep", "mit.edu")), new Email("scfeng", "mit.edu"));
        ListExpression definition1 = new Definition(name1, expression1);
        ListExpression definition2 = new Definition(name2, expression2);
        assertFalse(definition1.equals(definition2));
    }
    
    //Covers partition: list name, equal
    @Test
    public void testListNameEqual() {
        Listname name1 = new Listname("name1");
        Listname name2 = new Listname("name1");
        assertTrue(name1.equals(name2));
    }
    
    //Covers partition: list name, not equal
    @Test
    public void testListNameNotEqual() {
        Listname name1 = new Listname("name1");
        Listname name2 = new Listname("name2");
        assertFalse(name1.equals(name2));
    }
    
    //Covers partition: sequence, equal
    @Test
    public void testSequenceEqual() {
        Listname name1 = new Listname("name1");
        Listname name2 = new Listname("name2");
        ListExpression expression1 = new Union(new Union(new Email("kbejgo", "mit.edu"), new Email("maristep", "mit.edu")), new Email("scfeng", "mit.edu"));
        ListExpression expression2 = new Union(new Union(new Email("kbejgo", "mit.edu"), new Email("maristep", "mit.edu")), new Email("scfeng", "mit.edu"));
        ListExpression definition1 = new Definition(name1, expression1);
        ListExpression definition2 = new Definition(name2, expression2);
        ListExpression sequence1 = new Sequence(definition1, definition2);
        ListExpression sequence2 = new Sequence(definition1, definition2);
        assertTrue(sequence1.equals(sequence2));
    }
    
    //Covers partition: sequence, not equal
    @Test
    public void testSequenceNotEqual() {
        Listname name1 = new Listname("name1");
        Listname name2 = new Listname("name2");
        ListExpression expression1 = new Union(new Union(new Email("kbejgo", "mit.edu"), new Email("maristep", "mit.edu")), new Email("scfeng", "mit.edu"));
        ListExpression expression2 = new Union(new Union(new Email("kbejgo", "mit.edu"), new Email("kbejgo", "mit.edu")), new Email("kbejgo", "mit.edu"));
        ListExpression definition1 = new Definition(name1, expression1);
        ListExpression definition2 = new Definition(name2, expression2);
        ListExpression sequence1 = new Sequence(definition1, definition2);
        ListExpression sequence2 = new Sequence(definition1, definition1);
        assertFalse(sequence1.equals(sequence2));
    }
    
    //Covers partition: sequence, list def, not equal
    @Test
    public void testSequenceListDefNot() {
        //list definition
        Listname name = new Listname("name1");
        ListExpression expression = new Union(new Union(new Email("kbejgo", "mit.edu"), new Email("maristep", "mit.edu")), new Email("scfeng", "mit.edu"));
        ListExpression definition = new Definition(name, expression);
        //sequence
        Listname otherName = new Listname("otherName");
        ListExpression otherExpression = new Union(new Email("kbejgo", "mit.edu"), new Email("scfeng", "mit.edu"));
        ListExpression otherDefinition = new Definition(otherName, otherExpression);
        Listname secondName = new Listname("secondName");
        ListExpression sequence = new Sequence(otherDefinition, secondName);
        assertFalse(sequence.equals(definition));
    }
    
    //Covers partition: listname, email, not equal
    @Test
    public void testListNameEmailNot() {
        Listname listName = new Listname("name");
        ListExpression emailName = new Email("kbejgo", "mit.edu");
        assertFalse(listName.equals(emailName));
    }
    
    //// Tests for hashCode() ////
    
    //Covers partition: intersection, equal
    @Test
    public void testIntersectionHashEqual() {
        ListExpression union = new Union(new Email("kbejgo", "mit.edu"), new Email("maristep", "mit.edu"));
        ListExpression otherUnion = new Union(new Email("maristep", "mit.edu"), new Email("scfeng", "mit.edu"));
        ListExpression intersection = new Intersection(union, otherUnion);
        ListExpression expected = new Intersection(new Union(new Email("kbejgo", "mit.edu"), new Email("maristep", "mit.edu")), new Union(new Email("maristep", "mit.edu"), new Email("scfeng", "mit.edu")));
        assertTrue(expected.hashCode()==intersection.hashCode());
    }
    
    //Covers partition: intersection, not equal
    @Test
    public void testIntersectionHashNotEqual() {
        ListExpression union = new Union(new Email("kbejgo", "mit.edu"), new Email("maristep", "mit.edu"));
        ListExpression otherUnion = new Union(new Email("scfeng", "mit.edu"), new Email("scfeng", "mit.edu"));
        ListExpression intersection = new Intersection(union, otherUnion);
        ListExpression expected = new Intersection(new Union(new Email("kbejgo", "mit.edu"), new Email("maristep", "mit.edu")), new Union(new Email("maristep", "mit.edu"), new Email("scfeng", "mit.edu")));
        assertFalse(expected.hashCode()==intersection.hashCode());
    }
    
    //Covers partition: difference, equal
    @Test 
    public void testDifferenceHashEqual() {
        ListExpression difference = new Difference(new Email("maristep", "mit.edu"), new Email("scfeng", "mit.edu"));
        ListExpression otherDifference = new Difference(new Email("maristep", "mit.edu"), new Email("scfeng", "mit.edu"));
        assertTrue(difference.hashCode()==otherDifference.hashCode());
    }
    
    //Covers partition: difference, not equal
    @Test
    public void testDifferenceHashNotEqual() {
        ListExpression difference = new Difference(new Email("maristep", "mit.edu"), new Email("scfeng", "mit.edu"));
        ListExpression otherDifference = new Difference(new Email("scfeng", "mit.edu"), new Email("scfeng", "mit.edu"));
        assertFalse(difference.hashCode()==otherDifference.hashCode());
    }
    
    //Covers partition: union, equal
    @Test
    public void testUnionHashEqual() {
        ListExpression union = new Union(new Union(new Email("kbejgo", "mit.edu"), new Email("maristep", "mit.edu")), new Email("scfeng", "mit.edu"));
        ListExpression otherUnion = new Union(new Union(new Email("kbejgo", "mit.edu"), new Email("maristep", "mit.edu")), new Email("scfeng", "mit.edu"));
        assertTrue(union.hashCode()==otherUnion.hashCode());
    }
    
    //Covers partition: union, not equal
    @Test
    public void testUnionHashNotEqual() {
        ListExpression union = new Union(new Union(new Email("kbejgo", "mit.edu"), new Email("maristep", "mit.edu")), new Email("scfeng", "mit.edu"));
        ListExpression otherUnion = new Union(new Union(new Email("maristep", "mit.edu"), new Email("maristep", "mit.edu")), new Email("scfeng", "mit.edu"));
        assertFalse(union.hashCode()==otherUnion.hashCode());
    }
    
    //Covers partition: list def, equal
    @Test
    public void testListDefHashEqual() {
        Listname name1 = new Listname("name1");
        Listname name2 = new Listname("name1");
        ListExpression expression1 = new Union(new Union(new Email("kbejgo", "mit.edu"), new Email("maristep", "mit.edu")), new Email("scfeng", "mit.edu"));
        ListExpression expression2 = new Union(new Union(new Email("kbejgo", "mit.edu"), new Email("maristep", "mit.edu")), new Email("scfeng", "mit.edu"));
        ListExpression definition1 = new Definition(name1, expression1);
        ListExpression definition2 = new Definition(name2, expression2);
        assertTrue(definition1.hashCode()==definition2.hashCode());
    }
    
    //Covers partition: list def, not equal
    @Test
    public void testListDefHashNotEqual() {
        Listname name1 = new Listname("name1");
        Listname name2 = new Listname("name2");
        ListExpression expression1 = new Union(new Union(new Email("kbejgo", "mit.edu"), new Email("maristep", "mit.edu")), new Email("scfeng", "mit.edu"));
        ListExpression expression2 = new Union(new Union(new Email("kbejgo", "mit.edu"), new Email("maristep", "mit.edu")), new Email("scfeng", "mit.edu"));
        ListExpression definition1 = new Definition(name1, expression1);
        ListExpression definition2 = new Definition(name2, expression2);
        assertFalse(definition1.hashCode()==definition2.hashCode());
    }
    
    //Covers partition: list name, equal
    @Test
    public void testListNameHashEqual() {
        Listname name1 = new Listname("name1");
        Listname name2 = new Listname("name1");
        assertTrue(name1.hashCode()==name2.hashCode());
    }
    
    //Covers partition: list name, not equal
    @Test
    public void testListNameHashNotEqual() {
        Listname name1 = new Listname("name1");
        Listname name2 = new Listname("name2");
        assertFalse(name1.hashCode()==name2.hashCode());
    }
    
    //Covers partition: sequence, equal
    @Test
    public void testSequenceHashEqual() {
        Listname name1 = new Listname("name1");
        Listname name2 = new Listname("name2");
        ListExpression expression1 = new Union(new Union(new Email("kbejgo", "mit.edu"), new Email("maristep", "mit.edu")), new Email("scfeng", "mit.edu"));
        ListExpression expression2 = new Union(new Union(new Email("kbejgo", "mit.edu"), new Email("maristep", "mit.edu")), new Email("scfeng", "mit.edu"));
        ListExpression definition1 = new Definition(name1, expression1);
        ListExpression definition2 = new Definition(name2, expression2);
        ListExpression sequence1 = new Sequence(definition1, definition2);
        ListExpression sequence2 = new Sequence(definition1, definition2);
        assertTrue(sequence1.hashCode()==sequence2.hashCode());
    }
    
    //Covers partition: sequence, not equal
    @Test
    public void testSequenceHashNotEqual() {
        Listname name1 = new Listname("name1");
        Listname name2 = new Listname("name2");
        ListExpression expression1 = new Union(new Union(new Email("kbejgo", "mit.edu"), new Email("maristep", "mit.edu")), new Email("scfeng", "mit.edu"));
        ListExpression expression2 = new Union(new Union(new Email("kbejgo", "mit.edu"), new Email("kbejgo", "mit.edu")), new Email("kbejgo", "mit.edu"));
        ListExpression definition1 = new Definition(name1, expression1);
        ListExpression definition2 = new Definition(name2, expression2);
        ListExpression sequence1 = new Sequence(definition1, definition2);
        ListExpression sequence2 = new Sequence(definition1, definition1);
        assertFalse(sequence1.hashCode()==sequence2.hashCode());
    }
    
    //Covers partition: sequence, list def, not equal
    @Test
    public void testSequenceListDefNotHash() {
        //list definition
        Listname name = new Listname("name1");
        ListExpression expression = new Union(new Union(new Email("kbejgo", "mit.edu"), new Email("maristep", "mit.edu")), new Email("scfeng", "mit.edu"));
        ListExpression definition = new Definition(name, expression);
        //sequence
        Listname otherName = new Listname("otherName");
        ListExpression otherExpression = new Union(new Email("kbejgo", "mit.edu"), new Email("scfeng", "mit.edu"));
        ListExpression otherDefinition = new Definition(otherName, otherExpression);
        Listname secondName = new Listname("secondName");
        ListExpression sequence = new Sequence(otherDefinition, secondName);
        assertFalse(sequence.hashCode()==definition.hashCode());
    }
    
    //Covers partition: listname, email, not equal
    @Test
    public void testListNameEmailNotHash() {
        Listname listName = new Listname("name");
        ListExpression emailName = new Email("kbejgo", "mit.edu");
        assertFalse(listName.hashCode()==emailName.hashCode());
    }
    
      
    //// Tests for recipients(definedLists) ////
    
    private final ListExpression emptyExpression = new Empty();
    
    private final ListExpression kbejgo = new Email("kbejgo", "mit.edu");
    private final ListExpression maristep = new Email("maristep", "mit.edu");
    private final ListExpression scfeng = new Email("scfeng", "mit.edu");
    private final ListExpression bitdiddle = new Email("bitdiddle", "mit");
    
    private final Listname listname031 = new Listname("031_project");
    private final ListExpression union031 = new Union(kbejgo, new Union(maristep, scfeng));
    private final ListExpression definition031 = new Definition(listname031, union031);
    
    private final Map<String, ListExpression> emptyLists = new HashMap<>();
    private final Map<String, ListExpression> environment1 = Map.of("031_project", union031);
    
    private final Set<Listname> listnamesUsed = new HashSet<>();
    
    
    //Covers empty expression, set of length 0
    @Test
    public void testRecipientsEmpty() {
        ListExpression expression = emptyExpression;
        Set<Email> result = expression.recipients(emptyLists, listnamesUsed);
        assertTrue(result.isEmpty(), "should be set of size 0");
    }
    
    //Covers partition: single email, valid email, set of length 1
    @Test
    public void testRecipientsSingleValidEmail() {
        Set<Email> result = kbejgo.recipients(emptyLists, listnamesUsed);
        assertEquals(1, result.size(), "should be set of size 1");
        assertTrue(result.contains(kbejgo));
    }   
    
    //Covers partition: difference of 2 email lists, set of length 1
    @Test
    public void testRecipientBasicDifference() {
        ListExpression expression = new Difference(kbejgo, maristep);
        Set<Email> result = expression.recipients(emptyLists, listnamesUsed);
        assertEquals(1, result.size(), "should be set of size 1");
        assertTrue(result.contains(kbejgo));
    }
    
    //Covers partition: union of 3 email lists, set of length 3
    @Test
    public void testRecipientBasicUnion() {
        ListExpression expression = new Union(kbejgo, new Union(maristep, scfeng));
        Set<Email> result = expression.recipients(emptyLists, listnamesUsed);
        assertEquals(3, result.size(), "should be set of size 3");
        assertTrue(result.contains(kbejgo));
        assertTrue(result.contains(maristep));
        assertTrue(result.contains(scfeng));
    }
    
    //Covers partition: intersection of 3 email lists, empty set
    @Test
    public void testRecipientsBasicIntersection() {
        ListExpression expression = new Intersection(new Intersection(kbejgo, maristep), scfeng);
        Set<Email> result = expression.recipients(emptyLists, listnamesUsed);
        assertEquals(0, result.size(), "should be set of size 0");
    }
    
    //Covers partition: difference of union and intersection, set of length 1
    @Test
    public void testRecipientsDifferenceOfUnionIntersection() {
        ListExpression union = new Union(kbejgo, new Union(maristep, scfeng));
        ListExpression expression = new Difference(union, new Intersection(kbejgo, maristep));
        
        Set<Email> result = expression.recipients(emptyLists, listnamesUsed);
        assertEquals(3, result.size(), "should be set of size 3");
        assertTrue(result.contains(kbejgo));
        assertTrue(result.contains(scfeng));
        assertTrue(result.contains(maristep));
    }    
    
    //Covers partition: union of intersection and difference, set of length 1
    @Test
    public void testRecipientsUnionOfIntersectionDifference() {
        ListExpression expression = new Union(new Intersection(kbejgo, scfeng), 
                                              new Difference(kbejgo, maristep));
        Set<Email> result = expression.recipients(emptyLists, listnamesUsed);
        assertEquals(1, result.size(), "should be set of size 1");
        assertTrue(result.contains(kbejgo));
    }
    
    //Covers partition: intersection of difference and union, set of length 1
    @Test
    public void testRecipientsIntersectionOfDifferenceUnion() {
        ListExpression expression = new Intersection(new Difference(kbejgo, maristep),
                                                     new Union(kbejgo, maristep));
        Set<Email> result = expression.recipients(emptyLists, listnamesUsed);
        assertEquals(1, result.size(), "should be set of size 1");
        assertTrue(result.contains(kbejgo));
    }

    //Covers partition: difference of difference of union of intersection, empty set
    @Test
    public void testRecipientsDifferenceRecurseUnionIntersection() {
        ListExpression unionOfIntersection = new Union(new Intersection(maristep, scfeng), scfeng);
        ListExpression difference = new Difference(unionOfIntersection, kbejgo);
        ListExpression expression = new Difference(difference, scfeng);
        
        Set<Email> result = expression.recipients(emptyLists, listnamesUsed);
        assertEquals(0, result.size(), "should be set of size 0");
    }
    
    //Covers partition: union of difference of union of intersection, set of length 2
    @Test
    public void testRecipientsUnionRecurseDifferenceIntersection() {
        ListExpression unionOfIntersection = new Union(new Intersection(scfeng, scfeng), kbejgo);
        ListExpression difference = new Difference(unionOfIntersection, kbejgo);
        ListExpression unionOfDifference = new Union(difference, maristep);
        ListExpression expression = new Union(unionOfDifference, scfeng);
        
        Set<Email> result = expression.recipients(emptyLists, listnamesUsed);
        assertEquals(2, result.size(), "should be set of size 2");
        assertTrue(result.contains(scfeng));
        assertTrue(result.contains(maristep));
    }
    
    //Covers partition: intersection of intersection of difference of union, set of length 1
    @Test
    public void testRecipientsIntersectionRecurseDifferenceUnion() {
        ListExpression differenceOfUnion = new Difference(new Union(maristep, scfeng), kbejgo);
        ListExpression intersection = new Intersection(differenceOfUnion, maristep);
        ListExpression expression = new Intersection(intersection, maristep);
        
        Set<Email> result = expression.recipients(emptyLists, listnamesUsed);
        assertEquals(1, result.size(), "should be set of size 1");
        assertTrue(result.contains(maristep));
    }
    
    //Covers partition: difference of intersection of union of union, set of length 1
    @Test
    public void testRecipientsDifferenceIntersectionRecurse() {
        ListExpression initialUnion = new Union(new Union(scfeng, maristep), kbejgo);
        ListExpression otherUnion = new Union(kbejgo, scfeng);
        ListExpression intersection = new Intersection(initialUnion, otherUnion);
        ListExpression expression = new Difference(intersection, kbejgo);
        
        Set<Email> result = expression.recipients(emptyLists, listnamesUsed);
        assertEquals(1, result.size(), "should be set of size 1");
        assertTrue(result.contains(scfeng));
    }
    
    //Covers definition
    @Test
    public void testRecipientsSingleDefinition() {
        Set<Email> result = definition031.recipients(emptyLists, listnamesUsed);
        assertEquals(3, result.size(), "should be set of size 3");
        assertTrue(result.contains(kbejgo));
        assertTrue(result.contains(scfeng));
        assertTrue(result.contains(maristep));
    }
    
    //Covers listname, definedLists empty
    @Test
    public void testRecipientsListnameUndefined() {
        Set<Email> result = listname031.recipients(emptyLists, listnamesUsed);
        assertEquals(0, result.size(), "should be set of size 0");
    } 
    
    //Covers listname, defined in definedLists
    @Test
    public void testRecipientsSingleListname() {
        Set<Email> result = listname031.recipients(environment1, listnamesUsed);
        assertEquals(3, result.size(), "should be set of size 3");
        assertTrue(result.contains(kbejgo));
        assertTrue(result.contains(scfeng));
        assertTrue(result.contains(maristep));
    } 
    
    //
    @Test
    public void testRecipientsSequenceDefined() {
        ListExpression sequence = new Sequence(definition031, listname031);
        Set<Email> result = sequence.recipients(emptyLists, listnamesUsed);
        assertEquals(3, result.size(), "should be set of size 3");
        assertTrue(result.contains(kbejgo));
        assertTrue(result.contains(scfeng));
        assertTrue(result.contains(maristep));
    }
    
    //
    @Test
    public void testRecipientsSequenceUndefined() {
        ListExpression sequence = new Sequence(union031, listname031);
        Set<Email> result = sequence.recipients(emptyLists, listnamesUsed);
        assertEquals(0, result.size(), "should be set of size 0");
    } 
    
    //
    @Test
    public void testRecipientsSequenceEmpty() {
        ListExpression sequence = new Sequence(union031, new Empty());
        Set<Email> result = sequence.recipients(emptyLists, listnamesUsed);
        assertEquals(0, result.size(), "should be set of size 0");
    } 
    
    //
    @Test
    public void testRecipientsMutualRecursion() {
        ListExpression expression = new Sequence(new Definition(new Listname("a"), new Listname("b")),
                                                 new Definition(new Listname("b"), new Listname("a")));
        assertThrows(IllegalArgumentException.class, () -> {
            expression.recipients(emptyLists, listnamesUsed);
        }, "Mutually-recursive list definitions");
    }
    
    //
    @Test
    public void testRecipientsSequenceOverridingDefinition() {
        ListExpression sequence = new Sequence(new Definition(listname031, bitdiddle), listname031);
        Set<Email> result = sequence.recipients(environment1, listnamesUsed);
        assertEquals(1, result.size(), "should be set of size 1");
        assertTrue(result.contains(bitdiddle));
    }
    
    //
    @Test
    public void testRecipientsSequenceModifyingDefinition() {
        ListExpression sequence = new Sequence(new Definition(listname031, new Union(union031, bitdiddle)), 
                                               listname031);
        Set<Email> result = sequence.recipients(environment1, listnamesUsed);
        assertEquals(4, result.size(), "should be set of size 4");
        assertTrue(result.contains(kbejgo));
        assertTrue(result.contains(scfeng));
        assertTrue(result.contains(maristep));
        assertTrue(result.contains(bitdiddle));
    }
    
    //
    @Test
    public void testRecipientsSequenceMultipleDefinitions() {
        ListExpression sequence = new Sequence(new Sequence(new Definition(new Listname("list1"), union031),
                                                   new Definition(new Listname("list2"), maristep)),
                                               new Difference(new Listname("list1"), new Listname("list2")));

        Set<Email> result = sequence.recipients(emptyLists, listnamesUsed);
        assertEquals(2, result.size(), "should be set of size 2");
        assertTrue(result.contains(kbejgo));
        assertTrue(result.contains(scfeng));

        Set<Email> result2 = sequence.recipients(environment1, listnamesUsed);
        assertEquals(2, result2.size(), "should be set of size 2");
        assertTrue(result2.contains(kbejgo));
        assertTrue(result2.contains(scfeng));
    }
    
    
    //// Tests for definitions() ////
    
    //Covers no definitions
    @Test
    public void testNoDefinitions() {
        Map<String, ListExpression> result = kbejgo.definitions();
        assertEquals(0, result.size(), "should be empty map");
    }
    
    //Covers 1 definition
    @Test
    public void testSingleDefinition() {
        Map<String, ListExpression> result = definition031.definitions();
        assertEquals(1, result.size(), "should be map of size 1");
        assertTrue(result.containsKey("031_project"));
        assertEquals(union031, result.get("031_project"));
    }
    
    //Covers 1 definition inside another expression
    @Test
    public void testDefinitionInside() {
        ListExpression expression = new Union(definition031, maristep);
        Map<String, ListExpression> result = expression.definitions();
        assertEquals(1, result.size(), "should be map of size 1");
        assertTrue(result.containsKey("031_project"));
        assertEquals(union031, result.get("031_project"));
    }
    
    //Covers 1 definition
    @Test
    public void testDefinitionModified() {
        ListExpression expression = new Definition(listname031, new Union(union031, bitdiddle));
        Map<String, ListExpression> result = expression.definitions();
        assertEquals(1, result.size(), "should be map of size 1");
        assertTrue(result.containsKey("031_project"));
        assertEquals(new Union(union031, bitdiddle), result.get("031_project"));
    }
    
    private final ListExpression defRoom1 = new Definition(new Listname("room1"), new Email("alice", "mit.edu"));
    private final ListExpression defRoom2 = new Definition(new Listname("room2"), new Email("bob", "mit.edu"));
    
    //Covers >1 independent definitions
    @Test
    public void testMultipleDefinitions() {
        ListExpression expression = new Sequence(defRoom1, defRoom2);
        Map<String, ListExpression> result = expression.definitions();
        assertEquals(2, result.size(), "should be map of size 2");
        assertTrue(result.containsKey("room1"));
        assertEquals(new Email("alice", "mit.edu"), result.get("room1"));
        assertTrue(result.containsKey("room2"));
        assertEquals(new Email("bob", "mit.edu"), result.get("room2"));
    }
    
    //Covers >1 dependent definitions
    @Test
    public void testDependentDefinitions() {
        ListExpression expression = new Sequence(new Definition(new Listname("suite"),
                                                     new Union(new Listname("room1"), new Listname("room2"))), 
                                                 new Sequence(defRoom1, defRoom2));
        Map<String, ListExpression> result = expression.definitions();
        assertEquals(3, result.size(), "should be map of size 3");
        assertTrue(result.containsKey("room1"));
        assertEquals(new Email("alice", "mit.edu"), result.get("room1"));
        assertTrue(result.containsKey("room2"));
        assertEquals(new Email("bob", "mit.edu"), result.get("room2"));
        assertTrue(result.containsKey("suite"));
        assertEquals(new Union(new Listname("room1"), new Listname("room2")), result.get("suite"));
    }
}
