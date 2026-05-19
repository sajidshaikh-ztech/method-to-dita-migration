package com.ey.method.migration.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Context")
public class Context {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "contextId", length = 36)
    private String contextId;

    public String getContextId() { return contextId; }
    public void setContextId(String contextId) { this.contextId = contextId; }
}
