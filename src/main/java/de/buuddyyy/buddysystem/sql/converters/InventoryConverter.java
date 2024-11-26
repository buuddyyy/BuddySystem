package de.buuddyyy.buddysystem.sql.converters;

import jakarta.persistence.AttributeConverter;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class InventoryConverter implements AttributeConverter<Inventory, String> {

    @Override
    public String convertToDatabaseColumn(Inventory inventory) {
        try {
            final var outStream = new ByteArrayOutputStream();
            final var dataOutput = new BukkitObjectOutputStream(outStream);
            int size = inventory.getSize();
            dataOutput.writeInt(size);
            for (int i = 0; i < size; i++) {
                dataOutput.writeObject(inventory.getItem(i));
            }
            dataOutput.close();
            return Base64.getEncoder().encodeToString(outStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Inventory convertToEntityAttribute(String s) {
        try {
            byte[] b = Base64.getDecoder().decode(s);
            final var inStream = new ByteArrayInputStream(b);
            final var dataInput = new BukkitObjectInputStream(inStream);
            int size = dataInput.readInt();
            var inv = Bukkit.createInventory(null, size);
            for (int i = 0; i < size; i++) {
                inv.setItem(i, (ItemStack) dataInput.readObject());
            }
            dataInput.close();
            return inv;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
