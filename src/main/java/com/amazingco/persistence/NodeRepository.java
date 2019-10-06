package com.amazingco.persistence;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "nodes", path = "nodes")
public interface NodeRepository extends PagingAndSortingRepository<Node, Long> {
}