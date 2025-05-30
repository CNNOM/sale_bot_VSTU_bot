package org.example.OZON;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OzonPriceChecker {
    public static void main(String[] args) throws Exception {
        String productName = "iPhone 13";
        String url = "https://www.ozon.ru/api/entrypoint-api.bx/page/json/v2?url=/search/?text="
                + java.net.URLEncoder.encode(productName, "UTF-8");

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            request.setHeader("User-Agent", "Mozilla/5.0");

            String jsonResponse = EntityUtils.toString(httpClient.execute(request).getEntity());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(jsonResponse);

            // Парсим JSON-ответ (структура может меняться)
            if (rootNode.has("widgetStates")) {
                JsonNode items = rootNode.get("widgetStates").get("searchResultsV2").get("items");
                for (JsonNode item : items) {
                    String title = item.get("title").asText();
                    String price = item.get("price").asText();
                    System.out.println(title + " - " + price + " руб.");
                }
            }
        }
    }
}