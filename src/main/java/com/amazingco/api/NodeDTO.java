package com.amazingco.api;

import com.amazingco.persistence.Node;
import org.springframework.hateoas.core.Relation;

@Relation(value = "node", collectionRelation = "nodes")
public class NodeDTO {
    private Long id;
    private Long parentId;
    private Long rootId;
    private int height;

    public NodeDTO() {
    }

    public NodeDTO(Node node) {
        if (node.getId() != null) {
            this.id = node.getId();
        }
        if (node.getParent() != null) {
            this.parentId = node.getParent().getId();
        }
        if (node.getRoot() != null) {
            this.rootId = node.getRoot().getId();
        }
        this.height = node.getHeight();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

	public Long getRootId() {
		return rootId;
	}

	public void setRootId(Long rootId) {
		this.rootId = rootId;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
}