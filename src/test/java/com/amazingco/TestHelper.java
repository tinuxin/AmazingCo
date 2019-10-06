package com.amazingco;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


import com.amazingco.persistence.Node;

public class TestHelper {
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
}