# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```

Phase 2 Design: 
https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGJITgwamURkwHRhOnAUaYRnElknUG4lTlNA+BAIHEiFRsyXM0kgSFyFD8uE3RkM7RS9Rs4ylBQcDh8jqM1VUPGnTUk1SlHUoPUKHxgVKw4C+1LGiWmrWs06W622n1+h1g9W5U6Ai5lCJQpFQSKqJVYFPAmWFI6XGDXDp3SblVZPQN++oQADW6ErU32jsohfgyHM5QATE4nN0y0MxWMYFXHlNa6l6020C3Vgd0BxTF5fP4AtB2OSYAAZCDRJIBNIZLLdvJF4ol6p1JqtAzqBJoIei0azF5vDgHYsgwr5ks9K+KDvvorxLAC5wFrKaooOUCAHjysL7oeqLorE2IJoYLphm6ZIUgatLlqOJpEuGFocjA3K8gagrCjAQGhqRbqdph5Q0dojrOpeMHlFQwDIFomRVKBn6wux8ggQsSwYXKWEEjhLLlJ63pBgGQYhq60qRhR0YwLGwYcZhnb-mmyE8tmuaYCZLFXmmw63KOC6Tn006zs246tn0362Z22Q9jA-aDr0AxEaMTlfK5jbuROXlmJwq7eH4gReCg6B7gevjMMe6SZJgfkXkU1DXtIACiu4lfUJXNC0D6qE+3SRXO7a-mcQIAY16AQW1LWsTA8H2Jlal1lFaAyTBGoKaSRgoNwmT6UNM4jSRTLMdp5SRMMEA0Hp6mGbJxmQSWyGZRZCB5odILQSUfYDjANQANLNb555gDdTh3Y9y4JeugSQrau7QjAADio6stlp55S9bI-uUFRAxV1X2KODVBm5aBPcmF1XB1aBdam0OyXB0Ig6MqgLWjY1Oom8lMYpMDkmAJNqLCy1mhGhSWpRFIgKkMD8Qg9Og4xK1aVd4LA6DAOxJxiaY915RM6oUvnd1NlFWmivKxjpz5a9AUDt0mvQnFK6eIlG7YD4UDYNw8C6pkEujCkOVnjkzBXdetQNIjyPBKjI1DkjowAHKjt56tsiZ2P+3OsxBygoejHjUHcVTsEwMpmRM+TI1x6OicoJTXEyJN7oZ-bKDZ-HGml+R5S6dXe3jUmf5Y3bXpZ6Op0q-jLeFddpZ9PHACS0jheHxx97rN0AIxDkPo6j+PJvfUlASWDN8HJDAABSEA8o7hgBDoCCgA2kNuwT6uw1UlJ3i08co8Nc5DjbwAb1AcAQPBUB56Mo8Tw7HLVM0dn7oHfKfD+X8f5-xQAAqyF0r5p3KAAK33mgbOOMIHv0oNA6AsDR5F2piXWmU0GZV0XtIVmZE1pczADzQWoxaJhBHtIYWbM1bIMYSgeM+0xbyhgHxASeo8FwjflA7+0AiFyRISLKafhBKV1HLCRu8hqGrQ5hRBRepuG8ObsA4E5Q948iZt3BBqs+4w31u9B62tCjT2sR9UwX0zY-QCF4d+XYvSwGANgG2hB4iJGdhDXWSDiplQqlVVoxgMat3ll4vAsi9AGHTGiDE5je6p3FiAbgeAFB+OQCAQJaAWYyxkZpKaOTvGMmSSgUpFT2bsnKNIGaFJDD8yME3NOB14mIG8WY6yljbJvScT+Z6bsRm2K+kAA
