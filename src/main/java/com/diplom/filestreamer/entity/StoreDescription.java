package com.diplom.filestreamer.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Data
@Builder
@Table(name = "store_descriptions")
@NoArgsConstructor
@AllArgsConstructor
public class StoreDescription {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private String id;
    private String fileId;
    private long endByte;
    private long startByte;
    private String fileName;
    private String mediaType;
    private long contentLength;
    @Column(nullable = false)
    private String source;

    @Column(nullable = false)
    private Instant creationDate;
}
