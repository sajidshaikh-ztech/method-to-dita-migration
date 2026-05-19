package com.ey.method.migration.parser;

import jakarta.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class GuidanceXml {
    @XmlAttribute(name = "id")
    private String id;
    @XmlAttribute(name = "name")
    private String name;
    @XmlAttribute(name = "type")
    private String type;
    @XmlAttribute(name = "brief-description")
    private String briefDescription;
    @XmlAttribute(name = "method-link")
    private String methodLink;

    @XmlElementWrapper(name = "guidances")
    @XmlElement(name = "guidance-id")
    private List<String> relatedGuidanceIds;

    public String getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getBriefDescription() { return briefDescription; }
    public String getMethodLink() { return methodLink; }
    public List<String> getRelatedGuidanceIds() { return relatedGuidanceIds; }
}
