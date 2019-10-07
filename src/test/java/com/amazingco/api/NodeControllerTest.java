package com.amazingco.api;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import com.amazingco.TestHelper;
import com.amazingco.services.NodeService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.server.ResponseStatusException;

@RunWith(SpringRunner.class)
@WebMvcTest(NodeController.class)
public class NodeControllerTest {

    @MockBean
    private NodeService nodeService;

    @MockBean
    private EntityLinks entityLinks;

    @Autowired
    private MockMvc mockMvc;

    @Before
    public void setup() {
        when(entityLinks.linkToSingleResource(any(), anyLong())).thenAnswer(new Answer<Link>() {

            @Override
            public Link answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return TestHelper.buildMockedLink((Long) args[1]);
            }
        });
    }

    @Test
    public void getAllDecendants() throws Exception {
        // Arrange
        Long parentId = 1L;
        NodeDTO child1 = TestHelper.createNodeDTO(2L, parentId, 0L, 2);
        NodeDTO child2 = TestHelper.createNodeDTO(3L, parentId, 0L, 2);

        when(nodeService.getAllDecendants(parentId)).thenReturn(Arrays.asList(child1, child2));

        // Act
        ResultActions result = mockMvc.perform(get("/nodes/" + parentId + "/decendants"));

        // Assert
        result.andExpect(status().isOk()).andExpect(content().contentType("application/hal+json;charset=UTF-8"))
                .andExpect(jsonPath("_embedded.nodes", hasSize(2)));
        verifyJsonNode(result, child1, "_embedded.nodes[0]");
        verifyJsonNode(result, child2, "_embedded.nodes[1]");
        result.andExpect(
                jsonPath("_links.self.href", is(TestHelper.buildMockedLink(parentId).getHref() + "/decendants")));
    }

    @Test
    public void getAllDecendants_noDecendants() throws Exception {
        // Arrange
        Long parentId = 1L;
        NodeDTO child2 = TestHelper.createNodeDTO(3L, parentId, parentId, 1);

        when(nodeService.getAllDecendants(parentId)).thenReturn(Lists.emptyList());

        // Act
        ResultActions result = mockMvc.perform(get("/nodes/" + parentId + "/decendants"));

        // Assert
        result.andExpect(status().isOk()).andExpect(content().contentType("application/hal+json;charset=UTF-8"))
                .andExpect(jsonPath("_embedded").doesNotExist());
        result.andExpect(
                jsonPath("_links.self.href", is(TestHelper.buildMockedLink(parentId).getHref() + "/decendants")));
    }

    @Test
    public void updateNode() throws Exception {
        // Arrange
        Long id = 2l;
        NodeDTO updateNode = TestHelper.createNodeDTO(null, 0L, null, null);
        NodeDTO updatedNode = TestHelper.createNodeDTO(2L, 1L, 0L, 2);

        when(nodeService.updateNode(eq(updatedNode.getId()), any())).thenReturn(updatedNode);

        ObjectMapper objectMapper = new ObjectMapper();

        // Act
        ResultActions result = mockMvc.perform(
                patch("/nodes/" + id)
                .content(objectMapper.writeValueAsString(updateNode))
                .accept("application/hal+json;charset=UTF-8")
                .contentType("application/hal+json;charset=UTF-8"));

        // Assert
        result.andExpect(status().isOk()).andExpect(content().contentType("application/hal+json;charset=UTF-8"));
        verifyJsonNode(result, updatedNode, "$");
        result.andExpect(jsonPath("_links.self.href", is(TestHelper.buildMockedLink(updatedNode.getId()).getHref())));
    }

    @Test
    public void updateNode_parentNotFound() throws Exception {
        // Arrange
        Long id = 2l;
        NodeDTO updateNode = TestHelper.createNodeDTO(null, 0L, null, null);
        NodeDTO updatedNode = TestHelper.createNodeDTO(2L, 1L, 0L, 2);

        when(nodeService.updateNode(any(), any())).thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));

        ObjectMapper objectMapper = new ObjectMapper();

        // Act
        ResultActions result = mockMvc.perform(
                patch("/nodes/" + id)
                .content(objectMapper.writeValueAsString(updateNode))
                .accept("application/hal+json;charset=UTF-8")
                .contentType("application/hal+json;charset=UTF-8"));

        // Assert
        result.andExpect(status().is(400));
    }

    private ResultActions verifyJsonNode(ResultActions result, NodeDTO node, String path) throws Exception {
        return result
                .andExpect(jsonPath(path + ".id", is(node.getId().intValue())))
                .andExpect(jsonPath(path + ".height", is(node.getHeight())))
                .andExpect(jsonPath(path + ".parentId", is(node.getParentId().intValue())))
                .andExpect(jsonPath(path + ".rootId", is(node.getRootId().intValue())))
                .andExpect(jsonPath(path + "._links.self.href", is(TestHelper.buildMockedLink(node.getId()).getHref())))
                .andExpect(jsonPath(path + "._links.parent.href",
                        is(TestHelper.buildMockedLink(node.getParentId()).getHref())))
                .andExpect(jsonPath(path + "._links.root.href",
                        is(TestHelper.buildMockedLink(node.getRootId()).getHref())));
    }
}