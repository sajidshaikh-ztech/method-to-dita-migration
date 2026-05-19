package com.ey.method.migration.parser;

import jakarta.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class TaskXml {
    @XmlAttribute(name = "id")
    private String id;
    @XmlAttribute(name = "name")
    private String name;
    @XmlAttribute(name = "brief-description")
    private String briefDescription;
    @XmlAttribute(name = "variabilityBasedOnElement")
    private String variabilityBasedOn;

    @XmlElementWrapper(name = "method-roles")
    @XmlElement(name = "method-role-id")
    private List<String> roleIds;

    @XmlElementWrapper(name = "add-method-roles")
    @XmlElement(name = "method-role-id")
    private List<String> additionalRoleIds;

    @XmlElementWrapper(name = "input-work-products")
    @XmlElement(name = "wp-id")
    private List<String> inputWorkProductIds;

    @XmlElementWrapper(name = "output-work-products")
    @XmlElement(name = "wp-id")
    private List<String> outputWorkProductIds;

    @XmlElementWrapper(name = "guidances")
    @XmlElement(name = "guidance-id")
    private List<String> guidanceIds;

    @XmlAttribute(name = "method-link")
    private String methodLink;

    public String getId() { return id; }
    public String getName() { return name; }
    public String getBriefDescription() { return briefDescription; }
    public String getMethodLink() { return methodLink; }
    public String getVariabilityBasedOn() { return variabilityBasedOn; }
    public List<String> getRoleIds() { return roleIds; }
    public List<String> getAdditionalRoleIds() { return additionalRoleIds; }
    public List<String> getInputWorkProductIds() { return inputWorkProductIds; }
    public List<String> getOutputWorkProductIds() { return outputWorkProductIds; }
    public List<String> getGuidanceIds() { return guidanceIds; }
}
