package com.sopotek.aipower.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = "roleId")
@ToString(of = "roleId")
@Entity
@Table(name = "roles", indexes = {@Index(name = "idx_role_id", columnList = "role_id")})
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")  // Ensure the column name matches in the database
    private Long roleId;

    private String roleName;


}
