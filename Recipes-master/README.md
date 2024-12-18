# Run project by Docker

1. Download and install Docper Desktop form: 

https://www.docker.com/products/docker-desktop/

2. Pull Recipes application image by command:

 ```sh
  docker pull mszczekocka/recipe_app:app
  ```
  
3. Run app:

 ```sh
  docker run -p 8881:8881 mszczekocka/recipe_app:app
  ```


<b>Link to repository: </b>
https://hub.docker.com/repository/docker/mszczekocka/recipe_app
