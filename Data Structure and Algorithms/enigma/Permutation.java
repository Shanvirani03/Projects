package enigma;

import java.util.HashMap;
import java.util.Map;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Shan Ali Virani
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _cycles = cycles;

        for (int i = 0; i < alphabet.size(); i++) {
            maps.put(alphabet.toChar(i), alphabet.toChar(i));
            reverseMap.put(alphabet.toChar(i), alphabet.toChar(i));
        }

        int startCycleIndex = cycles.indexOf('(', 0);
        while (startCycleIndex != -1) {
            int endCycleIndex = cycles.indexOf(')', startCycleIndex);
            for (int i = startCycleIndex + 1; i < endCycleIndex - 1; i++) {
                maps.put(cycles.charAt(i), cycles.charAt(i + 1));
                reverseMap.put(cycles.charAt(i + 1), cycles.charAt(i));
            }

            maps.put(cycles.charAt(endCycleIndex - 1),
                    cycles.charAt(startCycleIndex + 1));
            reverseMap.put(cycles.charAt(startCycleIndex + 1),
                    cycles.charAt(endCycleIndex - 1));

            startCycleIndex = cycles.indexOf('(', endCycleIndex);
        }
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        return _alphabet.toInt(maps.get(_alphabet.toChar(p)));
    }

    /** Return the result of applying the inverse of this permutation
     *  to C modulo the alphabet size. */
    int invert(int c) {
        return _alphabet.toInt(reverseMap.get(_alphabet.toChar(c)));
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        return maps.get(p);
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        return reverseMap.get(c);
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (Map.Entry<Character, Character> val: maps.entrySet()) {
            if (val.getKey() == val.getValue()) {
                return false;
            }
        }
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;
    /** Cycles given the alphabet. */
    private String _cycles;
    /** Initialization of Cycles. */
    private HashMap<Character, Character> maps = new HashMap<>();
    /** Reverse Mapping of Alphabet in Cycles. */
    private HashMap<Character, Character> reverseMap = new HashMap<>();
}
