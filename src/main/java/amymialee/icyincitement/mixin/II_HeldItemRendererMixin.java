package amymialee.icyincitement.mixin;

import amymialee.icyincitement.IcyIncitement;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HeldItemRenderer.class)
public class II_HeldItemRendererMixin {
    @Inject(method = "isChargedCrossbow", at = @At("HEAD"))
    private static void isChargedCrossbow(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.isOf(IcyIncitement.EMPTY_SPRINKLER) || stack.isOf(IcyIncitement.SNOWBALL_SPRINKLER)) {
            cir.setReturnValue(true);
        }
    }
}
