package ch.ethy.transact;

import ch.ethy.transact.json.parse.JsonParser;
import ch.ethy.transact.ynab.BudgetResponse;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Transact {
  public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder()
        .uri(new URI("https://api.youneedabudget.com/v1/budgets"))
        .header("Authorization", "Bearer some_token")
        .GET()
        .build();

    HttpResponse<String> response = HttpClient.newBuilder()
        .build()
        .send(request, HttpResponse.BodyHandlers.ofString());

    String body = response.body();

    BudgetResponse resp = new JsonParser(body).parse(BudgetResponse.class);
  }
}
