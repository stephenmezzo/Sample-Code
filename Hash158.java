package hashtest;


import java.util.*;

public class Hash158 {

    Slot hashtable[];
    int tsize;
    int fcode;
    int entries;
    int occupiedslots;

    public Hash158(int size, int fcode1) {
        tsize = size;
        fcode = fcode1;
        entries = 0;
        hashtable = new Slot[size];
        for (int i = 0; i < tsize; i++) {
            hashtable[i] = new Slot();
        }

    }//Has158 Constructor

    public void add(String word) {
        if (fcode == 1) {
            int hash = word.hashCode();
            if (hash < 0) {
                hash += (1 << 31);
            }
            hashtable[hash % tsize].add(word);
            if (hashtable[hash % tsize].size() == 1) {
                occupiedslots++;
            }
            entries++;
        }//fcode = 1
        if (fcode == 2) {
            int value = 0;
            for (int i = 0; i < word.length(); i++) {
                value += (int) word.charAt(i);
            }
            hashtable[value % tsize].add(word);
            if (hashtable[value % tsize].size() == 1) {
                occupiedslots++;
            }
            entries++;
        }//fcode = 2
    }//add

    public int size() {
        return entries;
    }

    public void clear() {
        for (int i = 0; i < hashtable.length; i++) {
            hashtable[i].clear();
        }
    }//clear

    public boolean contains(String word) {
        if (fcode == 1) {
            int hash = word.hashCode();
            if (hash < 0) {
                hash += (1 << 31);
            }
            return hashtable[hash % tsize].contains(word);
        }//if 1
        if (fcode == 2) {
            int value = 0;
            for (int i = 0; i < word.length(); i++) {
                value += (int) word.charAt(i);
            }
            return hashtable[value % tsize].contains(word);
        }//if 2
        return false;
    }//contains

    public Hash158Iterator iterator() {
        return new Hash158Iterator(hashtable);
    }

    public double loadfactor() {
        return ((double)entries / tsize);
    }

    public int emptyslots() {
        return tsize - occupiedslots;
    }

    public double chisq() {
        double result = 0.0;
        double f = loadfactor();
        for (int i = 0; i < tsize; i++) {
            double difference = Math.pow(hashtable[i].size() - f, 2);
            result += difference;
        }
        return (result / f);
    }

    public double evenness() {
        double result = 0.0;
        double f = loadfactor();
        for (int i = 0; i < tsize; i++) {
            double difference = Math.pow(hashtable[i].size() - f, 2);
            result += difference;
        }
        double result2 = result / tsize;
        return Math.pow(result2, .5);
    }

}//CLASS HASH158

class Slot {

    ArrayList<String> t;

    Slot() {
        t = new ArrayList<>();
    }

    public int size() {
        return t.size();
    }

    public void add(String s) {
        t.add(s);
    }

    public String elementAt(int i) {
        return t.get(i);
    }

    public boolean contains(String s) {
        return t.contains(s);
    }

    public void clear() {
        t.clear();
    }

    public boolean remove(String s) {
        return t.remove(s);
    }

    public String get(int position) {
        String x = t.get(position);
        return x;
    }

}

class Hash158Iterator {

    int hashPosition;
    int slotPosition;
    Slot[] hashtable;

    public Hash158Iterator(Slot[] hashtable) {
        hashPosition = 0;
        slotPosition = 0;
        this.hashtable = hashtable;
    }

    public boolean hasNext() {
        return hashPosition < hashtable.length && hashtable[hashPosition].size()>0;
    }

    public String next() {
        while (!hasNext()) {
            hashPosition++;
            slotPosition = 0;
            hasNext();
        }
            String result = hashtable[hashPosition].get(slotPosition);
            if (hashtable[hashPosition].size()>slotPosition+1) {
              slotPosition++;
            }
            else {
                hashPosition++;
                slotPosition=0;
            }
            return result;
    }

    public void remove() {

    }
}
