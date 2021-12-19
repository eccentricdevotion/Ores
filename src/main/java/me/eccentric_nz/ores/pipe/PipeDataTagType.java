package me.eccentric_nz.ores.pipe;

import me.eccentric_nz.ores.Ores;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PipeDataTagType implements PersistentDataType<PersistentDataContainer, PipeData> {

    private static final UUIDDataType UUID_TAG_TYPE = new UUIDDataType();
    private Ores plugin;

    public PipeDataTagType(Ores plugin) {
        this.plugin = plugin;
    }

    @Override
    public Class<PersistentDataContainer> getPrimitiveType() {
        return PersistentDataContainer.class;
    }

    @Override
    public Class<PipeData> getComplexType() {
        return PipeData.class;
    }

    @Override
    public PersistentDataContainer toPrimitive(PipeData data, PersistentDataAdapterContext persistentDataAdapterContext) {
        PersistentDataContainer persistentDataContainer = persistentDataAdapterContext.newPersistentDataContainer();
        persistentDataContainer.set(key("uuid"), UUID_TAG_TYPE, data.getUuid());
        persistentDataContainer.set(key("shape"), PersistentDataType.STRING, data.getShape().toString());
//        persistentDataContainer.set(key("customtrail"), PersistentDataType.STRING, data.getCustomTrails() == null ? "undefined" : PipeData.getCustomTrails());
//        persistentDataContainer.set(key("rounds"), PersistentDataType.INTEGER, data.getRounds());
//        persistentDataContainer.set(key("boltpulled"), PersistentDataType.BYTE, data.isBoltpulled() ? (byte) 1 : 0);
//        persistentDataContainer.set(key("firemode"), PersistentDataType.INTEGER, data.getFireMode());
//        writeAttachments(persistentDataContainer, PipeData.getAttachments());
        return persistentDataContainer;
    }

    @Override
    public PipeData fromPrimitive(PersistentDataContainer persistentDataContainer, PersistentDataAdapterContext persistentDataAdapterContext) {
        PipeData data = new PipeData();
        data.setUuid(persistentDataContainer.get(key("uuid"), UUID_TAG_TYPE));
        data.setShape(persistentDataContainer.get(key("shape"), PersistentDataType.STRING));
//        data.setCustomTrails(persistentDataContainer.get(key("customtrail"), PersistentDataType.STRING));
//        data.setRounds(getOrDefault(persistentDataContainer.get(key("rounds"), PersistentDataType.INTEGER), 0));
//        data.setBoltpulled(fromByte(persistentDataContainer.get(key("boltpulled"), PersistentDataType.BYTE), false));
//        data.setFireMode(getOrDefault(persistentDataContainer.get(key("firemode"), PersistentDataType.INTEGER), 0));
//        PipeData.setAttachments(readAttachements(persistentDataContainer));
        return data;
    }

//    private void writeAttachments(PersistentDataContainer customItemTagContainer, Map<String, String> attachments) {
//        List<String> concatedAttachements = new ArrayList<>();
//        for (Map.Entry<String, String> attachment : attachments.entrySet()) {
//            concatedAttachements.add(attachment.getKey() + "," + attachment.getValue());
//        }
//        customItemTagContainer.set(key("weaponattachments"), PersistentDataType.STRING, String.join(";", concatedAttachements));
//    }
//
//    private Map<String, String> readAttachements(PersistentDataContainer persistentDataContainer) {
//        Map<String, String> list = new HashMap<>();
//        if (persistentDataContainer.has(key("weaponattachments"), PersistentDataType.STRING)) {
//            String attachments = persistentDataContainer.get(key("weaponattachments"), PersistentDataType.STRING);
//            String[] attachmentList = attachments.split(";");
//            for (String singleAttachement : attachmentList) {
//                String[] slit = singleAttachement.split(",");
//                if (slit.length == 2) {
//                    list.put(slit[0], slit[1]);
//                }
//            }
//        }
//        return list;
//    }

    private NamespacedKey key(String key) {
        return new NamespacedKey(plugin, key);
    }

    private int getOrDefault(Integer integer, int defaultValue) {
        return integer != null ? integer : defaultValue;
    }

    private boolean fromByte(Byte byteValue, boolean defaultValue) {
        return byteValue != null ? byteValue == 1 : defaultValue;
    }
}
