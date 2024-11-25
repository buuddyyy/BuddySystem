package de.buuddyyy.buddysystem.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import de.buuddyyy.buddysystem.BuddySystemPlugin;

public class ActionBarProtocolAdapter extends PacketAdapter {

    public ActionBarProtocolAdapter(BuddySystemPlugin plugin) {
        super(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.SET_ACTION_BAR_TEXT);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        final var packet = event.getPacket();
        final var actionBarString = packet.getStrings().read(0);
        System.out.println(actionBarString);
    }

}
