
package hashtest;

import java.io.*;
import java.util.*;

public class Hashtest {

    static BufferedReader br;
    static int tsize;
    static int fcode;

    
public static void main(String[] args) throws IOException {
    tsize = 100;                     /* Default table size */
    fcode = 1;                       /* Default hash function choice*/
    Hash158 h = new Hash158(tsize, fcode); /* Hash set being tested */
    HashSet<String> z = new HashSet<String>();      /* For verification */
    br = new BufferedReader(new InputStreamReader(System.in));

    while (true) {
	listCommands();
	System.out.print("Command: ");
        String cmd = readone();
	if (cmd == "") continue;
	switch (cmd.toLowerCase().charAt(0)) {
	

    /* Make a hash set, user specifies size */
    case 'n':
	tsize = readoneint("Number of hash set slots (1 - 1 million): ", 10000000);
        fcode = readoneint("Hash func: 1=builtin 2=custom: ", 2);
        h = new Hash158(tsize, fcode);
	z = new HashSet<String>();
        break;

    /* Clear a hash set */
    case 'c':
       System.out.println("Clearing hash set");
       h.clear();
       z.clear();
       break;

    /* Fill hash set object from input file */
    case 'r':
       System.out.print("Input file with vocabulary words: ");
       openAndRead(readone(), h, z);
       break;

    /* Possibly emit output file */
    case 'w':
       System.out.print("Output file for vocabulary words: ");
       emitWords(readone(), h);
       break;

    /* Process strings from standard input and print results */
    case 's':
       processStrings(h);
       break;

    /* Print statistics on the hashset */
    case 't':
       printStats(h, z);
       break;

    /* Iterator test */
    case 'i':
       iteratorTest();
       break;

    /* Done */
    case 'q':
       System.exit(1);
       break;

        } /* switch */
    } /* while */
}

    /*----------------------------------*/
    /* Read a token from standard input */
    /*----------------------------------*/

static String readone() throws IOException { 

    String line = br.readLine();
    StringTokenizer tk = new StringTokenizer(line);
    if (tk.hasMoreTokens())
        return tk.nextToken();
    else
	return "";
}

    /*--------------------------------*/
    /* Read an integer > 0 from stdin */
    /*--------------------------------*/

private static int readoneint(String prompt, int max) throws IOException {
    int rslt = 0;
    while ((rslt <= 0) || (rslt > max)) {
        System.out.print(prompt);
        try {
            rslt = Integer.parseInt(readone());
        } catch (NumberFormatException e) { }
    }
    return rslt;
}


    /*------------------------------------------------------------------*/
    /* Process strings from standard input, one per line, write results */
    /*------------------------------------------------------------------*/

static void processStrings(Hash158 h) {
    try {
        String linebuf;
	while (true) {
	    System.out.print("Words to check: ");
            if ((linebuf=br.readLine()) == null) break;

            /* Read line and tokenize it. Break out and return if empty input */
            StringTokenizer tk = new StringTokenizer(linebuf);
	    if (!tk.hasMoreTokens()) break;
            
            /* Check each token against hash table */
            while (tk.hasMoreTokens()) {
	        String s = tk.nextToken();
                if (!h.contains(s)) {
		    System.out.println(s + " NOT FOUND");
		} else {
		    System.out.println(s + " OK");
		}
	    }

        } // while readLine
    } catch (IOException e) {  
        err("Error reading standard input");
    }
} // processStrings()

    /*--------------------------------------------*/
    /*  Open and read input file into Hash158     */
    /*--------------------------------------------*/

static boolean openAndRead(String filename, Hash158 h, HashSet<String> z) {
    String linebuf;
    int entries = 0;

    /* Open words file */
    if (filename.equals("")) return false;
    try {
        BufferedReader r = new BufferedReader(new FileReader(filename));

        /* Process each line */
        while ((linebuf=r.readLine()) != null) {  

            /* Store in hash set */
            StringTokenizer tk = new StringTokenizer(linebuf);
            while (tk.hasMoreTokens()) {
		String word = tk.nextToken();
		h.add(word);
		z.add(word);
		entries += 1;
	    }

       } // while readLine
    } catch (IOException e) {  
        err("Error reading words file: " + filename);
	return false;
    } 
   
    System.out.println("Words read: " + entries); 
    return true;
} // openAndRead()

    /*-----------------------------------*/
    /* Print Statistics from the Hash158 */
    /*-----------------------------------*/

    // h is the Hash158 being tested.
    // z is a HashSet loaded with the same vocabulary, for verification
static void printStats(Hash158 h, HashSet z) {

    System.out.println("Entries " + h.size() + 
		    " (should be: " + z.size() + ")");
    System.out.println("Load Factor=" + h.loadfactor() + 
		    " (should be: " + ((float)z.size())/tsize + ")");
    System.out.println("Empty Slots=" + h.emptyslots());
    System.out.println("Chi-square=" + h.chisq());
    System.out.println("Evenness=" + h.evenness());
} // printStatistics 

    /*---------------*/
    /* Iterator Test */
    /*---------------*/

static void iteratorTest() throws IOException {

    Hash158 A, B;
    HashSet<String> rslts = new HashSet<String>();
    int i;
    String chars[] = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", 
    "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    String digits[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

    int asize = readoneint("Number of characters (1-26): ", 26);
    int bsize = readoneint("Number of digits (1-10): ", 10);
    A = new Hash158(asize/2+1, fcode);
    B = new Hash158(bsize/2+1, fcode);
    for (i=0; i<asize; i++) {A.add(chars[i]);}
    for (i=0; i<bsize; i++) {B.add(digits[i]);}

    int expected = asize * bsize * asize;
    System.out.println("Iterating " + asize + " x " + bsize + " x " + asize +
		    " = " + expected + " combinations.");

    ArrayList<String> combs = new ArrayList<String>();
    HashSet<String> uniquecombs = new HashSet<String>();

    /* Generate asize x bsize x asize combinations */

    Hash158Iterator i1 = A.iterator();
    while (i1.hasNext()) {
        String s1 = (String)i1.next();
        Hash158Iterator i2 = B.iterator();
	while (i2.hasNext()) {
            String s2 = s1 + (String)i2.next();
            Hash158Iterator i3 = A.iterator();
            while (i3.hasNext()) {
                String s3 = s2 + (String)i3.next();
		combs.add(s3);
		uniquecombs.add(s3);
	    }
	}
    }

    /*  Print all combinations, 10 to a line. */
    Iterator it = combs.iterator();
    while (it.hasNext()) {
        String line = "";
        while (it.hasNext() && (line.length() < 40)) {
            line += (String)it.next() + " ";
	}
	System.out.println(line);
    }

    /*  Print results */
    if ((combs.size() != expected) | (uniquecombs.size() != expected)) {
        System.out.println("Iterators produced " + combs.size() + 
		    " combinations, of which " + uniquecombs.size() +
		    " are unique.  (Expected: " + (asize*bsize*asize) + ")");
    } else {
	System.out.println("Iterators produced " + uniquecombs.size() +
            " combinations.  Test passed.");
    }
}

    /*-----------------------------------------*/
    /* Emit vocabulary from HASHET into a file */
    /*-----------------------------------------*/

static void emitWords(String filename, Hash158 h) {
    int nwords = 0;

    /* Open output file */
    if (filename.equals("")) return;
    try {
        PrintStream pw = new PrintStream(new FileOutputStream(filename), true);
	Hash158Iterator it = h.iterator();
	while (it.hasNext()) {
	    pw.println((String)it.next());
	    nwords += 1;
	}
    } catch (IOException e) {  
        err("Error emitting words file: " + filename);
    } 

    System.out.println("Words emitted: " + nwords);
} // emitWords()

    /*--------------------------*/
    /*  Error message and exit  */
    /*--------------------------*/

static void err(String errmes) {
    System.out.println(errmes + "\n");
    System.exit(0);
}


public static void listCommands() {
        System.out.println(" n  new hashset with n slots");
        System.out.println(" c  clear hashset");
        System.out.println(" r  read file of vocabulary words into hashset");
        System.out.println(" w  write vocabulary words from hashset");
        System.out.println(" s  spell check");
	System.out.println(" t  hashset statistics");
	System.out.println(" i  iterator test");
	System.out.println(" q  quit");
}

} // Class hashtest
