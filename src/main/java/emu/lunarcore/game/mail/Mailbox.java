package emu.lunarcore.game.mail;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import emu.lunarcore.LunarCore;
import emu.lunarcore.data.common.ItemParam;
import emu.lunarcore.game.inventory.GameItem;
import emu.lunarcore.game.player.BasePlayerManager;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.server.packet.send.PacketNewMailScNotify;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import lombok.AccessLevel;
import lombok.Getter;
import us.hebi.quickbuf.RepeatedInt;

@Getter(AccessLevel.PRIVATE)
public class Mailbox extends BasePlayerManager implements Iterable<Mail> {
    private final Int2ObjectMap<Mail> map;
    private int lastMailId;
    
    public Mailbox(Player player) {
        super(player);
        
        this.map = new Int2ObjectOpenHashMap<>();
    }
    
    private int getNextMailId() {
        return ++lastMailId;
    }

    public synchronized int size() {
        return getMap().size();
    }
    
    public synchronized void readMail(int id) {
        Mail mail = getMap().get(id);
        if (mail != null) {
            mail.setRead();
        }
    }
    
    public synchronized void sendMail(Mail mail) {
        // Set owner for mail first before we save
        mail.setOwner(this.getPlayer());
        
        // Put mail into our backing hash map
        this.putMail(mail);

        // Save mail to database
        mail.save();
            
        // Send packet
        this.getPlayer().sendPacket(new PacketNewMailScNotify(mail));
    }

    public synchronized List<Mail> takeMailAttachments(RepeatedInt idList) {
        List<Mail> attachments = new ArrayList<>();
        
        if (idList.length() == 0) {
            this.getMap().keySet().forEach(idList::add);
        }
        
        for (int id : idList) {
            // Get mail from hash map
            Mail mail = getMap().get(id);
            if (mail == null || mail.isRead() || mail.getAttachments() == null) {
                continue;
            }
            
            // Add attachments to inventory
            for (GameItem item : mail.getAttachments()) {
                getPlayer().getInventory().addItem(item);
            }
            
            // Set read
            mail.setRead();
            
            //
            attachments.add(mail);
        }
        
        return attachments;
    }
    
    public synchronized IntList deleteMail(RepeatedInt idList) {
        IntList deleteList = new IntArrayList();
        
        if (idList.length() == 0) {
            this.getMap().keySet().forEach(idList::add);
        }
        
        for (int id : idList) {
            // Get mail from hash map
            Mail mail = getMap().get(id);
            if (mail == null || !mail.isRead()) {
                continue;
            }
            
            // Remove
            getMap().remove(id);
            
            // Delete from database
            mail.delete();
            
            // Add to delete result list
            deleteList.add(mail.getUniqueId());
        }
        
        return deleteList;
    }
    
    public void sendWelcomeMail() {
        var welcomeMail = LunarCore.getConfig().getServerOptions().welcomeMail;
        if (welcomeMail == null) return;
        
        Mail mail = new Mail(welcomeMail.getTitle(), welcomeMail.getSender(), welcomeMail.getContent());
        
        for (ItemParam param : welcomeMail.getAttachments()) {
            mail.addAttachment(new GameItem(param.getId(), param.getCount()));
        }
        
        this.sendMail(mail);
    }
    
    // Internal method to put mail into the hash map
    private void putMail(Mail mail) {
        mail.setUniqueId(this.getNextMailId());
        getMap().put(mail.getUniqueId(), mail);
    }
    
    @Override
    public synchronized Iterator<Mail> iterator() {
        return getMap().values().iterator();
    }
    
    // Database

    public void loadFromDatabase() {
        Stream<Mail> stream = LunarCore.getGameDatabase().getObjects(Mail.class, "ownerUid", this.getPlayer().getUid());
        
        stream.forEach(this::putMail);
    }

}
