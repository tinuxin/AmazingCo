package com.amazingco.persistence;

import com.fasterxml.jackson.annotation.JsonBackReference;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Node {

    @Id
    @GeneratedValue
    private Long id;
    @Relationship(type = "PARENT", direction = "INCOMING")
    @JsonBackReference
    private Node parent;
    @Relationship(type = "ROOT", direction = "INCOMING")
    @JsonBackReference
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

    public void setRoot(Node root) {
        this.root = root;
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
}
