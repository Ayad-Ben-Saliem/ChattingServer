package edu.kse.logging;

public class Log {

    private volatile static boolean info = false;
    private volatile static boolean debug = false;
    private volatile static boolean error = false;
    private volatile static boolean warning = false;

    public synchronized static void enableInfo(){
        info = true;
    }
    public synchronized static void enableError(){
        error = true;
    }
    public synchronized static void enableWarning(){
        warning = true;
    }
    public synchronized static void enableDebug(){
        debug = true;
    }

    public synchronized static void disableInfo(){
        info = false;
    }

    public synchronized static void disableError(){
        error = false;
    }


    public synchronized static void disableWarning(){
        warning = false;
    }


    public synchronized static void disableDebug(){
        debug = false;
    }


    public synchronized static void enable(){
        enableInfo();
        enableError();
        enableDebug();
        enableWarning();
    }

    public synchronized static void disable(){
        disableInfo();
        disableError();
        disableDebug();
        disableWarning();
    }

    public static void i(Object ...objects){
        if (info) {
            System.out.print(Colors.BLUE);
            print(objects);
            System.out.print(Colors.RESET);
        }
    }

    public static void e(Object ...objects){
        if (debug) {
            System.out.print(Colors.RED);
            print(objects);
            System.out.print(Colors.RESET);
        }
    }

    public static void w(Object ...objects){
        if (debug) {
            System.out.print(Colors.YELLOW);
            print(objects);
            System.out.print(Colors.RESET);
        }
    }

    public static void d(Object ...objects){
        if (debug) {
            System.out.print(Colors.GREEN);
            print(objects);
            System.out.print(Colors.RESET);
        }
    }

    private static void print(Object ...objects){
        for (Object o: objects) {
            System.out.println(o);
        }
    }

    private final static class Colors {

        private Colors(){}

        public final static String RESET = "\033[0m";

        public final static String RED = "\033[0;31m";     // RED
        public final static String BLUE = "\033[0;34m";    // BLUE
        public final static String CYAN = "\033[0;36m";    // CYAN
        public final static String BLACK = "\033[0;30m";   // BLACK
        public final static String WHITE = "\033[0;37m";   // WHITE
        public final static String GREEN = "\033[0;32m";   // GREEN
        public final static String YELLOW = "\033[0;33m";  // YELLOW
        public final static String PURPLE = "\033[0;35m";  // PURPLE
    }
}
