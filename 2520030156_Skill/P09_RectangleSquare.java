public class P09_RectangleSquare {

    // ---------------- PART 1 : the mutable design (broken) ----------------
    static class Bay {
        protected int width, length;
        void setWidth(int w)  { this.width = w; }
        void setLength(int l) { this.length = l; }
        int area()            { return width * length; }
    }

    static class SquareBay extends Bay {
        @Override void setWidth(int w)  { this.width = w; this.length = w; }   // keeps sides equal
        @Override void setLength(int l) { this.width = l; this.length = l; }
    }

    /** A caller written for Bay. It trusts: after these two calls, area is 5 x 4 = 20. */
    static void caller(Bay bay) {
        bay.setWidth(5);
        bay.setLength(4);
        System.out.println(bay.getClass().getSimpleName() + " area = " + bay.area() + "   (caller expected 20)");
    }

    // ---------------- PART 2 : the immutable design (fixed) ----------------
    // A record cannot change after creation, so there are no setters and no sequences to break.
    record ImmutableBay(int width, int length) {
        int area() { return width * length; }
        static ImmutableBay square(int side) { return new ImmutableBay(side, side); }   // a square is just a special rectangle VALUE
    }

    public static void main(String[] args) {
        System.out.println("--- Mutable design ---");
        caller(new Bay());          // 20: fine
        caller(new SquareBay());    // 16: the subtype broke the caller's promise

        System.out.println("\n--- Immutable design ---");
        ImmutableBay rect = new ImmutableBay(5, 4);
        ImmutableBay sq   = ImmutableBay.square(4);
        System.out.println("rectangle 5x4 area = " + rect.area());
        System.out.println("square 4x4 area    = " + sq.area());
        System.out.println("Nobody can change a bay after it is made, so nobody can break the promise.");
    }
}
