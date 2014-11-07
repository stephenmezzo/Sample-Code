package hashtest;

import java.util.*;

public interface Hash158Interface {

	/*-------------------------------------*/
	/* The following are the usual methods */
	/*-------------------------------------*/

    /* The constructor requires the size of the underlying table */
    /* public Hash158(int tablesize);                            */

    /* Add string, return true if the string was already in the set */
    public boolean add(String s);

    /* Empties the set. */
    public void clear();

    /* Test, return true if the string is in the set. */
    public boolean contains(String s);

    /* Tests for emptiness */
    public boolean isEmpty();

    /* Remove string, return true if contained the string */
    public boolean remove(String s);

    /* Number of strings in the set */
    public int size();

    /* Return an Iterator over the set */
    /*  (You will need to create a separate private class.)
    /*  (Be sure it implements a remove method)
    */
    public Iterator iterator();

	/*------------------------------------------------------------*/
	/* Following are methods for evaluating the hashing function  */
	/*------------------------------------------------------------*/

    /* The load factor */
    public float loadfactor();

    /* Compute the chi-square statistic on the list lengths for each
       slot, where the expected length for each slot is the load factor.
       */
    public float chisq();

    /* Compute the standard deviation of the list lengths from
       all the non-empty table slots
       */
    public float evenness();

    /* The number of empty table slots */
    public int emptyslots(); 
}
