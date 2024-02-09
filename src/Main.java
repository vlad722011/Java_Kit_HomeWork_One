public class Main {
    public static void main(String[] args) {

        ServerWindow serverWindow = new ServerWindow();
        new ClientGUI(serverWindow, new User("Petya", "qwerty"));
        new ClientGUI(serverWindow, new User("Vasya", "password"));

    }
}
