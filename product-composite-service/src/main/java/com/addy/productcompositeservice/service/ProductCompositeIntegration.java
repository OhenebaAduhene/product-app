package com.addy.productcompositeservice.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import com.addy.productapi.core.product.IproductApi;
import com.addy.productapi.core.product.Product;
import com.addy.productapi.core.recommendation.IRecommendationApi;
import com.addy.productapi.core.recommendation.Recommendation;
import com.addy.productapi.core.review.IReviewApi;
import com.addy.productapi.core.review.Review;
import com.addy.productutil.HttpErrorInfo;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ProductCompositeIntegration implements IproductApi, IRecommendationApi, IReviewApi {

    private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeIntegration.class);

  private final RestTemplate restTemplate;
  private final ObjectMapper mapper;

  private final String productServiceUrl;
  private final String recommendationServiceUrl;
  private final String reviewServiceUrl;

  @Autowired
  public ProductCompositeIntegration(
    RestTemplate restTemplate,
    ObjectMapper mapper,
    @Value("${app.product-service.host}") String productServiceHost,
    @Value("${app.product-service.port}") int productServicePort,
    @Value("${app.recommendation-service.host}") String recommendationServiceHost,
    @Value("${app.recommendation-service.port}") int recommendationServicePort,
    @Value("${app.review-service.host}") String reviewServiceHost,
    @Value("${app.review-service.port}") int reviewServicePort) {

    this.restTemplate = restTemplate;
    this.mapper = mapper;

    productServiceUrl = "http://" + productServiceHost + ":" + productServicePort + "/product/";
    recommendationServiceUrl = "http://" + recommendationServiceHost + ":" + recommendationServicePort + "/recommendation?productId=";
    reviewServiceUrl = "http://" + reviewServiceHost + ":" + reviewServicePort + "/review?productId=";
  }

  public Product getProduct(int productId) {

    try {
      String url = productServiceUrl + productId;
      LOG.info("Will call getProduct API on URL: {}", url);

      Product product = restTemplate.getForObject(url, Product.class);
      LOG.info("Found a product with id: {}", product.getProductId());

      return product;

    } catch (HttpClientErrorException ex) {

        throw ex;
    //   switch (ex.getStatusCode()) {
    //     case NOT_FOUND -> throw new NotFoundException(getErrorMessage(ex));
    //     case UNPROCESSABLE_ENTITY -> throw new InvalidInputException(getErrorMessage(ex));
    //     default -> {
    //       LOG.warn("Got an unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
    //       LOG.warn("Error body: {}", ex.getResponseBodyAsString());
    //       throw ex;
    //     }
    //   }
    }
  }

//   private String getErrorMessage(HttpClientErrorException ex) {
//     try {
//       return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
//     } catch (IOException ioex) {
//       return ex.getMessage();
//     }
//   }

  public List<Recommendation> getRecommendations(int productId) {

    try {
      String url = recommendationServiceUrl + productId;

      LOG.debug("Will call getRecommendations API on URL: {}", url);
      List<Recommendation> recommendations = restTemplate
        .exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Recommendation>>() {})
        .getBody();

      LOG.debug("Found {} recommendations for a product with id: {}", recommendations.size(), productId);
      return recommendations;

    } catch (Exception ex) {
      LOG.warn("Got an exception while requesting recommendations, return zero recommendations: {}", ex.getMessage());
      return new ArrayList<>();
    }
  }

  public List<Review> getReviews(int productId) {

    try {
      String url = reviewServiceUrl + productId;

      LOG.debug("Will call getReviews API on URL: {}", url);
      List<Review> reviews = restTemplate
        .exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Review>>() {})
        .getBody();

      LOG.debug("Found {} reviews for a product with id: {}", reviews.size(), productId);
      return reviews;

    } catch (Exception ex) {
      LOG.warn("Got an exception while requesting reviews, return zero reviews: {}", ex.getMessage());
      return new ArrayList<>();
    }
  }

}
