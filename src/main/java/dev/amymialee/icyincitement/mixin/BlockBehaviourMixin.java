package dev.amymialee.icyincitement.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.amymialee.icyincitement.cca.BuzzsawComponent;
import dev.amymialee.icyincitement.item.BuzzsawItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockBehaviour.class)
public class BlockBehaviourMixin {
    @WrapMethod(method = "getDestroyProgress")
    protected float icyincitement$treeProgress(@NonNull BlockState blockState, Player player, BlockGetter blockGetter, BlockPos blockPos, @NonNull Operation<Float> original) {
        var result = original.call(blockState, player, blockGetter, blockPos);
        if (player.getComponent(BuzzsawComponent.KEY).shouldLumberjack()) return BuzzsawItem.getTreeSpeed(result, blockGetter, player, blockPos);
        return result;
    }
}