package xyz.amymialee.icyincitement.item;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
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
import xyz.amymialee.icyincitement.IcyIncitement;
import xyz.amymialee.icyincitement.cca.SnowComponent;
import xyz.amymialee.icyincitement.entity.IceBallEntity;

public class SnowballSprinklerItem extends Item {
    public SnowballSprinklerItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    @Override
    public void usageTick(World world, LivingEntity user, @NotNull ItemStack stack, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) return;
        var component = SnowComponent.KEY.get(player);
        if (!component.hasCharge()) return;
        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.1f, 1f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
        if (world instanceof ServerWorld serverWorld) ProjectileEntity.spawnWithVelocity((a, b, c) -> new IceBallEntity(a, b), serverWorld, Items.SNOWBALL.getDefaultStack(), user, 0.0F, IcyIncitement.SNOW_VELOCITY.get(), IcyIncitement.SNOW_DIVERGENCE.get());
        var look = user.getRotationVector().multiply(-IcyIncitement.HORIZONTAL_RECOIL.get(), -IcyIncitement.VERTICAL_RECOIL.get(), -IcyIncitement.HORIZONTAL_RECOIL.get());
        user.addVelocity(look.x, look.y, look.z);
        if (look.y > 0) user.fallDistance = Math.max(user.fallDistance - 1, 0);
        component.subtractCharge();
    }

    @Override
    public void mialib$renderCustomBar(DrawContext drawContext, ItemStack stack, int x, int y) {
        var player = MinecraftClient.getInstance().player;
        if (player == null) return;
        var component = SnowComponent.KEY.get(player);
        var charge = component.getCharge();
        if (charge >= 1) return;
        var width = component.getCharge() * 13f;
        var rg = (int) (component.getCharge() * 205 + 50);
        var colour = 0xFF0000FF | (rg << 8) | (rg << 16);
        drawContext.getMatrices().push();
        drawContext.mialib$fill(RenderLayer.getGui(), x + 2, y + 13, x + 15, y + 15, 200, Colors.BLACK);
        drawContext.mialib$fill(RenderLayer.getGui(), x + 2, y + 13, x + 2 + width, y + 14, 200, colour);
        drawContext.getMatrices().pop();
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }
}