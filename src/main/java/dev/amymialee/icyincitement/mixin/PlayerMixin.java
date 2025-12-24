package dev.amymialee.icyincitement.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.amymialee.icyincitement.item.BuzzsawItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public class PlayerMixin {
    @WrapOperation(method = "getDestroySpeed", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getDestroySpeed(Lnet/minecraft/world/level/block/state/BlockState;)F"))
    private float icyincitement$sawSpeed(@NonNull ItemStack instance, BlockState blockState, Operation<Float> original) {
        if (instance.getItem() instanceof BuzzsawItem buzzsaw) return buzzsaw.getDestroySpeed((Player) (Object) this, instance, blockState);
        return original.call(instance, blockState);
    }
}