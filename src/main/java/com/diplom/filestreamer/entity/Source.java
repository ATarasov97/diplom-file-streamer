package com.diplom.filestreamer.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Builder
@Table(name = "sources")
@NoArgsConstructor
@AllArgsConstructor
public class Source {
    @Id
    String id;
    long maxCacheAmount;
}
