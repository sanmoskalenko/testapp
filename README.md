# testapp

Demo application written in Clojure

## Prerequisites

You will need [Leiningen][] 2.0.0 or above installed.

[leiningen]: https://github.com/technomancy/leiningen

## Usage

To start the application for the first time, you need to run the following command:

    npm run start && lein run

next, the command is enough to run: 
    
    lein run


## Build
To build an application into one uberjar that includes frontend and backend, run the following command:

    npm run release && lein uberjar 

## Description

Application after launch is available on `localhost:8080/testapp`

Two types of requests are implemented:
* `GET` - returns a list of all orders, available at `localhost:8080/api/orders`
* `POST` - creates a new order, available by `localhost:8080/api/order`

`PUT` and `DELETE` requests are not available, attempting to call these methods will return an error stating that the methods are not implemented


## License

Copyright © 2022 sanmoskalenko
