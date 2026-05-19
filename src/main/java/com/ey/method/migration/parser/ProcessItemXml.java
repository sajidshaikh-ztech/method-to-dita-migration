package com.ey.method.migration.parser;

import jakarta.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class ProcessItemXml {
    @XmlAttribute(name = "id")
    private String id;
    @XmlAttribute(name = "name")
    private String name;
    @XmlAttribute(name = "type")
    private String type;
    @XmlAttribute(name = "brief-description")
    private String briefDescription;
    @XmlAttribute(name = "related-task")
    private String relatedTask;
    @XmlAttribute(name = "index")
    private Integer index;
    @XmlAttribute(name = "method-link")
    private String methodLink;

    @XmlElementWrapper(name = "process-items")
    @XmlElement(name = "process-item")
    private List<ProcessItemXml> children = new ArrayList<>();

    public String getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getBriefDescription() { return briefDescription; }
    public String getRelatedTask() { return relatedTask; }
    public Integer getIndex() { return index; }
    public String getMethodLink() { return methodLink; }
    public List<ProcessItemXml> getChildren() { return children; }
}
