import pika


def run():
    conn_parmas = pika.ConnectionParameters(
            host="127.0.0.1",
            credentials=pika.PlainCredentials(username="dev", password="dev"),
            virtual_host="dev",
    )
    connection = pika.BlockingConnection(parameters=conn_parmas)
    channel = connection.channel()
    queue_name = "queue.python.worker"
    channel.queue_declare(queue_name)
    channel.queue_bind(queue_name, "ex.spark.java", routing_key="#")
    print("Queues set up")

    def callback(ch, method, properties, body):
        print("Received body {}".format(body))
        nrs = body.decode("utf-8").split(",")
        result = "send by python {}".format(int(nrs[0]) + int(nrs[1]))
        channel.basic_publish(exchange="", routing_key=properties.reply_to, body=str(result))

    channel.basic_consume(queue=queue_name, on_message_callback=callback, auto_ack=True)
    channel.start_consuming()


run()
