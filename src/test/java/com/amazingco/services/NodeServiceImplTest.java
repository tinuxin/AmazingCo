package com.amazingco.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.amazingco.TestHelper;
import com.amazingco.persistence.Node;
import com.amazingco.persistence.NodeRepository;

import org.assertj.core.util.Lists;
import org.junit.Before;

import static org.assertj.core.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
public class NodeServiceImplTest {

    @Mock
    NodeRepository nodeRepository;

    
    NodeServiceImpl nodeService;

    @Before
    public void setup() {
        nodeService = new NodeServiceImpl(nodeRepository);
    }

    @Test 
    public void getAllDecendants() {
        // Arrange
        Node root = TestHelper.createMockedNode(0L, null, null, 0);
        Node parent = TestHelper.createMockedNode(1L, root, root, 1);
        Node child1 = TestHelper.createMockedNode(2L, parent, root, 2);
        Node child2 = TestHelper.createMockedNode(3L, parent, root, 2);

        when(nodeRepository.findAllDecendants(any())).thenReturn(new ArrayList(Arrays.asList(root, parent, child1, child2)));

        // Act
        List<Node> result = nodeService.getAllDecendants(1L);

        // Assert
        assertThat(result).containsExactlyInAnyOrder(child1, child2);
    }

    @Test 
    public void getAllDecendants_parentIsRoot() {
        // Arrange
        Node root = TestHelper.createMockedNode(0L, null, null, 0);
        Node child1 = TestHelper.createMockedNode(2L, root, root, 2);
        Node child2 = TestHelper.createMockedNode(3L, root, root, 2);

        when(nodeRepository.findAllDecendants(any())).thenReturn(new ArrayList(Arrays.asList(root, child1, child2)));

        // Act
        List<Node> result = nodeService.getAllDecendants(0L);

        // Assert
        assertThat(result).containsExactlyInAnyOrder(child1, child2);
    }

    @Test 
    public void getAllDecendants_noDecendants() {
        // Arrange
        when(nodeRepository.findAllDecendants(any())).thenReturn(Lists.emptyList());

        // Act
        List<Node> result = nodeService.getAllDecendants(0L);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test 
    public void createNode_withParent() {
        // Arrange
        Node root = TestHelper.createMockedNode(0L, null, null, 0);
        Node parent = TestHelper.createMockedNode(1L, root, root, 1);
        Node newNode = TestHelper.createMockedNode(null, parent, null, null);

        when(nodeRepository.save(newNode)).thenReturn(mock(Node.class));

        // Act
        nodeService.createNode(newNode);

        // Assert
        verify(newNode).setHeight(2);
        verify(newNode).setRoot(root);
        verify(newNode).setParent(parent);
    }

    @Test 
    public void createNode_noParent() {
        // Arrange
        Node newNode = TestHelper.createMockedNode(null, null, null, null);

        when(nodeRepository.save(newNode)).thenReturn(mock(Node.class));

        // Act
        nodeService.createNode(newNode);

        // Assert
        verify(newNode).setRoot(newNode);
        verify(newNode, never()).setHeight(anyInt());
        verify(newNode, never()).setParent(any());
    }

    @Test 
    public void updateNode_newParent() {
        // Arrange
        Node root = TestHelper.createMockedNode(0L, null, null, 0);
        Node parent1 = TestHelper.createMockedNode(1L, root, root, 1);
        Node parent2 = TestHelper.createMockedNode(2L, root, root, 2);
        Node existingNode = TestHelper.createMockedNode(3L, parent1, root, 2);

        Node updatedNode = TestHelper.createMockedNode(null, parent2, null, null);

        when(nodeRepository.findById(any())).thenReturn(Optional.of(existingNode));
        when(nodeRepository.save(any())).thenReturn(mock(Node.class));

        // Act
        nodeService.updateNode(updatedNode.getId(), updatedNode);

        // Assert
        verify(existingNode).setRoot(root);
        verify(existingNode).setHeight(3);
        verify(existingNode).setParent(parent2);
    }

    @Test 
    public void updateNode_notFound() {
        // Arrange
        when(nodeRepository.findById(any())).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        // Act
        Exception exception = null;
        try {
            nodeService.updateNode(0L, mock(Node.class));
        } catch (Exception e) {
            exception = e;
        }

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception).isInstanceOf(ResponseStatusException.class);
        assertThat(((ResponseStatusException) exception).getStatus()).isEqualTo(HttpStatus.NOT_FOUND);

        verify(nodeRepository, never()).save(any());
    }

    @Test 
    public void updateNode_parentNotFound() {
        // Arrange
        Node updatedNode = TestHelper.createMockedNode(null, null, null, null);

        when(nodeRepository.findById(any())).thenReturn(Optional.of(mock(Node.class)));

        // Act
        Exception exception = null;
        try {
            nodeService.updateNode(updatedNode.getId(), updatedNode);
        } catch (Exception e) {
            exception = e;
        }

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception).isInstanceOf(ResponseStatusException.class);
        assertThat(((ResponseStatusException) exception).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);

        verify(nodeRepository, never()).save(any());
    }
}
