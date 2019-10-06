package com.amazingco.persistence;

import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.util.List;
import java.util.Set;

import com.amazingco.TestHelper;
import com.amazingco.services.NodeService;
import com.amazingco.services.NodeServiceImpl;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.ogm.config.Configuration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest;
import org.springframework.context.annotation.Bean;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

@RunWith(SpringRunner.class)
@DataNeo4jTest
public class NodeRepositoryTest {

    @Autowired
    private NodeRepository nodeRepository;
    
    
    private NodeService nodeService;

    private Node root;
    private Node parent1;
    private Node parent2;
    private Node child1_1;
    private Node child1_2;
    private Node child2_1;
    
    @Before
    public void setup() {
        // Use NodeService to create nodes to enforce business rules
        nodeService = new NodeServiceImpl(nodeRepository);
        
        root = nodeService.createNode(TestHelper.createNodeDTO(null));

        parent1 = nodeService.createNode(TestHelper.createNodeDTO(root.getId()));
        parent2 = nodeService.createNode(TestHelper.createNodeDTO(root.getId()));

        child1_1 = nodeService.createNode(TestHelper.createNodeDTO(parent1.getId()));
        child1_2 = nodeService.createNode(TestHelper.createNodeDTO(parent1.getId()));
    }

    @Test
    public void findAllDecendants_root() {
        // Arrange

        // Act
        List<Node> result = nodeRepository.findAllDecendants(root.getId());

        //Assert
        assertThat(result).containsExactlyInAnyOrder(root, parent1, parent2, child1_1, child1_2);
    }

    @Test
    public void findAllDecendants_withDecendants() {
        // Arrange

        // Act
        List<Node> result = nodeRepository.findAllDecendants(parent1.getId());

        //Assert
        assertThat(result).containsExactlyInAnyOrder(root, parent1, child1_1, child1_2);
    }

    @Test
    public void findAllDecendants_noDecendants() {
        // Arrange

        // Act
        List<Node> result = nodeRepository.findAllDecendants(parent2.getId());

        //Assert
        assertThat(result).isEmpty();;
    }

    @Test
    public void findByHeight() {
        // Arrange

        // Act
        Set<Node> result = nodeRepository.findByHeight(0);

        //Assert

        assertThat(result).containsOnly(root);
    }

    @Test
    public void findByHeight_multipleBranches() {
        // Arrange

        // Act
        Set<Node> result = nodeRepository.findByHeight(1);

        //Assert
        assertThat(result).containsExactlyInAnyOrder(parent1, parent2);
    }

    @Test
    public void findByHeight_tooDeep() {
        // Arrange

        // Act
        Set<Node> result = nodeRepository.findByHeight(3);

        //Assert
        assertThat(result).isEmpty();
    }
}