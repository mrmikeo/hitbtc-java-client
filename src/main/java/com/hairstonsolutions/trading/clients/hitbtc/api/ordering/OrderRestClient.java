package com.hairstonsolutions.trading.clients.hitbtc.api.ordering;

import com.hairstonsolutions.trading.clients.hitbtc.Ticker;
import com.hairstonsolutions.trading.clients.hitbtc.api.HitBtcAPI;
import com.hairstonsolutions.trading.clients.hitbtc.api.open.TickerRestClient;
import com.hairstonsolutions.trading.clients.hitbtc.attributes.Side;
import com.hairstonsolutions.trading.clients.hitbtc.attributes.TimeInForce;
import com.hairstonsolutions.trading.clients.hitbtc.attributes.TradeType;
import com.hairstonsolutions.trading.clients.hitbtc.orders.Order;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class OrderRestClient {

    private static final String RESOURCE_PATH = "/order";
    private static final String REQUEST_URI = HitBtcAPI.BASE_URL + RESOURCE_PATH;
    private static final Log LOG = LogFactory.getLog(OrderRestClient.class);

    private HitBtcAPI hitBtcAPI;

    public OrderRestClient(HitBtcAPI hitBtcAPI) {
        this.hitBtcAPI = hitBtcAPI;
    }

    public ResponseEntity<String> getOrderStringByClientId(String clientOrderId) {
        RestTemplate restTemplate = new RestTemplate();
        String encodedCredentials = hitBtcAPI.getEncodedCredentials();
        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.set("Authorization", "Basic " + encodedCredentials);

        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);

        ResponseEntity<String> responseEntity = restTemplate.exchange(REQUEST_URI + "/" + clientOrderId, HttpMethod.GET, httpEntity, String.class);

        LOG.info(String.format("Return Values: %s", responseEntity.toString()));

        return responseEntity;
    }


    public static Order getOrderByClientId(String clientOrderId, HitBtcAPI hitBtcAPI) {
        RestTemplate restTemplate = new RestTemplate();
        String encodedCredentials = hitBtcAPI.getEncodedCredentials();
        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.set("Authorization", "Basic " + encodedCredentials);

        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);

        ResponseEntity<Order> responseEntity = restTemplate.exchange(REQUEST_URI + "/" + clientOrderId, HttpMethod.GET, httpEntity, Order.class);

        LOG.info(String.format("Return Values: %s", responseEntity.toString()));

        return responseEntity.getBody();
    }

    public static List<Order> getOpenOrders(HitBtcAPI hitBtcAPI) {
        RestTemplate restTemplate = new RestTemplate();
        String encodedCredentials = hitBtcAPI.getEncodedCredentials();
        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.set("Authorization", "Basic " + encodedCredentials);

        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);

        ResponseEntity<Order[]> responseEntity = restTemplate.exchange(REQUEST_URI, HttpMethod.GET, httpEntity, Order[].class);

        LOG.info(String.format("Return Values: %s", responseEntity.toString()));

        return Arrays.asList(Objects.requireNonNull(responseEntity.getBody()));
    }

    public static Order sendLimitOrder(HitBtcAPI hitBtcAPI, String symbol, String side, String quantity, String price) {
        TradeType tradeType = new TradeType(TradeType.LIMIT);
        TimeInForce timeInForce = new TimeInForce(TimeInForce.GTC_GOOD_TILL_CANCELLED);
        boolean postOnly = false;

        return OrderRestClient.sendOrder(hitBtcAPI, symbol, side, quantity, price, tradeType, timeInForce, postOnly);
    }

    public static Order sendMarketBuyOrder(HitBtcAPI hitBtcAPI, String symbol, String amount) {
        return sendMarketOrder(hitBtcAPI, symbol, amount, Side.BUY);
    }

    public static Order sendMarketSellOrder(HitBtcAPI hitBtcAPI, String symbol, String amount) {
        return sendMarketOrder(hitBtcAPI, symbol, amount, Side.SELL);
    }

    /* There are Risks involved when using this "Like-Market" order. Some of these Risks are:
    *  1) No Guarantees that your order will be filled fast, soon, or at all from the time the Order is placed.
    *  2) If there is a current Bull/Bear Market and a Like-Market Order is placed, then the actual market can run away from your order in the opposite direction rather fast.
    *  3) Other Trades can see your order as it is posted and then bid Over/Under you almost immediately, putting you further down the line.
    *  4) You Need to expect a Minimal Wait time of up to 5 minutes to monitor where the market is moving away or close to your order.
    *  If the above Risks and Warnings are too painful for you, Consider using the Market Order instead.
    */
    public static Optional<Order> sendLikeMarketBuyOrder(HitBtcAPI hitBtcAPI, String symbol, String amount) {
        return sendLikeMarketOrder(hitBtcAPI, symbol, amount, Side.BUY);
    }

    public static Optional<Order> sendLikeMarketSellOrder(HitBtcAPI hitBtcAPI, String symbol, String amount) {
        return sendLikeMarketOrder(hitBtcAPI, symbol, amount, Side.SELL);
    }

    private static Order sendMarketOrder(HitBtcAPI hitBtcAPI, String symbol, String amount, String side) {
        Ticker ticker = TickerRestClient.getTickerById(symbol);
        String price = ticker.getLast();
        String quantity = "";

        switch (side) {
            case "buy":
                quantity = ticker.getMarketBuyQuantityByAmount(Float.parseFloat(amount));
                break;
            case "sell":
                quantity = ticker.getMarketSellQuantityByAmount(Float.parseFloat(amount));
                break;
        }

        TradeType tradeType = new TradeType(TradeType.MARKET);
        TimeInForce timeInForce = new TimeInForce(TimeInForce.IOC_IMMEDIATE_OR_CANCEL);
        boolean postOnly = false;

        Order responseOrder = OrderRestClient.sendOrder(hitBtcAPI, symbol, side, quantity, price, tradeType, timeInForce, postOnly);
        responseOrder.reconcileMarketOrder();

        return responseOrder;
    }

    private static Optional<Order> sendLikeMarketOrder(HitBtcAPI hitBtcAPI, String symbol, String amount, String side) {
        Ticker ticker = TickerRestClient.getTickerById(symbol);
        String price = "";

        switch (side) {
            case "buy":
                price = ticker.getBidPlus();
                break;
            case "sell":
                price = ticker.getAskMinus();
                break;
        }

        String quantity = ticker.getMarketQuantityByAmount(Float.parseFloat(amount), Float.parseFloat(price));

        TradeType tradeType = new TradeType(TradeType.LIMIT);
        TimeInForce timeInForce = new TimeInForce(TimeInForce.GTC_GOOD_TILL_CANCELLED);
        boolean postOnly = true;

        return OrderRestClient.sendOptionalOrder(hitBtcAPI, symbol, side, quantity, price, tradeType, timeInForce, postOnly);
    }

    private static Order sendOrder(HitBtcAPI hitBtcAPI, String symbol, String side, String quantity, String price,
                                   TradeType tradeType, TimeInForce timeInForce, boolean postOnly) {
        RestTemplate restTemplate = new RestTemplate();
        String encodedCredentials = hitBtcAPI.getEncodedCredentials();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encodedCredentials);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("symbol", symbol);
        map.add("side", side);
        map.add("type", tradeType.toString());
        map.add("timeInForce", timeInForce.toString());
        map.add("quantity", quantity);
        map.add("price", price);
        if (postOnly)
            map.add("postOnly", "true");
        else map.add("postOnly", "false");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        LOG.info(String.format("Submitted %s %s Order: Price=%s, Quantity=%s, Symbol=%s, TimeInForce=%s",
                tradeType.toString(), side, price, quantity, symbol, timeInForce.toString()));

        ResponseEntity<Order> responseEntity = restTemplate.exchange(REQUEST_URI, HttpMethod.POST, request, Order.class);

        if (responseEntity.getStatusCodeValue() != 200) {
            LOG.error(String.format("Submitting Order Failed. HTTP Response: %s", responseEntity.toString()));
            return null;
        }

        LOG.debug(String.format("Return Status Code: %s", responseEntity.getStatusCode()));
        if (responseEntity.getBody() != null)
            LOG.info(String.format("Return Values: %s", responseEntity.getBody().toString()));

        return responseEntity.getBody();
    }

    private static Optional<Order> sendOptionalOrder(HitBtcAPI hitBtcAPI, String symbol, String side, String quantity, String price,
                                   TradeType tradeType, TimeInForce timeInForce, boolean postOnly) {
        RestTemplate restTemplate = new RestTemplate();
        String encodedCredentials = hitBtcAPI.getEncodedCredentials();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encodedCredentials);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("symbol", symbol);
        map.add("side", side);
        map.add("type", tradeType.toString());
        map.add("timeInForce", timeInForce.toString());
        map.add("quantity", quantity);
        map.add("price", price);
        if (postOnly)
            map.add("postOnly", "true");
        else map.add("postOnly", "false");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        LOG.info(String.format("Submitted %s %s Order: Price=%s, Quantity=%s, Symbol=%s, TimeInForce=%s",
                tradeType.toString(), side, price, quantity, symbol, timeInForce.toString()));

        ResponseEntity<Order> responseEntity = restTemplate.exchange(REQUEST_URI, HttpMethod.POST, request, Order.class);

        if (responseEntity.getStatusCodeValue() != 200) {
            LOG.error(String.format("Submitting Order Failed. HTTP Response: %s", responseEntity.toString()));
            return Optional.empty();
        }

        LOG.debug(String.format("Return Status Code: %s", responseEntity.getStatusCode()));
        if (responseEntity.getBody() != null)
            LOG.info(String.format("Return Values: %s", responseEntity.getBody().toString()));

        return Optional.ofNullable(responseEntity.getBody());
    }
}
