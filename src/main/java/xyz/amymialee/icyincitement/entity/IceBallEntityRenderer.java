package xyz.amymialee.icyincitement.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.FlyingItemEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class IceBallEntityRenderer extends EntityRenderer<IceBallEntity, FlyingItemEntityRenderState> {
    private static final ItemStack SNOWBALL = Items.SNOWBALL.getDefaultStack();
    private final ItemModelManager itemModelManager;
    private final float scale;
    private final boolean lit;

    public IceBallEntityRenderer(EntityRendererFactory.Context ctx, float scale, boolean lit) {
        super(ctx);
        this.itemModelManager = ctx.getItemModelManager();
        this.scale = scale;
        this.lit = lit;
    }

    public IceBallEntityRenderer(EntityRendererFactory.Context context) {
        this(context, 1.0F, false);
    }

    @Override
    protected int getBlockLight(IceBallEntity entity, BlockPos pos) {
        return this.lit ? 15 : super.getBlockLight(entity, pos);
    }

    @Override
    public void render(@NotNull FlyingItemEntityRenderState state, @NotNull MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {
        matrixStack.push();
        matrixStack.scale(this.scale, this.scale, this.scale);
        matrixStack.multiply(this.dispatcher.getRotation());
        state.itemRenderState.render(matrixStack, vertexConsumerProvider, light, OverlayTexture.DEFAULT_UV);
        matrixStack.pop();
        super.render(state, matrixStack, vertexConsumerProvider, light);
    }

    @Override
    public FlyingItemEntityRenderState createRenderState() {
        return new FlyingItemEntityRenderState();
    }

    @Override
    public void updateRenderState(IceBallEntity entity, FlyingItemEntityRenderState flyingItemEntityRenderState, float tickDelta) {
        super.updateRenderState(entity, flyingItemEntityRenderState, tickDelta);
        this.itemModelManager.updateForNonLivingEntity(flyingItemEntityRenderState.itemRenderState, SNOWBALL, ModelTransformationMode.GROUND, entity);
    }
}