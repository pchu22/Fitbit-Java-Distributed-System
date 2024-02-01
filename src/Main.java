public class Main {
    public static void main(String[] args) throws InterruptedException {
        startElement(new String[]{"4450", "E", "Manhattan"});
        Thread.sleep(20000);
        startElement(new String[]{"4451", "E", "KNN"});
        Thread.sleep(20000);
        startElement(new String[]{"4452", "E", "Hamming"});
    }

    private static void startElement(String[] args){
        int port = Integer.parseInt(args[0]);
        String role = args[1];
        String method = args[2];
        Element element = new Element (port, role, method);
        element.start();
    }
}