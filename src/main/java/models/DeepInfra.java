package models;

import okhttp3.*;
import org.json.JSONObject;

public class DeepInfra {
    private static final String API_URL = "https://api.deepinfra.com/v1/inference/mistralai/Mistral-7B-Instruct-v0.2";
    private static final String API_TOKEN = "Bearer VOT1rwrNaJp7KUPWyXbyvkd0lTdF3O99"; // <-- ta clé ici

    public static String generateComment(String prompt) throws Exception {
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        JSONObject payload = new JSONObject();
        payload.put("input", prompt);

        RequestBody body = RequestBody.create(payload.toString(), mediaType);

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("Authorization", API_TOKEN)
                .addHeader("Content-Type", "application/json")
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new RuntimeException("Erreur API DeepInfra : " + response);

        String responseBody = response.body().string();

        JSONObject jsonObject = new JSONObject(responseBody);
        String generatedText = jsonObject.getJSONArray("results")
                .getJSONObject(0)
                .getString("generated_text");

        return generatedText;
    }
}

