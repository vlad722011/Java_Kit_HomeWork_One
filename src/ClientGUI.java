import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientGUI extends JFrame {
    private static final int POS_X = 200;
    private static final int POS_Y = 300;
    private static final int WIDTH = 350;
    private static final int HEIGHT = 500;
    private final ServerWindow serverWindow;
    private final User user;
    private final JButton buttonSendMessage = new JButton("Send");
    private final JButton buttonLogin = new JButton("login");
    private final JTextField fieldLogin = new JTextField();
    private final JTextField messageForSend = new JTextField();
    public boolean isUserAutorized = false;
    public final JTextArea messages = new JTextArea();
    public final JPanel topPanel = new JPanel(new GridLayout(2, 1));

    ClientGUI(ServerWindow serverWindow, User user) {
        this.serverWindow = serverWindow;
        this.user = user;

        setTitle("Client Chat");
        setAlwaysOnTop(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBounds(POS_X, POS_Y, WIDTH, HEIGHT);
        setVisible(true);
        messages.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(messages);
        add(scrollPane, BorderLayout.CENTER);

        addPanels(user);
        addListeners();
        repaint();
    }

    private void addListeners() {
        buttonLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isUserAutorized && serverWindow.serverStatus()) {
                    isUserAutorized = true;
                    JTextArea infoMessages = serverWindow.getInfoMessages();
                    infoMessages.append(STR."\{fieldLogin.getText()} подключился к беседе!\n");
                    messages.append("Вы успешно подключились к беседе!" + "\n");
                    messages.append("\n");
                    topPanel.setVisible(false);
                    serverWindow.authorizedUsers.add(user);
                    serverWindow.clientGUIList.add(ClientGUI.this);
                    messages.append(serverWindow.getMessages().getText());
                } else if (!serverWindow.serverStatus()) {
                    messages.append("Подключение не удалось!" + "\n");
                }
            }
        });

        buttonSendMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessagesToServer();
            }
        });

        messageForSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessagesToServer();
            }
        });
    }

    private void sendMessagesToServer() {
        String message = messageForSend.getText();
        if (!message.isBlank()) {
            serverWindow.receiveMessage(fieldLogin.getText(), messageForSend.getText());
            messageForSend.setText("");
        }
    }

    public void addPanels(User user) {
        JPanel socketPanel = new JPanel(new GridLayout(1, 3));
        JTextField ipAddress = new JTextField("127.0.0.1");
        socketPanel.add(ipAddress);
        JTextField port = new JTextField("8090");
        socketPanel.add(port);
        socketPanel.add(new Box(1));

        fieldLogin.setText(user.getUserName());
        JPasswordField fieldPassword = new JPasswordField();
        fieldPassword.setText(user.getPassword());

        JPanel connectUserPanel = new JPanel(new GridLayout(1, 3));
        connectUserPanel.add(fieldLogin);
        connectUserPanel.add(fieldPassword);
        connectUserPanel.add(buttonLogin);

        topPanel.add(socketPanel);
        topPanel.add(connectUserPanel);
        add(topPanel, BorderLayout.NORTH);

        JPanel bottom = new JPanel(new GridLayout(1, 2));
        bottom.add(messageForSend, BorderLayout.CENTER);
        bottom.add(buttonSendMessage, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);
    }

    public void newMessageFromServer(String message) {
        messages.append(message);
    }
}
