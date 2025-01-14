###  Casper BLOCKCHAIN component
Since Camel 3.14.1

Both producer and consumer are supported.

Producer performs RPC calls to Casper blockchaine like querying blocks, deploys, accounts.

Consumer polls Casper Event Stream Server (port 9999).

The Casper blockchain component uses the Casper SDK API and allows you to interact with Casper Network nodes

Maven users will need to add the following dependency to their pom.xml for this component:

```java
<dependency>
    <groupId>io.caspercommunity</groupId>
    <artifactId>camel-casper</artifactId>
    	<version>3.14.1-SNAPSHOT</version>
    <!-- use the same version as your Camel core version -->
</dependency>
```

###   URI FORMAT
```java
casper://<local/remote host:port or local IPC path>[?options]
```

###  CONFIGURING OPTIONS

Camel components are configured on two separate levels:

- component level
- endpoint level

###  CONFIGURING COMPONENT OPTIONS

The component level is the highest level which holds general and common configurations that are inherited by the endpoints. For example a component may have security settings, credentials for authentication, urls for network connection and so forth.

Some components only have a few options, and others may have many. Because components typically have pre configured defaults that are commonly used, then you may often only need to configure a few options on a component; or none at all.

Configuring components can be done with [the Component DSL](https://camel.apache.org/manual/component-dsl.html), in a configuration file (application.properties|yaml), or directly with Java code.

### CONFIGURING ENDPOINT OPTIONS

Where you find yourself configuring the most is on endpoints, as endpoints often have many options, which allows you to configure what you need the endpoint to do. The options are also categorized into whether the endpoint is used as consumer (from) or as a producer (to), or used for both.

Configuring endpoints is most often done directly in the endpoint URI as path and query parameters. You can also use the [Endpoint DSL](https://camel.apache.org/manual/Endpoint-dsl.html) as a type safe way of configuring endpoints.

A good practice when configuring options is to use [Property Placeholders](https://camel.apache.org/manual/using-propertyplaceholder.html), which allows to not hardcode urls, port numbers, sensitive information, and other settings. In other words placeholders allows to externalize the configuration from your code, and gives more flexibility and reuse.

The following two sections lists all the options, firstly for the component followed by the endpoint.


### COMPONENT OPTIONS
The Casper Blockchain component supports 11 options, which are listed below.

| Name | Description | Default | Type |
|---|---|---|---|
| `configuration (producer)` | `Default configuration.` |  | CasperConfiguration |
| `operation (common)` | `Operation to use.` |  |  |
| `casperService (common)` | `Casper RPC API ` |  |  CasperService|
| `deployHash (producer)` | `Hash of a deploy in the blockchain ` |  |  String|
| `blockHash (producer)` | `Hash of a block in the blockchain ` |  |  String|
| `blockHeight (producer)` | `Height of a block in the blockchain ` |  |  String|
| `publicKey (producer)` | `Account publick key  ` |  |  String|
| `key (producer)` | `casper_types::Key as formatted string ` |  |  String|
| `path (producer)` | `Path components starting from the key as base ` |  |  String|
| `stateRootHash (producer)` | `state_Root_Hash : an identifier of the current network state ` |  |  String|
| `purseUref (producer)` | `purseUref : URef of an  account main purse` |  |  String|


### ENDPOINT OPTIONS

The Casper Blockchain endpoint is configured using URI syntax:

```java
casper:nodeUrl
```

with the following path and query parameters:

| Name | Description | Default | Type |
|---|---|---|---|
| `nodeUrl (common)` | `Required, sets the node url.` |  | String |


### QUERY PARAMETERS (11 PARAMETERS)

| Name | Description | Default | Type |
|---|---|---|---|
| `configuration (producer)` | `Default configuration.` |  | CasperConfiguration |
| `operation (common)` | `Operation to use.` |  |  |
| `casperService (common)` | `Casper RPC API ` |  |  CasperService|
| `deployHash (producer)` | `Hash of a deploy in the blockchain ` |  |  String|
| `blockHash (producer)` | `Hash of a block in the blockchain ` |  |  String|
| `blockHeight (producer)` | `Height of a block in the blockchain ` |  |  String|
| `publicKey (producer)` | `Account publick key  ` |  |  String|
| `key (producer)` | `casper_types::Key as formatted string ` |  |  String|
| `path (producer)` | `Path components starting from the key as base ` |  |  String|
| `stateRootHash (producer)` | `state_Root_Hash : an identifier of the current network state ` |  |  String|
| `purseUref (producer)` | `purseUref : URef of an  account main purse` |  |  String|



### SAMPLES

Consume BLOCK_ADDED events from Casper SSE and send block hashes to a jms queue :

```java
from("casper://127.0.0.1:9999/events/main?operation=BLOCK_ADDED")
.jsonpath("$blockAdded.block_hash", false, String.class, "block")
.to("jms:queue:new_blocks");

```
Use the block hashes to retrieve the block transfers:

```java
from("jms:queue:new_blocks")
.setHeader(BLOCK_HASH, body())
.to("casper://127.0.0.1:7777?operation=BLOCK_TRANSFERS");
```

### SPRING BOOT AUTO-CONFIGURATION
TODO



### How to install

In your maven pom file add :

```java
<dependency>
    <groupId>io.caspercommunity</groupId>
    <artifactId>camel-casper</artifactId>
   	<version>3.14.1-SNAPSHOT</version>
</dependency>
```

###  Running unit tests

### Requirements:

#### JAVA

Make sure you have the Java 8 JDK (also known as 1.8).
If you don’t have version 1.8 or higher, install the JDK

#### Maven 

Install maven (version 3.3.9 or higher).
Guide : https://maven.apache.org/install.html.

#### Clone the project

```java
git clone https://github.com/caspercommunityio/camel-casper
```

####  Run the unit tests :

```java
cd camel-casper
mvn test
```

###  Usage examples

- configure routes using RouteBuilder

````java


import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.casper.CasperConstants;
/**
 * A Camel Java DSL Router
 */
public class ARouteBuilder extends RouteBuilder {
/**
 * Let's configure the Camel routing rules using Java code...
 */
   public void configure()
   {
	   //this route  queries the node for latest block every 10 seconds and print the block object (using to string method)
	   from("timer://simpleTimer?period=10000")
	      .to("casper:http://65.21.227.180:7777/?operation="+CasperConstants.LAST_BLOCK)
	      .log("This call gives - ${body}");

	  
	  //this route reads a uref_purse from a file, use it to query an account balance and send the result via email
	  from("file://temp/input.txt")
                .convertBodyTo(String.class)
                .setHeader(PURSE_UREF, body())
                .to("casper:http://65.21.227.180:7777/?operation="+CasperConstants.ACCOUNT_BALANCE&stateRootHash=30cE5146268305AeeFdCC05a5f7bE7aa6dAF187937Eed9BB55Af90e1D49B7956)
                .to("smtp://user@mailserver?password=&to=tim@devxdao.com")
}

````

- Run the routes :

````java
import org.apache.camel.main.Main;

/**
* A Camel Application
  */
  public class MainApp {
  /**
    * A main() so we can easily run these routing rules in our IDE
      */
      public static void main(String...args) throws Exception
      {
        Main main = new Main();
        main.configure().addRoutesBuilder(new ARouteBuilder());
         main.run(args);
      }
  }
````

### Demo Project

You can find a demo project here : https://github.com/caspercommunityio/camel-example-casper

To build and run the project :
```
git clone https://github.com/caspercommunityio/camel-example-casper
cd camel-example-casper
mvn compile
mvn exec:java
```

The project contains 4 routes :
* Make a RPC request to get the state root hash
* Read a block hash stored in a file and make a RPC Request to get the block info from that hash
* Read a block height stored in a file and make a RPC Request to get the auction info from that specific block height and Then saves the validator slot of the auction to a json file
* Make a RPC request to get the state root hash then query an account using the state root hash and an account
