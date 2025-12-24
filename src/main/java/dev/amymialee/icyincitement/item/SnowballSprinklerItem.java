package dev.amymialee.icyincitement.item;

import dev.amymialee.icyincitement.IcyIncitement;
import dev.amymialee.icyincitement.cca.SnowComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.CommonColors;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.throwableitemprojectile.Snowball;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class SnowballSprinklerItem extends Item {
    public SnowballSprinklerItem(Properties settings) {
        super(settings);
    }

    @Override
    public void onUseTick(@NonNull Level world, @NonNull LivingEntity user, @NotNull ItemStack stack, int remainingUseTicks) {
        if (!(user instanceof Player player)) return;
        var component = SnowComponent.KEY.get(player);
        if (!component.hasCharge()) return;
        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.1f, 1f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
        if (world instanceof ServerLevel serverWorld) Projectile.spawnProjectileFromRotation((a, b, c) -> new Snowball(a, b, Items.SNOWBALL.getDefaultInstance()), serverWorld, Items.SNOWBALL.getDefaultInstance(), user, 0.0F, IcyIncitement.SNOW_VELOCITY.get(), IcyIncitement.SNOW_DIVERGENCE.get());
        var look = user.getLookAngle().multiply(-IcyIncitement.HORIZONTAL_RECOIL.get(), -IcyIncitement.VERTICAL_RECOIL.get(), -IcyIncitement.HORIZONTAL_RECOIL.get());
        user.push(look.x, look.y, look.z);
        user.fallDistance = Math.max(user.fallDistance - look.y * 8, 0);
        component.subtractCharge();
    }

    @Override
    public @NonNull InteractionResult use(@NonNull Level world, @NonNull Player user, @NonNull InteractionHand hand) {
        return ItemUtils.startUsingInstantly(world, user, hand);
    }

    @Override
    public int getUseDuration(@NonNull ItemStack stack, @NonNull LivingEntity user) {
        return 72000;
    }

    @Override
    public @NonNull ItemUseAnimation getUseAnimation(@NonNull ItemStack stack) {
        return ItemUseAnimation.BOW;
    }

    @Override
    public void mialib$renderCustomBar(GuiGraphics drawContext, ItemStack stack, int x, int y) {
        var player = Minecraft.getInstance().player;
        if (player == null) return;
        var charge = SnowComponent.KEY.get(player).getCharge();
        if (charge >= 1) return;
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