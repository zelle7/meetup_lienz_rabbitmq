# Simple Rabbit MQ test setup for a meetup presentation

Simple setup for showing two services talking via 
## To get started
* Make sure you have `docker-compose` on your machine
* Start the docker setup with `docker-compose up -d`
* Make sure you have python 3 on your machine
* Setup a python virtual env with `python3 -m venv venv`
* Activate it with `source venv/bin/activate`
* Install the python dependencies with `pip install -r python/requirements.txt`
* Run `python run.py` in the python directory

* Make sure you have java and maven installed
* Run `mvn package` in the java directory
* Run `java -jar target/spark.rabbitmq-1.0-SNAPSHOT-jar-with-dependencies.jar`
