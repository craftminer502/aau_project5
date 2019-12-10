public class MPN {

    // Native defines
    public native int main();
    public native int getMPN();


    // Call native library
    public static void main(String[] args) {
        System.loadLibrary("MPN");
        MPN mpnLib = new MPN();

        int mpn = mpnLib.main();
    }
}
