package com.amazingco.services;

import com.amazingco.persistence.Node;

import org.springframework.stereotype.Service;

@Service
public interface NodeService {

    Iterable<Node> getAll();

    Iterable<Node> getAllDecendants(Long nodeId);

    Iterable<Node> getByHeight(int height);

    Node getNodeById(Long nodeId);

    Node createNode(Node node);

    Node updateNode(Long nodeId, Node updatedNode);
}