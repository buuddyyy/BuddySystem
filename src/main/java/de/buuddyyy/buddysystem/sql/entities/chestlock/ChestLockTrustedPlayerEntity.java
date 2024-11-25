package de.buuddyyy.buddysystem.sql.entities.chestlock;

import de.buuddyyy.buddysystem.managers.ChestLockManager;
import de.buuddyyy.buddysystem.sql.entities.PlayerEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = ChestLockManager.TABLE_NAME_TRUSTED_PLAYERS, uniqueConstraints = {
        @UniqueConstraint(columnNames = {"playerId", "chestLockId"})
})
public final class ChestLockTrustedPlayerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Getter
    @JoinColumn(name = "playerId", referencedColumnName = "id")
    @ManyToOne(targetEntity = PlayerEntity.class, optional = false)
    private PlayerEntity trustedPlayer;

    @Getter
    @JoinColumn(name = "chestLockId", referencedColumnName = "id")
    @ManyToOne(targetEntity = ChestLockEntity.class, optional = false)
    private ChestLockEntity chestEntity;

    public ChestLockTrustedPlayerEntity(PlayerEntity trustedPlayer, ChestLockEntity chestEntity) {
        this.trustedPlayer = trustedPlayer;
        this.chestEntity = chestEntity;
    }

    public ChestLockTrustedPlayerEntity() {
    }

}
