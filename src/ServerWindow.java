import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ServerWindow extends JFrame {
    private static final int POS_X = 600;
    private static final int POS_Y = 200;
    private static final int WIDTH = 350;
    private static final int HEIGHT = 550;
    private final JButton buttonStart = new JButton("Start");
    private final JButton buttonStop = new JButton("Stop");
    private final JTextArea infoMessages = new JTextArea();
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss ");
    private JTextArea storedUserMessages;
    private boolean isServerRunning;
    public final List<User> authorizedUsers;
    public final List<ClientGUI> clientGUIList;
    private File messagesFile;

    ServerWindow() {
        this.clientGUIList = new ArrayList<>();
        this.authorizedUsers = new ArrayList<>();

        setTitle("Server Chat");
        setAlwaysOnTop(true);
        setBounds(POS_X, POS_Y, WIDTH, HEIGHT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

        infoMessages.setEditable(false);
        add(new JScrollPane(infoMessages), BorderLayout.CENTER);
        addPanels();
        addListeners();

        isServerRunning = false;
        repaint();
    }

    private void addPanels() {
        JPanel serverManagement = new JPanel(new GridLayout(1, 2));
        serverManagement.add(buttonStart);
        serverManagement.add(buttonStop);
        add(serverManagement, BorderLayout.SOUTH);
    }

    private void addListeners() {
        buttonStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                infoMessages.append(LocalDateTime.now().format(dateTimeFormatter));
                if (isServerRunning) {
                    infoMessages.append("Сервер уже активен!!!\n Повторный запуск не требуется!");
                    infoMessages.append("\n");
                } else {
                    isServerRunning = true;
                    storedUserMessages = readSavedUsersMessages();
                    infoMessages.append("Сервер запущен");
                    infoMessages.append("\n");
                }
            }
        });

        buttonStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isServerRunning) {
                    isServerRunning = false;
                    disconnectUsers();
                    infoMessages.append(STR."\{LocalDateTime.now()
                            .format(dateTimeFormatter)}Сервер остановлен!");
                    infoMessages.append("\n");
                    saveMessages(storedUserMessages);
                } else {
                    infoMessages.append(STR."\{LocalDateTime.now()
                            .format(dateTimeFormatter)}Сервер не был запущен! \n Сервер не требует остановки!!!");
                    infoMessages.append("\n");
                }
            }
        });
    }

    private void saveMessages(JTextArea userMessages) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(messagesFile))) {
                writer.write(userMessages.getText());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    public boolean serverStatus() {
        return isServerRunning;
    }

    public JTextArea getInfoMessages() {
        return infoMessages;
    }

    private JTextArea readSavedUsersMessages() {
        JTextArea messages = getMessages();
        return messages;
    }

    public JTextArea getMessages() {
        JTextArea messages = new JTextArea();
        messagesFile = new File("userMessages.log");
        if (messagesFile.exists() && messagesFile.isFile()) {
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(messagesFile))) {
                while (bufferedReader.ready()) {
                    messages.append(bufferedReader.readLine());
                    messages.append("\n");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return messages;
    }

    public void disconnectUsers() {
        for (int i = 0; i < authorizedUsers.size(); i++) {
            authorizedUsers.remove(authorizedUsers.get(i));
        }
        for (ClientGUI client : clientGUIList) {
            client.topPanel.setVisible(true);
            client.isUserAutorized = false;
            client.messages.append("Соединенение с сервером прервано!");
            client.messages.append("\n");
        }
    }


    public void receiveMessage(String login, String message) {
        for (User user : authorizedUsers) {
            if (user.getUserName().equals(login)) {
                updateMessages(STR."\{login}: \{message}\n");
            }
        }
    }

    private void updateMessages(String message) {
        infoMessages.append(message);
        storedUserMessages.append(message);
        for (ClientGUI client : clientGUIList) {
            client.newMessageFromServer(message);
        }
    }
}



