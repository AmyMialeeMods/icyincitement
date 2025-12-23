package dev.amymialee.icyincitement.item;

import dev.amymialee.icyincitement.IcyIncitement;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public class EmptySprinklerItem extends Item {
    public EmptySprinklerItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (entity instanceof SnowGolemEntity golem) {
            if (entity.getEntityWorld() instanceof ServerWorld serverWorld) {
                var item = golem.dropStack(serverWorld, IcyIncitement.SNOWBALL_SPRINKLER.getDefaultStack(), 1.0F);
                if (item != null) {
                    item.setPickupDelay(0);
                    item.setOwner(user.getUuid());
                    item.setVelocity(item.getVelocity().add((entity.getRandom().nextFloat() - entity.getRandom().nextFloat()) * 0.1F, entity.getRandom().nextFloat() * 0.05F, (entity.getRandom().nextFloat() - entity.getRandom().nextFloat()) * 0.1F));
                    entity.getEntityWorld().playSoundFromEntity(null, item, SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST, SoundCategory.PLAYERS, 0.6f, 1);
                    stack.decrement(1);
                }
            } else for (var i = 0; i < 16; i++) {
                entity.getEntityWorld().addParticleClient(ParticleTypes.POOF, entity.getParticleX(1.0), entity.getRandomBodyY(), entity.getParticleZ(1.0), entity.getRandom().nextGaussian() * 0.02, entity.getRandom().nextGaussian() * 0.02, entity.getRandom().nextGaussian() * 0.02);
            }
            entity.discard();
        }
        return super.useOnEntity(stack, user, entity, hand);
    }
}