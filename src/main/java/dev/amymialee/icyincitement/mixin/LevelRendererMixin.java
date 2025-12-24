package dev.amymialee.icyincitement.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.amymialee.icyincitement.util.BuzzsawVFXClient;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.state.LevelRenderState;
import net.minecraft.server.level.BlockDestructionProgress;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.SortedSet;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Shadow @Final private Long2ObjectMap<SortedSet<BlockDestructionProgress>> destructionProgress;
    @Shadow private @Nullable ClientLevel level;
    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "renderBlockOutline", at = @At("HEAD"), cancellable = true)
    private void icyincitement$buzzsawOutline(MultiBufferSource.BufferSource bufferSource, PoseStack poseStack, boolean bl, LevelRenderState levelRenderState, CallbackInfo ci) {
        BuzzsawVFXClient.buzzsawOutline(bufferSource, poseStack, levelRenderState, ci);
    }

    @Inject(method = "extractBlockDestroyAnimation", at = @At("TAIL"))
    private void icyincitement$buzzsawDestroy(Camera camera, LevelRenderState levelRenderState, CallbackInfo ci) {
        BuzzsawVFXClient.buzzsawDestroy(this.minecraft, this.level, this.destructionProgress, levelRenderState);
    }
}