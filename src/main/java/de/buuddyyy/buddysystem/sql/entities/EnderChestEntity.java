package de.buuddyyy.buddysystem.sql.entities;

import de.buuddyyy.buddysystem.managers.EnderChestManager;
import de.buuddyyy.buddysystem.sql.Mergeable;
import de.buuddyyy.buddysystem.sql.converters.InventoryConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.Inventory;

@Getter
@Entity
@Table(name = EnderChestManager.TABLE_NAME, uniqueConstraints = {
        @UniqueConstraint(columnNames = {"playerId"})
})
public class EnderChestEntity implements Mergeable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JoinColumn(name = "playerId", referencedColumnName = "id")
    @ManyToOne(targetEntity = PlayerEntity.class, optional = false)
    private PlayerEntity playerEntity;

    @Setter
    @Column(unique = true, columnDefinition = "TEXT")
    @Convert(converter = InventoryConverter.class)
    private Inventory inventory;

    public EnderChestEntity(PlayerEntity playerEntity, Inventory inventory) {
        this.playerEntity = playerEntity;
        this.inventory = inventory;
    }

    public EnderChestEntity() {
    }

    @Override
    public void toMerge(Mergeable mergeObj) {
        var fromObj = (EnderChestEntity) mergeObj;
        this.inventory = fromObj.getInventory();
    }

}
