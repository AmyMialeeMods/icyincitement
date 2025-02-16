package xyz.amymialee.icyincitement.cca;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.CommonTickingComponent;
import xyz.amymialee.icyincitement.IcyIncitement;

public class FrozenComponent implements AutoSyncedComponent, CommonTickingComponent {
    public static final ComponentKey<FrozenComponent> KEY = ComponentRegistry.getOrCreate(IcyIncitement.id("frozen"), FrozenComponent.class);
    private final LivingEntity entity;
    private int frost = 0;

    public FrozenComponent(LivingEntity entity) {
        this.entity = entity;
    }

    public void sync() {
        KEY.sync(this.entity);
    }

    @Override
    public void tick() {
        if (this.frost > 0) {
            this.frost--;
            var sync = this.frost <= 0;
            if (sync) this.sync();
        }
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        this.frost = tag.getInt("frost");
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putInt("frost", this.frost);
    }
}