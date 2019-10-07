package com.amazingco.services;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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

    public Iterable<NodeDTO> getAll() {
        return nodesToDTO(nodeRepository.findAll());
    }

    public Iterable<NodeDTO> getAllDecendants(Long nodeId) {
        List<Node> decendants = nodeRepository.findAllDecendants(nodeId);

        removeRelationshipsFromResult(nodeId, decendants);

        return nodesToDTO(decendants);
    }

    public Iterable<NodeDTO> getByHeight(int height) {
        return nodesToDTO(nodeRepository.findByHeight(height));
    }

    public NodeDTO getNodeById(Long nodeId) {
        return new NodeDTO(getNodeEntityById(nodeId));
    }

    public NodeDTO createNode(NodeDTO nodeDTO) {
        Node node = new Node();
        if (nodeDTO.getParentId() == null) {
            node.setRoot(node);
        } else {
            try {
                updateAttributesFromParent(node, getNodeEntityById(nodeDTO.getParentId()));
            } catch (ResponseStatusException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parent Node with id "+nodeDTO.getParentId()+" not found.");
            }
        }

        return new NodeDTO(nodeRepository.save(node));
    }

    public NodeDTO updateNode(Long nodeId, NodeDTO updatedNode) {
        Node existingNode = getNodeEntityById(nodeId);
        
        if (updatedNode.getParentId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter 'parent' not found.");
        } else {
            try {
                updateAttributesFromParent(existingNode, getNodeEntityById(updatedNode.getParentId()));
            } catch (ResponseStatusException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parent Node with id "+updatedNode.getParentId()+" not found.");
            }
        }

        return new NodeDTO(nodeRepository.save(existingNode));
    }

    private Iterable<NodeDTO> nodesToDTO(Iterable<Node> nodes) {
        return StreamSupport.stream(nodes.spliterator(), false)
            .map(NodeDTO::new)
            .collect(Collectors.toList());
    }

    private Node getNodeEntityById(Long nodeId) {
        return nodeRepository.findById(nodeId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    private void updateAttributesFromParent(Node node, Node parent) {
        node.setParent(parent);
        node.setRoot(parent.getRoot());
        node.setHeight(parent.getHeight() + 1);
    }

    private void removeRelationshipsFromResult(Long parentId, List<Node> decendants) {
        /*  This is a bit of a hack a but necessary as Neo4j needs to return parent and root along with the children in order to populate the parent and root relationship.
            This solution is not optimal as worst case it needs requires traversing the entire list of nodes to find the parent.
        */
        if (!decendants.isEmpty()) {
            Node parent = decendants.stream().filter((n) -> n.getId() == parentId).findAny().get();
            decendants.remove(parent);
            decendants.remove(parent.getParent());
            decendants.remove(parent.getRoot());
        }
    }
}