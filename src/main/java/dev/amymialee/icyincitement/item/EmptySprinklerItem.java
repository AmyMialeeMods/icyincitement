package dev.amymialee.icyincitement.item;

import dev.amymialee.icyincitement.IcyIncitement;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.golem.SnowGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

public class EmptySprinklerItem extends Item {
    public EmptySprinklerItem(Properties settings) {
        super(settings);
    }

    @Override
    public @NonNull InteractionResult interactLivingEntity(@NonNull ItemStack stack, @NonNull Player user, @NonNull LivingEntity entity, @NonNull InteractionHand hand) {
        if (entity instanceof SnowGolem) {
            if (entity.level() instanceof ServerLevel serverWorld) {
                var item = entity.spawnAtLocation(serverWorld, IcyIncitement.SNOWBALL_SPRINKLER.getDefaultInstance(), 1.0F);
                if (item != null) {
                    item.setPickUpDelay(0);
                    item.setTarget(user.getUUID());
                    item.setDeltaMovement(item.getDeltaMovement().add((entity.getRandom().nextFloat() - entity.getRandom().nextFloat()) * 0.1F, entity.getRandom().nextFloat() * 0.05F, (entity.getRandom().nextFloat() - entity.getRandom().nextFloat()) * 0.1F));
                    entity.level().playSound(null, item, SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.PLAYERS, 0.6f, 1);
                    stack.shrink(1);
                }
            } else for (var i = 0; i < 16; i++) {
                entity.level().addParticle(ParticleTypes.POOF, entity.getRandomX(1.0), entity.getRandomY(), entity.getRandomZ(1.0), entity.getRandom().nextGaussian() * 0.02, entity.getRandom().nextGaussian() * 0.02, entity.getRandom().nextGaussian() * 0.02);
            }
            entity.discard();
            return InteractionResult.SUCCESS_SERVER;
        }
        return super.interactLivingEntity(stack, user, entity, hand);
    }
}