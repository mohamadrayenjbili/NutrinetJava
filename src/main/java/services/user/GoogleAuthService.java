package services.user;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfo;
import com.sun.net.httpserver.HttpServer;
import javafx.scene.control.TextInputDialog;
import models.User;

import java.io.*;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Date;

public class GoogleAuthService {
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final String REDIRECT_URI = "http://localhost:5000/auth/callback";

    private static final java.util.Collection<String> SCOPES = Arrays.asList(
            "https://www.googleapis.com/auth/userinfo.profile",
            "https://www.googleapis.com/auth/userinfo.email"
    );

    private final GoogleAuthorizationCodeFlow flow;
    private final log_historyService logService = new log_historyService();

    private Connection getConnection() throws Exception {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/didou", "root", "");
    }

    public GoogleAuthService() throws IOException {
        InputStream in = GoogleAuthService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Créer le dossier tokens s'il n'existe pas
        File tokensDirectory = new File(TOKENS_DIRECTORY_PATH);
        if (!tokensDirectory.exists()) {
            tokensDirectory.mkdirs();
        }

        flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT,
                JSON_FACTORY,
                clientSecrets,
                SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(tokensDirectory))
                .setAccessType("offline")
                .build();
    }

    public String getAuthorizationUrl() {
        return flow.newAuthorizationUrl()
                .setRedirectUri(REDIRECT_URI)
                .build();
    }

    public String getAuthorizationCode() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(5000), 0);
        StringBuilder authCode = new StringBuilder();

        server.createContext("/auth/callback", exchange -> {
            String query = exchange.getRequestURI().getQuery();
            if (query != null && query.contains("code=")) {
                String code = query.split("code=")[1].split("&")[0];
                authCode.append(code);
                String response = "Vous pouvez maintenant retourner à l'application.";
                exchange.sendResponseHeaders(200, response.getBytes().length);
                exchange.getResponseBody().write(response.getBytes());
                exchange.getResponseBody().close();
                server.stop(0);
            }
        });

        server.start();

        // Ouvrir l'URL d'autorisation dans le navigateur
        java.awt.Desktop.getDesktop().browse(new java.net.URI(getAuthorizationUrl()));

        // Attendre que le code soit capturé
        while (authCode.isEmpty()) {
            Thread.sleep(100);
        }

        return authCode.toString();
    }


    public User authenticateWithGoogle(String authCode) throws Exception {
        GoogleTokenResponse tokenResponse = flow.newTokenRequest(authCode)
                .setRedirectUri(REDIRECT_URI)
                .execute();

        Credential credential = flow.createAndStoreCredential(tokenResponse, "user");

        Oauth2 oauth2 = new Oauth2.Builder(
                HTTP_TRANSPORT,
                JSON_FACTORY,
                credential)
                .setApplicationName("NutriNet")
                .build();

        Userinfo userInfo = oauth2.userinfo().get().execute();

        User user = findUserByEmail(userInfo.getEmail());
        if (user == null) {
            // Demander un mot de passe à l'utilisateur
            String password = promptForPassword();
            if (password.isEmpty()) {
                throw new Exception("Le mot de passe est requis pour créer un compte.");
            }

            // Créer un nouvel utilisateur
            user = new User();
            user.setEmail(userInfo.getEmail());
            user.setName(userInfo.getFamilyName());
            user.setPrename(userInfo.getGivenName());
            user.setPassword(password);
            user.setRole("client");
            user.setIsBanned("0");
            user.setAge(20);
            user.setPhoneNumber("00000000");
            user.setAddress("tunis");
            user.setDate(new java.sql.Date(new Date().getTime()));

            // Enregistrer l'utilisateur dans la base de données
            saveUser(user);

            // Récupérer l'utilisateur avec toutes ses informations après la sauvegarde
            user = findUserByEmail(userInfo.getEmail());
        }

        // Ajouter le log de connexion
        String details = "User " + user.getName() + " " + user.getPrename() + " logged in with Google";
        logService.addLog(user.getEmail(), "Google Login", details);

        return user;
    }

    private User findUserByEmail(String email) throws Exception {
        String query = "SELECT * FROM user WHERE email = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setPrename(rs.getString("prename"));
                user.setEmail(rs.getString("email"));
                user.setAge(rs.getInt("age"));
                user.setPhoneNumber(rs.getString("phone_number"));
                user.setAddress(rs.getString("address"));
                user.setRole(rs.getString("role"));
                user.setIsBanned(rs.getString("is_banned"));
                user.setDate(rs.getDate("date"));
                return user;
            }
        }
        return null;
    }

    private void saveUser(User user) throws Exception {
        String query = "INSERT INTO user (name, prename, email, password, age, phone_number, address, role, is_banned, date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getPrename());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPassword());
            ps.setInt(5, user.getAge());
            ps.setString(6, user.getPhoneNumber());
            ps.setString(7, user.getAddress());
            ps.setString(8, user.getRole());
            ps.setString(9, user.getIsBanned());
            ps.setDate(10, user.getDate());
            ps.executeUpdate();
        }
    }

    private String promptForPassword() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Saisir le mot de passe");
        dialog.setHeaderText("Veuillez saisir un mot de passe pour votre compte.");
        dialog.setContentText("Mot de passe :");

        return dialog.showAndWait().orElse("");
    }

}