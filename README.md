# P2C Load Balancer

A distributed load balancer implementation using the Power of Two Choices algorithm with dependency injection via Google Guice.

## Architecture

- **Load Balancer**: Routes requests to workers using P2C algorithm based on CPU utilization and request count
- **Workers**: Process N-Queen solver requests and send metrics to load balancer (This serves as an example since the worker is customizable)
- **Core**: Shared utilities and interfaces

## Components

### Load Balancer
- Receives client requests on `/solver/queen`
- Selects optimal worker using Power of Two Choices
- Receives worker metrics on `/metrics` endpoint
- Maintains worker registry for load balancing decisions

### Workers
- Solves N-Queen problems using genetic algorithm
- Sends periodic metrics (CPU utilization, request count) to load balancer
- Processes requests on `/solver/queen` endpoint

## Dependency Injection

Uses Google Guice for dependency management:
- `LoadBalancerModule`: Configures load balancer dependencies
- `WorkerModule`: Configures worker dependencies with metrics collection
- Context listeners manage injector lifecycle

## Running

**Start Load Balancer:**
```bash
./gradlew run --args="8080"
```

**Start Workers:**
```bash
./gradlew runWorker --args="8081"
./gradlew runWorker --args="8082"
```

**Test:**
```bash
curl "http://localhost:8080/solver/queen?n=8"
```

## Key Features

- **Power of Two Choices**: Selects best of 2 random workers
- **Metrics-based routing**: Uses CPU and request count for decisions
- **Genetic N-Queen solver**: Efficient problem solving algorithm
- **Dependency injection**: Clean separation of concerns with Guice
- **Concurrent processing**: Thread-safe worker registry and metrics