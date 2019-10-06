package com.amazingco;

import org.springframework.hateoas.Link;

import com.amazingco.api.NodeDTO;
import com.amazingco.persistence.Node;

import org.springframework.test.util.ReflectionTestUtils;

public class TestHelper {

    private static String TEST_API_ROOT = "http://localhost/nodes/";

    public static NodeDTO createNodeDTO(Long parentId) {
        NodeDTO nodeDTO = new NodeDTO();
        nodeDTO.setParentId(parentId);
        return nodeDTO;
    }

    public static Node createNode(Long id, Node parent, Node root, Integer height) {
        Node node = new Node();
        ReflectionTestUtils.setField(node, "id", id);
        if (height != null) {
            node.setHeight(height);
        }
        node.setParent(parent);
        node.setRoot(root == null ? node : root);
        return node;
    }

    public static Link buildMockedLink(Long id) {
        return new Link(TEST_API_ROOT+id);
    }
}