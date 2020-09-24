# AmazingCo

# The challenge
We in Amazing Co need to model how our company is structured so we can do awesome stuff.

We have a root node and several children nodes, each one with its own children as well. It's a tree-based structure. Something like:     


We need two HTTP APIs that will serve the two basic operations:

1) Get all children nodes of a given node (the given node can be anyone in the tree structure).

2) Change the parent node of a given node (the given node can be anyone in the tree structure).

They need to answer quickly, even with tons of nodes. Also,we can't afford to lose this information, so some sort of persistence is required. 

Each node should have the following info:

1) node identification

2) who is the parent node 

3) who is the root node 

4) the height of the node. In the above example,height(root) = 0 and height(a) == 1.

Our boss is evil and we can only have docker and docker-compose on our machines. So your server needs to be ran using them.


# Build

## Build JAR
`docker run --rm -u gradle -v "$PWD":/home/gradle/project -w /home/gradle/project gradle:5.6.2-jdk12 gradle build`

## Build docker image
`docker build . -t amazingco`

# Deploy
`docker-compose up -d`

# Test
`docker run --rm -v "$PWD":/home/gradle/project -w /home/gradle/project gradle:5.6.2-jdk12 gradle test`

The two required endpoints are available as:
## Get all decendants
`curl -X GET http://localhost:8080/nodes/<ID_OF_PARENT>/decendants`

## Change parent
`curl -X PATCH -H "Content-Type: application/json" -d '{"parentId": <ID_OF_NEW_PARENT>}' http://localhost:8080/nodes/{id}`

## Create tree
To populate the database one can do the following to create a root
`curl -X POST -H "Content-Type: application/json" -d '{}' http://localhost:8080/nodes`
and then
`curl -X POST -H "Content-Type: application/json" -d '{"parentId": <ID_OF_PARENT>}' http://localhost:8080/nodes`
to add nodes to the tree

## Extra
To get all nodes
`curl -X GET http://localhost:8080/nodes`

To get specific node
`curl -X GET http://localhost:8080/nodes/<NODE_ID>`

To get all nodes of a specific height
`curl -X GET http://localhost:8080/nodes/height/<HEIGHT>`

## TODO
I would have liked to have tested all the endpoints in NodeControllerTest but the foundation for the rest of the tests is in place.
