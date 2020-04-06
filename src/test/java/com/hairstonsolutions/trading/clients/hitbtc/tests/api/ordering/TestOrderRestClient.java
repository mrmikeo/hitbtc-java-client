package com.hairstonsolutions.trading.clients.hitbtc.tests.api.ordering;

import com.hairstonsolutions.trading.clients.hitbtc.api.HitBtcAPI;
import com.hairstonsolutions.trading.clients.hitbtc.api.ordering.OrderRestClient;
import com.hairstonsolutions.trading.clients.hitbtc.attributes.Side;
import com.hairstonsolutions.trading.clients.hitbtc.attributes.TimeInForce;
import com.hairstonsolutions.trading.clients.hitbtc.attributes.TradeType;
import com.hairstonsolutions.trading.clients.hitbtc.orders.Order;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public class TestOrderRestClient {

    final String TESTCONFIGFILE = "src/test/resources/hitbtckey.properties";
    private HitBtcAPI hitBtcAPI;

    @Before
    public void load() {
        hitBtcAPI = new HitBtcAPI();
        hitBtcAPI.loadKeysFromPropertiesFile(TESTCONFIGFILE);
    }

    @Test
    public void getOrderByClientIdString() {
        String clientOrderId = "9dde33f8-afbb-4d3f-a182-6fa90dc";

        OrderRestClient orderRestClient = new OrderRestClient(hitBtcAPI);

        ResponseEntity<String> responseEntity = orderRestClient.getOrderStringByClientId(clientOrderId);

        assert responseEntity.getStatusCode().toString().equals("200 OK");
        System.out.println(responseEntity.getStatusCode().toString());

        String orderResponse = responseEntity.getBody();
        System.out.println(orderResponse);
    }

    @Test
    public void getOrderByClientIdObject() {
        String clientOrderId = "9dde33f8-afbb-4d3f-a182-6fa90dc";

        Order orderResponse = OrderRestClient.getOrderByClientId(clientOrderId, hitBtcAPI);
        System.out.println(orderResponse);
    }

    @Test
    public void getAllOpenOrders() {
        List<Order> openOrders = OrderRestClient.getOpenOrders(hitBtcAPI);

        for (Order orders : openOrders) {
            System.out.println(orders);
        }
        System.out.println(String.format("Orders Total: %s", openOrders.size()));
    }

    @Ignore
    @Test
    public void postLowLimitBuyOrder() {
        String symbol = "BTCUSD";
        String side = Side.BUY;
        TradeType tradeType = new TradeType(TradeType.LIMIT);
        TimeInForce timeInForce = new TimeInForce(TimeInForce.GTC_GOOD_TILL_CANCELLED);
        String quantity = "0.00130";
        String price = "8002.16";

        Order OrderReponse = OrderRestClient.sendLimitOrder(
                hitBtcAPI, symbol, side, quantity, price);

        System.out.println(OrderReponse);
    }

    @Test
    public void postHighLimitSellOrder() {

    }

    @Ignore
    @Test
    public void buyMarketOrder() {
        String symbol = "BTCUSD";
        String amount = "15.00";

        Order myMarketOrder = OrderRestClient.sendMarketBuyOrder(hitBtcAPI, symbol, amount);

        System.out.println(myMarketOrder);
        assert (myMarketOrder.getTradesReport() != null);
    }

    @Ignore
    @Test
    public void sellMarketOrder() {
        String symbol = "BTCUSD";
        String amount = "21.00";

        Order myMarketOrder = OrderRestClient.sendMarketSellOrder(hitBtcAPI, symbol, amount);

        System.out.println(myMarketOrder);
        assert (myMarketOrder.getTradesReport() != null);
    }

    @Ignore
    @Test
    public void marketLikeBuyOrder() {
        String symbol = "BTCUSDC";
        String amount = "15.00";

        Optional<Order> myLikeMarketOrder = OrderRestClient.sendLikeMarketBuyOrder(hitBtcAPI, symbol, amount);

        myLikeMarketOrder.ifPresent(System.out::println);

        assert (myLikeMarketOrder.isPresent());
    }

    @Ignore
    @Test
    public void marketLikeSellOrder() {
        String symbol = "BTCUSDC";
        String amount = "15.00";

        Optional<Order> myLikeMarketOrder = OrderRestClient.sendLikeMarketSellOrder(hitBtcAPI, symbol, amount);

        myLikeMarketOrder.ifPresent(System.out::println);

        assert (myLikeMarketOrder != null);
    }

    @Test
    public void stopLimitOrder() {

    }

    @Test
    public void stopMarketOrder() {

    }
}
