package amymialee.icyincitement.mixin;

import amymialee.icyincitement.items.SnowballSprinklerItem;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrawContext.class)
public abstract class DrawContextMixin {
    @Inject(method = "drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At("TAIL"))
    public void drawItemInSlot(TextRenderer renderer, ItemStack stack, int x, int y, String countLabel, CallbackInfo ci) {
        if (!stack.isEmpty()) {
            if (stack.getItem() instanceof SnowballSprinklerItem && shouldShowSnowBar(stack)) {
                RenderSystem.disableDepthTest();
                RenderSystem.disableBlend();
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferBuilder = tessellator.getBuffer();
                int i = getSnowBarStep(stack);
                int j = getSnowBarColor(stack);
                this.renderGuiQuad(bufferBuilder, x + 2, y + 13, i + 1, 2, 0, 0, 0, 255);
                this.renderGuiQuad(bufferBuilder, x + 2, y + 13, i, 1, j >> 16 & 255, j >> 8 & 255, j & 255, 255);
                RenderSystem.enableBlend();
                RenderSystem.enableDepthTest();
            }
        }
    }

    private void renderGuiQuad(BufferBuilder buffer, int x, int y, int width, int height, int red, int green, int blue, int alpha) {
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex((double)(x + 0), (double)(y + 0), 0.0).color(red, green, blue, alpha).next();
        buffer.vertex((double)(x + 0), (double)(y + height), 0.0).color(red, green, blue, alpha).next();
        buffer.vertex((double)(x + width), (double)(y + height), 0.0).color(red, green, blue, alpha).next();
        buffer.vertex((double)(x + width), (double)(y + 0), 0.0).color(red, green, blue, alpha).next();
        BufferRenderer.drawWithGlobalProgram(buffer.end());
    }

    public boolean shouldShowSnowBar(ItemStack stack) {
        return !(stack.getDamage() == 0);
    }

    public int getSnowBarStep(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof SnowballSprinklerItem) {
            return Math.round(13.0F - (float)stack.getDamage() * 13.0F / (float)item.getMaxDamage());
        }
        return 0;
    }

    private static int getSnowBarColor(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof SnowballSprinklerItem) {
            float f = Math.max(0.0F, ((float)item.getMaxDamage() - (float)stack.getDamage()) / (float)item.getMaxDamage());
            return MathHelper.hsvToRgb(0.5f, MathHelper.clamp((f * 1.25f) - 0.25f, 0, 1), 1.0F);
        }
        return 0;
    }
}