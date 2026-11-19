interface Callable {
    void makeCall(String number);
}

interface InternetEnabled {
    void browseInternet(String url);
}

// KeypadPhone only promises what it can actually do -> LSP SAFE!
class KeypadPhone implements Callable {
    @Override
    public void makeCall(String number) {
        System.out.println("Dialing " + number + " via 2G network...");
    }
}

// SmartPhone promises both capabilities
class SmartPhone implements Callable, InternetEnabled {
    @Override
    public void makeCall(String number) {
        System.out.println("Calling " + number + " via VoLTE...");
    }

    @Override
    public void browseInternet(String url) {
        System.out.println("Opening " + url + " on 5G browser...");
    }
}

public class Demo1_Interfaces {
    public static void main(String[] args) {
        Callable basic = new KeypadPhone();
        basic.makeCall("9876543210");

        SmartPhone smart = new SmartPhone();
        smart.makeCall("9876543210");
        smart.browseInternet("https://kluniversity.in");
    }
}
