# Context

We focus here on exposing the features of the Java library to non-Java developers.
We do so by implementing a web service.

# Objectives

We aim to provide:
- all the features of the Java library
- an easy to use, discoverable, stateless service

By discoverable, we mean that we can request the root endpoint of the service and look at its response to know what else we can do.
More precisely, we provide a [REST](https://en.wikipedia.org/wiki/Representational_state_transfer) web service.

By stateless, we mean that we don't ant to store any user-related data.
In other words, one would request the service by providing all the relevant data to compute and receive a response with all the result.
No storage of dynamic data is allowed.

# Focus

The focus of the service is on providing meaningful resources to interact with in a stateless manner.
In other words:
- the library features should be exposed by the service in a way that makes them easy to use
- the required data must be encoded/decoded in an efficient way in the request/response exchanges

The second point is the one requiring the most effort.
Because we store nothing, we may need a fair amount of data to be exchanged.
We must pay attention to exchange only the required data and represent it efficiently.

# Technologies

We program in Java.
We use SpringBoot for a simple way to build our web service.
Testing is done through jUnit 5 for unit tests and RestAssured for system tests.
