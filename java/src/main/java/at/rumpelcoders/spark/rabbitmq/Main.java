package at.rumpelcoders.spark.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import static spark.Spark.get;

public class Main {
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("dev");
        factory.setPassword("dev");
        factory.setVirtualHost("dev");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        String exchange = "ex.spark.java";
        channel.exchangeDeclare(exchange, "topic");


        get("/hello", (req, res) -> "Hello World");

        get("/pythonjob", (req, res) -> {
                    String callbackQueueName = channel.queueDeclare().getQueue();
                    AMQP.BasicProperties props = new AMQP.BasicProperties
                            .Builder()
                            .replyTo(callbackQueueName)
                            .build();
                    String data = req.queryParams("n1") + "," + req.queryParams("n2");
                    channel.basicPublish(
                            exchange,
                            "rk.a",
                            props,
                            data.getBytes(StandardCharsets.UTF_8)
                    );

                    AtomicReference<String> result = new AtomicReference<>();
                    DeliverCallback callback = ((consumerTag, message) -> {
                        result.set(new String(message.getBody(), StandardCharsets.UTF_8));
                    });

                    channel.basicConsume(callbackQueueName, true, callback, (consumerTag, sig) -> {
                    });
                    int count = 0;
                    while (result.get() == null) {
                        count++;
                        if(count > 15) {
                            break;
                        }
                        Thread.sleep(1000);
                        //obviously not a good idea to do that!!
                    }
                    if(count > 15) {
                        return "Timeout";
                    }
                    return result.get();
                }
        );
    }
}
