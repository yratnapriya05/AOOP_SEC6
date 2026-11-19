/*
 * WEEK 02 - PROGRAM 8 : THE LISKOV DIRECTION RULE
 * Deck slides: "Liskov, stated precisely", "The direction rule", "Three more Liskov clauses"
 *
 * CONCEPT
 * -------
 * LISKOV SUBSTITUTION: if S is a subtype of T, any code written for T must keep working
 * when it is given an S, without knowing.
 *
 * THE DIRECTION RULE (memorise this one sentence):
 *     A subtype may WEAKEN preconditions   (accept MORE)
 *     and STRENGTHEN postconditions        (promise MORE).
 *     It may NEVER do the reverse.
 *
 * Think from the CALLER's side. The caller was written for the supertype:
 *   - subtype accepts MORE  -> every call the caller makes is still legal.      SAFE
 *   - subtype accepts LESS  -> a legal call is now rejected.                    BROKEN
 *   - subtype promises MORE -> the caller gets a better result than it needed.  SAFE
 *   - subtype promises LESS -> the caller loses a guarantee it relied on.       BROKEN
 *
 * WHAT THIS PROGRAM SHOWS
 * -----------------------
 * One caller (FleetImporter) that only knows the SpotAllocator contract, and four subtypes.
 *
 * HOW TO RUN :  java P08_LiskovDirectionRule.java
 */
import java.util.*;

public class P08_LiskovDirectionRule {

    enum Size { MOTORCYCLE, CAR, SUV, TRUCK }
    record Vehicle(String plate, Size size) { }
    record Spot(String id, int distance, boolean free) { }

    /**
     * CONTRACT of SpotAllocator (the supertype)
     * PRE  : vehicle != null. ANY size is acceptable.
     * POST : returns a spot that is free. Never null.
     */
    interface SpotAllocator {
        Spot allocate(Vehicle v);
    }

    // ---- SAFE 1: promises MORE (strengthened postcondition) ----
    // Contract only says "a free spot". This one returns the NEAREST free spot. Callers are happy.
    static class NearestFirst implements SpotAllocator {
        public Spot allocate(Vehicle v) { return new Spot("S1", 5, true); }
    }

    // ---- SAFE 2: accepts MORE (weakened precondition) ----
    // Contract says vehicle != null. This one even tolerates null (treats it as a CAR).
    static class Forgiving implements SpotAllocator {
        public Spot allocate(Vehicle v) {
            Size size = (v == null) ? Size.CAR : v.size();     // a null vehicle is quietly treated as a CAR
            return new Spot("S2", 10, true);
        }
    }

    // ---- BROKEN 1: accepts LESS (strengthened precondition) ----
    // Contract says ANY size. This one rejects motorcycles. A legal call now explodes.
    static class StrictSize implements SpotAllocator {
        public Spot allocate(Vehicle v) {
            if (v.size() == Size.MOTORCYCLE) throw new IllegalArgumentException("cars and larger only");
            return new Spot("S3", 20, true);
        }
    }

    // ---- BROKEN 2: promises LESS (weakened postcondition) ----
    // Contract says "a free spot". This one may return a spot that is NOT free.
    static class Careless implements SpotAllocator {
        public Spot allocate(Vehicle v) { return new Spot("S4", 30, false); }
    }

    /** The caller. It knows ONLY the contract. It does not check which subtype it holds. */
    static void fleetImporter(String name, SpotAllocator allocator) {
        List<Vehicle> fleet = List.of(
            new Vehicle("TS09AB1234", Size.CAR),
            new Vehicle("TS09CD5678", Size.MOTORCYCLE));      // legal under the contract!
        try {
            for (Vehicle v : fleet) {
                Spot s = allocator.allocate(v);
                if (!s.free()) throw new IllegalStateException("got a spot that is not free");
            }
            System.out.println(String.format("%-12s -> caller works fine (substitutable)", name));
        } catch (RuntimeException e) {
            System.out.println(String.format("%-12s -> caller BROKEN: %s", name, e.getMessage()));
        }
    }

    public static void main(String[] args) {
        fleetImporter("NearestFirst", new NearestFirst());   // promises more   -> safe
        fleetImporter("Forgiving",    new Forgiving());      // accepts more    -> safe
        fleetImporter("StrictSize",   new StrictSize());     // accepts less    -> violation
        fleetImporter("Careless",     new Careless());       // promises less   -> violation
        System.out.println("\nRemember: preconditions and postconditions move in OPPOSITE directions.");
    }
}
