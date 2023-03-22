package enigma;

/** Class that represents a rotor that has no ratchet and does not advance.
 *  @author Shan Ali Virani
 */
class FixedRotor extends Rotor {

    /** A non-moving rotor named NAME whose permutation at the 0 setting
     * is given by PERM. */
    FixedRotor(String name, Permutation perm) {
        super(name, perm);
    }

    @Override
    void set(char cposn) {
        System.out.println("FixedRotor.set: setting position to " + cposn);
        super.set(alphabet().toInt(cposn));
    }

    @Override
    boolean atNotch() {
        return false;
    }

    @Override
    boolean rotates() {
        return false;
    }

    @Override
    void advance() {
    }
}
