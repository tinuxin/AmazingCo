package com.amazingco.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpStatus;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import com.amazingco.TestHelper;
import com.amazingco.api.NodeDTO;
import com.amazingco.persistence.Node;
import com.amazingco.persistence.NodeRepository;

import org.assertj.core.api.Condition;
import org.assertj.core.util.Lists;
import org.junit.Before;

import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
public class NodeServiceImplTest {

    @Mock
    NodeRepository nodeRepository;

    NodeServiceImpl nodeService;

    private final static Condition<NodeDTO> SAME_ID_CONDITION = new Condition<NodeDTO>(node -> node.getId() == 60, "equals");

    @Before
    public void setup() {
        nodeService = new NodeServiceImpl(nodeRepository);

        when(nodeRepository.save(any())).thenAnswer(new Answer<Node>() {

            @Override
            public Node answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Node node = (Node) args[0];
                if (node.getId() == null) {
                    ReflectionTestUtils.setField(node, "id", (long) (Math.random()));
                }
                return (Node) args[0];
            }
        });
    }

    @Test
    public void getAllDecendants() {
        // Arrange
        Node root = TestHelper.createNode(0L, null, null, 0);
        Node parent = TestHelper.createNode(1L, root, root, 1);
        Node child1 = TestHelper.createNode(2L, parent, root, 2);
        Node child2 = TestHelper.createNode(3L, parent, root, 2);

        when(nodeRepository.findAllDecendants(any()))
                .thenReturn(new ArrayList(Arrays.asList(root, parent, child1, child2)));

        // Act
        Iterable<NodeDTO> result = nodeService.getAllDecendants(1L);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).anyMatch(node -> node.getId() == child1.getId());
        assertThat(result).anyMatch(node -> node.getId() == child2.getId());
    }

    @Test
    public void getAllDecendants_parentIsRoot() {
        // Arrange
        Node root = TestHelper.createNode(0L, null, null, 0);
        Node child1 = TestHelper.createNode(2L, root, root, 2);
        Node child2 = TestHelper.createNode(3L, root, root, 2);

        when(nodeRepository.findAllDecendants(any())).thenReturn(new ArrayList(Arrays.asList(root, child1, child2)));

        // Act
        Iterable<NodeDTO> result = nodeService.getAllDecendants(0L);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).anyMatch(node -> node.getId() == child1.getId());
        assertThat(result).anyMatch(node -> node.getId() == child2.getId());
    }

    @Test
    public void getAllDecendants_noDecendants() {
        // Arrange
        when(nodeRepository.findAllDecendants(any())).thenReturn(Lists.emptyList());

        // Act
        Iterable<NodeDTO> result = nodeService.getAllDecendants(0L);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    public void createNode_withParent() {
        // Arrange
        Node root = TestHelper.createNode(0L, null, null, 0);
        Node parent = TestHelper.createNode(1L, root, root, 1);

        NodeDTO newNode = TestHelper.createNodeDTO(parent.getId());

        when(nodeRepository.findById(parent.getId())).thenReturn(Optional.of(parent));

        // Act
        NodeDTO result = nodeService.createNode(newNode);

        // Assert
        assertThat(result.getHeight()).isEqualTo(2);
        assertThat(result.getRootId()).isEqualTo(root.getId());
        assertThat(result.getParentId()).isEqualTo(parent.getId());
    }

    @Test
    public void createNode_newRoot() {
        // Arrange
        NodeDTO newNode = TestHelper.createNodeDTO(null);

        // Act
        NodeDTO result = nodeService.createNode(newNode);

        // Assert
        assertThat(result.getHeight()).isEqualTo(0);
        assertThat(result.getRootId()).isEqualTo(result.getId());
        assertThat(result.getParentId()).isEqualTo(null);
    }

    @Test
    public void updateNode_newParent() {
        // Arrange
        Node root = TestHelper.createNode(0L, null, null, 0);
        Node parent1 = TestHelper.createNode(1L, root, root, 1);
        Node parent2 = TestHelper.createNode(2L, root, root, 2);
        Node existingNode = TestHelper.createNode(3L, parent1, root, 2);

        NodeDTO updatedNode = TestHelper.createNodeDTO(parent2.getId());

        when(nodeRepository.findById(parent2.getId())).thenReturn(Optional.of(parent2));
        when(nodeRepository.findById(existingNode.getId())).thenReturn(Optional.of(existingNode));

        // Act
        NodeDTO result = nodeService.updateNode(3L, updatedNode);

        // Assert
        assertThat(result.getHeight()).isEqualTo(3);
        assertThat(result.getRootId()).isEqualTo(root.getId());
        assertThat(result.getParentId()).isEqualTo(parent2.getId());
    }

    @Test
    public void updateNode_notFound() {
        // Arrange
        when(nodeRepository.findById(any())).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        // Act
        Exception exception = null;
        try {
            nodeService.updateNode(0L, mock(NodeDTO.class));
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
    public void updateNode_noParent() {
        // Arrange
        NodeDTO updatedNode = TestHelper.createNodeDTO(null);

        when(nodeRepository.findById(any())).thenReturn(Optional.of(mock(Node.class)));

        // Act
        Exception exception = null;
        try {
            nodeService.updateNode(0L, updatedNode);
        } catch (Exception e) {
            exception = e;
        }

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception).isInstanceOf(ResponseStatusException.class);
        assertThat(((ResponseStatusException) exception).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);

        verify(nodeRepository, never()).save(any());
    }

    @Test
    public void updateNode_parentNotFound() {
        // Arrange
        Node root = TestHelper.createNode(0L, null, null, 0);
        Node parent = TestHelper.createNode(1L, root, root, 1);

        NodeDTO updatedNode = TestHelper.createNodeDTO(null);

        when(nodeRepository.findById(any())).thenReturn(Optional.of(mock(Node.class)));

        // Act
        Exception exception = null;
        try {
            nodeService.updateNode(0L, updatedNode);
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
