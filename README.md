# RHO-Backend-Challenge
My solution of the RHO Backend Software Engineer (Data Analytics) Challenge

## The problem

Build a service that will provide all the necessary tools to be able to flag to a Business Ops
when a customer, playing a game, stakes more than £n (n pounds) in a given interval X, referred to as the time window.
If a player goes over the threshold in the interval X, a notification should be
generated. That is, for each new stake the player creates (at instant t), the system must check if in t -
time_window there were stakes that, in total, go over the threshold (see image).

Source messages use the following format: { account: 123, stake: 4 } 

### Instructions

> Create the backend endpoints that will facilitate the generation and evaluation of players’ stakes.
> Hitting the threshold will trigger a notification stating the cumulated amount and player ID.

> All notifications should be persisted in a persistence layer (sql or nosql). Besides being persisted, there a
> mechanism should exist such that it would be possible for a client to “listen” for those notifications.
> Therefore, we also intend for your API to have a Websockets endpoint through which all those notifications
> should be pushed.

<p align="center">
<img src="https://github.com/joaoamaral28/RHO-Backend-Challenge/blob/master/problem.png" width=500px>
</p>

## The application

The application consists of a WebServer built using the [Spring Framework](https://spring.io/projects/spring-framework) composed by three main layers: the controller, the service and the repository. 

### Controller
The controller is the entry point of the application. It implements a WebSocket through which requests (bets) are received and responses, either as acknowledges or notifications, are pushed. 

The client of the server connects to the guide WebSocket at *'/gs-guide-websocket'* and subscribes to the topic *'/topic/notifications'* in order to receive incoming notifications. The bets are sent by the client via the topic *'/monitor/process_bet'*.

### Service
The service implements all the business logic of the application. It processes the requests (bets) received by the controller, validates them and keeps track of the accounts betting history. When a new bet is received, the service will check if that client has issued any bets that, in total, within the last X seconds, surpass the limited betting threshold Y (X default value is 60 seconds and Y is 100 (with no specific associated currency)). 

In the case they do not exceed the limit, an acknowledge message is sent back to the client. However, if it is exceeded, a new Notification will be produced and locally stored in the database, which will then be sent back to the client. 

### Repository
The repository is responsible for communicating with the local database and perform the respective queries. Since the application does not need to perform any complex set of queries (it only stores the produced notifications), there is no need to explicitly implement any additional functionality to the repository as Spring will handle its implementation automatically via the [CrudRepository](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/CrudRepository.html) interface.

As for the database itself, the java SQL database H2 was used. Despite being an in-memory database, it is enough for the simple task of storing notifications. 


All of the application modules were also extensively tested via unit and integration tests. 

## Prerequisites

* [Maven](https://maven.apache.org/download.cgi)

* [Docker](https://www.docker.com/get-started)

## Deployment (local)

Clone the repository to your local machine 

```shell
$ git clone https://github.com/joaoamaral28/RHO-Backend-Challenge.git
```
Build the application manually or using your favorite IDE

```shell
$ cd RHO-Backend-Challenge
$ mvn clean install
```

Build the Docker image

```shell
$ docker build -f Dockerfile -t rho-challenge ./
```

Run the image

```shell
$ docker run -p 8080:8080 rho-challenge
```

## Manual test flow

You can manually test the application (simulating the BusinessOps service) at localhost:8080 by connecting to the server websocket and sending bets by specifying the ID and the stake amount.

#### Example 1 (notification by threshold generated): 
> * Account 1 sends a bet with stake amount 65. Server acknowledges the bet as valid. 
> * In the next instant account 1 sends a bet with stake 50.
>  * Result: Server originates a notification stating that account 1 exceeded the stake threshold of 100 
> by placing two bets which their sum, within the time window of 60 seconds, is equal to 115.

#### Example 2 (no notification generated): 
>  * Account 1 sends a bet with stake amount 60. Server acknowledges the bet as valid.
>  * After 30 seconds, account 1 sends a bet with stake amount 30. Server acknowledges the bet as valid.
>  * After 40 seconds, account 1 sends a bet with stake amount 60. Server aknowledges the bet as valid since the 
>  first bet occurred over 60 seconds ago, that is, its older than the bet processing window and the resulting stake sum is equal to 90.  

## Known issues 

The server does not verify the source of the requests nor does it authenticates subscribers in its topics since 
it was not within both the scope and objectives of this challenge.
