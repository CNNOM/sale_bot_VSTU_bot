package org.example.OZON;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpHeaders;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MarketParserApiExample {
    private static final String API_KEY = "NThjZjM1NzEyYzFjYWM1OTM0MTg2MjU5MDA2ZWJmNzM2ZTVmYTZkZg";
    private static final String API_URL = "https://api.marketparser.ru/api/v1/search";

    public static void main(String[] args) {
        String productName = "iPhone 13";

        try {
            String result = getProductPrices(productName);
            System.out.println("Результаты поиска:\n" + result);
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }

    public static String getProductPrices(String productName) throws Exception {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String url = String.format("%s?query=%s",
                    API_URL,
                    java.net.URLEncoder.encode(productName, "UTF-8"));

            HttpGet request = new HttpGet(url);
            // Добавляем ключ в заголовок
            request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + API_KEY);
            request.setHeader("Content-Type", "application/json");

            System.out.println("Отправка запроса на: " + url);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String responseBody = EntityUtils.toString(response.getEntity());

                if (response.getStatusLine().getStatusCode() != 200) {
                    throw new RuntimeException("Ошибка API: " + responseBody);
                }

                return parseResponse(responseBody);
            }
        }
    }

    private static String parseResponse(String jsonResponse) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(jsonResponse);

        StringBuilder result = new StringBuilder();
        if (rootNode.has("data")) {
            for (JsonNode item : rootNode.get("data")) {
                result.append(String.format(
                        "Магазин: %s\nНазвание: %s\nЦена: %.2f руб.\nСсылка: %s\n\n",
                        item.get("marketplace").asText(),
                        item.get("name").asText(),
                        item.get("price").asDouble(),
                        item.get("url").asText()
                ));
            }
        } else {
            result.append("Структура ответа не распознана. Полный ответ:\n");
            result.append(jsonResponse);
        }

        return result.toString();
    }
}
