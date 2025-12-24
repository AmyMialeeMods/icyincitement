package dev.amymialee.icyincitement.cca;

import dev.amymialee.icyincitement.IcyIncitement;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.CommonTickingComponent;

public class SnowComponent implements AutoSyncedComponent, CommonTickingComponent {
    public static final ComponentKey<SnowComponent> KEY = ComponentRegistry.getOrCreate(IcyIncitement.id("snow"), SnowComponent.class);
    private final Player player;
    private float charge = 0;

    public SnowComponent(Player player) {
        this.player = player;
    }

    public void sync() {
        KEY.sync(this.player);
    }

    @Override
    public void tick() {
        if (!(this.charge < 1f)) return;
        this.charge += 1f / (IcyIncitement.MAX_SNOWBALLS.get() / IcyIncitement.RECHARGE_RATE.get());
        if (this.charge >= 1f) {
            this.charge = 1f;
            this.sync();
        }
    }

    public boolean hasCharge() {
        return this.charge > 1f / IcyIncitement.MAX_SNOWBALLS.get();
    }

    public float getCharge() {
        return this.charge;
    }

    public void subtractCharge() {
        this.charge -= 1f / IcyIncitement.MAX_SNOWBALLS.get();
        if (this.charge < 0f) this.charge = 0f;
        this.sync();
    }

    @Override
    public void readData(@NonNull ValueInput readView) {
        this.charge = readView.getFloatOr("charge", 0f);
    }

    @Override
    public void writeData(@NonNull ValueOutput writeView) {
        writeView.putDouble("charge", this.charge);
    }
}