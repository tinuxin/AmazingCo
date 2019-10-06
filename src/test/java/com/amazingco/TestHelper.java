package com.amazingco;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import org.springframework.hateoas.Link;

import com.amazingco.persistence.Node;

import org.springframework.test.util.ReflectionTestUtils;

public class TestHelper {

    private static String TEST_API_ROOT = "http://localhost/nodes/";

    public static Node createMockedNode(Long id, Node parent, Node root, Integer height) {
        Node node = mock(Node.class);
        when(node.getId()).thenReturn(id);
        if (height != null) {
            when(node.getHeight()).thenReturn(height);
        }
        when(node.getParent()).thenReturn(parent);
        when(node.getRoot()).thenReturn(root);

        return node;
    }

    public static Node createMockedNode() {
        return createMockedNode(null, null, null, null);
    }

    public static Node createNode(Node parent) {
        Node node = new Node();
        node.setParent(parent);
        return node;
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