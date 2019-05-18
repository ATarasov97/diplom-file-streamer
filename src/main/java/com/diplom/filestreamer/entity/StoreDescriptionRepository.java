package com.diplom.filestreamer.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StoreDescriptionRepository extends JpaRepository<StoreDescription, String> {
    List<StoreDescription> findAllByFileId(String fileId);

    List<StoreDescription> findAllByFileIdAndFileNameIsNotNull(String fileId);

    Optional<StoreDescription> findFirstBySourceOrderByCreationDate(String source);

    @Query("select sum(s.endByte - s.startByte) from StoreDescription  s where s.source=:source")
    Optional<Long> calculateFilledSpace(@Param("source") String source);

    Optional<StoreDescription> findFirstByStartByteLessThanEqualAndEndByteGreaterThanEqual(long start, long end);

    Optional<StoreDescription> findFirstByStartByteGreaterThanEqualAndEndByteLessThanEqual(long start, long end);

//    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    @QueryHints(@QueryHint(name = "javax.persistence.lock.timeout", value = "1"))
//    StoreDescription findAndLockById(String id);
//
//    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    @QueryHints(@QueryHint(name = "javax.persistence.lock.timeout", value = "0"))
//    StoreDescription findAndWaitLockById(String id);
}
