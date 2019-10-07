package com.amazingco.services;

import com.amazingco.api.NodeDTO;

import org.springframework.stereotype.Service;

@Service
public interface NodeService {

    Iterable<NodeDTO> getAll();

    Iterable<NodeDTO> getAllDecendants(Long nodeId);

    Iterable<NodeDTO> getByHeight(int height);

    NodeDTO getNodeById(Long nodeId);

    NodeDTO createNode(NodeDTO node);

    NodeDTO updateNode(Long nodeId, NodeDTO updatedNode);
}