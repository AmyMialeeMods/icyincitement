package dev.amymialee.icyincitement.item;

import dev.amymialee.icyincitement.IcyIncitement;
import dev.amymialee.icyincitement.cca.SnowComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.item.consume.UseAction;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Colors;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class SnowballSprinklerItem extends Item {
    public SnowballSprinklerItem(Settings settings) {
        super(settings);
    }

    @Override
    public void usageTick(World world, LivingEntity user, @NotNull ItemStack stack, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) return;
        var component = SnowComponent.KEY.get(player);
        if (!component.hasCharge()) return;
        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.1f, 1f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
        if (world instanceof ServerWorld serverWorld) ProjectileEntity.spawnWithVelocity((a, b, c) -> new SnowballEntity(a, b, Items.SNOWBALL.getDefaultStack()), serverWorld, Items.SNOWBALL.getDefaultStack(), user, 0.0F, IcyIncitement.SNOW_VELOCITY.get(), IcyIncitement.SNOW_DIVERGENCE.get());
        var look = user.getRotationVector().multiply(-IcyIncitement.HORIZONTAL_RECOIL.get(), -IcyIncitement.VERTICAL_RECOIL.get(), -IcyIncitement.HORIZONTAL_RECOIL.get());
        user.addVelocity(look.x, look.y, look.z);
        user.fallDistance = Math.max(user.fallDistance - look.y * 8, 0);
        component.subtractCharge();
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public void mialib$renderCustomBar(DrawContext drawContext, ItemStack stack, int x, int y) {
        var player = MinecraftClient.getInstance().player;
        if (player == null) return;
        var charge = SnowComponent.KEY.get(player).getCharge();
        if (charge >= 1) return;
        var matrices = drawContext.getMatrices();
        var rg = (int) (charge * 205 + 50);
        drawContext.fill(x + 2, y + 13, x + 15, y + 15, Colors.BLACK);
        matrices.pushMatrix();
        matrices.translate(x + 2, y + 13);
        matrices.scale(charge * 13, 1f);
        drawContext.fill(0, 0, 1, 1, 0xFF0000FF | (rg << 8) | (rg << 16));
        matrices.popMatrix();
    }
}