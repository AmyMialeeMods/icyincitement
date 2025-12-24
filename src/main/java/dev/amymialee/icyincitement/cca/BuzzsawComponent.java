package dev.amymialee.icyincitement.cca;

import dev.amymialee.icyincitement.IcyIncitement;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.CommonTickingComponent;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

public class BuzzsawComponent implements AutoSyncedComponent, CommonTickingComponent {
    public static final ComponentKey<BuzzsawComponent> KEY = ComponentRegistry.getOrCreate(IcyIncitement.id("buzzsaw"), BuzzsawComponent.class);
    private final Player player;
    private float speed = 0;

    public BuzzsawComponent(Player player) {
        this.player = player;
    }

    public void sync() {
        KEY.sync(this.player);
    }

    @Override
    public void tick() {
        var held = this.player.getMainHandItem().is(IcyIncitement.BUZZSAW);
        if (held && this.player.mialib$holdingAttack()) {
            if (this.speed < 1f) {
                this.speed = Mth.lerp(0.1f, this.speed, 1f);
                if (this.speed >= 0.99f) this.setSpeed(1f);
            }
        } else {
            if (this.speed > 0f) {
                this.speed = Mth.lerp(0.125f, this.speed, 0f);
                if (this.speed <= 0.01f) this.setSpeed(0f);
            }
        }
    }

    public boolean shouldLumberjack() {
        return this.player.getMainHandItem().is(IcyIncitement.BUZZSAW) && this.player.isCrouching();
    }

    public float getSpeed() {
        return this.speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
        this.sync();
    }

    @Override
    public void readData(@NonNull ValueInput readView) {
        this.speed = readView.getFloatOr("speed", 0f);
    }

    @Override
    public void writeData(@NonNull ValueOutput writeView) {
        writeView.putDouble("speed", this.speed);
    }

    public static @NonNull Set<BlockPos> fillTargetSet(@NonNull BlockGetter level, BlockPos pos, @NonNull Set<BlockPos> targets) {
        targets.clear();
        if (!level.getBlockState(pos).is(BlockTags.LOGS)) return targets;
        var pending = new ArrayDeque<BlockPos>();
        var visited = new HashSet<BlockPos>();
        pending.add(pos);
        visited.add(pos);
        var count = 0;
        var mutable = new BlockPos.MutableBlockPos();
        while (!pending.isEmpty() && count < 96) {
            var current = pending.poll();
            count++;
            targets.add(current);
            for (var x = -1; x <= 1; x++) for (var y = -1; y <= 1; y++) for (var z = -1; z <= 1; z++) {
                if (x == 0 && y == 0 && z == 0) continue;
                mutable.setWithOffset(current, x, y, z);
                var immutable = mutable.immutable();
                if (visited.add(immutable)) {
                    var blockState = level.getBlockState(immutable);
                    if (blockState.is(BlockTags.LOGS)) pending.add(immutable);
                }
            }
        }
        return targets;
    }
}