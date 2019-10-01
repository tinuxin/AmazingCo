# AmazingCo

# The challenge
We in Amazing Co need to model how our company is structured so we can do awesome stuff.

We have a root node (only one) and several children nodes, each one with its own children as well. It's a tree-based structure. Something like:     


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

`docker run --rm -u gradle -v "$PWD":/home/gradle/project -w /home/gradle/project gradle:5.6.2-jdk12 gradle build --build-cache --info`

`docker build . -t amazingco`

# RUN
`docker run --rm -p 8080:8080 amazingco`