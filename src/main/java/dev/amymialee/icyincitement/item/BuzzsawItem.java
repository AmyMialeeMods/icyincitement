package dev.amymialee.icyincitement.item;

import dev.amymialee.icyincitement.cca.BuzzsawComponent;
import dev.amymialee.icyincitement.entities.SawbladeEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.CommonColors;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.item.component.UseEffects;
import net.minecraft.world.item.component.Weapon;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;

import java.util.HashSet;

public class BuzzsawItem extends Item {
    public BuzzsawItem(@NonNull Properties properties) {
        super(properties
                .attributes(ItemAttributeModifiers.builder()
                        .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 4f, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                        .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, -3f, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).build())
                .enchantable(15)
                .component(DataComponents.SWING_ANIMATION, new SwingAnimation(SwingAnimationType.NONE, 0))
                .component(DataComponents.USE_EFFECTS, new UseEffects(true, true, 1.0F))
                .component(DataComponents.WEAPON, new Weapon(0, 2f)));
    }

    @Override
    public boolean releaseUsing(@NonNull ItemStack stack, @NonNull Level world, @NonNull LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof ServerPlayer player)) return false;
        var component = BuzzsawComponent.KEY.get(player);
        if (component.getSpeed() > 0.5f) {
            var ball = new SawbladeEntity(world, player);
            ball.setPos(user.position().add(0, user.getBbHeight() / 1.6, 0));
            ball.setDeltaMovement(user.getLookAngle().add(0, 0.15, 0).scale(1.5));
            ball.setOwner(user);
            world.addFreshEntity(ball);
            world.playSound(null, player, SoundEvents.TRIDENT_THROW.value(), SoundSource.PLAYERS, 1.2F, 0.85f + 0.3f * user.getRandom().nextFloat());
            component.setSpeed(0f);
        }
        return false;
    }

    @Override
    public boolean isCorrectToolForDrops(@NonNull ItemStack itemStack, @NonNull BlockState blockState) {
        if (!blockState.is(BlockTags.INCORRECT_FOR_NETHERITE_TOOL) && blockState.is(BlockTags.MINEABLE_WITH_AXE)) return true;
        if (!blockState.is(BlockTags.INCORRECT_FOR_COPPER_TOOL) && (blockState.is(BlockTags.MINEABLE_WITH_HOE) || blockState.is(BlockTags.MINEABLE_WITH_SHOVEL) || blockState.is(BlockTags.SWORD_EFFICIENT))) return true;
        if (!blockState.is(BlockTags.INCORRECT_FOR_STONE_TOOL) && blockState.is(BlockTags.MINEABLE_WITH_PICKAXE)) return true;
        return super.isCorrectToolForDrops(itemStack, blockState);
    }

    public float getDestroySpeed(Player player, @NonNull ItemStack itemStack, @NonNull BlockState blockState) {
        var max = 0f;
        if (!blockState.is(BlockTags.INCORRECT_FOR_NETHERITE_TOOL) && blockState.is(BlockTags.MINEABLE_WITH_AXE)) {
            max = 11f;
        } else if (!blockState.is(BlockTags.INCORRECT_FOR_COPPER_TOOL) && (blockState.is(BlockTags.MINEABLE_WITH_HOE) || blockState.is(BlockTags.MINEABLE_WITH_SHOVEL) || blockState.is(BlockTags.SWORD_EFFICIENT))) {
            max = 5f;
        } else if (!blockState.is(BlockTags.INCORRECT_FOR_STONE_TOOL) && blockState.is(BlockTags.MINEABLE_WITH_PICKAXE)) {
            max = 3f;
        }
        return (float) (super.getDestroySpeed(itemStack, blockState) + max * Math.pow(player.getComponent(BuzzsawComponent.KEY).getSpeed(), 4));
    }

    public static float getTreeSpeed(float result, BlockGetter blockGetter, @NonNull Player player, BlockPos blockPos) {
        if (player.getComponent(BuzzsawComponent.KEY).shouldLumberjack()) {
            var tree = BuzzsawComponent.fillTargetSet(blockGetter, blockPos, new HashSet<>());
            if (!tree.isEmpty()) return (float) (result / Math.pow(tree.size(), 0.5));
        }
        return result;
    }

    @Override
    public @NonNull InteractionResult use(@NonNull Level world, @NonNull Player user, @NonNull InteractionHand hand) {
        return ItemUtils.startUsingInstantly(world, user, hand);
    }

    @Override
    public int getUseDuration(@NonNull ItemStack stack, @NonNull LivingEntity user) {
        return Integer.MAX_VALUE;
    }

    @Override
    public @NonNull ItemUseAnimation getUseAnimation(@NonNull ItemStack stack) {
        return ItemUseAnimation.BOW;
    }

    @Override
    public int mialib$getNameColor(ItemStack stack) {
        return 0xFF9999FF;
    }

    @Override
    public void mialib$renderCustomBar(GuiGraphics drawContext, ItemStack stack, int x, int y) {
        var player = Minecraft.getInstance().player;
        if (player == null) return;
        var component = BuzzsawComponent.KEY.get(player);
        var charge = component.getSpeed();
        if (charge <= 0) return;
        var matrices = drawContext.pose();
        var rg = (int) (charge * 205 + 50);
        drawContext.fill(x + 2, y + 13, x + 15, y + 15, CommonColors.BLACK);
        matrices.pushMatrix();
        matrices.translate(x + 2, y + 13);
        matrices.scale(charge * 13, 1f);
        drawContext.fill(0, 0, 1, 1, 0xFF0000FF | (rg << 8) | (rg << 16));
        matrices.popMatrix();
    }
}