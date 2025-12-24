package dev.amymialee.icyincitement.util;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.amymialee.icyincitement.cca.BuzzsawComponent;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.BlockBreakingRenderState;
import net.minecraft.client.renderer.state.LevelRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.BlockDestructionProgress;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;

public class BuzzsawVFXClient {
    private static final Set<BlockPos> targets = new HashSet<>();
    private static BlockPos target = BlockPos.ZERO;

    public static void buzzsawOutline(MultiBufferSource.BufferSource bufferSource, PoseStack poseStack, LevelRenderState levelRenderState, CallbackInfo ci) {
        var client = Minecraft.getInstance();
        if (client.player == null || client.level == null) return;
        if (!client.player.getComponent(BuzzsawComponent.KEY).shouldLumberjack() || targets.isEmpty()) return;
        var shapes = new HashMap<BlockPos, VoxelShape>();
        for (var pos : targets) {
            var state = client.level.getBlockState(pos);
            shapes.put(pos, state.getShape(client.level, pos).move(pos.subtract(target)));
        }
        var voxelShape = Shapes.or(shapes.get(target), shapes.values().toArray(VoxelShape[]::new));
        var cameraPos = levelRenderState.cameraRenderState.pos;
        var b = (int) (BuzzsawComponent.KEY.get(client.player).getSpeed() * 205 + 50);
        var rg = b / 2;
        ShapeRenderer.renderShape(poseStack, bufferSource.getBuffer(RenderTypes.lines()), voxelShape, target.getX() - cameraPos.x, target.getY() - cameraPos.y, target.getZ() - cameraPos.z, 0xFF000000 | b | (rg << 8) | (rg << 16), 7.0F);
        ci.cancel();
    }

    public static void buzzsawDestroy(@NonNull Minecraft minecraft, ClientLevel level, @NonNull Long2ObjectMap<SortedSet<BlockDestructionProgress>> destructionProgress, @NonNull LevelRenderState levelRenderState) {
        if (minecraft.player == null || level == null || !minecraft.player.getComponent(BuzzsawComponent.KEY).shouldLumberjack()) return;
        var targetLong = target.asLong();
        if (destructionProgress.containsKey(targetLong)) {
            var value = destructionProgress.get(targetLong);
            if (value.isEmpty()) return;
            for (var target : targets) if (!destructionProgress.containsKey(target.asLong())) levelRenderState.blockBreakingRenderStates.add(new BlockBreakingRenderState(level, target, value.last().getProgress()));
        }
    }

    public static void tick(@NonNull Minecraft client) {
        if (client.level == null || client.player == null || !(client.hitResult instanceof BlockHitResult blockHitResult)) return;
        var current = blockHitResult.getBlockPos();
        if (client.player.tickCount % 20 == 0 || target != current) {
            target = current;
            if (client.player.getComponent(BuzzsawComponent.KEY).shouldLumberjack()) {
                BuzzsawComponent.fillTargetSet(client.level, current, targets);
            } else {
                targets.clear();
            }
        }
    }
}