package com.talaty.model;

import com.talaty.enums.Role;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Admin extends User {

    public Admin() {
        this.setRole(Role.ADMIN);
    }

}
