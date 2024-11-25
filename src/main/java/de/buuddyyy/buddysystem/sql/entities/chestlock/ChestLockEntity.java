package de.buuddyyy.buddysystem.sql.entities.chestlock;

import de.buuddyyy.buddysystem.managers.ChestLockManager;
import de.buuddyyy.buddysystem.sql.entities.PlayerEntity;
import jakarta.persistence.*;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = ChestLockManager.TABLE_NAME_CHEST_LOCKS, uniqueConstraints = {
        @UniqueConstraint(columnNames = {"worldName", "blockX", "blockY", "blockZ"})
})
public final class ChestLockEntity {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Getter
    @JoinColumn(name = "ownerId", referencedColumnName = "id")
    @ManyToOne(targetEntity = PlayerEntity.class, optional = false)
    private PlayerEntity ownerEntity;

    private String worldName;
    private int blockX;
    private int blockY;
    private int blockZ;

    @Getter
    @JoinColumn(name = "id", referencedColumnName = "chestLockId")
    @OneToMany(targetEntity = ChestLockTrustedPlayerEntity.class, cascade = {CascadeType.REMOVE})
    private List<ChestLockTrustedPlayerEntity> trustedPlayers;

    public ChestLockEntity(PlayerEntity playerEntity, Location location) {
        super();
        this.ownerEntity = playerEntity;
        this.worldName = location.getWorld().getName();
        this.blockX = location.getBlockX();
        this.blockY = location.getBlockY();
        this.blockZ = location.getBlockZ();
    }

    public ChestLockEntity() {
        this.trustedPlayers = new ArrayList<>();
    }

    public Location getLocation() {
        final var w = Bukkit.getWorld(this.worldName);
        return new Location(w, this.blockX, this.blockY, this.blockZ);
    }

    public boolean isOwner(Player player) {
        return player.getUniqueId().equals(ownerEntity.getPlayerUuid());
    }

    public boolean isTrusted(Player player) {
        if (this.isOwner(player)) {
            return true;
        }
        for (ChestLockTrustedPlayerEntity trustedPlayer : trustedPlayers) {
            if (player.getUniqueId().equals(trustedPlayer.getTrustedPlayer().getPlayerUuid())) {
                return true;
            }
        }
        return false;
    }

}
