package com.amazingco.services;

import java.util.List;

import com.amazingco.api.NodeDTO;
import com.amazingco.persistence.Node;
import com.amazingco.persistence.NodeRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class NodeServiceImpl implements NodeService {

    private final NodeRepository nodeRepository;

    public NodeServiceImpl(NodeRepository nodeRepository) {
        this.nodeRepository = nodeRepository;
    }

    public Iterable<Node> getAll() {
        return nodeRepository.findAll();
    }

    public List<Node> getAllDecendants(Long nodeId) {
        List<Node> decendants = nodeRepository.findAllDecendants(nodeId);

        /*  This is obviously a hack a but I simply cannot figure out how to make neo4j return only the children while also populating the the parent and root relationship.
            This solution is sub par both in design and in performance as it worst case requires traversing the entire list of nodes to find the parent.
        */
        // HACK START
        if (!decendants.isEmpty()) {
            Node parent = decendants.stream().filter((n) -> n.getId() == nodeId).findAny().get();
            decendants.remove(parent);
            decendants.remove(parent.getParent());
            decendants.remove(parent.getRoot());
        }
        // HACK END

        return decendants;
    }

    public Iterable<Node> getByHeight(int height) {
        return  nodeRepository.findByHeight(height);
    }

    public Node getNodeById(Long nodeId) {
        return nodeRepository.findById(nodeId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public Node createNode(NodeDTO nodeDTO) {
        Node node = new Node();
        if (nodeDTO.getParentId() == null) {
            node.setRoot(node);
        } else {
            try {
                updateAttributesFromParent(node, getNodeById(nodeDTO.getParentId()));
            } catch (ResponseStatusException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parent Node with id "+nodeDTO.getParentId()+" not found.");
            }
        }

        return nodeRepository.save(node);
    }

    public Node updateNode(Long nodeId, NodeDTO updatedNode) {
        Node existingNode = getNodeById(nodeId);
        
        if (updatedNode.getParentId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter 'parent' not found.");
        } else {
            try {
                updateAttributesFromParent(existingNode, getNodeById(updatedNode.getParentId()));
            } catch (ResponseStatusException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parent Node with id "+updatedNode.getParentId()+" not found.");
            }
        }

        return nodeRepository.save(existingNode);
    }

    private void updateAttributesFromParent(Node node, Node parent) {
        node.setParent(parent);
        node.setRoot(parent.getRoot());
        node.setHeight(parent.getHeight() + 1);
    }

}