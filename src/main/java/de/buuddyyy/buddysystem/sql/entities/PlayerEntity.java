package de.buuddyyy.buddysystem.sql.entities;

import de.buuddyyy.buddysystem.managers.PlayerManager;
import de.buuddyyy.buddysystem.sql.converters.UUIDConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Entity
@Table(name = PlayerManager.TABLE_NAME)
public final class PlayerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, columnDefinition = "CHAR(36)")
    @Convert(converter = UUIDConverter.class)
    private UUID playerUuid;

    @Setter
    private String playerName;

    @Setter
    private Timestamp lastOnline;

    @Getter
    private boolean autoChestLock = false;

    public PlayerEntity(UUID playerUuid, String playerName) {
        this.playerUuid = playerUuid;
        this.playerName = playerName;
    }

    public PlayerEntity() {
    }

    public void toggleAutoChestLock() {
        this.autoChestLock = !this.autoChestLock;
    }

}
