package enigma;

/** Superclass that represents a rotor in the enigma machine.
 *  @author Shan Ali Virani
 */
class Rotor {

    /** A rotor named NAME whose permutation is given by PERM. */
    Rotor(String name, Permutation perm) {
        _name = name;
        _permutation = perm;
        _setting = 0;
    }

    /** Return my name. */
    String name() {
        return _name;
    }

    /** Return my alphabet. */
    Alphabet alphabet() {
        return _permutation.alphabet();
    }

    /** Return my permutation. */
    Permutation permutation() {
        return _permutation;
    }

    /** Return the size of my alphabet. */
    int size() {
        return alphabet().size();
    }

    /** Return true iff I have a ratchet and can move. */
    boolean rotates() {
        return false;
    }

    /** Return true iff I reflect. */
    boolean reflecting() {
        return false;
    }

    /** Return my current setting. */
    int setting() {
        return _setting;
    }

    /** Set setting() to POSN.  */
    void set(int posn) {
        _setting = _permutation.wrap(posn);
    }

    /** Set setting() to character CPOSN. */
    void set(char cposn) {
        _setting = alphabet().toInt(cposn);
    }

    /** Return the conversion of P (an integer in the range 0..size()-1)
     *  according to my permutation. */
    int convertForward(int p) {
        int convertedIndex = _permutation.
                wrap(p + setting());
        int towards = _permutation.
                permute(convertedIndex);
        int result = _permutation.
                wrap(towards - setting());

        if (Main.verbose()) {
            System.err.printf("Error");
        }
        return result;
    }

    /** Return the conversion of E (an integer in the range 0..size()-1)
     *  according to the inverse of my permutation. */
    int convertBackward(int e) {
        int convertedIndex = _permutation.
                wrap(e + setting());
        int towards = _permutation.
                invert(convertedIndex);
        int result = _permutation.
                wrap(towards - setting());

        if (Main.verbose()) {
            System.err.printf("Error");
        }

        return result;
    }

    /** Returns the positions of the notches, as a string giving the letters
     *  on the ring at which they occur. */
    String notches() {
        return "";
    }

    /** Returns true iff I am positioned to allow the rotor to my left
     *  to advance. */
    boolean atNotch() {
        return true;
    }

    /** Advance me one position, if possible. By default, does nothing. */
    void advance() {
        if (_setting >= _permutation.alphabet().size()) {
            _setting = 0;
        } else {
            _setting += 1;
        }
    }

    @Override
    public String toString() {
        return "Rotor " + _name;
    }
    /** My name. */
    private final String _name;
    /** The permutation implemented by this rotor in its 0 position. */
    private Permutation _permutation;
    /** Shows the setting in its position. */
    private int _setting;
}
