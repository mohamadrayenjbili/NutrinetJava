package services;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class QuotableService {
    private static final String API_BASE_URL = "https://api.quotable.io/random";
    private static final List<String> FRENCH_QUOTES = Arrays.asList(
            "\"Le succès c'est d'aller d'échec en échec sans perdre son enthousiasme.\" - Winston Churchill",
            "\"Fixez-vous des objectifs élevés et ne vous arrêtez pas avant de les avoir atteints.\" - Bo Jackson",
            "\"Les limites ne sont que des illusions créées par l'esprit.\" - Michael Jordan",
            "\"Un objectif bien défini est à moitié atteint.\" - Abraham Lincoln",
            "\"La discipline est le pont entre les objectifs et leur réalisation.\" - Jim Rohn",
            "\"Ne limitez pas vos défis, défiez vos limites.\" - Inconnu",
            "\"Les grands accomplissements commencent par de grandes ambitions.\" - Inconnu",
            "\"La seule limite à notre réalisation de demain sera nos doutes d'aujourd'hui.\" - Franklin D. Roosevelt",
            "\"Atteindre un objectif n'est pas le plus important, c'est ce que vous devenez en essayant de l'atteindre qui compte.\" - Zig Ziglar",
            "\"Les montagnes les plus hautes ont les pentes les plus raides.\" - Inconnu",
            "\"Visez la lune, même si vous échouez, vous atterrirez parmi les étoiles.\" - Les Brown",
            "\"Le succès n'est pas final, l'échec n'est pas fatal : c'est le courage de continuer qui compte.\" - Winston Churchill"
    );

    public String getRandomQuote() {
        try {
            URI uri = new URIBuilder(API_BASE_URL)
                    .addParameter("tags", "success,goals,motivational")
                    .addParameter("limit", "1")
                    .build();

            SSLContext sslContext = new SSLContextBuilder()
                    .loadTrustMaterial(null, (certificate, authType) -> true)
                    .build();

            SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(
                    sslContext,
                    NoopHostnameVerifier.INSTANCE
            );

            CloseableHttpClient httpClient = HttpClients.custom()
                    .setSSLSocketFactory(socketFactory)
                    .build();

            HttpGet request = new HttpGet(uri);
            HttpResponse response = httpClient.execute(request);

            if (response.getStatusLine().getStatusCode() == 200) {
                String jsonResponse = EntityUtils.toString(response.getEntity());
                ObjectMapper mapper = new ObjectMapper();
                Quote quote = mapper.readValue(jsonResponse, Quote.class);

                if (isRelevantQuote(quote.getContent())) {
                    return "\"" + quote.getContent() + "\" - " + quote.getAuthor();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return getRandomFrenchQuote();
    }

    private boolean isRelevantQuote(String content) {
        if (content == null) return false;
        String lowerContent = content.toLowerCase();
        return lowerContent.contains("goal") || lowerContent.contains("success") ||
                lowerContent.contains("limit") || lowerContent.contains("achieve") ||
                lowerContent.contains("target") || lowerContent.contains("ambition") ||
                lowerContent.contains("dream") || lowerContent.contains("challenge");
    }

    private String getRandomFrenchQuote() {
        Random random = new Random();
        return FRENCH_QUOTES.get(random.nextInt(FRENCH_QUOTES.size()));
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Quote {
        private String content;
        private String author;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }
    }
}