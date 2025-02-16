package xyz.amymialee.icyincitement.cca;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.property.numeric.NumericProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.CommonTickingComponent;
import xyz.amymialee.icyincitement.IcyIncitement;

public class SnowComponent implements AutoSyncedComponent, CommonTickingComponent {
    public static final ComponentKey<SnowComponent> KEY = ComponentRegistry.getOrCreate(IcyIncitement.id("snow"), SnowComponent.class);
    private final PlayerEntity player;
    private double charge = 0;

    public SnowComponent(PlayerEntity player) {
        this.player = player;
    }

    public void sync() {
        KEY.sync(this.player);
    }

    @Override
    public void tick() {
        if (this.charge < IcyIncitement.MAX_SNOWBALLS.get()) {
            this.charge += IcyIncitement.RECHARGE_RATE.get();
            if (this.charge >= IcyIncitement.MAX_SNOWBALLS.get()) {
                this.sync();
            }
        }
    }

    public boolean hasCharge() {
        return this.charge > 0;
    }

    public float getCharge() {
        return (float) this.charge / IcyIncitement.MAX_SNOWBALLS.get();
    }

    public void subtractCharge() {
        this.charge--;
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        this.charge = tag.getDouble("charge");
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putDouble("charge", this.charge);
    }

    @Environment(EnvType.CLIENT)
    public record SnowProperty() implements NumericProperty {
        public static final MapCodec<SnowProperty> CODEC = MapCodec.unit(new SnowProperty());

        @Override
        public float getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity holder, int seed) {
            if (holder instanceof PlayerEntity player) return KEY.get(player).getCharge();
            return 0.0F;
        }

        @Override
        public MapCodec<SnowProperty> getCodec() {
            return CODEC;
        }
    }
}