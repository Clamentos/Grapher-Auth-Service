package io.github.clamentos.grapher.auth.persistence.entities;

///
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

///.
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

///
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Entity @Table(name = "AUDIT")

///
public class Audit {

    ///
    @Id @Column(name = "id")
    @GeneratedValue(generator = "audit_id_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "audit_id_seq", sequenceName = "audit_id_seq", allocationSize = 1)
    private long id;

    @Column(name = "record_id")
    private long recordId;

    @Column(name = "table_name")
    private String tableName;

    @Column(name = "columns")
    private String columns;

    @Column(name = "action")
    private char action;

    @Column(name = "created_at")
    private long createdAt;

    @Column(name = "created_by")
    private String createdBy;

    ///
}
