package amymialee.icyincitement;

import amymialee.icyincitement.common.items.EmptySprinklerItem;
import amymialee.icyincitement.common.items.SnowballSprinklerItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.tag.TagFactory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class IcyIncitement implements ModInitializer {
    public static String MODID = "icyincitement";
    public static Item EMPTY_SPRINKLER = new EmptySprinklerItem(new FabricItemSettings().group(ItemGroup.COMBAT).maxCount(1));
    public static Item SNOWBALL_SPRINKLER = new SnowballSprinklerItem(new FabricItemSettings().group(ItemGroup.COMBAT).maxCount(1).maxDamage(192));
    public static final Tag<Item> SNOWBALLS = TagFactory.ITEM.create(new Identifier(MODID, "snowballs"));

    @Override
    public void onInitialize() {
        Registry.register(Registry.ITEM, new Identifier(MODID, "empty_sprinkler"), EMPTY_SPRINKLER);
        Registry.register(Registry.ITEM, new Identifier(MODID, "snowball_sprinkler"), SNOWBALL_SPRINKLER);
    }
}
