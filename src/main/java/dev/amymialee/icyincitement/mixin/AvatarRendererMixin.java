package dev.amymialee.icyincitement.mixin;

import dev.amymialee.icyincitement.IcyIncitement;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.HumanoidArm;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AvatarRenderer.class)
public class AvatarRendererMixin {
    @Inject(method = "getArmPose(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/world/entity/HumanoidArm;)Lnet/minecraft/client/model/HumanoidModel$ArmPose;", at = @At("HEAD"), cancellable = true)
    private static void icyincitement$buzzsaw(@NonNull Avatar avatar, HumanoidArm hand, CallbackInfoReturnable<HumanoidModel.ArmPose> cir) {
        if (avatar.getActiveItem().is(IcyIncitement.BUZZSAW)) cir.setReturnValue(HumanoidModel.ArmPose.BOW_AND_ARROW);
    }
}