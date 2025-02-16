package xyz.amymialee.icyincitement.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.consume.UseAction;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.icyincitement.IcyIncitement;
import xyz.amymialee.icyincitement.entity.IceBallEntity;

public class EmptySprinklerItem extends Item {
    public EmptySprinklerItem(Settings settings) {
        super(settings);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public ActionResult use(@NotNull World world, @NotNull PlayerEntity user, Hand hand) {
        var itemStack = user.getStackInHand(hand);
        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, world.getRandom().nextFloat() * 0.2F + 0.2F);
        if (world instanceof ServerWorld serverWorld) ProjectileEntity.spawnWithVelocity((a, b, c) -> new IceBallEntity(a, b), serverWorld, Items.SNOWBALL.getDefaultStack(), user, 0.0F, 1.0f, 6.0F);
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        user.getItemCooldownManager().set(itemStack, 30);
        return ActionResult.SUCCESS;
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (entity instanceof SnowGolemEntity golem) {
            if (entity.getWorld() instanceof ServerWorld serverWorld) {
                var item = golem.dropStack(serverWorld, IcyIncitement.SNOWBALL_SPRINKLER.getDefaultStack(), 1.0F);
                if (item != null) {
                    item.setPickupDelay(0);
                    item.setOwner(user.getUuid());
                    item.setVelocity(item.getVelocity().add((entity.getRandom().nextFloat() - entity.getRandom().nextFloat()) * 0.1F, entity.getRandom().nextFloat() * 0.05F, (entity.getRandom().nextFloat() - entity.getRandom().nextFloat()) * 0.1F));
                    entity.getWorld().playSoundFromEntity(null, item, SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST, SoundCategory.PLAYERS, 0.6f, 1);
                    stack.decrement(1);
                }
            } else for (var i = 0; i < 16; i++) {
                entity.getWorld().addParticle(ParticleTypes.POOF, entity.getParticleX(1.0), entity.getRandomBodyY(), entity.getParticleZ(1.0), entity.getRandom().nextGaussian() * 0.02, entity.getRandom().nextGaussian() * 0.02, entity.getRandom().nextGaussian() * 0.02);
            }
            entity.discard();
        }
        return super.useOnEntity(stack, user, entity, hand);
    }
}