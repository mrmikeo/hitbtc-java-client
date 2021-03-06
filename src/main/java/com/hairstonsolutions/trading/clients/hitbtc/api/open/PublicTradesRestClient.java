package com.hairstonsolutions.trading.clients.hitbtc.api.open;

import com.hairstonsolutions.trading.clients.hitbtc.api.HitBtcAPI;
import com.hairstonsolutions.trading.clients.hitbtc.trades.PublicTrade;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class PublicTradesRestClient {

    private static final String RESOURCE_PATH = "/public/trades";
    private static final String REQUEST_URI = HitBtcAPI.BASE_URL + RESOURCE_PATH;
    private static final Log LOG = LogFactory.getLog(TickerRestClient.class);


    public static List<PublicTrade> getPublicTrades(String tickerId) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<PublicTrade[]> responseEntity = restTemplate.getForEntity(REQUEST_URI + "/{tickerId}", PublicTrade[].class, tickerId);

        LOG.info(String.format("Return Values: %s", responseEntity.toString()));

        return Arrays.asList(Objects.requireNonNull(responseEntity.getBody()));
    }

    public static List<PublicTrade> getPublicTrades(String tickerId, int amount) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<PublicTrade[]> responseEntity = restTemplate.getForEntity(REQUEST_URI + "/{tickerId}?limit={amountToRetrieve}", PublicTrade[].class, tickerId, amount);

        LOG.info(String.format("Return Values: %s", responseEntity.toString()));

        return Arrays.asList(Objects.requireNonNull(responseEntity.getBody()));
    }
}
