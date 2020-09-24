package com.amazingco.persistence;

import java.util.List;
import java.util.Set;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface NodeRepository extends CrudRepository<Node, Long> {

    @Query("MATCH (n:Node) WHERE ID(n)={ parentId } MATCH (n)-[:PARENT*]->(s) RETURN s, (n)<-[:ROOT]-(), (s)<-[:ROOT]-(), ()-[:PARENT*]->(s)")
    List<Node> findAllDecendants(@Param("parentId") Long parentId);

    Set<Node> findByHeight(int height);

    @Query("MATCH (n) RETURN count(*)")
    int getGraphSize();
}