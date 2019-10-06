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
import com.amazingco.persistence.Node;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

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
        Node root = TestHelper.createNode(0L, null, null, 0);
        Node parent = TestHelper.createNode(1L, root, root, 1);
        Node child1 = TestHelper.createNode(2L, parent, root, 2);
        Node child2 = TestHelper.createNode(3L, parent, root, 2);

        when(nodeService.getAllDecendants(parent.getId())).thenReturn(Arrays.asList(child1, child2));

        // Act
        ResultActions result = mockMvc.perform(get("/nodes/" + parent.getId() + "/decendants"));

        // Assert
        result.andExpect(status().isOk()).andExpect(content().contentType("application/hal+json;charset=UTF-8"))
                .andExpect(jsonPath("_embedded.nodes", hasSize(2)));
        verifyJsonNode(result, child1, "_embedded.nodes[0]");
        verifyJsonNode(result, child2, "_embedded.nodes[1]");
        result.andExpect(
                jsonPath("_links.self.href", is(TestHelper.buildMockedLink(parent.getId()).getHref() + "/decendants")));
    }

    @Test
    public void getAllDecendants_noDecendants() throws Exception {
        // Arrange
        Node root = TestHelper.createNode(0L, null, null, 0);
        Node parent = TestHelper.createNode(1L, root, root, 1);

        when(nodeService.getAllDecendants(parent.getId())).thenReturn(Lists.emptyList());

        // Act
        ResultActions result = mockMvc.perform(get("/nodes/" + parent.getId() + "/decendants"));

        // Assert
        result.andExpect(status().isOk()).andExpect(content().contentType("application/hal+json;charset=UTF-8"))
                .andExpect(jsonPath("_embedded").doesNotExist());
        result.andExpect(
                jsonPath("_links.self.href", is(TestHelper.buildMockedLink(parent.getId()).getHref() + "/decendants")));
    }

    private ResultActions verifyJsonNode(ResultActions result, Node node, String path) throws Exception {
        return result.andExpect(jsonPath(path + ".height", is(node.getHeight())))
                .andExpect(jsonPath(path + "._links.self.href", is(TestHelper.buildMockedLink(node.getId()).getHref())))
                .andExpect(jsonPath(path + "._links.parent.href",
                        is(TestHelper.buildMockedLink(node.getParent().getId()).getHref())))
                .andExpect(jsonPath(path + "._links.root.href",
                        is(TestHelper.buildMockedLink(node.getRoot().getId()).getHref())));
    }

    private class NodeBody {
        private Node parent;

        public NodeBody(Node parent) {
            this.parent = parent;
        }

        public Node getParent() {
            return parent;
        }

        public String toJson() throws Exception {
            ObjectMapper objectMapper = new ObjectMapper();
            System.out.println("HALLULJA: "+objectMapper.writeValueAsString(this));
            return objectMapper.writeValueAsString(this);
        }
    }

}