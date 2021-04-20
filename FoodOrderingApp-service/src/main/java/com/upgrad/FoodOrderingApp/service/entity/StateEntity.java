package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;
import javax.validation.constraints.Size;

@NamedQueries({
        @NamedQuery(name = "getAllStates", query = "select s from StateEntity s"),
        @NamedQuery(name = "getStateById", query = "select s from StateEntity s where s.uuid=:stateuuid")
})

@Entity
@Table(name = "state")
public class StateEntity {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "UUID")
    @Size(max = 200)
    private String uuid;

    @Column(name = "STATE_NAME")
    @Size(max = 30)
    private String state_name;

//    public StateEntity(String stateUuid, String stateName) {
//        this.uuid = UUID.fromString(stateUuid);
//        this.state_name = stateName;
//    }

    public StateEntity(){}



    public StateEntity(String stateUuid, String state) {
        this.uuid = stateUuid;
        this.state_name = state;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getState_name() {
        return state_name;
    }

    public void setState_name(String state_name) {
        this.state_name = state_name;
    }
}
