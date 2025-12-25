package dev.amymialee.icyincitement.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.amymialee.icyincitement.IcyIncitement;
import dev.amymialee.icyincitement.entities.SawbladeEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.util.Mth;
import org.jspecify.annotations.NonNull;

public class SawbladeRenderer extends EntityRenderer<SawbladeEntity, SawbladeRenderState> {
    public SawbladeRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public void submit(SawbladeRenderState sawblade, @NonNull PoseStack poseStack, @NonNull SubmitNodeCollector submitNodeCollector, @NonNull CameraRenderState cameraRenderState) {
        super.submit(sawblade, poseStack, submitNodeCollector, cameraRenderState);
        if (sawblade.ageInTicks < 2 && cameraRenderState.pos.distanceToSqr(sawblade.getPos()) < Mth.square(3.5F)) return;
        poseStack.pushPose();
        poseStack.mulPose(sawblade.rotation);
        submitNodeCollector.submitCustomGeometry(
                poseStack,
                RenderTypes.itemEntityTranslucentCull(IcyIncitement.id("textures/item/buzzblade_big.png")),
                (pose, vertexConsumer) -> {
                    vertexConsumer.addVertex(pose, (float) sawblade.x, (float) sawblade.y, (float) sawblade.z).setUv(0, 1);
                    vertexConsumer.addVertex(pose, (float) sawblade.x, (float) sawblade.y, (float) sawblade.z).setUv(0, 1);
                    vertexConsumer.addVertex(pose, (float) sawblade.x, (float) sawblade.y, (float) sawblade.z).setUv(0, 1);
                    vertexConsumer.addVertex(pose, (float) sawblade.x, (float) sawblade.y, (float) sawblade.z).setUv(0, 1);
                    vertexConsumer.addVertex(pose, (float) sawblade.x, (float) sawblade.y, (float) sawblade.z).setUv(0, 1);
                }
        );
        poseStack.popPose();
    }

    @Override
    public SawbladeRenderState createRenderState() {
        return new SawbladeRenderState();
    }

    @Override
    public void extractRenderState(SawbladeEntity sawblade, SawbladeRenderState sawbladeRenderState, float f) {
        super.extractRenderState(sawblade, sawbladeRenderState, f);
        sawbladeRenderState.rotation = sawblade.prevRotation.nlerp(sawblade.rotation, f);
        sawbladeRenderState.yRot = sawblade.getYRot(f);
        sawbladeRenderState.xRot = sawblade.getXRot(f);
    }

    @Override
    public boolean shouldRender(SawbladeEntity entity, @NonNull Frustum frustum, double d, double e, double f) {
        return true;
    }
}