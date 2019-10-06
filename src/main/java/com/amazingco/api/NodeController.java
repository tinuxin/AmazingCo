package com.amazingco.api;

import java.util.ArrayList;
import java.util.Collection;

import com.amazingco.persistence.Node;
import com.amazingco.services.NodeService;

import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

@RestController
@RequestMapping(path="nodes")
public class NodeController {

    private final EntityLinks entityLinks;

    private final NodeService nodeService;

    public NodeController(NodeService nodeService, EntityLinks entityLinks) {
        this.entityLinks = entityLinks;
        this.nodeService = nodeService;
    }

    @GetMapping
    public @ResponseBody ResponseEntity<?> getAll() {
        Iterable<Node> nodes = nodeService.getAll();

        return ResponseEntity.ok(toResources(nodes, linkTo(methodOn(NodeController.class).getAll()).withSelfRel()));
    }

    @GetMapping("/{nodeId}/decendants")
    public @ResponseBody ResponseEntity<?> getAllDecendants(@PathVariable Long nodeId) {
        Iterable<Node> nodes = nodeService.getAllDecendants(nodeId);

        return ResponseEntity.ok(toResources(nodes, linkTo(methodOn(NodeController.class).getAllDecendants(nodeId)).withSelfRel()));
    }

    @GetMapping("/height/{height}")
    public @ResponseBody ResponseEntity<?> getByHeight(@PathVariable int height) {
        Iterable<Node> nodes = nodeService.getByHeight(height);

        return ResponseEntity.ok(toResources(nodes, linkTo(methodOn(NodeController.class).getByHeight(height)).withSelfRel()));
    }

    @GetMapping("/{nodeId}")
    public @ResponseBody ResponseEntity<?> getNode(@PathVariable Long nodeId) {
        Node node = nodeService.getNodeById(nodeId);

        return ResponseEntity.ok(toResource(node));
    }

    @PostMapping
    public @ResponseBody ResponseEntity<?> createNode(@RequestBody Resource<NodeDTO> nodeBody) {
        Node persistedNode = nodeService.createNode(nodeBody.getContent());

        return ResponseEntity.ok(toResource(persistedNode));
    }

    @PatchMapping("/{nodeId}")
    public @ResponseBody ResponseEntity<?> updateNode(@PathVariable Long nodeId, @RequestBody Resource<NodeDTO> updatedNodeBody) {
        Node updatedNode = nodeService.updateNode(nodeId, updatedNodeBody.getContent());

        return ResponseEntity.ok(toResource(updatedNode));
    }

    private Resource<NodeDTO> toResource(Node node) {
        Resource<NodeDTO> resource = new Resource<NodeDTO>(new NodeDTO(node));
        resource.add(entityLinks.linkToSingleResource(Node.class, node.getId()).withSelfRel());
        if (node.getParent() != null) {
            resource.add(entityLinks.linkToSingleResource(Node.class, node.getParent().getId()).withRel("parent"));
        }
        if (node.getRoot() != null) {
            resource.add(entityLinks.linkToSingleResource(Node.class, node.getRoot().getId()).withRel("root"));
        }

        return resource;
    }

    private Resources<Resource<NodeDTO>> toResources(Iterable<Node> nodes, Link self) {
        Collection<Resource<NodeDTO>> nodeResources = new ArrayList();
        nodes.forEach((node) -> nodeResources.add(toResource(node)));
        Resources<Resource<NodeDTO>> resources = new Resources<Resource<NodeDTO>>(nodeResources);

        resources.add(self);

        return resources;
    }
}