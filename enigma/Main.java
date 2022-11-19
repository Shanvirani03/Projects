package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Vector;

import ucb.util.CommandArgs;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Shan Ali Virani
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            CommandArgs options =
                    new CommandArgs("--verbose --=(.*){1,3}", args);
            if (!options.ok()) {
                throw error("Usage: java enigma.Main [--verbose] "
                        + "[INPUT [OUTPUT]]");
            }

            _verbose = options.contains("--verbose");
            new Main(options.get("--")).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Open the necessary files for non-option arguments ARGS (see comment
     *  on main). */
    Main(List<String> args) {
        _config = getInput(args.get(0));

        if (args.size() > 1) {
            _input = getInput(args.get(1));
        } else {
            _input = new Scanner(System.in);
        }

        if (args.size() > 2) {
            _output = getOutput(args.get(2));
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        boolean x = false;
        Machine machine = readConfig();
        while (_input.hasNextLine()) {
            String conf = _input.nextLine();
            if (conf.startsWith("*")) {
                x = true;
                setUp(machine, conf);
            } else {
                if (!x) {
                    throw new EnigmaException("a");
                }
                String encrypyed = machine.convert(conf);
                printMessageLine(encrypyed);
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            String alphabetInput = _config.next();
            _alphabet = new Alphabet(alphabetInput);
            if (_alphabet.toString().contains("*")
                    || _alphabet.toString().contains("(")
                    || _alphabet.toString().contains(")")) {
                throw new EnigmaException("Configuration Format is incorrect");
            }

            int numRotors = _config.nextInt();
            int pawls = _config.nextInt();
            Vector<Rotor> allRotors = new Vector<>();
            while (_config.hasNext()) {
                allRotors.add(readRotor());
            }
            _alphabet = new Alphabet();
            return new Machine(_alphabet, numRotors, pawls, allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String name = _config.next();
            String rotorDef = _config.next();
            String permutation = new String();
            while (_config.hasNext("\\(.*\\)")) {
                permutation += _config.next();
            }
            Permutation perm = new Permutation(permutation, _alphabet);
            switch (rotorDef.charAt(0)) {
            case 'M':
                String notches = rotorDef.substring(1);
                Rotor moveable = new MovingRotor(name, perm, notches);
                return moveable;
            case 'N':
                Rotor rotor = new FixedRotor(name, perm);
                return rotor;
            case 'R':
                Rotor reflector = new Reflector(name, perm);
                return reflector;
            default:
                break;
            }
            return null;
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description: " + excp.getMessage());
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        String[] words = settings.trim().split(" ");
        String add = "";
        String[] rotorNames = new String[M.numRotors()];
        for (int i = 1; i < words.length; i++) {
            if (i <= M.numRotors()) {
                rotorNames[i - 1] = words[i];
            } else if (i > M.numRotors() + 1) {
                add = add.concat(words[i] + " ");
            }
        }
        for (int i = 0; i < rotorNames.length - 1; i++) {
            for (int k = i + 1; k < rotorNames.length; k++) {
                if (rotorNames[i].equals(rotorNames[k])) {
                    throw new EnigmaException("Can not have duplicate Rotors");
                }
            }
        }
        M.insertRotors(rotorNames);
        if (!M.getRotor(0).reflecting()) {
            throw new EnigmaException("First rotor must be reflector");
        }
        if (M.numRotors() + 1 >= words.length) {
            throw new EnigmaException("Index is Incorrect");
        }
        M.setRotors(words[M.numRotors() + 1]);
        M.setPlugboard(new Permutation(add, _alphabet));
    }

    /** Return true iff verbose option specified. */
    static boolean verbose() {
        return _verbose;
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        String compressedMsg = msg.replaceAll("\\s", "");
        for (int i = 0; i < compressedMsg.length() - 1; i += 5) {
            String segment;
            if (i + 5 > compressedMsg.length()) {
                segment = compressedMsg.substring(i);
            } else {
                segment = compressedMsg.substring(i, i + 5);
            }
            System.out.print(segment + " ");
        }
        System.out.println("");
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** True if --verbose specified. */
    private static boolean _verbose;
}
