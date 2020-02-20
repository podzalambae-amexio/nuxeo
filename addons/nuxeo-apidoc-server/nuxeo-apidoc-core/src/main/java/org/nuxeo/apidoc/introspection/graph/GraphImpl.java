/*
 * (C) Copyright 2020 Nuxeo (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Anahide Tchertchian
 */
package org.nuxeo.apidoc.introspection.graph;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.apidoc.api.BaseNuxeoArtifact;
import org.nuxeo.apidoc.api.graph.Edge;
import org.nuxeo.apidoc.api.graph.Graph;
import org.nuxeo.apidoc.api.graph.Node;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.impl.blob.StringBlob;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * @since 11.1
 */
public class GraphImpl extends BaseNuxeoArtifact implements Graph {

    protected final String id;

    protected final List<NodeImpl> nodes = new ArrayList<>();

    protected final List<EdgeImpl> edges = new ArrayList<>();

    @JsonCreator
    public GraphImpl(@JsonProperty("id") String id) {
        super();
        this.id = id;
    }

    @Override
    @JsonIgnore
    public String getVersion() {
        return null;
    }

    @Override
    public String getArtifactType() {
        return ARTIFACT_TYPE;
    }

    @Override
    @JsonIgnore
    public String getHierarchyPath() {
        return null;
    }

    @Override
    @JsonIgnore
    public String getName() {
        return getId();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    @JsonIgnore
    public String getContent() {
        final ObjectMapper mapper = new ObjectMapper().registerModule(
                new SimpleModule().addAbstractTypeMapping(Node.class, NodeImpl.class)
                                  .addAbstractTypeMapping(Edge.class, EdgeImpl.class));
        List<Object> values = new ArrayList<>();
        values.addAll(nodes);
        values.addAll(edges);
        try {
            return mapper.writerFor(ArrayList.class)
                         .with(JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM)
                         .without(JsonGenerator.Feature.AUTO_CLOSE_TARGET)
                         .withDefaultPrettyPrinter()
                         .writeValueAsString(values);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @JsonIgnore
    public Blob getBlob() {
        return new StringBlob(getContent());
    }

    public void addNode(NodeImpl node) {
        this.nodes.add(node);
    }

    public void addEdge(EdgeImpl edge) {
        this.edges.add(edge);
    }

}
