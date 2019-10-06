package com.amazingco.persistence;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Node {
    @Id @GeneratedValue
    private Long id;
    private Node parent;
    private Node root;
    private int height;

    public Node() {
    }

    public Long getId() {
        return id;
    }

    public Node getRoot() {
        return root;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "Node [height=" + height + ", id=" + id + ", parent=" + parent + ", root=" + root + "]";
    }
}
