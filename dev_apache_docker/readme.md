# Checkout Dev Apache docker image
  It contains BookingApp specific apache configuration.
  Base image: ```registry.docker.hcom/hotels/apache:latest```
  
## What's the difference
* Missing rewrite rules added
* Fake authentication rewrite rules added

## How to build it
1. Build docker image
  ```docker build -t registry.docker.hcom/v_ltarcsanyi/apache-checkout-test:<version> .```
2. Push image to the repository
  ```docker push registry.docker.hcom/v_ltarcsanyi/apache-checkout-test:<version>```
3. Tag latest
  ```docker tag registry.docker.hcom/v_ltarcsanyi/apache-checkout-test:<version> registry.docker.hcom/v_ltarcsanyi/apache-checkout-test:latest```
4. Push latest
  ```docker push registry.docker.hcom/v_ltarcsanyi/apache-checkout-test:latest```
