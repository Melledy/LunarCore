package emu.lunarcore.game.mail;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bson.types.ObjectId;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;
import emu.lunarcore.LunarCore;
import emu.lunarcore.game.inventory.GameItem;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.ClientMailOuterClass.ClientMail;
import emu.lunarcore.proto.ItemListOuterClass.ItemList;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity(value = "mail", useDiscriminator = false)
public class Mail {
    @Id private ObjectId id;
    @Indexed private int ownerUid; // Uid of player that this mail belongs to
    
    @Setter
    private transient int uniqueId;
    
    private String title;
    private String sender;
    private String content;
    private long time;
    private long expiry;
    private boolean isRead;
    private List<GameItem> attachments;
    
    @Deprecated // Morphia only!
    public Mail() {}
    
    public Mail(String title, String sender, String content) {
        this.title = title;
        this.sender = sender;
        this.content = content;
        this.time = System.currentTimeMillis() / 1000;
        this.expiry = this.time + TimeUnit.DAYS.toSeconds(30);
    }
    
    public void setOwner(Player player) {
        this.ownerUid = player.getUid();
    }
    
    public void setRead() {
        if (!this.isRead) {
            this.isRead = true;
            this.save();
        }
    }
    
    public void addAttachment(GameItem item) {
        if (this.attachments == null) {
            this.attachments = new ArrayList<>();
        }
        this.attachments.add(item);
    }
    
    // Database
    
    public void save() {
        LunarCore.getGameDatabase().save(this);
    }
    
    public void delete() {
        LunarCore.getGameDatabase().delete(this);
    }
    
    // Proto
    
    public ClientMail toProto() {
        var proto = ClientMail.newInstance()
                .setId(this.getUniqueId())
                .setTitle(this.getTitle())
                .setContent(this.getContent())
                .setSender(this.getSender())
                .setTime(this.getTime())
                .setExpireTime(this.getExpiry())
                .setIsRead(this.isRead());
        
        // Add attachments
        ItemList list = ItemList.newInstance();
        
        if (this.attachments != null) {
            this.attachments.stream().map(GameItem::toProto).forEach(list::addItemList);
        }
        
        proto.setAttachment(list);
        
        return proto;
    }
}