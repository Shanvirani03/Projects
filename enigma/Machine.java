package enigma;
import java.util.ArrayList;
import java.util.Collection;

/** Class that represents a complete enigma machine.
 *  @author Shan Ali Virani
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = allRotors;
        _plugboard = new Permutation("", alpha);
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Return Rotor #K, where Rotor #0 is the reflector, and Rotor
     *  #(numRotors()-1) is the fast Rotor.  Modifying this Rotor has
     *  undefined results. */
    Rotor getRotor(int k) {
        return _setRotors.get(k);
    }

    Alphabet alphabet() {
        return _alphabet;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        if (_setRotors.size() > _allRotors.size()) {
            throw new EnigmaException("Collection of "
                    + "Rotors is greater than size of all Rotors.");
        }
        if (_setRotors.size() != 0) {
            _setRotors.clear();
        }
        for (String rotor : rotors) {
            for (Rotor rotorName : _allRotors) {
                if (rotorName.name().equals(rotor)
                        && !(_setRotors.contains(rotorName))) {
                    _setRotors.add(rotorName);
                }
            }
            if (!_setRotors.get(0).reflecting()) {
                throw new EnigmaException("Reflector in wrong place");
            }
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (_numRotors - 1 != setting.length()) {
            throw new EnigmaException("Setting must be "
                    + "of length numRotors - 1!");
        }
        for (int x = 0; x < setting.length(); x++) {
            if (!_alphabet.contains(setting.charAt(x))) {
                throw new EnigmaException("Setting is not in alphabet!");
            }
        }
        for (int i = 0; i < setting.length(); i++) {
            _setRotors.get(i + 1).set(_alphabet.toInt(setting.charAt(i)));
        }
    }

    /** Return the current plugboard's permutation. */
    Permutation plugboard() {
        return _plugboard;
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        advanceRotors();
        if (Main.verbose()) {
            System.err.printf("[");
            for (int r = 1; r < numRotors(); r += 1) {
                System.err.printf("%c",
                        alphabet().toChar(getRotor(r).setting()));
            }
            System.err.printf("] %c -> ", alphabet().toChar(c));
        }
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(c));
        }
        c = applyRotors(c);
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c%n", alphabet().toChar(c));
        }
        return c;
    }

    /** Advance all rotors to their next position. */
    private void advanceRotors() {
        boolean [] notch = new boolean[_setRotors.size()];
        notch[_setRotors.size() - 1] = true;
        for (int i = 1; i < _setRotors.size() - 1; i++) {
            Rotor currentRotor = _setRotors.get(i);
            Rotor next = _setRotors.get(i - 1);
            Rotor before = _setRotors.get(i + 1);
            if (currentRotor.atNotch() && next.rotates()) {
                notch[i] = true;
            } else if (before.atNotch()) {
                notch[i] = true;
            }
        }
        for (int j = 0; j < _setRotors.size(); j++) {
            if (notch[j]) {
                _setRotors.get(j).advance();
            }
        }
    }


    /** Return the result of applying the rotors to the character C (as an
     *  index in the range 0..alphabet size - 1). */
    private int applyRotors(int c) {
        for (int j = _setRotors.size() - 1; j >= 0; j--) {
            c = _setRotors.get(j).convertForward(c);
        }
        for (int i = 1; i < _setRotors.size(); i++) {
            c = _setRotors.get(i).convertBackward(c);
        }
        return c;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        char [] messageChars = new char[msg.length()];
        for (int i = 0; i < msg.length(); i++) {
            char c = msg.charAt(i);
            if (_alphabet.contains(c)) {
                messageChars[i] = _alphabet.toChar(convert(
                        _alphabet.toInt(msg.charAt(i))));
            } else {
                messageChars[i] = c;
            }
        }
        String letters = String.valueOf(messageChars);
        return letters;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;
    /** Specific bag of rotors chosen from the full collection of rotors. */
    private ArrayList<Rotor> _setRotors = new ArrayList<Rotor>();
    /** Number of Rotors. */
    private int _numRotors;
    /** Number of Pawls. */
    private int _pawls;
    /** Collection of all Rotors. */
    private Collection<Rotor> _allRotors;
    /** Permutation of the plugboard. */
    private Permutation _plugboard;
}
