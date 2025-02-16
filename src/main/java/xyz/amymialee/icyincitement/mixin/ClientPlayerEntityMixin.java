package xyz.amymialee.icyincitement.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.network.ClientPlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.amymialee.icyincitement.IcyIncitement;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @WrapOperation(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"))
    private boolean icyincitement$noslowdown(ClientPlayerEntity player, @NotNull Operation<Boolean> original) {
        return original.call(player) && !player.getActiveItem().isOf(IcyIncitement.EMPTY_SPRINKLER) && !player.getActiveItem().isOf(IcyIncitement.SNOWBALL_SPRINKLER);
    }
}
