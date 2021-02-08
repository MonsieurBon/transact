package ch.ethy.transact.ynab;

import ch.ethy.transact.json.parse.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class YnabApi {
  private static final String BASE_URL = "https://api.youneedabudget.com/v1";

  private final HttpClient client;
  private final String token;

  public YnabApi(HttpClient client, String token) {
    this.client = client;
    this.token = token;
  }

  public BudgetData getBudgets() {
    String body = makeGetCall("/budgets");
    BudgetResponse resp = new JsonParser(body).parse(BudgetResponse.class);
    return resp.getData();
  }

  private String makeGetCall(String path) {
    try {
      HttpRequest request = HttpRequest.newBuilder()
          .uri(new URI(BASE_URL + path))
          .header("Authorization", "Bearer " + token)
          .GET()
          .build();

      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      return response.body();
    } catch (IOException | InterruptedException | URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }
}
