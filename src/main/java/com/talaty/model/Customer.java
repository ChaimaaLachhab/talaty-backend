package com.talaty.model;

import com.talaty.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Customer extends User {

    public Customer() {
        this.setRole(Role.USER);
    }

}
